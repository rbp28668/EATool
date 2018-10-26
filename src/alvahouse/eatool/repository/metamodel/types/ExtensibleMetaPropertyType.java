/*
 * ExtensibleMetaPropertyType.java
 * Project: EATool
 * Created on 10-Jul-2006
 *
 */
package alvahouse.eatool.repository.metamodel.types;

import java.io.IOException;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.util.ClassUtils;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * ExtensibleMetaPropertyType
 * 
 * @author rbp28668
 */
public abstract class ExtensibleMetaPropertyType extends MetaPropertyType implements IXMLContentHandler{

    private transient String xmlValue = null;
    
    /**
     * 
     */
    public ExtensibleMetaPropertyType() {
        super();
    }

    /**
     * @param key
     */
    public ExtensibleMetaPropertyType(UUID key) {
        super(key);
    }

    /**
     * Writes the list out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public abstract void writeXML(XMLWriter out) throws IOException;
    
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String local, Attributes attrs) throws InputException {
        if(local.equals(ClassUtils.baseClassNameOf(this))){
            String struuid = attrs.getValue("uuid");
            if(struuid == null)
                throw new IllegalArgumentException("Missing UUID attribute in XML");
            UUID uuid = new UUID(struuid);
            setKey(uuid);
            
            String name = attrs.getValue("name");
            if(name == null){
                throw new IllegalArgumentException("Missing name in XML");
            }
            setName(name);
            
            String desc = attrs.getValue("description");
            if(desc != null){
                setDescription(desc);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
     */
    public void characters(String str) throws InputException {
        xmlValue = str;
    }

    protected String getXMLValue(){
        return xmlValue;
    }
}
