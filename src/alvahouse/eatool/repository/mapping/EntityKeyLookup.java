/*
 * EntityKeyLookup.java
 *
 * Created on 26 February 2002, 04:35
 */

package alvahouse.eatool.repository.mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;

/**
 * EntityKeyLookup looks up entities by natural key (not uuid) so that
 * imported entities can be matched against existing instances that
 * already exist in the model.
 * @author  rbp28668
 */
class EntityKeyLookup {

	/** The cache of entities by compound natural key */
    private Map<String,Entity> cache = new HashMap<String,Entity>();

    /** Used to generate a composite key from an entity */
    private PropertyTranslationCollection trans;
    
    /** Creates an entity cache given an EntityTranslation.  This builds the 
     * cache that is indexed by the natural key of each entity (as defined
     * in this EntityTranslation) 
     * @param model is the model to lookup the entities from
     * @param me specifies the type of entities we want.
     * @param trans is the key generator for the type of entity we
     * want to cache
     */
    EntityKeyLookup(Model model, MetaEntity me, PropertyTranslationCollection trans) throws Exception {
    	assert(model != null);
    	assert(me != null);
    	assert(trans != null);
    	
    	this.trans = trans;
        List<Entity> entities = model.getEntitiesOfType(me);
        for(Entity e : entities){
            add(e);
        }
    }

    /**
     * Adds an entity to the cache keyed by its natural key.
     * @param e is the entity to add.
     */
    void add(Entity e){
        cache.put(trans.getKeyOf(e), e);
    }
    
    /** Looks up an entity by natural key.
     * @param key is the natural key to lookup by
     * @return is the matching entity, or null if not found
     */
    Entity get(String key) {
        return cache.get(key);
    }

}