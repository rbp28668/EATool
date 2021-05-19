/*
 * ExtensibleTypes.java
 * Project: EATool
 * Created on 10-Jul-2006
 *
 */
package alvahouse.eatool.repository.metamodel.types;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import alvahouse.eatool.repository.persist.MetaModelPersistence;
import alvahouse.eatool.util.XMLWriter;

/**
 * ExtensibleTypes is the top-level container for lists of extensible types.
 * It also provides a listener mechanism for flagging updates.
 * 
 * @author rbp28668
 */
public class ExtensibleTypes {

	private final MetaModelPersistence persistence;
//    private List<ExtensibleTypeList> types = new LinkedList<ExtensibleTypeList>();
//    private Map<Class<? extends ExtensibleMetaPropertyType>,ExtensibleTypeList> lookup = new HashMap<Class<? extends ExtensibleMetaPropertyType>,ExtensibleTypeList>();
	private Set<Class<? extends ExtensibleMetaPropertyType>> allowedTypes = new HashSet<>();
	
	private List<TypeEventListener> listeners = new LinkedList<TypeEventListener>();
    
    /**
     * 
     */
    public ExtensibleTypes(MetaModelPersistence persistence) {
        super();
        this.persistence = persistence;
        
        add(ControlledListType.class);
        add(RegexpCheckedType.class);
        add(TimeSeriesType.class);
    }
    
    private void add(Class<? extends ExtensibleMetaPropertyType> allowedType){
        allowedTypes.add(allowedType);
    }

    public Collection<ExtensibleTypeList> getTypes() throws Exception{

        List<ExtensibleTypeList> types = new LinkedList<ExtensibleTypeList>();
        Map<Class<? extends ExtensibleMetaPropertyType>,ExtensibleTypeList> lookup = new HashMap<Class<? extends ExtensibleMetaPropertyType>,ExtensibleTypeList>();

        add(types, lookup, ControlledListType.class,"Controlled List");
        add(types, lookup, RegexpCheckedType.class,"Filtered Input");
        add(types, lookup, TimeSeriesType.class,"Time Series");
    	
        for(ExtensibleMetaPropertyType type : persistence.getDefinedTypes()) {
            ExtensibleTypeList list = lookup.get(type.getClass());
            if(list == null){ // Should never happen but...
                throw new IllegalArgumentException("Type " + type.getClass().getCanonicalName() + " is not known");
            }
            list.add(type);
        }
        return types;
    }
    
    /**
	 * @param types
	 * @param lookup
	 * @param extensibleTypeList
	 */
	private void add(List<ExtensibleTypeList> types,
			Map<Class<? extends ExtensibleMetaPropertyType>, ExtensibleTypeList> lookup,
			Class<? extends ExtensibleMetaPropertyType> implementingClass, String name) {

		ExtensibleTypeList list = new ExtensibleTypeList(implementingClass, name);
        types.add(list);
        lookup.put(list.getImplementingClass(), list);
	}

	/**
     * Writes the list out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Types");
        
        try {
	        for(ExtensibleTypeList list : getTypes()){
	            if(!list.getTypes().isEmpty()){
	                list.writeXML(out);
	            }
	        }
        } catch(Exception e) {
        	throw new IOException("Unable to write extensible types to XML",e);
        }
        
        out.stopEntity();
    }

    /**
     * Clears out any extensible types.
     */
    public void deleteContents() throws Exception{
//        for(ExtensibleTypeList list : types){
//            list.deleteContents();
//        }
//        
        persistence.deleteDefinedTypes();
    }

    /**
     * @param listener
     */
    public void addListener(TypeEventListener listener){
        listeners.add(listener);
    }

    public void removeListener(TypeEventListener listener){
        listeners.remove(listener);
    }
    
    private void fireTypeAdded(ExtensibleMetaPropertyType type) throws Exception{
        TypeEvent event = new TypeEvent(type);
        for(TypeEventListener listener : listeners){
            listener.typeAdded(event);
        }
    }
    
    private void fireTypeChanged(ExtensibleMetaPropertyType type) throws Exception{
        TypeEvent event = new TypeEvent(type);
        for(TypeEventListener listener : listeners){
            listener.typeChanged(event);
        }
        
    }
    
    private void fireTypeDeleted(ExtensibleMetaPropertyType type) throws Exception{
        TypeEvent event = new TypeEvent(type);
        for(TypeEventListener listener : listeners){
            listener.typeDeleted(event);
        }
        
    }

    /**
     * Internal method for adding types from XML etc where we don't want the version
     * to be changed, nor change events to fire.
     * @param type
     */
    public void _add(ExtensibleMetaPropertyType type) throws Exception{
        if(!allowedTypes.contains(type.getClass())){
            throw new IllegalArgumentException("Type " + type.getClass().getCanonicalName() + " is not known");
        }
        persistence.addType(type);
    }

    /**
     * @param type
     */
    public void addType(ExtensibleMetaPropertyType type) throws Exception{
        if(!allowedTypes.contains(type.getClass())){
            throw new IllegalArgumentException("Type " + type.getClass().getCanonicalName() + " is not known");
        }
		String user = System.getProperty("user.name");
		type.getVersion().createBy(user);

        persistence.addType(type);
        fireTypeAdded(type);
    }

    /**
     * @param type
     */
    public void updateType(ExtensibleMetaPropertyType type) throws Exception{
        if(!allowedTypes.contains(type.getClass())){
            throw new IllegalArgumentException("Type " + type.getClass().getCanonicalName() + " is not known");
        }

        String user = System.getProperty("user.name");
		type.getVersion().modifyBy(user);

        persistence.updateType(type);
        fireTypeChanged(type);
    }

    /**
     * @param type
     */
    public void deleteType(ExtensibleMetaPropertyType type) throws Exception{
        if(!allowedTypes.contains(type.getClass())){
            throw new IllegalArgumentException("Type " + type.getClass().getCanonicalName() + " is not known");
        }
        persistence.deleteType(type.getKey());
        fireTypeDeleted(type);
    }

    public boolean isValidType(Class<? extends ExtensibleMetaPropertyType> implementingClass) {
    	return allowedTypes.contains(implementingClass);
    }
    
    /**
     * @param type
     * @return
     */
    public ExtensibleTypeList lookupList(ExtensibleMetaPropertyType type) throws Exception{
        return lookupList(type.getClass());
    }

    /**
     * @param implementingClass
     * @return
     */
    public ExtensibleTypeList lookupList(Class<? extends ExtensibleMetaPropertyType> implementingClass) throws Exception{
     	for(ExtensibleTypeList list : getTypes()) {
     		if(list.getImplementingClass().equals(implementingClass)) {
     			return list;
     		}
     	}
     	throw new IllegalArgumentException("Extensible MetaPropertyType " + implementingClass.getCanonicalName() + " is not known to the repository");
    }

    /** Get the number of extensible types.
     * @return the number of types.
     */
    public int getTypeCount() {
        return allowedTypes.size();
    }
    

}
