/*
 * ExtensibleTypes.java
 * Project: EATool
 * Created on 10-Jul-2006
 *
 */
package alvahouse.eatool.repository.metamodel.types;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.util.XMLWriter;

/**
 * ExtensibleTypes is the top-level container for lists of extensible types.
 * It also provides a listener mechanism for flagging updates.
 * 
 * @author rbp28668
 */
public class ExtensibleTypes {

    private List<ExtensibleTypeList> types = new LinkedList<ExtensibleTypeList>();
    private Map<Class<? extends ExtensibleMetaPropertyType>,ExtensibleTypeList> lookup = new HashMap<Class<? extends ExtensibleMetaPropertyType>,ExtensibleTypeList>();
    private List<TypeEventListener> listeners = new LinkedList<TypeEventListener>();
    
    /**
     * 
     */
    public ExtensibleTypes() {
        super();
        add(new ExtensibleTypeList(ControlledListType.class,"Controlled List"));
        add(new ExtensibleTypeList(RegexpCheckedType.class,"Filtered Input"));
        add(new ExtensibleTypeList(TimeSeriesType.class,"Time Series"));
    }
    
    private void add(ExtensibleTypeList list){
        types.add(list);
        lookup.put(list.getImplementingClass(), list);
     }

    public Collection<ExtensibleTypeList> getTypes() {
        return Collections.unmodifiableList(types);
    }
    
    /**
     * Writes the list out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Types");
        
        for(ExtensibleTypeList list : types){
            if(!list.getTypes().isEmpty()){
                list.writeXML(out);
            }
        }
        
        out.stopEntity();
    }

    /**
     * Clears out any extensible types.
     */
    public void deleteContents() {
        for(ExtensibleTypeList list : types){
            list.deleteContents();
        }
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
    
    public void fireTypeAdded(ExtensibleMetaPropertyType type) throws Exception{
        TypeEvent event = new TypeEvent(type);
        for(TypeEventListener listener : listeners){
            listener.typeAdded(event);
        }
    }
    
    public void fireTypeChanged(ExtensibleMetaPropertyType type) throws Exception{
        TypeEvent event = new TypeEvent(type);
        for(TypeEventListener listener : listeners){
            listener.typeChanged(event);
        }
        
    }
    
    public void fireTypeDeleted(ExtensibleMetaPropertyType type) throws Exception{
        TypeEvent event = new TypeEvent(type);
        for(TypeEventListener listener : listeners){
            listener.typeDeleted(event);
        }
        
    }

    /**
     * @param type
     */
    public void addType(ExtensibleMetaPropertyType type) throws Exception{
        ExtensibleTypeList list = lookupList(type);
        if(list == null){
            throw new IllegalArgumentException("Type " + type.getClass().getCanonicalName() + " is not known");
        }
        list.add(type);
        fireTypeAdded(type);
    }

    /**
     * @param type
     */
    public void deleteType(ExtensibleMetaPropertyType type) throws Exception{
        ExtensibleTypeList list = lookupList(type);
        if(list == null){
            throw new IllegalArgumentException("Type " + type.getClass().getCanonicalName() + " is not known");
        }
        list.remove(type);
        fireTypeDeleted(type);
    }

    /**
     * @param type
     * @return
     */
    public ExtensibleTypeList lookupList(ExtensibleMetaPropertyType type) {
        return lookup.get(type.getClass());
    }

    /**
     * @param implementingClass
     * @return
     */
    public ExtensibleTypeList lookupList(Class<? extends ExtensibleMetaPropertyType> implementingClass) {
        return lookup.get(implementingClass);
    }

    /** Get the number of extensible types.
     * @return the number of types.
     */
    public int getTypeCount() {
        return types.size();
    }
    

}
