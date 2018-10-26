/*
 * ExportMappingFactory.java
 * Project: EATool
 * Created on 29-Dec-2005
 *
 */
package alvahouse.eatool.repository.mapping;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.ProgressCounter;
import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.util.IXMLContentHandler;

/**
 * ExportMappingFactory
 * 
 * @author rbp28668
 */
public class ExportMappingFactory extends FactoryBase implements
        IXMLContentHandler {

    private ExportMappings mappings;
    private ExportMapping currentMapping = null;
    private StringBuffer description = new StringBuffer();
    private StringBuffer transform = new StringBuffer();
    private StringBuffer currentText = null;
    private ProgressCounter counter;

    
    /**
     * @param counter
     * 
     */
    public ExportMappingFactory(ProgressCounter counter, ExportMappings mappings) {
        super();
        if(mappings == null){
            throw new NullPointerException("Can't add to null mappings");
        }
       this.mappings = mappings;
       this.counter = counter;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String local, Attributes attrs)
            throws InputException {

        uri = null;
        if(local.equals("ExportTranslation")){
            if(currentMapping != null) {
                throw new InputException("Nested ExportTranslations");
            }
            
            currentMapping = new ExportMapping();
            
            String attr = attrs.getValue("name");
            if(attr == null) {
                throw new InputException("Missing name attribute on ExportTranslation");
            }
            currentMapping.setName(attr);

            attr = attrs.getValue("components");
            if(attr == null) {
                throw new InputException("Missing components attribute on ExportTranslation");
            }
            int components = Integer.parseInt(attr);
            currentMapping.setComponents(components);
            
        } else if (local.equals("Description")) {
            description.delete(0,description.length());
            currentText = description;
	    } else if (local.equals("Transform")) {
	        transform.delete(0,transform.length());
	        currentText = transform;
	    }

    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local) throws InputException {
        if(local.equals("ExportTranslation")){
            mappings.add(currentMapping);
            currentMapping = null;
            counter.count("Export Mapping");
        } else if (local.equals("Description")) {
            currentMapping.setDescription(description.toString());
        } else if (local.equals("Transform")) {
            currentMapping.setTransformPath(transform.toString());
        }
     
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
     */
    public void characters(String str) throws InputException {
        currentText.append(str);
    }

}
