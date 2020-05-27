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
@Scripted(description="An HTML Page - these correspond to the framework pages")
public class HTMLPageProxy {

	private final HTMLPage page;
	/**
	 * 
	 */
	public HTMLPageProxy(HTMLPage page) {
		this.page = page;
	}

	@Scripted(description="Set this page as dynamic i.e. its contents are generated by script."
			+ " The contents of dynamic pages aren't saved in the repository as they are "
			+ " always generated anew when displayed.")
	public void setDynamic(boolean isDynamic) {
		page.setDynamic(isDynamic);
	}

	@Scripted(description="Sets the HTML content of the page.")
	public void setContent(HTMLProxy html) {
		page.setHtml(html.toString());
	}
	
	@Scripted(description="Creates a new empty HTML object for creating content.")
	public HTMLProxy builder() {
		return new HTMLProxy(new HTML());
	}
}
