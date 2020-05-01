/*
 * HTMLPage.java
 * Project: EATool
 * Created on 04-May-2007
 *
 */
package alvahouse.eatool.repository.html;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

import alvahouse.eatool.repository.base.NamedRepositoryItem;
import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * HTMLPage is a single, named, page of HTML for frameworks, reporting etc.
 * 
 * @author rbp28668
 */
public class HTMLPage extends NamedRepositoryItem {

    private String html;
    private boolean isDynamic;
    private final EventMap eventMap;
    
    public final static String ON_DISPLAY_EVENT = "OnDisplay";
    public final static String ON_CLOSE_EVENT = "OnClose";
    
    /**
     * @param uuid
     */
    public HTMLPage(UUID uuid) {
        super(uuid);
        eventMap = new EventMap();
        eventMap.addEvent(ON_DISPLAY_EVENT);
        eventMap.addEvent(ON_CLOSE_EVENT);
    }
    

    /**
     * Shows that the page is dynamic - dynamic pages are created
     * by attached script and should not be saved.
	 * @return the isDynamic
	 */
	public boolean isDynamic() {
		return isDynamic;
	}


	/**
	 * @param isDynamic the isDynamic to set
	 */
	public void setDynamic(boolean isDynamic) {
		this.isDynamic = isDynamic;
	}


	/**
	 * @return the eventMap
	 */
	public EventMap getEventMap() {
		return eventMap;
	}


	/**
     * @return Returns the html.
     */
    public String getHtml() {
        return html;
    }
    /**
     * @param html The html to set.
     */
    public void setHtml(String html) {
        this.html = html;
    }
    
    /**
     * Writes the HTMLProxy page out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Page");
        
        writeAttributesXML(out);        

        eventMap.writeXML(out, "PageEvents");
        
        if(!isDynamic()) {
	        byte[] raw = html.getBytes();
	        byte[] encoded = Base64.encodeBase64Chunked(raw);
	        String toWrite = new String(encoded);
	        out.text(toWrite);
        }
        out.stopEntity();
    }


	/**
	 * 
	 */
	public void scriptsUpdated() {
	}



}
