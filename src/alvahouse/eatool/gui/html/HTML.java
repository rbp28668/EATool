/*
 * HTML.java
 * Project: EATool
 * Created on 24-Apr-2007
 *
 */
package alvahouse.eatool.gui.html;

import java.util.Iterator;
import java.util.LinkedList;


	/**
     * class to generate HTML for various types of "thing". Where
     * you want complex content of an item e.g. a paragraph, build that
     * up in another instance of HTML and insert it.  To display the HTML,
     * call display.
     */
    public class HTML {
        private LinkedList tokens = new LinkedList();
        
        public HTML(){
        }
        
        public String getPage(){
            StringBuffer temp = new StringBuffer();
            temp.append("<html><head></head><body>");
            temp.append(toString());
            temp.append("</body></html>");

            return temp.toString();
        }
        
        public String toString(){
            StringBuffer temp = new StringBuffer();
            for(Iterator iter = tokens.iterator(); iter.hasNext();){
                String token = (String)iter.next();
                temp.append(token);
            }
            return temp.toString();
        }
        
 
        public HTML hr(){
            tokens.addLast("<hr>");
            tokens.addLast("</hr>");
            return this;
        }
        
        public HTML table(HTML contents){
            if(this == contents){
                tokens.addFirst("<table>");
                tokens.addLast("</table>");
            } else {
                tokens.addLast("<table>");
                tokens.addAll(contents.tokens);
                tokens.addLast("</table>");
                
            }
            return this;
        }

        public HTML tr(HTML contents){
            if(this == contents){
                tokens.addFirst("<tr>");
                tokens.addLast("</tr>");
            } else {
	            tokens.addLast("<tr>");
	            tokens.addAll(contents.tokens);
	            tokens.addLast("</tr>");
            }
            return this;
        }

        public HTML td(HTML s){
            if(this == s){
                tokens.addFirst("<td>");
                tokens.addLast("</td>");

            } else {
	            tokens.addLast("<td>");
	            tokens.addAll(s.tokens);
	            tokens.addLast("</td>");
            }
            return this;
        }

        public HTML h1(HTML h1){
            if(this == h1){
                tokens.addFirst("<h1>");
                tokens.addLast("</h1>");
            } else {
	            tokens.addLast("<h1>");
	            tokens.addAll(h1.tokens);
	            tokens.addLast("</h1>");
            }
            return this;
        }

        public HTML h2(HTML h2){
            if(this == h2){
                tokens.addFirst("<h2>");
                tokens.addLast("</h2>");
            } else {
	            tokens.addLast("<h2>");
	            tokens.addAll(h2.tokens);
	            tokens.addLast("</h2>");
            }
            return this;
        }

        public HTML h3(HTML h3){
            if(this == h3){
                tokens.addFirst("<h3>");
                tokens.addLast("</h3>");
            } else {
	            tokens.addLast("<h3>");
	            tokens.addAll(h3.tokens);
	            tokens.addLast("</h3>");
            }
            return this;
        }
        
        public HTML p(HTML contents){
            if(this == contents){
	            tokens.addFirst("<p>");
	            tokens.addLast("</p>");
            } else {
	            tokens.addLast("<p>");
	            tokens.addAll(contents.tokens);
	            tokens.addLast("</p>");
            }
            return this;
        }
        
        public HTML text(String text){
            tokens.addLast(text);
            return this;
        }
        
        public HTML ul(HTML contents){
            if(this == contents){
	            tokens.addFirst("<ul>");
	            tokens.addLast("</ul>");
            } else {
                tokens.addLast("<ul>");
                tokens.addAll(contents.tokens);
                tokens.addLast("</ul>");
            }
            return this;
        }
        
        public HTML li(HTML contents){
            if(this == contents){
	            tokens.addFirst("<li>");
	            tokens.addLast("</li>");
            } else {
                tokens.addLast("<li>");
                tokens.addAll(contents.tokens);
                tokens.addLast("</li>");
            }
            return this;
        }
        
        public HTML a(String href, HTML text){
            if(this == text){
	            tokens.addFirst("<a href=\"" + href + "\">");
	            tokens.addLast("</a>");
            } else {
                tokens.addLast("<a href=\"" + href + "\">");
                tokens.addAll(text.tokens);
                tokens.addLast("</a>");
            }
            return this;
        }
        
         
    }