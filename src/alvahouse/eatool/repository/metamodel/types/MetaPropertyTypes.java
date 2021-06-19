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
import alvahouse.eatool.util.UUID;

/**
 * MetaPropertyTypes is a container class for MetaPropertyTypes (the
 * field types in the model). This is an immutable class - once created there is no way to add or remove
 * meta property types.  As such these should be created as short-term sets of types (e.g. for
 * loading a repository).  Manages the built-in primitive types such as boolean, integer etc. As such
 * the set of types are the combination of the native and extensible types.
 * For historical reasons, a primitive type can be looked up by UUID (preferred) or name.
 * @author rbp28668
 */
public class MetaPropertyTypes {

    // Note that these are keyed by both the type name and the string representation of the UUID.
    private final HashMap<String,MetaPropertyType> typeLookup = new HashMap<String,MetaPropertyType>();
    private ExtensibleTypes extensibleTypes = null;
    
    private final static List<MetaPropertyType> builtinTypes = new LinkedList<MetaPropertyType>();
    private final static HashMap<String,MetaPropertyType> builtinLookup = new HashMap<String,MetaPropertyType>();
    private final static HashMap<String,String> oldKeyConcordance = new HashMap<>();
    
    static {
        addNativeType(new MetaPropertyType.TypeBoolean(), Keys.OLD_TYPE_BOOLEAN);
        addNativeType(new MetaPropertyType.TypeChar(), Keys.OLD_TYPE_CHAR);
        addNativeType(new MetaPropertyType.TypeByte(), Keys.OLD_TYPE_BYTE);
        addNativeType(new MetaPropertyType.TypeShort(), Keys.OLD_TYPE_SHORT);
        addNativeType(new MetaPropertyType.TypeInt(), Keys.OLD_TYPE_INT);
        addNativeType(new MetaPropertyType.TypeLong(), Keys.OLD_TYPE_LONG);
        addNativeType(new MetaPropertyType.TypeFloat(), Keys.OLD_TYPE_FLOAT);
        addNativeType(new MetaPropertyType.TypeDouble(), Keys.OLD_TYPE_DOUBLE);
        addNativeType(new MetaPropertyType.TypeString(), Keys.OLD_TYPE_STRING);
        addNativeType(new MetaPropertyType.TypeUUID(), Keys.OLD_TYPE_UUID);
        addNativeType(new MetaPropertyType.TypeDate(), Keys.OLD_TYPE_DATE);
        addNativeType(new MetaPropertyType.TypeTime(), Keys.OLD_TYPE_TIME);
        addNativeType(new MetaPropertyType.TypeTimeStamp(), Keys.OLD_TYPE_TIMESTAMP);
        addNativeType(new MetaPropertyType.TypeInterval(), Keys.OLD_TYPE_INTERVAL);
        addNativeType(new MetaPropertyType.TypeURL(), Keys.OLD_TYPE_URL);
        addNativeType(new MetaPropertyType.TypeText(), Keys.OLD_TYPE_TEXT);
    }
    
    /**
     * Constructor to produce a set of MetaPropertyTypes that just consists of
     * the built-in types.  Call extend(ExtensibleTypes) to extend with 
     * user defined types.
     * @throws Exception
     */
    public MetaPropertyTypes(){
        super();
        init();
    }
    
    /**
     * All in one constructor to initialise and extend with user defined types.
     * @param extensibleTypes
     * @throws Exception
     */
    public MetaPropertyTypes(ExtensibleTypes extensibleTypes) throws Exception{
        super();
        init();
        this.extensibleTypes = extensibleTypes;
    }

    private void init() {
        typeLookup.clear();
        
        for(MetaPropertyType mpt : builtinTypes){
        	String key = mpt.getKey().toString().toLowerCase();
            typeLookup.put(mpt.getTypeName(), mpt);
            typeLookup.put(key,mpt);
            
            // Original keys for built-in types were incorrect but still want to be able to use them 
            // when reading an old repository
            String oldKey = oldKeyConcordance.get(key);
            if(oldKey != null) {
            	typeLookup.put(oldKey, mpt);
            }

        }
        
    }

    public void extend(ExtensibleTypes extensibleTypes){
        this.extensibleTypes = extensibleTypes;
    }

    /**
     * Extends the list of types with some extensible (user defined) types.
     * @param extensibleTypes
     * @throws Exception
     */
    private ExtensibleMetaPropertyType lookupExtensible(String typeName) throws Exception{
    	if(extensibleTypes == null) {
    		return null;
    	}
    	
        for(ExtensibleTypeList list : extensibleTypes.getTypes()) {
        	ExtensibleMetaPropertyType type  = list.get(typeName);
        	if(type != null) {
        		return type;
        	}
        }
        
        return null;
    }
    
    /**
     * Gets the meta property type for a given type-name.
     * @param typename
     * @throws IllegalArgumentException
     * @return the MetaPropertyType corresponding to the given type name */    
    public MetaPropertyType typeFromName(String typename) throws Exception {
        typename = typename.toLowerCase();
        MetaPropertyType mpt = typeLookup.get(typename);
        if(mpt == null) {
        	mpt = lookupExtensible(typename);
        }
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
        MetaPropertyType mpt = builtinLookup.get(typename);
        if(mpt == null)
            throw new IllegalArgumentException("Unrecognised type " + typename);
        return mpt;
    }

    /**
     * extendTypesFromConfig extends the default list of basic types from 
	 * a settings manager.
	 * @param cfg is the SettingsManager to look for extensions in.
	 */
	public static void extendTypesFromConfig(SettingsManager cfg) {
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
                addNativeType((MetaPropertyType)o, null);
            } else {
                throw new IllegalArgumentException(typeClass + " does not extend MetaPropertyType");
            }
            
        }
    }
    
	public Collection<MetaPropertyType> getTypes() throws Exception {
		List<MetaPropertyType> types = new LinkedList<>();
		types.addAll(builtinTypes);

        for(ExtensibleTypeList list : extensibleTypes.getTypes()) {
        	types.addAll(list.getTypes());
        }
 
		return types;
	}
	
    private static void addNativeType(MetaPropertyType mpt, UUID oldKey) {
        builtinTypes.add(mpt);
        builtinLookup.put(mpt.getTypeName(),mpt);
        if(oldKey != null) {
        	oldKeyConcordance.put(mpt.getKey().toString().toLowerCase(), oldKey.toString().toLowerCase());
        }
    }
}
