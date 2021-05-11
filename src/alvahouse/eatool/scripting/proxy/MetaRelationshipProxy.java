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
@Scripted(description="Part of the meta model this describes a  possible relationship between 2 enties.")  
public class MetaRelationshipProxy {

    private MetaRelationship meta;
    
    /**
     * 
     */
    MetaRelationshipProxy(MetaRelationship meta) {
        super();
        this.meta = meta;
    }

    MetaRelationship get() {
    	return meta;
    }
    
    /**
     * Gets the name of the MetaRelationship.
     * @return String containing the name.
     */
    @Scripted(description="Gets the name of the meta-relationship.")
    public String getName(){
        return meta.getName();
    }
    
    /**
     * Gets the description of the MetaRelationship.
     * @return the String containing the description.
     */
    @Scripted(description="Gets the description of the meta-elationship.")
    public String getDescription(){
        return meta.getDescription();
    }
    
    /**
     * Gets the MetaRole that describes the start end of the
     * relationship.
     * @return the MetaRole for the start end.
     */
    @Scripted(description="Gets the meta-role that describes the start end of the relationship.")
    public MetaRoleProxy getStart(){
        return new MetaRoleProxy(meta.start());
    }
    
    /**
     * Gets the MetaRole that describes the finish end of the
     * relationship.
     * @return the MetaRole for the finish end.
     */
    @Scripted(description="Gets the meta-role that describes the finish end of the relationship.")
    public MetaRoleProxy getFinish(){
        return new MetaRoleProxy(meta.finish());
    }
    
    /**
     * Converts this MetaRelationship into a MetaRelationshipSet containing
     * just this item.
     * @return a new MetaRelationshipSet.
     */
    @Scripted(description="Converts this meta relationship into a meta-relationship set containing just this item.")
    public MetaRelationshipSet toSet(){
        MetaRelationshipSet set = new MetaRelationshipSet();
        set.add(meta);
        return set;
    }
    
    /**
     * Gets the set of MetaProperties corresponding to this MetaRelationship.
     * @return the MetaPropertySet.
     */
    @Scripted(description="Gets the set of meta-properties corresponding to this meta-relationship.")    
    public MetaPropertySet getMetaProperties() throws Exception{
        return new MetaPropertySet(meta.getMetaProperties());
    }

    
}
