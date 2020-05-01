/**
 * 
 */
package alvahouse.eatool.webexport;

import java.io.IOException;

import alvahouse.eatool.repository.html.HTMLPage;
import alvahouse.eatool.repository.html.HTMLPages;
import alvahouse.eatool.util.XMLWriter;

/**
 * @author bruce_porteous
 *
 */
public class PageListExportProxy implements ExportProxy {

	private final HTMLPages pages;
	/**
	 * 
	 */
	public PageListExportProxy(HTMLPages pages) {
		this.pages = pages;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.webexport.ExportProxy#export(alvahouse.eatool.util.XMLWriter)
	 */
	@Override
	public void export(XMLWriter out) throws IOException {
        out.startEntity("HTMLPageList");
        
        for(HTMLPage page : pages.getPages()) {
        	out.startEntity("HTMLPage");
	        out.textEntity("Name",page.getName());
	        out.textEntity("UUID",page.getKey().toString());
	        out.textEntity("Description",page.getDescription());
	        out.stopEntity();
        }
        
        out.stopEntity();
	}

}
