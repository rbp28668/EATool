/*
 * HTMLProxy.java
 * Project: EATool
 * Created on 24-Apr-2007
 *
 */
package alvahouse.eatool.gui.scripting.proxy;

import alvahouse.eatool.gui.html.HTML;
import alvahouse.eatool.scripting.proxy.Scripted;

/**
 * HTMLProxy is used to build up HTMLProxy for use in an HTMLDisplayProxy.  Note that this includes
 * a toString method so you use multiple instances to build up chunks of HTMLProxy which are
 * included in larger chunks or the final page.
 * 
 * @author rbp28668
 */
@Scripted(description="Use this to build up HTML for display")
public class HTMLProxy {

    private HTML html;
    /**
     * 
     */
    public HTMLProxy(HTML html) {
        super();
        this.html = html;
    }

    /**
     * Geta a complete HTMLProxy page wrapped with appropriate html, head and body.
     * @return a html page.
     */
    String getPage(){
        return html.getPage();
    }
    
    /**
     * Gets the String equivalent of this HTMLProxy.
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return html.toString();
    }
    
    /**
     * Clears the HTML.
     */
    public void clear() {
    	html.clear();
    }
    
    /**
     * Generates a first level heading.
     * @param h1 is the heading text.
     */
    public HTMLProxy h1(HTMLProxy h1) {
        html.h1(h1.html);
        return this;
    }
    /**
     * Generates a second level heading.
     * @param h2 is the heading text.
     */
    public HTMLProxy h2(HTMLProxy h2) {
        html.h2(h2.html);
        return this;
    }
    /**
     * Generates a third level heading.
     * @param h3 is the heading text.
     */
    public HTMLProxy h3(HTMLProxy h3) {
        html.h3(h3.html);
        return this;
    }

    /**
     * Adds a horizontal rule. 
     */
    public HTMLProxy hr() {
        html.hr();
        return this;
    }
    
    /**
     * Creates a list item.
     * @param contents is the contents of the list item.
     */
    public HTMLProxy li(HTMLProxy contents) {
        html.li(contents.html);
        return this;
    }
    
    /**
     * Creates a new paragraph.
     * @param contents is the paragraph contents.
     */
    public HTMLProxy p(HTMLProxy contents) {
        html.p(contents.html);
        return this;
    }
    
    /**
     * Creates a new table.
     * @param contents is the table contents.
     */
    public HTMLProxy table(HTMLProxy contents) {
        html.table(contents.html);
        return this;
    }

    /**
     * Creates table header.  
     * @param s
     */
    public HTMLProxy th(HTMLProxy s) {
        html.th(s.html);
        return this;
    }

    /**
     * Creates table data.  One table row may contain many table data elements.
     * @param s
     */
    public HTMLProxy td(HTMLProxy s) {
        html.td(s.html);
        return this;
    }
    /** 
     * Adds raw text.
     * @param text is the text to add.
     */
    public HTMLProxy text(String text) {
        html.text(text);
        return this;
    }
    /**
     * Creates a table row. 
     * @param contents contains the contents of this row.
     */
    public HTMLProxy tr(HTMLProxy contents) {
        html.tr(contents.html);
        return this;
    }
    /**
     * Creates an un-numbered list.
     * @param contents is the list contents ( li() items).
     */
    public HTMLProxy ul(HTMLProxy contents) {
        html.ul(contents.html);
        return this;
    }
}
