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
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * HTMLPage is a single, named, page of HTML for frameworks etc.
 * 
 * @author rbp28668
 */
public class HTMLPage extends NamedRepositoryItem {

    private String html;
    
    /**
     * @param uuid
     */
    public HTMLPage(UUID uuid) {
        super(uuid);
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
     * Writes the HTML page out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Page");
        
        writeAttributesXML(out);        

        byte[] raw = html.getBytes();
        byte[] encoded = Base64.encodeBase64Chunked(raw);
        String toWrite = new String(encoded);
        
        out.text(toWrite);
        out.stopEntity();
    }

}
