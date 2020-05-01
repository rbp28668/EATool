/*
 * HTMLPages.java
 * Project: EATool
 * Created on 04-May-2007
 *
 */
package alvahouse.eatool.repository.html;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * HTMLPages is a collection of HTMLProxy pages.
 * 
 * @author rbp28668
 */
public class HTMLPages {

    private Map<UUID,HTMLPage> pages = new HashMap<UUID,HTMLPage>();
    private List<PagesChangeListener> listeners = new LinkedList<PagesChangeListener>();
    
    /**
     * Creates an empty page collection. 
     */
    public HTMLPages() {
        super();
    }

    /**
     * Adds a new HTMLPage.
     * @param page is the page to be added. Must not be null.
     */
    public void add(HTMLPage page){
        if(page == null){
            throw new NullPointerException("Can't add null HTMLProxy page");
        }
        pages.put(page.getKey(),page);
    }
    
    /**
     * Looks up a page by its key.
     * @param uuid is the key of the page to look up.
     * @return the corresponding HTMLPage or null if not found.
     */
    public HTMLPage lookup(UUID uuid){
        HTMLPage page = (HTMLPage)pages.get(uuid);
        return page;
    }
    
    /**
     * Gets a read-only collection of pages.
     * @return Collection of HTMLPage.
     */
    public Collection<HTMLPage> getPages(){
        return Collections.unmodifiableCollection(pages.values());
    }

    /**
     * Removes an individual page.
     * @param page is the page to remove.
     */
    public void delete(HTMLPage page) {
        pages.remove(page.getKey());
        
    }

    /**
     * Clears all the HTMLProxy pages.
     */
    public void reset(){
        pages.clear();
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
    public void firePageEdited(HTMLPage page){
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
        
        for(HTMLPage page : pages.values()){
            page.writeXML(out);
        }
        out.stopEntity();
    }

    
}
