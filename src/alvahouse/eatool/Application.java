/*
 * Application.java
 * Project: EATool
 * Created on 2 Dec 2007
 *
 */
package alvahouse.eatool;

import alvahouse.eatool.gui.CommandFrame;
import alvahouse.eatool.gui.WindowCoordinator;
import alvahouse.eatool.util.SettingsManager;

public interface Application {

    public SettingsManager getConfig();

    public SettingsManager getSettings();

    public WindowCoordinator getWindowCoordinator();

    public CommandFrame getCommandFrame();
    
    /**
     * @return the currentPath
     */
    public String getCurrentPath();

    /**
     * @param currentPath the currentPath to set
     */
    public void setCurrentPath(String currentPath);
    
    public void dispose();
}
