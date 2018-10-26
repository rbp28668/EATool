/*
 * MetaModel.java
 * Project: EATool
 * Created on 06-Mar-2006
 *
 */
package alvahouse.eatool.scripting.proxy;

import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.impl.MetaEntityImpl;
import alvahouse.eatool.repository.metamodel.impl.MetaRelationshipImpl;
import alvahouse.eatool.util.UUID;

/**
 * MetaModel is a proxy object for the meta model that provides 
 * simplified lookup for scripting.
 * 
 * @author rbp28668
 */
public class MetaModelProxy {

    private MetaModel meta;
    
    /**
     * Creates a new proxy object tied to the underlying meta model.
     * @param meta is the underlying meta-model.
     */
    MetaModelProxy(
            MetaModel meta) {
        super();
        this.meta = meta;
    }

    /**
     * Gets all the MetaEntities in the meta model.
     * @return a MetaEntitySet containing all the MetaEntities in the meta model.
     */
    public MetaEntitySet getAllMetaEntities(){
        MetaEntitySet set = new MetaEntitySet();
        set.add(meta.getMetaEntities());
        return set;
    }
    
    /**
     * Gets all the MetaEntities apart from those which are marked as Abstract or
     * which do not have a display hint set.
     * @return a MetaEntitySet containing all the MetaEntities apart from those Abstract
     * or without a display hint.
     */
    public MetaEntitySet getMetaEntities(){
        MetaEntitySet set = new MetaEntitySet();
        for(MetaEntity me : meta.getMetaEntities()){
            if(!me.isAbstract() && me.getDisplayHint() != null){
                set.add(me);
            }
        }
        return set;
        
    }
    /**
     * Gets a MetaEntitySet containing a single meta-entity.
     * @param key is the string key (UUID) that identifies the
     * MetaEntity.
     * @return a new MetaEntitySet containing the MetaEntity given by
     * the key or empty if the key does not match.
     */
    public MetaEntitySet getMetaEntity(String key){
        MetaEntity me = meta.getMetaEntity(new UUID(key));
        MetaEntitySet set = new MetaEntitySet();
        if(me != null){
            set.add(me);
        }
        return set;
    }
    
    /**
     * Adds a Meta entity to an existing MetaEntitySet.
     * @param key is the string key (UUID) that identifies the
     * MetaEntity to add.
     * @return the updated MetaEntitySet.
     */
    public MetaEntitySet addMetaEntity(MetaEntitySet start, String key){
        MetaEntity me = meta.getMetaEntity(new UUID(key));
        if(me != null){
            start.add(me);
        }
        return start;
    }

    /**
     * Gets all the MetaRelationships.
     * @return a MetaRelationshipSet containing all the MetaRelationships.
     */
    public MetaRelationshipSet getMetaRelationships(){
        MetaRelationshipSet set = new MetaRelationshipSet();
        for(MetaRelationship mr : meta.getMetaRelationships()){
            set.add(mr);
        }
        return set;
        
    }

    /**
     * Gets a MetaRelationshipSet containing a single meta-relationship.
     * @param key is the string key (UUID) that identifies the
     * MetaRelationship.
     * @return a new MetaRelationshipSet containing the MetaRelationship given by
     * the key or empty if the key does not match.
     */
    public MetaRelationshipSet getMetaRelationship(String key){
        MetaRelationship mr = meta.getMetaRelationship(new UUID(key));
        MetaRelationshipSet set = new MetaRelationshipSet();
        if(mr != null){
            set.add(mr);
        }
        return set;
    }

    /**
     * Adds a Meta relationship to an existing MetaRelationshipSet.
     * @param key is the string key (UUID) that identifies the
     * MetaRelationship to add.
     * @return the updated MetaRelationshipSet.
     */
    public MetaRelationshipSet addMetaRelationship(MetaRelationshipSet start, String key){
        MetaRelationship mr = meta.getMetaRelationship(new UUID(key));
        if(mr != null){
            start.add(mr);
        }
        return start;
    }

}
