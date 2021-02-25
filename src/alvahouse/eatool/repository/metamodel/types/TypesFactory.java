/*
 * TypesFactory.java
 * Project: EATool
 * Created on 17-Jul-2006
 *
 */
package alvahouse.eatool.repository.metamodel.types;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.ProgressCounter;
import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.util.ClassUtils;
import alvahouse.eatool.util.IXMLContentHandler;

/**
 * TypesFactory de-serialises user defined types.
 * 
 * @author rbp28668
 */
public class TypesFactory extends FactoryBase implements IXMLContentHandler {

    private ExtensibleTypes types;
    private ExtensibleTypeList currentList = null;
    private String currentTypeName = null;
    private ExtensibleMetaPropertyType currentType = null;
    private ProgressCounter counter;

    
    /**
     * @param counter
     * 
     */
    public TypesFactory(ProgressCounter counter, ExtensibleTypes types) {
        super();
        this.types = types;
        this.counter = counter;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @SuppressWarnings("unchecked")
    public void startElement(String uri, String local, Attributes attrs)
            throws InputException {
        if(local.equals("TypeList")){
            String className = attrs.getValue("class");
            Class<? extends ExtensibleMetaPropertyType> implementingClass;
            try {
                implementingClass = (Class <? extends ExtensibleMetaPropertyType>)Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new InputException("Type class " + className + " is not known");
            }
            currentList = types.lookupList(implementingClass);
            if(currentList == null){
                throw new InputException("No type list defined for " + implementingClass.getCanonicalName());
            }
            
            currentTypeName = ClassUtils.baseClassNameOf(implementingClass);
        } else if (currentTypeName != null && local.equals(currentTypeName)){
            try {
                currentType = currentList.createNew();
            } catch (InstantiationException e) {
                throw new InputException("Unable to create type " + currentList.getImplementingClass().getName());
            } catch (IllegalAccessException e) {
                throw new InputException("No access to create type " + currentList.getImplementingClass().getName());
            }
            currentType.startElement(uri,local,attrs);
        } else {
            // Delegate to current type.
            if(currentType != null) {
                currentType.startElement(uri,local,attrs);
            }
        }

    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local) throws InputException {
        if(local.equals("TypeList")){
            currentList = null;
            currentTypeName = null;
        } else if (currentTypeName != null && local.equals(currentTypeName)){
            try {
				currentType.endElement(uri,local);
				// type is finished - add to list.
				currentList.add(currentType);
				types.fireTypeAdded(currentType);
				currentType = null;
				counter.count("Property Type");
			} catch (Exception e) {
				throw new InputException("Unable to add type",e);
			}
        } else {
            // Delegate to current type.
            if(currentType != null) {
                currentType.endElement(uri,local);
            }
        }

    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
     */
    public void characters(String str) throws InputException {
        if(currentType != null){
            currentType.characters(str);
        }

    }

}
