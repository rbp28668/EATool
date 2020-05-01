/*
 * HTMLProxy.java
 * Project: EATool
 * Created on 24-Apr-2007
 *
 */
package alvahouse.eatool.gui.html;

import java.util.LinkedList;


	/**
     * class to generate HTMLProxy for various types of "thing". Where
     * you want complex content of an item e.g. a paragraph, build that
     * up in another instance of HTMLProxy and insert it.  To display the HTML,
     * call display.
     */
    public class HTML {
        private LinkedList<String> tokens = new LinkedList<>();
        
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
            for(String token : tokens){
                temp.append(token);
            }
            return temp.toString();
        }
        
        public void clear() {
        	tokens.clear();
        }
        
        public HTML hr(){
            tokens.addLast("<hr>");
            tokens.addLast("</hr>");
            return this;
        }
        
        private void add(HTML contents, String tag) {
        	String open = "<" + tag + ">";
        	String close = "</" + tag + ">";
        	
            if(this == contents){
                tokens.addFirst(open);
                tokens.addLast(close);
            } else {
                tokens.addLast(open);
                tokens.addAll(contents.tokens);
                tokens.addLast(close);
            }
        	
        }
        public HTML table(HTML contents){
        	add(contents, "table");
            return this;
        }

        public HTML tr(HTML contents){
        	add(contents, "tr");
            return this;
        }

        public HTML th(HTML contents){
        	add(contents, "th");
            return this;
        }

        public HTML td(HTML contents){
        	add(contents, "td");
            return this;
        }

        public HTML h1(HTML h1){
        	add(h1,"h1");
            return this;
        }

        public HTML h2(HTML h2){
        	add(h2,"h2");
            return this;
        }

        public HTML h3(HTML h3){
        	add(h3,"h3");
            return this;
        }
        
        public HTML p(HTML contents){
        	add(contents,"p");
            return this;
        }
        
        public HTML text(String text){
            tokens.addLast(text);
            return this;
        }
        
        public HTML ul(HTML contents){
        	add(contents,"ul");
            return this;
        }
        
        public HTML li(HTML contents){
        	add(contents,"li");
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