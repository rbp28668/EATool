/*
 * WindowCoordinator.java
 *
 * Created on 23 January 2002, 08:31
 */

package alvahouse.eatool.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import alvahouse.eatool.repository.Repository;

/**
 * WindowCoordinator
 * Has repsonsibility for tracking which top-level windows exist in the application
 * and provides a central point for managing sets of them.
 * @author  rbp28668
 */
public class WindowCoordinator {

    private Map<String, JInternalFrame> frames = new HashMap<String, JInternalFrame>();
    private Map<String, WindowFactory> factories = new HashMap<String, WindowFactory>();
    private JDesktopPane desktopPane = null;

    /** Creates new WindowCoordinator */
    public WindowCoordinator() {
    }
    
    /** Adds a frame with a given name.  The frame is also added to 
     * the desktop.
     * @param frame is the frame to add.
     * @param internalName is the name assigned to the frame that will
     * allow that frame to be retrieved.
     */
    public void addFrame(JInternalFrame frame, String internalName) {
        if(frame == null || internalName == null)
            throw new NullPointerException();
        if(internalName.length() == 0)
            throw new IllegalArgumentException("Empty name for window");
        
        frames.put(internalName, frame);
        if(desktopPane != null){
            desktopPane.add(frame);
            desktopPane.getDesktopManager().activateFrame(frame);
        }
    }
    
    /** gets a window with the given name, creating one if necessary.  getFrame
     * effectively implements a singleton for a given window name.
     * @param internalName is the name of the window to get
     * @return the window with the given name
     */
    public JInternalFrame getFrame(String internalName) throws Exception {
        if(internalName == null)
            throw new NullPointerException();
        JInternalFrame frame = frames.get(internalName);
        if(frame == null) {
            frame = createFrame(internalName);
        }
		frame.moveToFront();
        return frame;
    }
    
	/** gets a window with the given name, creating one if necessary using the 
	 * given WindowFactory.
	 * getFrame effectively implements a singleton for a given window name.
	 * @param internalName is the name of the window to get
	 * @param factory is the WindowFactory to use if there is no current
	 * window with the given name.
	 * @return the window with the given name
	 */
	public JInternalFrame getFrame(String internalName, WindowFactory factory)  throws Exception{
		if(internalName == null)
			throw new NullPointerException();
		if(factory == null)
			throw new NullPointerException();
			
		JInternalFrame frame = frames.get(internalName);
		if(frame == null) {
			frame = factory.createFrame();
			addFrame(frame,internalName);
		}
		frame.moveToFront();
		return frame;
	}
    
    /** Creates a new frame from the factory with the given name.
     * @param internalName is the name of the factory to use.  The window is
     * also registered under this name and is added to the desktop (if set).
     * @return the newly created window
     * @throw IllegalArgumentException if the internalName does not identify
     * a window factory.
     */
    public JInternalFrame createFrame(String internalName)  throws Exception{
        if(internalName == null)
            throw new NullPointerException();
        WindowFactory factory = factories.get(internalName);
        if(factory == null)
            throw new IllegalArgumentException("Unknown window factory name " + internalName);
        JInternalFrame frame = factory.createFrame();
        addFrame(frame,internalName);
        return frame;
    }
   
    /** Removes a named frame from the window manager
     * @param internalName is the name of the frame to remove
     */
    public void removeFrame(String internalName) {
        if(internalName == null)
            throw new NullPointerException();
        JInternalFrame frame = frames.remove(internalName);

        if(desktopPane != null){
            desktopPane.remove(frame);
            desktopPane.repaint();
        }
    }
    
    /** removes the given frame from the window manager and the desktop
     * @param is the frame to remove.
     */
    public void removeFrame(JInternalFrame frame) {
        for(Map.Entry<String, JInternalFrame> entry : frames.entrySet()) {
            if(entry.getValue() == frame) {
                frames.remove(entry.getKey());
                break;
            }
        }
        if(desktopPane != null){
            desktopPane.remove(frame);
            desktopPane.repaint();
        }
    }
    
    /**
     * Closes all the windows, or more precisely, calls dispose on all the
     * registered windows.
     */
    public void closeAll(){
        Set<JInternalFrame> allFrames = new HashSet<JInternalFrame>();
        allFrames.addAll(frames.values());
        for(Iterator<JInternalFrame> iter = allFrames.iterator(); iter.hasNext();) {
            JInternalFrame frame = iter.next();
            frame.dispose();
        }
    }
    
    /**
     * Removes all windows.  Use when swapping between repositories.
     */
    public void removeAll() {
    	closeAll();
    	frames.clear();
    }
    
    /** Adds a factory to the window coordinator so windows can be created by
     * type name.  
     * @param factory is the window factory that will create new windows.
     * @param internalName is the name for the factory (and the windows
     * created by the factory)
     */
    public void addFactory(WindowFactory factory, String internalName) {
        if(factory == null || internalName == null)
            throw new NullPointerException();
        if(internalName.length() == 0)
            throw new IllegalArgumentException("Empty name for window factory");
        
        factories.put(internalName, factory);
    }
    
    /**
     * Determines whether a window factory exists for a given internal name.  Used
     * to support dynamic factories - e.g. for scripts where you want to have
     * separate windows for each script.
     * @param internalName is the name to check.
     * @return true if there's a window factory registered for the given name.
     */
    public boolean hasFactory(String internalName) {
    	return factories.containsKey(internalName);
    }
    
    /** sets the desktop pane so that windows can be automatically added
     * and removed from the desktop when they are created or removed.
     * @param desktop is the application desktop pane.
     */
    public void setDesktop(JDesktopPane desktop) {
        if(desktop == null)
            throw new NullPointerException("Setting null desktop pane in WindowCoordinator");
        desktopPane = desktop;
    }

    /** gets the desktop pane.  This is the top level window in the application
	* @return the current desktop
     */
    public JDesktopPane getDesktop() {
        return desktopPane;
    }

    /** interface to be implemented by any factory that needs to create
     * application windows in the context of the window manager
     */
    public interface WindowFactory {
        public JInternalFrame createFrame() throws Exception;
    }
    
}
