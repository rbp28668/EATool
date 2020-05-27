/*
 * Model.java
 * Project: EATool
 * Created on 06-Mar-2006
 *
 */
package alvahouse.eatool.scripting.proxy;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.queryParser.ParseException;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.repository.model.Role;
import alvahouse.eatool.util.UUID;

/**
 * Model is a proxy object for easy access to the model for scripting.
 * 
 * @author rbp28668
 */
@Scripted(description="Model contains the core entity/relationship data in the repository.")
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
    @Scripted(description="Searches for a set of Entities in the model using a free text search query string.")
    public EntitySet searchFor(String query) throws IOException, ParseException{
        Set<Entity> entities = repository.searchForEntities(query);
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
    @Scripted(description="Searches for a set of Entities in the model using a free text search query string."
    		+ " The set of entities are constrained to belong to the types described by the given set of meta-entities")
    public EntitySet searchForIn(String query, MetaEntitySet metaSet) throws IOException, ParseException{
        Set<Entity> entities = repository.searchForEntitiesOfType(query,metaSet.getContents());
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
    @Scripted(description="Get an EntitySet containing all the entities of a given type or types.")
    public EntitySet getEntitiesOf(MetaEntitySet metaSet){
        EntitySet set = new EntitySet();
        
        for(MetaEntity meta : metaSet.getContents()){
            List<Entity> entities = repository.getModel().getEntitiesOfType(meta);
            set.addAll(entities);
        }
        return set;
    }
    
    /**
     * Gets a set of entities that are related to an initial seed set of
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
    @Scripted(description="Gets a set of entities that are related to an initial seed set of"  
    		+ " entities.  As the relationships are directional this also allows" 
    		+ " only start, finish or either end to be included.  This is important"  
    		+ " when parent/child relationships are being processed. "
    		+ " Parameters are the initial set, include start and include finish.")
    public EntitySet getRelated(EntitySet seed, boolean includeStart, boolean includeFinish){
        EntitySet related = new EntitySet();
        for(Entity e : seed.getContents()){
            Set<Relationship> connected = e.getConnectedRelationships();
            for(Relationship rel : connected){
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
    @Scripted(description="Gets a set of entities that are related to an initial seed set of"  
    		+ " entities through an allowed set of relationship types."
    		+ " As the relationships are directional this also allows" 
    		+ " only start, finish or either end to be included.  This is important"  
    		+ " when parent/child relationships are being processed. "
    		+ " Parameters are the initial set, allowed routes, include start and include finish.")
    public EntitySet getRelatedVia(EntitySet seed, MetaRelationshipSet routes, boolean includeStart, boolean includeFinish){
        EntitySet related = new EntitySet();
        for(Entity e : seed.getContents()){
            
            Set<Relationship> connected = new HashSet<>();
            for(MetaRelationship route : routes.getContents()){
                Set<Relationship> other = e.getConnectedRelationshipsOf(route);
                connected.addAll(other);
            }
            for(Relationship rel : connected){
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
     * Retrieves an entity with the given key
     * @param key is the string representation of the UUID key.
     * @return the given entity (as proxy) or null if not found.
     */
    @Scripted(description="Get an entity with the given key.")
    public EntityProxy entity(String key) {
    	Entity e = repository.getModel().getEntity(new UUID(key));
    	return  (e != null) ? new EntityProxy(e) : null;
    }

    /**
     * Retrieves a relationship with the given key
     * @param key is the string representation of the UUID key.
     * @return the given relationship (as proxy) or null if not found.
     */
    @Scripted(description="Get relationship with the given key.")
    public RelationshipProxy relationship(String key) {
    	Relationship r = repository.getModel().getRelationship(new UUID(key));
    	return  (r != null) ? new RelationshipProxy(r) : null;
    }

    /**
     * Creates a new entity of the given type.
     * @param type is a MetaEntityProxy that describes the type of entity to create.
     * @return the given entity (as proxy).
     */
    @Scripted(description="Creates a new entity of the given type.")
    public EntityProxy newEntity(MetaEntityProxy type) {
    	Entity e = new Entity(type.get());
    	repository.getModel().addEntity(e);
    	return new EntityProxy(e);
    }

    /**
     * Creates a new relationship of a given type between 2 entities.
     * @param type describes the type of relationship to create.
     * @param start is the start entity to be connected by the relationship.
     * @param finish is the finish entity to be connected to the relationship.
     * @return a new relationship of the given type linking the 2 entities.
     */
    @Scripted(description="Creates a relationship of the given type between start and finish entities.")
    public RelationshipProxy newRelationship(MetaRelationshipProxy type, EntityProxy start, EntityProxy finish) {
    	
    	MetaRelationship mr = type.get();
    	
    	Relationship r = new Relationship(mr); 
    	
    	Role startRole = new Role(mr.start());
    	startRole.setConnection(start.get());
    	startRole.setRelationship(r);
    	r.setStart(startRole);
    	
    	Role finishRole = new Role(mr.finish());
    	finishRole.setConnection(finish.get());
    	finishRole.setRelationship(r);
    	r.setFinish(finishRole);
    	
    	repository.getModel().addRelationship(r);
    	return  new RelationshipProxy(r);
    }

    /**
     * Create a new Filter.
     * @return a new (empty) filter.
     */
    @Scripted(description="Create a new, empty Filter.")
    public Filter createFilter(){
        return new Filter();
    }
}
