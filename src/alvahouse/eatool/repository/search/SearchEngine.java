/*
 * SearchEngine.java
 * Created on 21-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.search;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import alvahouse.eatool.repository.ProgressCounter;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.ModelChangeEvent;
import alvahouse.eatool.repository.model.ModelChangeListener;
import alvahouse.eatool.repository.model.Property;
import alvahouse.eatool.util.UUID;

/**
 * SearchEngine is a wrapper round Lucene code to provide search
 * facilities to the repository.
 * Note - now uses a custom analyser to include stemming.
 * @author Bruce.Porteous
 *
 */
public class SearchEngine implements ModelChangeListener{
	
	private Directory directory;
	private Analyzer analyzer;
	private Model model;
	
	private Entity invalid = null;

	/**
	 * 
	 */
	public SearchEngine() {
		super();
		analyzer = new StemmingAnalyser();		
	}
	
	
	/**
	 * Indexes the model for search.
	 * @param counter is an optional ProgressCounter that will be updated
	 * on every document.  May be null.
	 * @param model is the Model to be indexed.
	 * @throws IOException
	 */
	public void indexModel(ProgressCounter counter, Model model) throws IOException{
		if(model == null){
			throw new NullPointerException("Can't index a null repository");
		}

		try {
			directory = new RAMDirectory();

			IndexWriter index = new IndexWriter(directory, analyzer, true);
			for(Entity entity : model.getEntities()){
				Document doc = entityToDoc(entity);
				index.addDocument(doc);
				if(counter != null){
				    counter.count("Indexing");
				}
			}
			index.optimize();
			index.close();
		} catch (Exception e) {
			throw new IOException("Unable to index model",e);
		}
		
		this.model = model;
		invalid = null;
	}

	/**
	 * @param entity
	 * @return
	 */
	private Document entityToDoc(Entity entity){
		Document doc = new Document();
		doc.add(Field.Keyword("uuid",entity.getKey().toString()));
		doc.add(Field.UnStored("meta",entity.getMeta().getName()));
		doc.add(Field.Keyword("metauuid",entity.getMeta().getKey().toString()));
		StringBuffer all = new StringBuffer();
		for(Property property : entity.getProperties()){
			String value = property.getValue();
			if(value != null) {
				doc.add(Field.UnStored(property.getMeta().getName(), value));
				all.append(property.getValue());
			}
		}
		doc.add(Field.UnStored("all",all.toString()));
		
		return doc;
	}
	
	/**
	 * @param queryString
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public Set<Entity> searchForEntities(String queryString) throws IOException, ParseException{
		if(queryString == null){
			throw new NullPointerException("Null query in search");
		}
		
		if(model == null){
			throw new IllegalStateException("Can't search on a repository that has not been indexed");
		}
		
		flushInvalid();
		
		QueryParser parser = new QueryParser("all",analyzer);	
		Query query = parser.parse(queryString);
		
		Set<Entity> results = runQuery(query);
		
		return results;
	}

	/**
	 * @param queryString
	 * @param types
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public Set<Entity> searchForEntitiesOfType(String queryString, Collection<MetaEntity> types) throws IOException, ParseException{
		if(queryString == null){
			throw new NullPointerException("Null query in search");
		}
		
		if(model == null){
			throw new IllegalStateException("Can't search on a repository that has not been indexed");
		}
		
		flushInvalid();
		
		BooleanQuery metaQuery = new BooleanQuery();
		for(MetaEntity meta : types){
		    Term term = new Term("metauuid", meta.getKey().toString());
		    metaQuery.add(new TermQuery(term), false, false );
		}
		QueryParser parser = new QueryParser("all",analyzer);	

		Query textQuery = parser.parse(queryString);
		
		BooleanQuery query = new BooleanQuery();
		query.add(metaQuery,true,false);
		query.add(textQuery,true,false);
		
		Set<Entity> results = runQuery(query);
		
		return results;
	}
	
	/**
     * @param query
     * @return
     * @throws IOException
     */
    private Set<Entity> runQuery(Query query) throws IOException {
        Searcher searcher = new IndexSearcher(directory);
		Hits hits = searcher.search(query);	// add security filter at some point.
		
		Set<Entity> results;
		try {
			results = new HashSet<Entity>();
			int nResults = hits.length();
			for(int i=0; i<nResults; ++i){
				Document doc = hits.doc(i);
				Field uuidField = doc.getField("uuid");
				UUID key = new UUID(uuidField.stringValue());
				Entity e = model.getEntity(key);
				results.add(e);
			}
		} catch (Exception e) {
			throw new IOException("Unable to run query",e);
		}

		searcher.close();
        return results;
    }


