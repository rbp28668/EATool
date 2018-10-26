/*
 * MetaRelationship.java
 * Project: EATool
 * Created on 24-Apr-2007
 *
 */
package alvahouse.eatool.scripting.proxy;

import alvahouse.eatool.repository.metamodel.MetaRelationship;


/**
 * MetaRelationship is part of the meta-model and describes a possible relationship
 * between 2 enties.
 * 
 * @author rbp28668
 */
public class MetaRelationshipProxy {

    private MetaRelationship meta;
    
    /**
     * 
     */
    MetaRelationshipProxy(MetaRelationship meta) {
        super();
        this.meta = meta;
    }

    /**
     * Gets the name of the MetaRelationship.
     * @return String containing the name.
     */
    public String getName(){
        return meta.getName();
    }
    
    /**
     * Gets the description of the MetaRelationship.
     * @return the String containing the description.
     */
    public String getDescription(){
        return meta.getDescription();
    }
    
    /**
     * Gets the MetaRole that describes the start end of the
     * relationship.
     * @return the MetaRole for the start end.
     */
    public MetaRoleProxy getStart(){
        return new MetaRoleProxy(meta.start());
    }
    
    /**
     * Gets the MetaRole that describes the finish end of the
     * relationship.
     * @return the MetaRole for the finish end.
     */
    public MetaRoleProxy getFinish(){
        return new MetaRoleProxy(meta.finish());
    }
    
    /**
     * Converts this MetaRelationship into a MetaRelationshipSet containing
     * just this item.
     * @return a new MetaRelationshipSet.
     */
    public MetaRelationshipSet toSet(){
        MetaRelationshipSet set = new MetaRelationshipSet();
        set.add(meta);
        return set;
    }
}
