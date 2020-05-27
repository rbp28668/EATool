/*
 * HTMLDisplayProxy.java
 * Project: EATool
 * Created on 24-Apr-2007
 *
 */
package alvahouse.eatool.gui.scripting.proxy;

import java.awt.EventQueue;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.WindowCoordinator;
import alvahouse.eatool.gui.html.HTML;
import alvahouse.eatool.gui.html.HTMLDisplay;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.scripting.proxy.Scripted;
import alvahouse.eatool.util.UUID;


/**
 * HTMLDisplayProxy allows a script to build up a page of HTMLProxy that is displayed
 * in a window for simple reporting etc.
 * 
 * @author rbp28668
 */
@Scripted(description="Provides a display for an HTML object."
		+ " This allows you to build up a report in HTML and display it in the application.")
public class HTMLDisplayProxy implements HTMLPageScriptProxy {



    private HTMLDisplay display;
    /**
     * <code>windowName</code> is the internal window name used by the window coordinator.
     */
    private UUID windowName; 

    private final Application app;
    private final Repository repository;
    
    /**
     * 
     */
    public HTMLDisplayProxy(Application app, Repository repository) {
        super();
        this.app = app;
        this.repository = repository;
        
        display = new alvahouse.eatool.gui.html.HTMLDisplay(app, repository);
        windowName = new UUID();
    }
    
    /**
     * 
     */
    public HTMLDisplayProxy(HTMLDisplay display, Application app,  Repository repository) {
        super();
        this.app = app;
        this.display = display;
        this.repository = repository;
        
        display = new alvahouse.eatool.gui.html.HTMLDisplay(app, repository);
        windowName = new UUID();
    }

    /* (non-Javadoc)
	 * @see alvahouse.eatool.gui.scripting.proxy.HTMLPageScriptProxy#setTitle(java.lang.String)
	 */
    @Override
    @Scripted(description="Sets the window title.")
	public void setTitle(String title){
        display.setTitle(title);
    }
    
    /* (non-Javadoc)
	 * @see alvahouse.eatool.gui.scripting.proxy.HTMLPageScriptProxy#getHTML()
	 */
    @Override
    @Scripted(description="Gets a blank HTML object to work with.")
	public HTMLProxy getHTML(){
        return new HTMLProxy(new HTML());
    }
    
    /* (non-Javadoc)
	 * @see alvahouse.eatool.gui.scripting.proxy.HTMLPageScriptProxy#showPage(alvahouse.eatool.gui.scripting.proxy.HTMLProxy)
	 */
    @Override
    @Scripted(description="Displays the given HTML")
	public void showPage(HTMLProxy html) throws IOException{
        display.setText(html.getPage());
    }
    
    /* (non-Javadoc)
	 * @see alvahouse.eatool.gui.scripting.proxy.HTMLPageScriptProxy#repaint()
	 */
    @Override
    @Scripted(description="Repaints the window")
	public void repaint() {
        if(display != null) {
             display.repaint();
        }
    }

    /* (non-Javadoc)
	 * @see alvahouse.eatool.gui.scripting.proxy.HTMLPageScriptProxy#show()
	 */
    @Override
    @Scripted(description="Shows the window")
	public void show() throws InterruptedException, InvocationTargetException{
        if(!display.isVisible()){
            EventQueue.invokeAndWait( new Runnable(){
                public void run(){
            		WindowCoordinator wc = app.getWindowCoordinator();
            		wc.addFrame(display,windowName.toString());
            		display.show();
                }
            });
        }

    }
    
    /* (non-Javadoc)
	 * @see alvahouse.eatool.gui.scripting.proxy.HTMLPageScriptProxy#hide()
	 */
    @Override
    @Scripted(description="Hides the window")
	public void hide() throws InterruptedException, InvocationTargetException{
        if(display.isVisible()){
            EventQueue.invokeAndWait( new Runnable(){
                public void run(){
                    display.setVisible(false);
                    display.dispose();
                    display = null;
                }
            });
        }
    }

    /* (non-Javadoc)
	 * @see alvahouse.eatool.gui.scripting.proxy.HTMLPageScriptProxy#refresh()
	 */
    @Override
    @Scripted(description="Refreshes the display. Should be called once the HTML is set.")
	public void refresh() throws InterruptedException, InvocationTargetException{
        if(display.isVisible()){
            EventQueue.invokeAndWait( new Runnable(){
                public void run(){
                    display.refresh();
                }
            });
        }
    }


}