    /**
	 * flushInvalid updates any entity pointed to by <code>invalid</code>.
	 * This allows property updates to be coalesced into a single update that
	 * is applied when needed.
     * @throws IOException
     */
    private void flushInvalid() throws IOException {
        if(invalid != null){
		    deleteEntity(invalid);
		    addEntity(invalid);
		    invalid = null;
		}
    }


    /**
     * @param e
     * @throws IOException
     */
    public void deleteEntity(Entity e) throws IOException{
		Term term = new Term("uuid",e.getKey().toString());
		Query query = new TermQuery(term);
		IndexReader reader = IndexReader.open(directory);
		
		Searcher searcher = new IndexSearcher(reader);
		Hits hits = searcher.search(query);
		if(hits.length() != 1){
		    throw new IllegalStateException("Entity " + e.toString() + " is not known to the search index");
		}
		
		int id = hits.id(0);
		searcher.close();
		
		reader.delete(id);
		reader.close();
	}

    /**
     * @param e
     * @throws IOException
     */
    private void addEntity(Entity e) throws IOException {
        IndexWriter index = new IndexWriter(directory, analyzer, false);
        Document doc = entityToDoc(e);
        index.addDocument(doc);
        index.optimize();
        index.close();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.model.ModelChangeListener#modelUpdated(alvahouse.eatool.repository.model.ModelChangeEvent)
     */
    public void modelUpdated(ModelChangeEvent e) {
        Object o = e.getSource();
        if( !(o instanceof Model)){
            throw new IllegalArgumentException("invalid type of modelUpdated event source");
        }
        try {
            indexModel(null,(Model)o);
        } catch (Exception ex){
            model = null;
        }
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.model.ModelChangeListener#EntityAdded(alvahouse.eatool.repository.model.ModelChangeEvent)
     */
    public void EntityAdded(ModelChangeEvent e) {
        Object o = e.getSource();
        if( !(o instanceof Entity)){
            throw new IllegalArgumentException("invalid type of EntityAdded event source");
        }
        
        try {
            flushInvalid();
			addEntity((Entity)o);
        } catch (Exception ex){
            // NOP
        }
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.model.ModelChangeListener#EntityChanged(alvahouse.eatool.repository.model.ModelChangeEvent)
     */
    public void EntityChanged(ModelChangeEvent e) {
        Object o = e.getSource();
        if( !(o instanceof Entity)){
            throw new IllegalArgumentException("invalid type of EntityChanged event source");
        }
        
        try {
            flushInvalid();
            Entity entity = (Entity)o;
            deleteEntity(entity);
			addEntity(entity);
        } catch (Exception ex){
            // NOP
        }
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.model.ModelChangeListener#EntityDeleted(alvahouse.eatool.repository.model.ModelChangeEvent)
     */
    public void EntityDeleted(ModelChangeEvent e) {
        Object o = e.getSource();
        if( !(o instanceof Entity)){
            throw new IllegalArgumentException("invalid type of EntityDeleted event source");
        }
        
        try {
            flushInvalid();
            Entity entity = (Entity)o;
            deleteEntity(entity);
        } catch (Exception ex){
            // NOP
        }
        
    }



    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.model.ModelChangeListener#RelationshipAdded(alvahouse.eatool.repository.model.ModelChangeEvent)
     */
    public void RelationshipAdded(ModelChangeEvent e) {
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.model.ModelChangeListener#RelationshipChanged(alvahouse.eatool.repository.model.ModelChangeEvent)
     */
    public void RelationshipChanged(ModelChangeEvent e) {
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.model.ModelChangeListener#RelationshipDeleted(alvahouse.eatool.repository.model.ModelChangeEvent)
     */
    public void RelationshipDeleted(ModelChangeEvent e) {
    }


}
