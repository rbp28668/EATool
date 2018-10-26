/*
 * HTML.java
 * Project: EATool
 * Created on 24-Apr-2007
 *
 */
package alvahouse.eatool.gui.scripting.proxy;



/**
 * HTML is used to build up HTML for use in an HTMLDisplay.  Note that this includes
 * a toString method so you use multiple instances to build up chunks of HTML which are
 * included in larger chunks or the final page.
 * 
 * @author rbp28668
 */
public class HTML {

    private alvahouse.eatool.gui.html.HTML html;
    /**
     * 
     */
    HTML(alvahouse.eatool.gui.html.HTML html) {
        super();
        this.html = html;
    }

    /**
     * Geta a complete HTML page wrapped with appropriate html, head and body.
     * @return a html page.
     */
    String getPage(){
        return html.getPage();
    }
    
    /**
     * Gets the String equivalent of this HTML.
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return html.toString();
    }
    
    /**
     * Generates a first level heading.
     * @param h1 is the heading text.
     */
    public HTML h1(HTML h1) {
        html.h1(h1.html);
        return this;
    }
    /**
     * Generates a second level heading.
     * @param h2 is the heading text.
     */
    public HTML h2(HTML h2) {
        html.h2(h2.html);
        return this;
    }
    /**
     * Generates a third level heading.
     * @param h3 is the heading text.
     */
    public HTML h3(HTML h3) {
        html.h3(h3.html);
        return this;
    }

    /**
     * Adds a horizontal rule. 
     */
    public HTML hr() {
        html.hr();
        return this;
    }
    
    /**
     * Creates a list item.
     * @param contents is the contents of the list item.
     */
    public HTML li(HTML contents) {
        html.li(contents.html);
        return this;
    }
    
    /**
     * Creates a new paragraph.
     * @param contents is the paragraph contents.
     */
    public HTML p(HTML contents) {
        html.p(contents.html);
        return this;
    }
    
    /**
     * Creates a new table.
     * @param contents is the table contents.
     */
    public HTML table(HTML contents) {
        html.table(contents.html);
        return this;
    }
    
    /**
     * Creates table data.  One table row may contain many table data elements.
     * @param s
     */
    public HTML td(HTML s) {
        html.td(s.html);
        return this;
    }
    /** 
     * Adds raw text.
     * @param text is the text to add.
     */
    public HTML text(String text) {
        html.text(text);
        return this;
    }
    /**
     * Creates a table row. 
     * @param contents contains the contents of this row.
     */
    public HTML tr(HTML contents) {
        html.tr(contents.html);
        return this;
    }
    /**
     * Creates an un-numbered list.
     * @param contents is the list contents ( li() items).
     */
    public HTML ul(HTML contents) {
        html.ul(contents.html);
        return this;
    }
}
