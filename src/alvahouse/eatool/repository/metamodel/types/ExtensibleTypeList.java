/*
 * ExtensibleTypeList.java
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
 * ExtensibleTypeList provides a container and factory for a single extensible type.
 * Note that 2 type lists are deemed to be equal if their implementing classes 
 * (i.e. the type of the extensible types they contain) are the same.
 * 
 * @author rbp28668
 */
public class ExtensibleTypeList {

    private List<ExtensibleMetaPropertyType> types = new LinkedList<ExtensibleMetaPropertyType>();
    private Map<String, ExtensibleMetaPropertyType> lookup = new HashMap<>();
    
    private Class<? extends ExtensibleMetaPropertyType> implementingClass;
    private String name;
    /**
     * 
     */
    public ExtensibleTypeList(Class<? extends ExtensibleMetaPropertyType> impl, String name) {
        super();
        if(impl == null) {
            throw new NullPointerException("Can't use null implementing class");
        }
        if(!ExtensibleMetaPropertyType.class.isAssignableFrom(impl)){
            throw new IllegalArgumentException(impl.getCanonicalName() + " is not a property type");
        }
        implementingClass = impl;
        this.name = name;
    }

    public ExtensibleMetaPropertyType createNew() throws InstantiationException, IllegalAccessException{
        ExtensibleMetaPropertyType type = (ExtensibleMetaPropertyType)implementingClass.newInstance();
        return type;
    }
    
    public void add(ExtensibleMetaPropertyType mpt){
        if(mpt.getClass() != implementingClass){
            throw new IllegalArgumentException("Adding invalid type to extensible type list " + name);
        }
        types.add(mpt);
        lookup.put(mpt.getTypeName().toLowerCase(), mpt);
   		String key = mpt.getKey().toString().toLowerCase();
        lookup.put(key,mpt);

    }

    public void update(ExtensibleMetaPropertyType mpt){
        if(mpt.getClass() != implementingClass){
            throw new IllegalArgumentException("Updating invalid type in extensible type list " + name);
        }
        // assume edit in place at the moment
        lookup.put(mpt.getTypeName().toLowerCase(), mpt);
   		String key = mpt.getKey().toString().toLowerCase();
        lookup.put(key,mpt);
    }
    
    public ExtensibleMetaPropertyType get(String typeName) {
    	return lookup.get(typeName);
    }

    public void remove(ExtensibleMetaPropertyType mpt){
        types.remove(mpt);
        lookup.remove(mpt.getTypeName().toLowerCase());
   		String key = mpt.getKey().toString().toLowerCase();
        lookup.remove(key);
    }
    
    public Collection<ExtensibleMetaPropertyType> getTypes(){
        return Collections.unmodifiableCollection(types);
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Writes the list out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("TypeList");
        out.addAttribute("class",implementingClass.getCanonicalName());
        
        for(ExtensibleMetaPropertyType type : types){
            type.writeXML(out);
        }
        
        out.stopEntity();
    }

    /**
     * Deletes any extensible types of this class. 
     */
    public void deleteContents() {
        types.clear();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return getName();
    }

    /**
     * Gets the Class of the implementing type.
     * @return
     */
    public Class<? extends ExtensibleMetaPropertyType> getImplementingClass() {
        return implementingClass;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return implementingClass.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ExtensibleTypeList) {
			ExtensibleTypeList other = (ExtensibleTypeList)obj;
			return implementingClass.equals(other.implementingClass);
		}
		return false;
	}
    
    
    
}
