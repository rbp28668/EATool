/*
 * RelationshipTranslation.java
 * Project: EATool
 * Created on 04-Dec-2005
 *
 */
package alvahouse.eatool.repository.mapping;

import java.io.IOException;

import alvahouse.eatool.repository.dto.mapping.RelationshipTranslationDto;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRelationshipProxy;
import alvahouse.eatool.util.XMLWriter;


/**
 * RelationshipTranslation does the mapping needed to map an entitity (and its
 * properties) in the import XML format into the existing model.
 * <pre>
 * <Relationship type="Business Process Order">
 *  <Role type="predecessor">
 *      <PropertyKey type="Name" value="discover drugs"/>
 *  </Role>
 *  <Role type="successor">
 *      <PropertyKey type="Name" value="develop drugs"/>
 *  <Role>
 * </Relationship>
 *
 * The type in Relationship translation maps a name to the key of a relationship.
 * Similarly for roles.  The property translations allow compound keys to be set up
 * to identify which entities each role connects to.
 * <RelationshipTranslation type="Business Process Order" uuid="9e6b51-cd45-11d5-936a-00047660c89a">
 *      <RoleTranslation type="predecessor" uuid="000000-0000-0000-0000-00000000" >
 *         <PropertyTranslation type="Name" uuid="000000-0000-0000-0000-00000000" key="true"/>
 *      </RoleTranslation>
 *      <RoleTranslation type="successor" uuid="000000-0000-0000-0000-00000000" >
 *         <PropertyTranslation type="Name" uuid="000000-0000-0000-0000-00000000" key="true"/>
 *      </RoleTranslation>
 * </RelationshipTranslation>
 * </pre>
 */

public class RelationshipTranslation {

    private String type="";             	   // what the "type" attribute of the input record is
    private MetaRelationshipProxy meta = new MetaRelationshipProxy();      // maps to this meta relationship 
    private RoleTranslation startRoleTranslation = null;
    private RoleTranslation finishRoleTranslation = null;

    
    /**
     * Creates an empty RelationshipTranslation.
     */
    public RelationshipTranslation() {
        startRoleTranslation = new RoleTranslation(this);
        finishRoleTranslation = new RoleTranslation(this);
    }

    /**
	 * @param rtdto
	 */
	public RelationshipTranslation(RelationshipTranslationDto rtdto) {
		type = rtdto.getType();
		meta = new MetaRelationshipProxy();
		meta.setKey(rtdto.getMetaRelationshipKey());
		startRoleTranslation = new RoleTranslation(this, rtdto.getStartRoleTranslation());
		finishRoleTranslation = new RoleTranslation(this, rtdto.getFinishRoleTranslation());
	}

	public RelationshipTranslationDto toDto() {
		RelationshipTranslationDto dto = new RelationshipTranslationDto();
		copyTo(dto);
		return dto;
	}
	

	/**
     * Gets the type name used to identify the relationship in the input XML.
     * @return the input type name.
     */
    public String getTypeName() {
        return type;
    }
    
    /**
     * Gets the MetaRelationship that an imported relationship with the given
     * type name will be mapped to.
     * @return the target MetaRelationship.
     */
    public MetaRelationship getMeta(MetaModel metaModel) throws Exception{
        return meta.get(metaModel);
    }
    
    /**
     * Gets one of the child RoleTranslations based on type name.
     * @param typename is the type name for a child RoleTranslation.
     * @return the given RoleTranslation.
     * @throws IllegalArgumentException if the typename does not match either
     * child RoleTranslations.
     */
    public RoleTranslation getRoleByTypename(String typename) {
        if(startRoleTranslation.getTypeName().compareTo(typename) == 0)
            return startRoleTranslation;
        if(finishRoleTranslation.getTypeName().compareTo(typename) == 0)
            return finishRoleTranslation;
        throw new IllegalArgumentException("Unknown role type " + typename + " in relationship translation " + getTypeName());
    }
    
    /**
     * Gets the start Role translation.
     * @return the start RoleTranslation.
     */
    public RoleTranslation getStart(){
        return startRoleTranslation;
    }

    /**
     * Sets the start RoleTranslation.
     * @param rt the RoleTranslation to set.
     */
    public void setStart(RoleTranslation rt) {
        if(rt == null){
            throw new NullPointerException("Can't set null (start) RoleTranslation");
        }
        startRoleTranslation = rt;
    }
    
    /**
     * Gets the finish Role translation.
     * @return the finish RoleTranslation.
     */
    public RoleTranslation getFinish(){
        return finishRoleTranslation;
    }
    
    /**
     * Sets the finish RoleTranslation.
     * @param rt the RoleTranslation to set.
     */
    public void setFinish(RoleTranslation rt) {
        if(rt == null){
            throw new NullPointerException("Can't set null (finish) RoleTranslation");
        }
        finishRoleTranslation = rt;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Relationship " + type + " to " + meta.getKey();
    }

    /**
     * @param meta
     */
    public void setMeta(MetaRelationship meta) {
        if(meta == null){
            throw new NullPointerException("Can't set null Meta in EntityTranslation");
        }
        this.meta.set(meta);
    }

    /**
     * @param type
     */
    public void setType(String type) {
        if(type == null || type.length() == 0){
            throw new IllegalArgumentException("Must supply a valid type name");
        }
        this.type = type;
    }

    /**
     * Writes this RelationshipTranslation to XML.
     * @param writer is the XMLWriterDirect to write to.
     * @throws IOException
     */
    public void writeXML(XMLWriter writer) throws IOException {
        writer.startEntity("RelationshipTranslation");
        writer.addAttribute("type",getTypeName());
        writer.addAttribute("uuid",meta.getKey().toString());

        getStart().writeXML(writer);
        getFinish().writeXML(writer);
        
        writer.stopEntity();
    }
    
    @Override
    public Object clone() {
    	RelationshipTranslation copy = new RelationshipTranslation();
    	cloneTo(copy);
        return copy;
    }
    
    protected void cloneTo(RelationshipTranslation copy) {
        copy.type = type;
        copy.meta = (MetaRelationshipProxy)meta.clone(); 
        copy.startRoleTranslation = (RoleTranslation) startRoleTranslation.clone();
        copy.finishRoleTranslation = (RoleTranslation) finishRoleTranslation.clone();
   }
    
	/**
	 * @param dto
	 */
	protected void copyTo(RelationshipTranslationDto dto) {
		dto.setType(type);
		dto.setMetaRelationshipKey(meta.getKey());
		dto.setStartRoleTranslation(startRoleTranslation.toDto());
		dto.setFinishRoleTranslation(finishRoleTranslation.toDto());
		
	}
    

}