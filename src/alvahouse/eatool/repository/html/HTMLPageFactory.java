/*
 * HTMLPageFactory.java
 * Project: EATool
 * Created on 04-May-2007
 *
 */
package alvahouse.eatool.repository.html;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.xml.sax.Attributes;

import alvahouse.eatool.repository.ProgressCounter;
import alvahouse.eatool.repository.base.NamedRepositoryItemFactory;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.scripting.EventMapFactory;
import alvahouse.eatool.repository.scripting.Scripts;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;

/**
 * HTMLPageFactory de-serialises HTMLProxy pages.
 * 
 * @author rbp28668
 */
public class HTMLPageFactory extends NamedRepositoryItemFactory implements IXMLContentHandler {

    private final ProgressCounter counter;
    private final HTMLPages pages;
    private final Scripts scripts;
    private HTMLPage currentPage = null;
    private StringBuilder text = new StringBuilder();
    private EventMapFactory eventMapFactory;
    

    /**
     * 
     */
    public HTMLPageFactory(ProgressCounter counter, HTMLPages pages, Scripts scripts) {
        super();
        this.counter = counter;
        this.pages = pages;
        this.scripts = scripts;
        

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
            text.delete(0, text.length());
            
            eventMapFactory = new EventMapFactory(counter, currentPage.getEventMap(), scripts);
        } else if(local.equals("Version")){
        	VersionImpl.readXML(attrs, currentPage);
        } else {
        	if(eventMapFactory != null) {
        		eventMapFactory.startElement(uri, local, attrs);
        	}
        }
        
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local) throws InputException {
        try {
			if(local.equals("Page")){
			    byte[] decoded = Base64.decodeBase64(text.toString().getBytes("UTF-8"));
			    currentPage.setHtml(new String(decoded));
			    pages._add(currentPage);
			    currentPage = null;
			    eventMapFactory = null;
			} else {
				if(eventMapFactory != null) {
					eventMapFactory.endElement(uri, local);
				}
			}
		} catch (UnsupportedEncodingException e) {
			throw new InputException("Unable to handle UTF-8 content");
		} catch (Exception e) {
			throw new InputException("Unable to load HTML pages from XML", e);
		}
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
     */
    public void characters(String str) throws InputException {
        text.append(str);
    }

}
