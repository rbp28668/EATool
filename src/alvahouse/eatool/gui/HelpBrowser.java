/*
 * ModelBrowser.java
 * Project: EATool
 * Created on 24-Feb-2006
 *
 */
package alvahouse.eatool.gui;

import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;

import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import alvahouse.eatool.Application;

/**
 * HelpBrowser provides a simple help browser.
 * 
 * @author rbp28668
 */
public class HelpBrowser extends JInternalFrame {

    private static final long serialVersionUID = 1L;
    private DisplayPane display;
    private final static String WINDOW_SETTINGS = "/Windows/HelpBrowser";
    private Application app;

    public HelpBrowser(Application app){
        setTitle("EATool Help");
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        GUIBuilder.loadBounds(this,WINDOW_SETTINGS,app);

        display = new DisplayPane();
        JScrollPane scrollPane = new JScrollPane(display);
        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
    }
    
    public void browse(){
        display.setPage();
    }
    public void browse(Class<?> sourceClass){
        display.setPage(sourceClass);
    }

    
    
    public void dispose() {
        GUIBuilder.saveBounds(this,WINDOW_SETTINGS,app);
        app.getWindowCoordinator().removeFrame(this);
        super.dispose();
    }

    private class DisplayPane extends JEditorPane{
        private static final long serialVersionUID = 1L;

        public DisplayPane() {
            super();
            setEditable(false);
            setContentType("text/html");
            
            addHyperlinkListener( new HyperlinkListener() {
               public void hyperlinkUpdate(HyperlinkEvent ev){
                   try{
	                   if(ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
	                       URL url = ev.getURL();
	                       if(url != null){
	                           
	                           String type = null;
	                           String subject = null;
	                           StringTokenizer toks = new StringTokenizer(url.getPath(),"/");
	                           if(toks.hasMoreTokens()){
	                               type = toks.nextToken();
	                           }
	                           if(toks.hasMoreTokens()){
	                               subject = toks.nextToken();
	                           }
	                           if(type != null && type.equals("help")){
	                               if(subject == null){
	                                   setText(HelpBrowser.this.getDefaultHelp());
	                               }
	                           }
	                       }
	                   }
                   } catch (Exception e){
                       new ExceptionDisplay(HelpBrowser.this,e);
                   }
                   
               }
            });
         }

        /**
         * Sets the help page for the given dialog.  This is keyed by
         * class name to provide context sensitive help.
         * @param sourceClass is the Class of the source window.
         */
        public void setPage(Class<?> sourceClass) {
            String name = sourceClass.getName();
            int idx = name.lastIndexOf('.');
            if(idx >= 0){
                name = name.substring(idx+1);
            }
            System.out.println("Getting help for " + name);
            URL url = getClass().getResource("help/" + name + ".html");
            if(url != null){
                try {
                    setPage(url);
                } catch (IOException e) {
                    url = null;
                }
            }
            
            if(url == null){
                setPage();
            }
            
        }

        /**
         * Sets the default help page.
         */
        public void setPage() {
            URL url = getClass().getResource("help/index.html");
            if(url != null){
                try {
                    setPage(url);
                } catch (IOException e) {
                    url = null;
                }
            } 
            if(url == null){
                setText(getDefaultHelp());
            }
            
        }

    }

    /**
     * @return
     */
    protected String getDefaultHelp() {
        String html = 
        "<html><head><title>EATool Help</title></head><body>"
        + "<h1>EATool Help</h1>"
        + "<p>Help pages not found</p>"
        + "</body></html>"
        ;
        return html;
        
    }
    
  
}
