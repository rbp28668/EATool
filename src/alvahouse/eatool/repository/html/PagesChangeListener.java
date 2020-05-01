/*
 * PagesChangeListener.java
 * Project: EATool
 * Created on 16-Aug-2007
 *
 */
package alvahouse.eatool.repository.html;

/**
 * PagesChangeListener is a callback interface to receive change events about a 
 * collection of HTMLProxy pages.
 * 
 * @author rbp28668
 */
public interface PagesChangeListener {
    
    /**
     * Signals that a page has been added to the collection.
     * @param event references the page that has been added.
     */
    public void pageAdded(PageChangeEvent event);
    
    /**
     * Signals that a page has been edited.
     * @param event references the edited page.
     */
    public void pageEdited(PageChangeEvent event);
    
    /**
    * Signals that a page has been removed.
     * @param event references the removed page.
     */
    public void pageRemoved(PageChangeEvent event);
}
