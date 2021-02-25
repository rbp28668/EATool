/*
 * ImageFactory.java
 * Project: EATool
 * Created on 04-Jul-2007
 *
 */
package alvahouse.eatool.repository.images;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.ProgressCounter;
import alvahouse.eatool.repository.base.NamedRepositoryItemFactory;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;

/**
 * ImageFactory deserialises Images from XML.
 * 
 * @author rbp28668
 */
public class ImageFactory extends NamedRepositoryItemFactory implements IXMLContentHandler {

    private ProgressCounter counter;
    private Images images;
    private Image currentImage = null;
    private String text = null;

     /**
     * @param counter
     * @param images
     */
    public ImageFactory(ProgressCounter counter, Images images) {
        super();
        this.counter = counter;
        this.images = images;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String local, Attributes attrs) throws InputException {
        if(local.equals("Image")){
            if(currentImage != null){
                throw new IllegalStateException("Nested Image elements in input");
            }
            UUID uuid = getUUID(attrs);
            currentImage = new Image(uuid);
            getCommonFields(currentImage,attrs);
            String format = attrs.getValue("format");
            currentImage.setFormat(format); 
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local) throws InputException {
        if(local.equals("Image")){
            try {
                currentImage.readDataFrom(text);
                images.addImage(currentImage);
            } catch (Exception e) {
                throw new InputException("Unable to read image data ",e);
            } finally {
                currentImage = null;
            }
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
     */
    public void characters(String str) throws InputException {
        text = str;
    }
    

}
