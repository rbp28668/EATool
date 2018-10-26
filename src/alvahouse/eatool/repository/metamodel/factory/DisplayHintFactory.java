package alvahouse.eatool.repository.metamodel.factory;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.ProgressCounter;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaEntityDisplayHint;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;

/**
 * Factory class for reading in display hints from an xml file. 
 * The @see DisplayHint provides entities with a name that can 
 * be displayed to the user.  It may be a name, but can be a combination
 * of other fields.
 * <p> The <b>DisplayHint</b> element in the XML has a property 
 * <b>hintFor</b> that contains the UUID of the meta-entity this hint
 * is destined for.  There must be one or more <b>NameKey</b> sub-elements
 * each of which having a <b>key</b> attribute with the UUID of the 
 * meta-property to display. 
 * @author  rbp28668
 */
public class DisplayHintFactory implements IXMLContentHandler {
    
    private MetaModel metaModel;
    
    private MetaEntityDisplayHint currentHint = null;
    
    private MetaEntity targetMetaEntity = null;
    private ProgressCounter counter;

    
    /** Creates new MetaEntityDisplayHintFactory  
     * @param counter*/
    public DisplayHintFactory(ProgressCounter counter, MetaModel mm) {
        metaModel = mm;
        this.counter = counter;
    }
    
	/**
	 * @see alvahouse.eatool.util.IXMLContentHandler#characters(String)
	 */
    public void characters(String str) throws InputException {
    }
    
	/**
	 * Assigns the (now complete) display hint to its target
	 * meta entity.
	 * @see alvahouse.eatool.util.IXMLContentHandler#endElement(String, String)
	 */
    public void endElement(String uri, String local) throws InputException {
        if(local.compareTo("DisplayHint") == 0) {
            targetMetaEntity.setDisplayHint(currentHint);
            targetMetaEntity = null;
            currentHint = null;
            counter.count("Display Hint");
        }
    }
    
	/**
	 * Does the main work of assembling the display hint.
	 * @see alvahouse.eatool.util.IXMLContentHandler#startElement(String, String, Attributes)
	 */
    public void startElement(String uri, String local, Attributes attrs) throws InputException {
        uri = ""; // disable namespaces for attributes
        
        if(local.compareTo("DisplayHint") == 0) {
            String attr = attrs.getValue(uri,"hintFor");
            if(attr == null)
                throw new InputException("missing meta-entity key in display hint");
            
            targetMetaEntity = metaModel.getMetaEntity(new UUID(attr));
            if(targetMetaEntity == null)
                throw new InputException("unable to find target meta-entity for display hint - key: "
                + attr);
            currentHint = new MetaEntityDisplayHint(targetMetaEntity);
        } else if (local.compareTo("NameKey") == 0) {
            String attr = attrs.getValue(uri,"key");
            if(attr == null)
                throw new InputException("missing key value for name key in display hint");
            
            UUID key = new UUID(attr);
            if(targetMetaEntity.getMetaProperty(key) == null)
                throw new InputException("Invalid key in display hint for " + targetMetaEntity.getName());
            
            currentHint.addPropertyKey(key);
        }
    }
    
   
}

