/*
 * MetaPropertyTypes.java
 * Project: EATool
 * Created on 07-Jul-2006
 *
 */
package alvahouse.eatool.repository.metamodel.types;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.util.SettingsManager;

/**
 * MetaPropertyTypes is a container class for MetaPropertyTypes (the
 * field types in the model).
 * 
 * @author rbp28668
 */
public class MetaPropertyTypes {

    // Note that these are keyed by both the type name and the string representation of the UUID.
    private HashMap<String,MetaPropertyType> metaProperties = new HashMap<String,MetaPropertyType>();
    private List<MetaPropertyType> metaList = new LinkedList<MetaPropertyType>();

    private static List<MetaPropertyType> builtinTypes = new LinkedList<MetaPropertyType>();
    private static HashMap<String,MetaPropertyType> builtinLookup = new HashMap<String,MetaPropertyType>();
    
    static {
        addNativeType(new MetaPropertyType.TypeBoolean());
        addNativeType(new MetaPropertyType.TypeChar());
        addNativeType(new MetaPropertyType.TypeByte());
        addNativeType(new MetaPropertyType.TypeShort());
        addNativeType(new MetaPropertyType.TypeInt());
        addNativeType(new MetaPropertyType.TypeLong());
        addNativeType(new MetaPropertyType.TypeFloat());
        addNativeType(new MetaPropertyType.TypeDouble());
        addNativeType(new MetaPropertyType.TypeString());
        addNativeType(new MetaPropertyType.TypeUUID());
        addNativeType(new MetaPropertyType.TypeDate());
        addNativeType(new MetaPropertyType.TypeTime());
        addNativeType(new MetaPropertyType.TypeTimeStamp());
        addNativeType(new MetaPropertyType.TypeInterval());
        addNativeType(new MetaPropertyType.TypeURL());
        addNativeType(new MetaPropertyType.TypeText());

    }
    
    /**
     * 
     */
    public MetaPropertyTypes() {
        super();
        init();
    }

    private void init(){
        metaProperties.clear();
        metaList.clear();
        
        for(MetaPropertyType mpt : builtinTypes){
            metaProperties.put(mpt.getTypeName(), mpt);
            metaProperties.put(mpt.getKey().toString().toLowerCase(),mpt);
            metaList.add(mpt);
        }
    }
    
    /**
     * Gets the meta property type for a given type-name.
     * @param typename
     * @throws IllegalArgumentException
     * @return the MetaPropertyType corresponding to the given type name */    
    public MetaPropertyType typeFromName(String typename) throws IllegalArgumentException {
        typename = typename.toLowerCase();
        MetaPropertyType mpt = (MetaPropertyType) metaProperties.get(typename);
        if(mpt == null)
            throw new IllegalArgumentException("Unrecognised type " + typename);
        return mpt;
    }

    /**
     * Gets the meta property type for a given type-name.
     * @param typename
     * @throws IllegalArgumentException
     * @return the MetaPropertyType corresponding to the given type name */    
    public static MetaPropertyType getBuiltInType(String typename) throws IllegalArgumentException {
        typename = typename.toLowerCase();
        MetaPropertyType mpt = (MetaPropertyType) builtinLookup.get(typename);
        if(mpt == null)
            throw new IllegalArgumentException("Unrecognised type " + typename);
        return mpt;
    }

    /**
     * extendTypesFromConfig extends the default list of basic types from 
	 * a settings manager.
	 * @param cfg is the SettingsManager to look for extensions in.
	 */
	public void extendTypesFromConfig(SettingsManager cfg) {
        SettingsManager.Element eRoot = cfg.getOrCreateElement("/MetaTypes");
        for(SettingsManager.Element e : eRoot.getChildren()){
            if(e.getName().compareTo("MetaType") != 0)
                throw new IllegalArgumentException("Invalid MetaPropertyType Extension: illegal entity - " + e.getName());
            String typeClass = e.attributeRequired("class");
            Object o = null;
            try {
                o = Class.forName(typeClass);
            }
            catch(Exception x) {
                throw new IllegalArgumentException("Unable to create new MetaPropertyType: " + typeClass);
            }
            if(o instanceof MetaPropertyType) {
                addNativeType((MetaPropertyType)o);
            } else {
                throw new IllegalArgumentException(typeClass + " does not extend MetaPropertyType");
            }
            
        }
    }
    
	public Collection<MetaPropertyType> getTypes() {
	    return Collections.unmodifiableCollection(metaList);
	}
	
//    public String[] getTypeNames() {
//        return (String[])(metaProperties.keySet().toArray(new String[0]));
//    }
    
	public void addType(MetaPropertyType mpt){
        metaProperties.put(mpt.getKey().toString().toLowerCase(),mpt);
        metaList.add(mpt);
	}
	
    private static void addNativeType(MetaPropertyType mpt) {
        builtinTypes.add(mpt);
        builtinLookup.put(mpt.getTypeName(),mpt);
    }

    /**
     * Resets the contents to just the built-in types. 
     */
    public void deleteContents() {
        init();
    }

    /**
     * Removes a type from the list.
     * @param deletedType is the type to remove.
     */
    public void removeType(MetaPropertyType deletedType) {
        metaProperties.remove(deletedType.getKey().toString().toLowerCase());
        metaList.remove(deletedType);
    }

}
