/*
 * MetaPropertyContainerFactory.java
 * Project: EATool
 * Created on 07-Jul-2006
 *
 */
package alvahouse.eatool.repository.metamodel.factory;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.base.NamedRepositoryItemFactory;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaPropertyContainer;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyType;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyTypes;
import alvahouse.eatool.util.UUID;

/**
 * MetaPropertyContainerFactory is a base class for factories that
 * produce containers for meta-properties (such as MetaEntity, MetaRole
 * and MetaRelationship). 
 * 
 * @author rbp28668
 */
public class MetaPropertyContainerFactory extends NamedRepositoryItemFactory{

    /** The MetaProperty currently being read in */
    private MetaProperty currentMetaProperty = null;
    
    /** Possible types for the properties, create on demand. */
    private MetaPropertyTypes types = null;

    /**
     * Constructor to create the meta property container factory.
     * @param extensibleTypes provides the basis for the meta property type system and 
     * is used to create the collection of MetaPropertyTypes when needed.
     */
    public MetaPropertyContainerFactory(MetaPropertyTypes types) {
        super();
        if(types == null){
            throw new NullPointerException("Can't use Null types in meta property factory");
        }
        this.types = types;
    }

    /**
     * Starts a MetaProperty definition.  This checks to see if it's a
     * meta-property definition and is a NOP if not.
     * @param container is the MetaPropertyContainer that will receive this meta property.
     * @param uri
     * @param local
     * @param attrs
     * @return true if it processes a meta property, false otherwise.
     */
    protected boolean startMetaProperty(MetaPropertyContainer container, String uri, String local, Attributes attrs) throws Exception{
        boolean isMetaProperty = false;
        if(local.equals("MetaProperty")) {
	        if(container == null) 
	            throw new InputException("MetaPropety outside container while loading XML");
	            
	        if(currentMetaProperty != null)
	            throw new InputException("Nested Meta Property loading XML into repository");

            isMetaProperty = true;
	        
	        UUID uuid = getUUID(attrs);
	        
//	        currentMetaProperty = container.getMetaProperty(uuid);
//	        if(currentMetaProperty == null){
//	            currentMetaProperty = new MetaProperty(uuid);
//	        }
            currentMetaProperty = new MetaProperty(uuid);
	        
	        getCommonFields(currentMetaProperty, attrs);
	        
	        String attr = attrs.getValue("type");
	        if(attr == null)
	            throw new InputException("Missing type attribute for Meta Property");
	        MetaPropertyType mpt = getTypes().typeFromName(attr);
	        currentMetaProperty.setMetaPropertyType(mpt);

	        attr = attrs.getValue("default");
	        if(attr != null){
	            mpt.validate(attr);
	            currentMetaProperty.setDefaultValue(attr);
	        }

	        attr = attrs.getValue("mandatory");
	        if(attr != null){
	            currentMetaProperty.setMandatory(attr.equals("true"));
	        }

	        attr = attrs.getValue("readonly");
	        if(attr != null){
	            currentMetaProperty.setReadOnly(attr.equals("true"));
	        }
	        
	        attr = attrs.getValue("summary");
	        if(attr != null){
	            currentMetaProperty.setSummary(attr.equals("true"));
	        }
	        
        }
        return isMetaProperty;
    }
    
    /**
     * Gets the MetaPropertyTypes.
	 * @return
	 */
	private MetaPropertyTypes getTypes() throws Exception{
		return types;
	}

	/**
     * Optionally ends a meta property.
     * @param container
     * @param uri
     * @param local
     * @return true if it was the end of a meta property, false otherwise.
     */
    protected boolean endMetaProperty(MetaPropertyContainer container, String uri, String local){
        boolean isMetaProperty = false;
	    if (local.equals("MetaProperty")) {
	        //System.out.println("  Adding MetaProperty " + currentMetaProperty.getName() + ", " + currentMetaProperty.getKey().toString());
	        try {
				container.addMetaProperty(currentMetaProperty);
			} catch (Exception e) {
				throw new InputException("Unable to add MetaProperty",e);
			}
	        currentMetaProperty = null;
	        isMetaProperty = true;
	    }
	    return isMetaProperty;
    }
    
    /**
     * Will set the meta property description if the current meta
     * property is extant.
     * @param str is the description to set.
     * @return true if the current meta property is not null.
     */
    public boolean setPropertyDescription(String str){
        if(currentMetaProperty != null){
            currentMetaProperty.setDescription(str);
        }
        return currentMetaProperty != null;
    }

}
