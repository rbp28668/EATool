/*
 * PageExportProxy.java
 * Project: EATool
 * Created on 15-May-2007
 *
 */
package alvahouse.eatool.webexport;

import java.io.IOException;

import alvahouse.eatool.repository.html.HTMLPage;
import alvahouse.eatool.util.XMLWriter;

/**
 * PageExportProxy is an export proxy for HTML pages.
 * 
 * @author rbp28668
 */
public class PageExportProxy implements ExportProxy {

    private HTMLPage page;
    /**
     * 
     */
    public PageExportProxy(HTMLPage page) {
        super();
        assert(page != null);
        this.page = page;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.webexport.ExportProxy#export(alvahouse.eatool.util.XMLWriter)
     */
    public void export(XMLWriter out) throws IOException {
    	String html = page.getHtml();
    	if(html != null) {
    		out.text(page.getHtml());
    	}
    }

}
