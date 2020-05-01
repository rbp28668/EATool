/**
 * 
 */
package alvahouse.eatool.scripting.proxy;

import alvahouse.eatool.gui.html.HTML;
import alvahouse.eatool.gui.scripting.proxy.HTMLProxy;
import alvahouse.eatool.repository.html.HTMLPage;

/**
 * @author bruce_porteous
 *
 */
public class HTMLPageProxy {

	private final HTMLPage page;
	/**
	 * 
	 */
	public HTMLPageProxy(HTMLPage page) {
		this.page = page;
	}

	public void setDynamic(boolean isDynamic) {
		page.setDynamic(isDynamic);
	}

	public void setContent(HTMLProxy html) {
		page.setHtml(html.toString());
	}
	
	public HTMLProxy builder() {
		return new HTMLProxy(new HTML());
	}
}
