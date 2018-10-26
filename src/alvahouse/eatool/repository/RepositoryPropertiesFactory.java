/*
 * RepositoryPropertiesFactory.java
 * Project: EATool
 * Created on 12-May-2006
 *
 */
package alvahouse.eatool.repository;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.util.IXMLContentHandler;

/**
 * RepositoryPropertiesFactory de-serialises RepositoryProperties from an XML stream.
 * 
 * @author rbp28668
 */
public class RepositoryPropertiesFactory extends FactoryBase implements IXMLContentHandler{

    private RepositoryProperties properties;
    private String name = null;
    private String value = null;
    private StringBuffer chars = new StringBuffer(64);
    private ProgressCounter counter;
    
    /**
     * @param counter
     * 
     */
    public RepositoryPropertiesFactory(ProgressCounter counter, RepositoryProperties properties) {
        super();
        this.properties = properties;
        this.counter = counter;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String local, Attributes attrs) throws InputException {
        if(local.equals("Property")) {
            name = null;
            value = null;
        } else if(local.equals("Name")) {
            if(name != null){
                throw new InputException("Nested Name in Property");
            }
        } if(local.equals("Value")) {
            if(value != null) {
                throw new InputException("Nested Value in Property");
            }
        }
        
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local) throws InputException {
        if(local.equals("Property")) {
            if(name == null){
                throw new InputException("Missing name in property XML");
            }
            if(value == null){
                throw new InputException("Missing value in property XML");
            }
            properties.setProperty(name,value);
            name = null;
            value = null;
            counter.count("Repository Property");
        } else if(local.equals("Name")) {
            if(chars.length() == 0){
                throw new InputException("No text for name in property XML");
            }
            name = chars.toString();
            chars.delete(0,chars.length());
        } if(local.equals("Value")) {
            // Empty string is ok for value.
            value = chars.toString();
            chars.delete(0,chars.length());
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
     */
    public void characters(String str) throws InputException {
        chars.append(str);
    }

}
