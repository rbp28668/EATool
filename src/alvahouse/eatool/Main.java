/*
 * Main.java
 *
 * Created on 10 January 2002, 19:58
 */

package alvahouse.eatool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import alvahouse.eatool.gui.CommandFrame;
import alvahouse.eatool.gui.EAToolWindowCoordinator;
import alvahouse.eatool.gui.ProgressDisplay;
import alvahouse.eatool.gui.WindowCoordinator;
import alvahouse.eatool.repository.LoadProgress;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.RepositoryImpl;
import alvahouse.eatool.repository.scripting.ScriptManager;
import alvahouse.eatool.util.SettingsManager;
import alvahouse.eatool.util.UUID;

/**
 * This is the main class for the enterprise architecture tool. It's 
 * purpose in life is to initialise the tool and to provide a home for
 * the main sub-components.
 * @author  rbp28668
 */
public class Main implements Application{

	private WindowCoordinator windowCoordinator;
	private Repository m_repository;
	private SettingsManager config;
	private SettingsManager settings;
	private CommandFrame commandFrame;
	private String configPath = "config.xml";
	private String settingsPath = "settings.xml";

	private String currentPath = null;
	
	private static Main app = null;

	/** Creates new Main - private to ensure singleton. 
	 *  
	 */
	private Main() {
	    // NOP
	}

	/**
	* @param args the command line arguments
	*/
	public static void main(String args[]) {
		//System.out.println("Main running");
		Main prog = getApp();
		prog.run(args);
		//System.out.println("Main finished");
	}

	private static Main getApp() {
		if (app == null)
			app = new Main();
		return app;
	}

	public SettingsManager getConfig() {
		return config;
	}

	public SettingsManager getSettings() {
		return settings;
	}

	public Repository getRepository() {
		return m_repository;
	}

	public WindowCoordinator getWindowCoordinator() {
		return windowCoordinator;
	}

	public CommandFrame getCommandFrame() {
	    return commandFrame;
	}
	
	/** Tidy up on close
	 */
	public void dispose() {

		// Need to save state of UUID generator between settings.  Also will
		// allow disconnected use where a network device is disabled.		
		SettingsManager.Element me = getSettings().getOrCreateElement("/UUID");
		me.setAttribute("state",UUID.getState());
		
//		java.net.URL url = getClass().getResource(settingsPath);
		String path = settingsPath;
//		if(url != null)
//			path = url.getFile();
		getSettings().save(path);
	}

	private void run(String args[]) {

		try {


			// Setup basic configuration files
			// Configuration file should be in the jar file.  If it's missing
			// then the app cannot set up its menus so die gracefully.
			config = new SettingsManager(); // configuration
			InputStream stream = getClass().getResourceAsStream(configPath);
			if(stream == null)
				throw new IOException("Missing Config file: " + configPath);
			config.load(stream);
			
			
			// Settings file however is in user-land.  The first time through
			// there may not be a settings file.
			settings = new SettingsManager(); // user settings
			stream = null;
			String dir = System.getProperty("user.home");
			if(dir != null){
				File file = new File(dir,".eatoolrc.xml");
				settingsPath = file.getCanonicalPath();
				if(file.exists()){
				    stream = new FileInputStream(file);
				}
			}
			
			if(stream != null) {
				settings.load(stream);
			} else {
				settings.setEmptyRoot("EAToolSettings");
			}

			// Make sure UUIDs are initialised.
			initUUID();


			// Create & configure the repository.
			m_repository = new RepositoryImpl(config);

			// Make the app known to the script manager.
			ScriptManager.getInstance().declareObject("app", 
		                new alvahouse.eatool.gui.scripting.proxy.Application(this,m_repository), 
		                alvahouse.eatool.gui.scripting.proxy.Application.class);


			// Init the GUI
			windowCoordinator = new EAToolWindowCoordinator(this, m_repository);
			commandFrame = new CommandFrame(this,getRepository());
			commandFrame.setVisible(true);


			// Load any files specified on the command line.
			int nArgs = args.length;
			if (nArgs > 0) {
			    ProgressDisplay progressDisplay = new ProgressDisplay(this);
			    LoadProgress progress = new LoadProgress(progressDisplay);
				for (int i = 0; i < nArgs; ++i) {
					//System.out.println("Parsing " + args[i]);
					m_repository.loadXML(args[i],progress);
				}
				progressDisplay.setComplete();
			} 
			
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Method initUUID initialises the UUID handling for this session..
	 */
	private void initUUID() {
		String mac = UUID.findMACAddress();
		String state = null;

		try {
			SettingsManager.Element me = null;
			me = settings.getElement("/UUID");
			state = me.attribute("state");
		} catch (Exception e) {
			// NOP - fail quietly with null state.
		}

		UUID.initialise(mac, state);
	}

    /**
     * @return the currentPath
     */
    public String getCurrentPath() {
        return currentPath;
    }

    /**
     * @param currentPath the currentPath to set
     */
    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

}
