/*
 * PageExportProxy.java
 * Project: EATool
 * Created on 15-May-2007
 *
 */
package alvahouse.eatool.webexport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;

import alvahouse.eatool.gui.html.HTML;
import alvahouse.eatool.gui.scripting.proxy.HTMLPageScriptProxy;
import alvahouse.eatool.gui.scripting.proxy.HTMLProxy;
import alvahouse.eatool.repository.html.HTMLPage;
import alvahouse.eatool.util.XMLWriter;

/**
 * PageExportProxy is an export proxy for HTMLProxy pages.  This also acts as a scripting proxy so that
 * pages can be written once the script has generated them.
 * 
 * @author rbp28668
 */
public class PageExportProxy implements ExportProxy, HTMLPageScriptProxy {

    private HTMLPage page;
    private File outputFolder;
    private String file;
    
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

    public void export(File outputFolder, String file) throws IOException{
    	String html = page.getHtml();
    	if(html != null) {
    		File outputFile = new File(outputFolder, file);
    		try(PrintWriter out = new PrintWriter(
    				new OutputStreamWriter(
    						new FileOutputStream(outputFile), Charset.forName("UTF-8")))){
    			writePreamble(out);
    			out.print(html);
    			writePostamble(out);
    		}
    	}
    	
    }

    /**
     * Set the eventual destination for writing the output for deferred writing controlled
     * by the script
     * @param outputFolder
     * @param file
     */
    public void setDestination(File outputFolder, String file) {
    	this.outputFolder = outputFolder;
    	this.file = file;
    }
    
    
	/**
	 * @param out
	 */
	private void writePreamble(PrintWriter out) {
		out.print("<html>");
		out.print("<head>");
		out.print("<link type=\"text/css\" rel=\"stylesheet\" href=\"../ea.css\"/>");
		out.print("<title>Page " + page.getName() + "</title>");
		out.print("</head>");
		out.print("<body>");
	}

	/**
	 * @param out
	 */
	private void writePostamble(PrintWriter out) {
		out.print("</body>");
		out.print("</html>");
		out.println();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.scripting.proxy.HTMLPageScriptProxy#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String title) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.scripting.proxy.HTMLPageScriptProxy#getHTML()
	 */
	@Override
	public HTMLProxy getHTML() {
		return new HTMLProxy(new HTML());
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.scripting.proxy.HTMLPageScriptProxy#showPage(alvahouse.eatool.gui.scripting.proxy.HTMLProxy)
	 */
	@Override
	public void showPage(HTMLProxy html) throws IOException {
		throw new IllegalStateException("showPage not appropriate in page script");
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.scripting.proxy.HTMLPageScriptProxy#repaint()
	 */
	@Override
	public void repaint() {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.scripting.proxy.HTMLPageScriptProxy#show()
	 */
	@Override
	public void show() throws InterruptedException, InvocationTargetException {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.scripting.proxy.HTMLPageScriptProxy#hide()
	 */
	@Override
	public void hide() throws InterruptedException, InvocationTargetException {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.scripting.proxy.HTMLPageScriptProxy#refresh()
	 */
	@Override
	public void refresh() throws InterruptedException, InvocationTargetException {
		try {
			export(outputFolder, file);
		} catch (IOException e) {
			// fail quietly - TODO fix properly 
			System.err.println(e.getMessage());
		}
	}
}
