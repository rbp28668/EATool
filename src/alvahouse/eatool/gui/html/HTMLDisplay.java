/*
 * HTMLDisplayProxy
 * Project: EATool
 * Created on 24-Feb-2006
 *
 */
package alvahouse.eatool.gui.html;

import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.bsf.BSFException;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.gui.GUIBuilder;
import alvahouse.eatool.gui.URLResolver;
import alvahouse.eatool.gui.graphical.EventErrorHandler;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.html.HTMLPage;
import alvahouse.eatool.repository.scripting.ScriptContext;
import alvahouse.eatool.repository.scripting.ScriptManager;
import alvahouse.eatool.scripting.proxy.ScriptWrapper;

/**
 * HTMLDisplayProxy is a HTMLProxy based browser for reporting etc.
 * 
 * @author rbp28668
 */
public class HTMLDisplay extends JInternalFrame {

    private static final long serialVersionUID = 1L;
    private DisplayPane display;
    private final static String WINDOW_SETTINGS = "/Windows/HTMLDisplayProxy";
    private Application app; 
    private Repository repository;
    private HTMLPage page;
    

    /**
     * Creates a new, empty browser.
     */
    public HTMLDisplay(Application app, Repository repository){
    	this(null, app, repository);
    }

    
    public HTMLDisplay(HTMLPage page, Application app, Repository repository){
    	this.page = page;
        this.app = app;
        this.repository = repository;
        
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        GUIBuilder.loadBounds(this,WINDOW_SETTINGS, app);

        display = new DisplayPane();
        JScrollPane scrollPane = new JScrollPane(display);
        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
        
        if(page != null) {
        	if(!page.getName().isEmpty()) {
        		setTitle(page.getName());
        	}
        	display.setText(page.getHtml());
        }
    }
    
     
    public void dispose() {
        GUIBuilder.saveBounds(this,WINDOW_SETTINGS,app);
        app.getWindowCoordinator().removeFrame(this);
        super.dispose();
    }

 
    /**
     * Sets display text directly. Intended for use where this is not 
     * bound to a HTMLPage.
     * @param html is the html formatted text to display.
     */
    public void setText(String html) {
        display.setText(html);
        if(isVisible()) {
        	repaint();
        }
    }
    
    /**
     * Binds the display to the given page and displays it.  Useful for scripts
     * when you wish to display a given page.
     * @param page
     * @throws IOException
     * @throws BSFException
     */
    public void showPage(HTMLPage page) throws IOException, BSFException{
    	this.page = page;
    	fireDisplayEvent(page);
        display.setText(page.getHtml());
        setVisible(true);
    }
    
    private void  fireDisplayEvent(HTMLPage page) throws BSFException {
		ScriptContext context = page.getEventMap().getContextFor(HTMLPage.ON_DISPLAY_EVENT);
		if(context != null) {
			Object proxy = ScriptWrapper.wrap(page);
		    context.addObject("page", proxy, proxy.getClass());
		    
		    proxy = ScriptWrapper.wrap(this, app, repository);
		    context.addObject("viewer", proxy, proxy.getClass());
		    
		    EventErrorHandler errHandler = new EventErrorHandler(this);
		    context.setErrorHandler(errHandler);
		    ScriptManager.getInstance().runScript(context);
		}
	}

    /**
     * Refreshes the display from the given page.
     */
    public void refresh() {
    	if(page != null) {
    		setText(page.getHtml());
    	}
    }
    
    private class DisplayPane extends JEditorPane{
        private static final long serialVersionUID = 1L;
        private URLResolver resolver;
        
        public DisplayPane() {
            super();
            setEditable(false);
            setContentType("text/html");

            resolver = new URLResolver(app,repository);

            addHyperlinkListener( new HyperlinkListener() {
               public void hyperlinkUpdate(HyperlinkEvent ev){
                   try{
	                   if(ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
	                       URL url = ev.getURL();
	                       if(url == null) {  // will be null if relative URL
	                    	   String link = ev.getDescription();
	                    	   if(link.startsWith("../")) {
	                    		   link = "http://localhost/" + link.substring(3);
	                    		   url = new URL(link);
	                    	   }
	                       }
	                       if(url != null){
	                           resolver.resolve(url,HTMLDisplay.this);
	                       }
	                   }
                   } catch (Exception e){
                       new ExceptionDisplay(HTMLDisplay.this,e);
                   }
                   
               }
            });
         
        
            HTMLEditorKit kit = new HTMLEditorKit() {
                private static final long serialVersionUID = 1L;

                public ViewFactory getViewFactory() {
                      return new HTMLFactory() {
                         public View create(Element elem) {
                             View view = null;
                             if(elem.getName().equals("img")){
                                view = new LocalImageView(elem, repository.getImages());
                             } else {
    	                        view = super.create(elem);
                             }
                            return view;
                         }
                      };
                   }
                };
            setEditorKit(kit);
        
        }

    }
 
    /*
    private static class LocalImageView extends ImageView{
        
        private java.awt.Image image;
        private URL url;
        
        LocalImageView(Element elem){
            super(elem);
            
            String src = (String)elem.getAttributes().getAttribute(HTMLProxy.Attribute.SRC);
            if(src != null){
                String uuidMask = "image/([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})";
                // "image/00000000-0000-0000-8000-00000000"
                Pattern pattern = Pattern.compile(uuidMask);
                Matcher matcher = pattern.matcher(src);
                if(matcher.find()){
                    String strUUID = matcher.group(1);
                    Images images = Main.getApp().getRepository().getImages();
                    Image found = images.lookupImage(new UUID(strUUID));
                    if(found != null){
                        image = found.getImage();
                        try {
                            url = new URL(src);
                        } catch (MalformedURLException e) {
                            url = null;
                        }
                    }
                }
            }
        }
        
        public java.awt.Image getImage(){
            return image;
        }
        
        public URL getImageURL(){
            return super.getImageURL(); //url;
        }
    }
    */
}
