/**
 * 
 */
package alvahouse.eatool.gui.scripting.proxy;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Common interface to allow different scripting proxies to be created that act as a proper viewer 
 * based proxy and as a headless proxy for exporting pages.
 * @author bruce_porteous
 *
 */
public interface HTMLPageScriptProxy {

	/**
	 * Sets the window title.
	 * @param title is the title to set.
	 */
	void setTitle(String title);

	/**
	 * Gets the HTMLProxy object used to build up the contents of the window.
	 * @return the HTMLProxy.
	 */
	HTMLProxy getHTML();

	/**
	 * Displays a page of HTMLProxy.
	 * @param html is the HTMLProxy to display.
	 * @throws IOException
	 */
	void showPage(HTMLProxy html) throws IOException;

	/**
	 * Repaints the diagram.
	 */
	void repaint();

	/**
	 * Displays the HTMLProxy display
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	void show() throws InterruptedException, InvocationTargetException;

	/**
	 * Hides the HTMLProxy window
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	void hide() throws InterruptedException, InvocationTargetException;

	/**
	 * Refreshes the screen or writes the output once created.
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	void refresh() throws InterruptedException, InvocationTargetException;

}