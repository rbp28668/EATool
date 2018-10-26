/*
 * HTMLPageFactory.java
 * Project: EATool
 * Created on 04-May-2007
 *
 */
package alvahouse.eatool.repository.html;

import org.apache.commons.codec.binary.Base64;
import org.xml.sax.Attributes;

import alvahouse.eatool.repository.ProgressCounter;
import alvahouse.eatool.repository.base.NamedRepositoryItemFactory;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;

/**
 * HTMLPageFactory de-serialises HTML pages.
 * 
 * @author rbp28668
 */
public class HTMLPageFactory extends NamedRepositoryItemFactory implements IXMLContentHandler {

    private ProgressCounter counter;
    private HTMLPages pages;
    private HTMLPage currentPage = null;
    private String text = null;
    /**
     * 
     */
    public HTMLPageFactory(ProgressCounter counter, HTMLPages pages) {
        super();
        this.counter = counter;
        this.pages = pages;
    }
    

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String local, Attributes attrs) throws InputException {
        if(local.equals("Page")){
            if(currentPage != null){
                throw new IllegalStateException("Nested Page elements in input");
            }
            UUID uuid = getUUID(attrs);
            currentPage = new HTMLPage(uuid);
            getCommonFields(currentPage,attrs);
        }
        
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local) throws InputException {
        if(local.equals("Page")){
            byte[] decoded = Base64.decodeBase64(text.getBytes());
            currentPage.setHtml(new String(decoded));
            pages.add(currentPage);
            currentPage = null;
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
     */
    public void characters(String str) throws InputException {
        text = str;
    }

}
