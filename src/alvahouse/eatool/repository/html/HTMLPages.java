/*
 * HTMLPages.java
 * Project: EATool
 * Created on 04-May-2007
 *
 */
package alvahouse.eatool.repository.html;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.repository.persist.HTMLPagePersistence;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * HTMLPages is a collection of HTMLProxy pages.
 * 
 * @author rbp28668
 */
public class HTMLPages {

	private final HTMLPagePersistence persistence;
    private final List<PagesChangeListener> listeners = new LinkedList<PagesChangeListener>();
    
    /**
     * Creates an empty page collection. 
     */
    public HTMLPages(HTMLPagePersistence persistence) {
        super();
        this.persistence = persistence;
    }

    /**
     * Adds a new HTMLPage.
     * @param page is the page to be added. Must not be null.
     */
    public void add(HTMLPage page) throws Exception{
        if(page == null){
            throw new NullPointerException("Can't add null HTMLProxy page");
        }
 		String user = System.getProperty("user.name");
		page.getVersion().createBy(user);
        persistence.addHTMLPage(page);
        firePageAdded(page);
    }

    /**
     * Adds a page but without changing version info or firing events.  Used for importing
     * a repository.
     * @param page is the page to be added. Must not be null.
     */
    public void _add(HTMLPage page) throws Exception{
        if(page == null){
            throw new NullPointerException("Can't add null HTMLProxy page");
        }
        persistence.addHTMLPage(page);
    }
    
    /**
     * Adds a new HTMLPage.
     * @param page is the page to be added. Must not be null.
     */
    public void update(HTMLPage page) throws Exception{
        if(page == null){
            throw new NullPointerException("Can't update null HTMLProxy page");
        }
 		String user = System.getProperty("user.name");
		page.getVersion().modifyBy(user);
        persistence.updateHTMLPage(page);
        firePageUpdated(page);
    }

    /**
     * Looks up a page by its key.
     * @param uuid is the key of the page to look up.
     * @return the corresponding HTMLPage or null if not found.
     */
    public HTMLPage lookup(UUID uuid) throws Exception{
        HTMLPage page = persistence.getHTMLPage(uuid);
        return page;
    }
    
    /**
     * Gets a read-only collection of pages.
     * @return Collection of HTMLPage.
     */
    public Collection<HTMLPage> getPages() throws Exception{
        return persistence.getHTMLPages();
    }

    /**
     * Removes an individual page.
     * @param page is the page to remove.
     */
    public void delete(HTMLPage page) throws Exception{
        persistence.deleteHTMLPage(page.getKey());
        firePageRemoved(page);
    }

    /**
     * Clears all the HTMLProxy pages.
     */
    public void reset() throws Exception {
        persistence.dispose();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return "Pages";
    }

    
    /**
     * Adds a listener to receive change events to the pages.
     * @param listener is the PagesChangeListener to add.
     */
    public void addChangeListener(PagesChangeListener listener){
        if(listener == null){
            throw new NullPointerException("Can't add null PagesChangeListener");
        }
        listeners.add(listener);
    }
    
    /**
     * Removes a listener which will no longer receive change events.
     * @param listener is the PagesChangeListener to remove.
     */
    public void removeChangeListener(PagesChangeListener listener){
        listeners.remove(listener);
    }
    
    /**
     * Signals that an page has been added to the collection.
     * @param page is the page that has been added.
     */
    private void firePageAdded(HTMLPage page) {
        PageChangeEvent event = new PageChangeEvent(page);
        for(PagesChangeListener listener : listeners){
            listener.pageAdded(event);
        }
    }
 
    /**
     * Signals that an page has been removed from the collection.
     * @param page is the page that has been removed.
     */
    private void firePageRemoved(HTMLPage page) {
        PageChangeEvent event = new PageChangeEvent(page);
        for(PagesChangeListener listener : listeners){
            listener.pageRemoved(event);
        }
    }

    /**
     * Signals that an page has been edited.
     * @param page is the page that has been edited.
     */
    private void firePageUpdated(HTMLPage page){
        PageChangeEvent event = new PageChangeEvent(page);
        for(PagesChangeListener listener : listeners){
            listener.pageEdited(event);
        }
    }
    
    /**
     * Writes the HTMLProxy pages out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Pages");
        
        try {
			for(HTMLPage page : getPages()){
			    page.writeXML(out);
			}
		} catch (Exception e) {
			throw new IOException("Unable to write HTML pages",e);
		}
        out.stopEntity();
    }

    
}
