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
import alvahouse.eatool.util.UUID;

/**
 * MetaModel is a proxy object for the meta model that provides 
 * simplified lookup for scripting.
 * 
 * @author rbp28668
 */
@Scripted(description="The current meta model.")    
public class MetaModelProxy {

    private MetaModel meta;
    
    /**
     * Creates a new proxy object tied to the underlying meta model.
     * @param meta is the underlying meta-model.
     */
    public MetaModelProxy(
            MetaModel meta) {
        super();
        this.meta = meta;
    }

    /**
     * Gets all the MetaEntities in the meta model.
     * @return a MetaEntitySet containing all the MetaEntities in the meta model.
     */
    @Scripted(description="Gets all the MetaEntities in the meta model."
    		+ " This includes abstract meta entities and ones where the display hint isn't set.")    
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
    @Scripted(description="Gets all the MetaEntities apart from those which are marked as Abstract or" + 
    		" which do not have a display hint set.")    
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
     * Gets a MetaEntity corresponding to a given key.
     * @param key is the string key (UUID) that identifies the
     * MetaEntity.
     * @return a new MetaEntity containing or null if the key does not match.
     */
    @Scripted(description="Gets a meta entity identified by the given key or null if the key does not match any meta entities.")    
    public MetaEntityProxy getMetaEntity(String key){
        MetaEntity me = meta.getMetaEntity(new UUID(key));
        return (me != null) ? new MetaEntityProxy(me) : null;
    }
    
    /**
     * Adds a Meta entity to an existing MetaEntitySet.
     * @param key is the string key (UUID) that identifies the
     * MetaEntity to add.
     * @return the updated MetaEntitySet.
     */
    @Scripted(description="Adds a meta entity identified by the given key to the supplied set.")    
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
    @Scripted(description="Gets all the MetaRelationships.")    
    public MetaRelationshipSet getMetaRelationships(){
        MetaRelationshipSet set = new MetaRelationshipSet();
        for(MetaRelationship mr : meta.getMetaRelationships()){
            set.add(mr);
        }
        return set;
        
    }

    /**
     * Gets a MetaRelationshipProxy corresponding to a given key.
     * @param key is the string key (UUID) that identifies the
     * MetaRelationship.
     * @return a new MetaRelationshipProxy containing the MetaRelationship given by
     * the key or null
     */
    @Scripted(description="Gets a set containing the single meta-relationship identified by the given key or empty if the key does not match.")    
    public MetaRelationshipProxy getMetaRelationship(String key){
        MetaRelationship mr = meta.getMetaRelationship(new UUID(key));
        return (mr != null) ? new MetaRelationshipProxy(mr) : null;
    }

    /**
     * Adds a Meta relationship to an existing MetaRelationshipSet.
     * @param key is the string key (UUID) that identifies the
     * MetaRelationship to add.
     * @return the updated MetaRelationshipSet.
     */
    @Scripted(description="Adds a Meta relationship to an existing MetaRelationshipSet."
    		+ " The meta relationship identified by the given key is added to the ")    
    public MetaRelationshipSet addMetaRelationship(MetaRelationshipSet start, String key){
        MetaRelationship mr = meta.getMetaRelationship(new UUID(key));
        if(mr != null){
            start.add(mr);
        }
        return start;
    }

}
