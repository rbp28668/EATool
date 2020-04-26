/*
 * Model.java
 * Project: EATool
 * Created on 06-Mar-2006
 *
 */
package alvahouse.eatool.scripting.proxy;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.queryParser.ParseException;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Relationship;

/**
 * Model is a proxy object for easy access to the model for scripting.
 * 
 * @author rbp28668
 */
public class ModelProxy {

    private Repository repository;
    /**
     * Creates a new proxy object tied to the given model.
     * @param model is the underlying Model. 
     */
    public ModelProxy(Repository repository) {
        super();
        this.repository = repository;
    }

    /**
     * Searches for a set of Entities in the model.
     * @param query is the search query.
     * @return an EntitySet containing the results of the search (maybe empty).
     * @throws IOException
     * @throws ParseException
     */
    public EntitySet searchFor(String query) throws IOException, ParseException{
        Set entities = repository.searchForEntities(query);
        EntitySet set = new EntitySet(entities);
        return set;
    }
    
    /**
     * Searches for a set of Entities in the model of a given type.
     * @param query is the search query.
     * @param metaSet is a MetaEntitySet that is used to restrict the
     * Entities to the types specified in the MetaEntitySet.
     * @return a new EntitySet with the results of the search.
     * @throws IOException
     * @throws ParseException
     */
    public EntitySet searchForIn(String query, MetaEntitySet metaSet) throws IOException, ParseException{
        Set entities = repository.searchForEntitiesOfType(query,metaSet.getContents());
        EntitySet set = new EntitySet(entities);
        return set;
    }

    /**
     * Get an EntitySet containing all the entities of a given type or
     * types.
     * @param metaSet is a MetaEntitySet containing the type(s) of entities
     * to return.
     * @return a new EntitySet containing all the entities of the given type(s).
     */
    public EntitySet getEntitiesOf(MetaEntitySet metaSet){
        EntitySet set = new EntitySet();
        
        for(Iterator iter = metaSet.getContents().iterator(); iter.hasNext();){
            MetaEntity meta = (MetaEntity)iter.next();
            List entities = repository.getModel().getEntitiesOfType(meta);
            set.addAll(entities);
        }
        return set;
    }
    
    /**
     * Gets a set of entities that are related to an intial seed set of
     * entities.  As the relationships are directional this also allows
     * only start, finish or either end to be included.  This is important
     * when parent/child relationships are being processed. 
     * @param seed is the seed set of entities.
     * @param includeStart includes the entities at the start end of the relationship
     * if true.  Otherwise those entities are excluded.
     * @param includeFinish includes the entities at the finish end of the relationship
     * if true.  Otherwise those entities are excluded.
     * @return a new EntitySet containing the related entities.
     */
    public EntitySet getRelated(EntitySet seed, boolean includeStart, boolean includeFinish){
        EntitySet related = new EntitySet();
        for(Iterator iter = seed.getContents().iterator(); iter.hasNext();){
            Entity e = (Entity)iter.next();
            Set connected = e.getConnectedRelationships();
            for(Iterator iterRel = connected.iterator(); iterRel.hasNext(); ){
                Relationship rel = (Relationship)iterRel.next();
                Entity start = rel.start().connectsTo();
                Entity finish = rel.finish().connectsTo();
                if(!start.equals(e) && includeStart){
                    related.add(start);
                }
                if(!finish.equals(e) && includeFinish){
                    related.add(finish);
                }
            }
        }
        return related;
    }
    
    /**
     * Gets a set of entities that are related to an intial seed set of
     * entities through one of a set of relationship types.  As the relationships are directional this also allows
     * only start, finish or either end to be included.  This is important
     * when parent/child relationships are being processed. 
     * @param seed is the seed set of entities.
     * @param routes provides a set of relationship types that describe
     * which relationships should be considered.
     * @param includeStart includes the entities at the start end of the relationship
     * if true.  Otherwise those entities are excluded.
     * @param includeFinish includes the entities at the finish end of the relationship
     * if true.  Otherwise those entities are excluded.
     * @return a new EntitySet containing the related entities.
     */
    public EntitySet getRelatedVia(EntitySet seed, MetaRelationshipSet routes, boolean includeStart, boolean includeFinish){
        EntitySet related = new EntitySet();
        for(Iterator iter = seed.getContents().iterator(); iter.hasNext();){
            Entity e = (Entity)iter.next();
            
            Set connected = new HashSet();
            for(Iterator iterRoute = routes.getContents().iterator(); iterRoute.hasNext();){
                MetaRelationship route = (MetaRelationship)iterRoute.next();
                Set other = e.getConnectedRelationshipsOf(route);
                connected.addAll(other);
            }
            for(Iterator iterRel = connected.iterator(); iterRel.hasNext(); ){
                Relationship rel = (Relationship)iterRel.next();
                Entity start = rel.start().connectsTo();
                Entity finish = rel.finish().connectsTo();
                if(!start.equals(e) && includeStart){
                    related.add(start);
                }
                if(!finish.equals(e) && includeFinish){
                    related.add(finish);
                }
            }
        }
        return related;
    }

    /**
     * Create a new Filter.
     * @return a new (empty) filter.
     */
    public Filter createFilter(){
        return new Filter();
    }
}
