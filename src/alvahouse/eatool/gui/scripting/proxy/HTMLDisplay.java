/*
 * HTMLDisplay.java
 * Project: EATool
 * Created on 24-Apr-2007
 *
 */
package alvahouse.eatool.gui.scripting.proxy;

import java.awt.EventQueue;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import alvahouse.eatool.gui.WindowCoordinator;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.util.UUID;


/**
 * HTMLDisplay allows a script to build up a page of HTML that is displayed
 * in a window for simple reporting etc.
 * 
 * @author rbp28668
 */
public class HTMLDisplay {



    private alvahouse.eatool.gui.html.HTMLDisplay display;
    /**
     * <code>windowName</code> is the internal window name used by the window coordinator.
     */
    private UUID windowName; 

    private alvahouse.eatool.Application app;
    private Repository repository;
    
    /**
     * 
     */
    HTMLDisplay(alvahouse.eatool.Application app, Repository repository) {
        super();
        this.app = app;
        this.repository = repository;
        
        display = new alvahouse.eatool.gui.html.HTMLDisplay(app, repository);
        windowName = new UUID();
    }
    
    /**
     * Sets the window title.
     * @param title is the title to set.
     */
    public void setTitle(String title){
        display.setTitle(title);
    }
    
    /**
     * Gets the HTML object used to build up the contents of the window.
     * @return the HTML.
     */
    public HTML getHTML(){
        return new HTML(new alvahouse.eatool.gui.html.HTML());
    }
    
    /**
     * Displays a page of HTML.
     * @param html is the HTML to display.
     * @throws IOException
     */
    public void showPage(HTML html) throws IOException{
        display.setText(html.getPage());
    }
    
    /**
     * Displays the HTML display
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
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
    
    /**
     * Hides the HTML window
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
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


}
