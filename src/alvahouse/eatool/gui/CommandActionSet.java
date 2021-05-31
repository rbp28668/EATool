/*
 * CommandActionSet.java
 *
 * Created on 23 January 2002, 22:49
 */

package alvahouse.eatool.gui;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.graphical.standard.metamodel.MetaModelDiagramTypes;
import alvahouse.eatool.gui.graphical.standard.metamodel.MetaModelViewer;
import alvahouse.eatool.gui.graphical.standard.model.ModelViewer;
import alvahouse.eatool.gui.html.HTMLEditor;
import alvahouse.eatool.gui.html.HTMLPagesExplorer;
import alvahouse.eatool.gui.images.ImageExplorer;
import alvahouse.eatool.gui.mappings.ExportMappingExplorer;
import alvahouse.eatool.gui.mappings.ImportMappingExplorer;
import alvahouse.eatool.gui.matrix.MatrixViewer;
import alvahouse.eatool.gui.scripting.EventMapDialog;
import alvahouse.eatool.gui.scripting.FunctionHelpBrowser;
import alvahouse.eatool.gui.scripting.ScriptExplorer;
import alvahouse.eatool.gui.types.TypesExplorer;
import alvahouse.eatool.repository.LoadProgress;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.RepositoryProperties;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.mapping.ExportMapping;
import alvahouse.eatool.repository.mapping.ExportMappings;
import alvahouse.eatool.repository.mapping.ImportMapping;
import alvahouse.eatool.repository.mapping.ImportMappings;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.repository.scripting.Scripts;
import alvahouse.eatool.util.SettingsManager;
import alvahouse.eatool.webexport.WebExport;

/**
 * CommandActionSet provides the ActionSet for the main EATool menus.
 * @author Bruce.Porteous
 *
 */
public class CommandActionSet extends ActionSet {

    /** parent command frame */
    private CommandFrame frame;
    private Application app;
    private Repository repository;

    /** Creates new CommandActionSet */
    public CommandActionSet(CommandFrame f, Application app, Repository repository) {
        super();
        
        this.frame = f;
        this.app = app;
        this.repository = repository;
        

        // -- File --
        addAction("FileNew",actionFileNew);
        addAction("FileLoad",actionFileLoad);
        addAction("FileSave",actionFileSave);
        addAction("FileSaveAs",actionFileSaveAs);
        addAction("FileImport",actionFileImport);
        addAction("FileMerge",actionFileMerge);
        addAction("FileExport",actionFileExport);
        addAction("FileProperties",actionFileProperties);
        addAction("FileExit",actionFileExit);
        
        // -- Meta Model --
        addAction("MetaModelExplore",actionMetaModelExplore);
        addAction("MetaModelView", actionMetaModelView);
        addAction("MetaModelDiagram", actionMetaModelDiagram);
        addAction("MetaModelTypes", actionMetaModelTypes);
        
        // -- Model --
        addAction("ModelBrowse", actionModelBrowse);
        addAction("ModelSearch", actionModelSearch);
        addAction("ModelExplore", actionModelExplore);
        addAction("ModelView", actionModelView);
        addAction("ModelDiagram", actionModelDiagram);
        addAction("ModelMatrix", actionModelMatrix);
        addAction("ModelTime", actionModelTime);
        addAction("ModelImages", actionModelImages);
        
        // -- Tools --
        addAction("ToolsPages",actionToolsPages);
        addAction("ToolsScripts",actionToolsScripts);
        addAction("ToolsEventMappings",actionToolsEventMappings);
        addAction("ToolsImportMappings",actionToolsImportMappings);
        addAction("ToolsExportMappings",actionToolsExportMappings);
        addAction("ToolsExportWebSite",actionToolsExportWebSite);
        addAction("ToolsSettings",actionToolsSettings);
        addAction("ToolsConfig",actionToolsConfig);

		// -- Window --
		addAction("WindowPLAFMetal", actionWindowPLAFMetal);        
		addAction("WindowPLAFMotif", actionWindowPLAFMotif);        
		addAction("WindowPLAFWindows", actionWindowPLAFWindows);        

        // -- Test --
        addAction("TestException", actionTestException);
		addAction("IndexRepository", actionIndexRepository);
		addAction("Search", actionSearch);
		addAction("Test", actionTest);
        
        // -- Help --
        addAction("HelpContents", actionHelpContents);
        addAction("HelpAbout", actionHelpAbout);
        addAction("HelpScripts", actionScriptHelpBrowser);
        
    }    
    
    /**
     * BasicFileFilter provides a simple file filter that only accepts files
     * with the suffix xml.
     * @author Bruce.Porteous
     *
     */
    private class BasicFileFilter extends FileFilter {
        
        private String extension;
        private String description;
        /**
    	 * Constructor for the filter.
    	 */
    	public BasicFileFilter(String extension, String description) {
            super();
            this.extension = extension;
            this.description = description;
        }
        
        /* (non-Javadoc)
    	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
    	 * Accepts .xml files
    	 */
    	public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith(extension);
        }
        
        /* (non-Javadoc)
    	 * @see javax.swing.filechooser.FileFilter#getDescription()
    	 */
    	public String getDescription() {
            return description;
        }
    }    
    /**
	 * XMLFileFilter provides a simple file filter that only accepts files
	 * with the suffix xml.
	 * @author Bruce.Porteous
	 *
	 */
	private class XMLFileFilter extends BasicFileFilter {
        /**
		 * Constructor for the filter.
		 */
		XMLFileFilter() {
            super(".xml", "XML Files");
        }
	}        
    // Handlers

    /** File New action - empties the repository*/
    private final Action actionFileNew = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                fileNew();
                showMetaModel();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };   

    /**
     * Clear the repository. 
     * @throws Exception
     */
    public void fileNew() throws Exception {
        repository.deleteContents();
        app.getWindowCoordinator().closeAll();
        app.setCurrentPath(null);
    }

    /** File Load action - loads a new repository*/
    private final Action actionFileLoad = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                fileLoad(true);
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };   

    /** File Merge action - merges a repository with the existing one*/
    private final Action actionFileMerge = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                fileLoad(false);
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };   

    /**
     * Prompts the user for a file and loads it into the repository.
     * @param deleteExisting is true if we wish to delete the existing
     * repository first.  Differentiates between a load and a merge.
	 * @return true if loaded, false otherwise.
	 * @throws Exception in the event of a read error or other issue.
	 */
    private boolean fileLoad(boolean deleteExisting) throws Exception{
        SettingsManager.Element cfg = app.getSettings().getOrCreateElement("/Files/XMLLoad");
        String path = cfg.attribute("path");
        
        boolean loaded = false;

        JFileChooser chooser = new JFileChooser();
        if(path == null) 
            chooser.setCurrentDirectory( new File("."));
        else
            chooser.setSelectedFile(new File(path));
        chooser.setFileFilter( new XMLFileFilter());

        if( chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        	frame.setCursor(Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
			try {
				path = chooser.getSelectedFile().getPath();
				cfg.setAttribute("path",path);
				//System.out.println("Loading " + path);
                if(deleteExisting){
                    repository.deleteContents();
                    app.getWindowCoordinator().closeAll();
                    app.setCurrentPath(path);
                }
				loadFile(path);
				loaded = true;
			} finally {
				frame.setCursor(Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
			}
        }
        return loaded;
    }   

    /**
     * Loads a file into the repository.
     * @param path is the path of the file on disk.
     * @throws IOException
     */
    public void loadFile(String path) throws IOException{
//        Repository repository = Main.getApp().getRepository();
//        Main.getApp().getCommandFrame().repaint();
//        ProgressDisplay progress = new ProgressDisplay();
//		repository.loadXML(path,progress);
//		progress.setTask("Indexing");
//		Main.getApp().getSearchEngine().indexModel(repository.getModel());
//		progress.setComplete();

        ProgressDisplay progress = new ProgressDisplay(app);
		FileLoader loader = new FileLoader(path,progress);
		Thread thread = new Thread(loader,"Loader Thread");
		thread.start();
    }
    
    private class FileLoader implements Runnable {
        String path;
        //ProgressDisplay progressDisplay;
        LoadProgress progress;
        Exception failure = null;

        FileLoader(String path, ProgressDisplay progressDisplay){
            this.path = path;
            this.progress = new LoadProgress(progressDisplay);
        }
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                app.getCommandFrame().repaint();
                repository.loadXML(path,progress);
                progress.getStatus().setTask("Indexing");
                repository.index(progress);
            } catch (InputException e) {
                failure = e;
            } catch (IOException e) {
                failure = e;
            } finally {
                progress.getStatus().setComplete();                
            }
            if(failure != null){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run() {
                        new ExceptionDisplay(frame,failure);
                    }
                });
            }
        }
    }
    
    /** File Save action - saves the repository as XML*/
    private final Action actionFileSave = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            //System.out.println("File Save called.");
            try {
                String path = app.getCurrentPath();
                if(path != null) {
                    saveFile(path);
                }
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };   

    /** File Save action - saves the repository as XML*/
    private final Action actionFileSaveAs = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            //System.out.println("File SaveAs called.");
            try {
                SettingsManager.Element cfg = app.getSettings().getOrCreateElement("/Files/XMLSave");
                
                String path = app.getCurrentPath();
                if(path == null){
                    path = cfg.attribute("path");
                }
 
                JFileChooser chooser = new JFileChooser();
                if(path == null) 
                    chooser.setCurrentDirectory( new File("."));
                else
                    chooser.setSelectedFile(new File(path));
                chooser.setFileFilter( new XMLFileFilter());

                if( chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    path = chooser.getSelectedFile().getPath();
                    cfg.setAttribute("path",path);
                    app.setCurrentPath(path);                    
                    saveFile(path);
                }
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };   
    
    /**
     * Save the repository to a given file.
     * @param path
     */
    public void saveFile(String path){
        repository.saveXML(path);
    }
    
    /** File Import action - imports an XML file*/
    private final Action actionFileImport = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                if(fileImport())
                    showMetaModel();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
                
        }
    };   
    
    /**
     * Prompts the user for an xml file and imports it into
     * the repository.
	 * @return true if a file is loaded.
	 * @throws InputException in the event of a read error
	 */
    private boolean fileImport() throws InputException {
        
        boolean loaded = false;

        ImportMappings mappings = repository.getImportMappings();
        SettingsManager settings = app.getSettings();
        ImportMapping selected = null;
        try {
	        // Doesn't make sense to continue if no translations
	        if(mappings.getImportMappings().size() == 0) {
	            JOptionPane.showMessageDialog(null, "No input translations defined", "EATool Import", JOptionPane.ERROR_MESSAGE);
	            return false;
	        }
	            
	        Object[] translations = mappings.getImportMappings().toArray();
	        selected = (ImportMapping)JOptionPane.showInputDialog(null, 
	            "Select input translation", "EATool Import", 
	            JOptionPane.QUESTION_MESSAGE,null,translations,translations[0]);
	        if(selected == null)
	            return false;
        } catch (Exception e) {
        	throw new InputException("Unable to get input translations from repository",e);
        }
        // Get default of where to import from
        SettingsManager.Element cfg = settings.getOrCreateElement("/Files/XMLImport");
        String path = cfg.attribute("path");

        JFileChooser chooser = new JFileChooser();
        if(path == null) { 
            chooser.setCurrentDirectory( new File("."));
        } else {
            chooser.setSelectedFile(new File(path));
        }
        
        String fileExt = mappings.lookupFileExt(selected.getParserName());
        String fileDesc = mappings.lookupFileDesc(selected.getParserName());
        chooser.setFileFilter( new BasicFileFilter(fileExt, fileDesc));

        if( chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            path = chooser.getSelectedFile().getPath();
            cfg.setAttribute("path",path);
            System.out.println("Loading " + path);
            repository.ImportXML(path, selected);
            loaded = true;
        }
        return loaded;
    }   
    
    /** File Properties action - allows the user to edit the repository properties*/
    private final Action actionFileProperties = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                RepositoryProperties properties = repository.getProperties();
                RepositoryPropertiesDialog dlg = new RepositoryPropertiesDialog(frame,"Edit Repository Properties", properties);
                dlg.setVisible(true);
                if(dlg.wasEdited()) {
                	repository.updateProperties(properties);
                }
                
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };   

    
    
    /** File Export action - saves the repository as XML*/
    // Currently the same as file save.
    private final Action actionFileExport = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            //System.out.println("File Export called.");
            try {
                
                ExportMappings mappings = repository.getExportMappings();
                SettingsManager settings = app.getSettings();
                
                // Doesn't make sense to continue if no translations
                if(mappings.getExportMappings().size() == 0) {
                    JOptionPane.showMessageDialog(null, "No export translations defined", "EATool Export", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                    
                Object[] translations = mappings.getExportMappings().toArray();
                ExportMapping selected = (ExportMapping)JOptionPane.showInputDialog(null, 
                    "Select export translation", "EATool Export", 
                    JOptionPane.QUESTION_MESSAGE,null,translations,translations[0]);
                if(selected == null)
                    return;
                
                // Get default of where to import from
                
                SettingsManager.Element cfg = settings.getOrCreateElement("/Files/XMLExport");
                String path = cfg.attribute("path");
 
                JFileChooser chooser = new JFileChooser();
                if(path == null) 
                    chooser.setCurrentDirectory( new File("."));
                else
                    chooser.setSelectedFile(new File(path));
                chooser.setFileFilter( new XMLFileFilter());

                if( chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    path = chooser.getSelectedFile().getPath();
                    cfg.setAttribute("path",path);
                    repository.ExportXML(path, selected);
                }
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };   
    
    /** File Exit action - terminates the application. */
    private final Action actionFileExit = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            //System.out.println("File Exit called.");
            if(JOptionPane.showConfirmDialog(null,"Exit application?","EATool",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                fileExit();
            }
        }
    };   
    
    /**
     * Close the application.
     */
    public void fileExit(){
        app.dispose(); // tidy up
        System.exit(0);
    }
    
    /** MetaModel Explore action - displays a tree view of the meta-model. */
    private final Action actionMetaModelExplore = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            System.out.println("MetaModel Explore called.");
            try {
                showMetaModel();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };   

    /**
	 * Shows the meta model explorer (tree view of meta-model).
	 */
	public void showMetaModel() {
        try {
			MetaModelExplorerFrame ef = (MetaModelExplorerFrame) app.getWindowCoordinator().getFrame("MetaModelExplorer");
			ef.refresh();
			ef.show();
        } catch(Throwable t) {
            new ExceptionDisplay(frame,t);
        }
    }

    /** MetaModel View action  - shows graphical view of the meta-model.*/
    private final Action actionMetaModelView = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            //System.out.println("MetaModel View called.");
            try {
                viewMetaModel();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };   

    /**
	 * Views the meta-model (graphical display).
	 */
	public void viewMetaModel() {
        try {
			MetaModelViewer v = (MetaModelViewer) app.getWindowCoordinator().getFrame("MetaModelViewer");
			v.refresh();
			v.show();
        } catch(Throwable t) {
            new ExceptionDisplay(frame,t);
        }
    }

	
    /** MetaModel StandardDiagram action  - shows meta model diagram explorer.*/
    private final Action actionMetaModelDiagram = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                showMetaModelDiagrams();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    }; 

    /**
     * Shows the diagram explorer.
     */
    public void showMetaModelDiagrams(){
        try {
			DiagramExplorer dex = (DiagramExplorer) app.getWindowCoordinator().getFrame("MetaDiagramExplorer");
			dex.setRepository(MetaModelDiagramTypes.getInstance(repository.getScripts()),
					repository.getMetaModelDiagrams());
			dex.refresh();
			dex.show();
        } catch(Throwable t) {
            new ExceptionDisplay(frame,t);
        }
    }

    /** MetaModel Types action  - shows types.*/
    private final Action actionMetaModelTypes = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                TypesExplorer tex = (TypesExplorer) app.getWindowCoordinator().getFrame("TypesExplorer");
                tex.show();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    }; 
    
    /** Model Browse action - shows a html view of the model */
    private final Action actionModelBrowse = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                browseModel();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };   

    /**
     * Browses the model.
     */
    public void browseModel() throws Exception{
        MetaModel metaModel = repository.getMetaModel();
        ModelBrowser browser = (ModelBrowser) app.getWindowCoordinator().getFrame("ModelBrowser");
        browser.browse(metaModel);
        browser.setVisible(true);
    }
    
	/** Model Search action - searches for entities and allows the user
	 * to pick one to browse.
	*/
	private final Action actionModelSearch = new AbstractAction() {
	    private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			try {
				String query =
					(String) JOptionPane.showInputDialog(
						frame,
						"Query to search with",
						"EATool",
						JOptionPane.QUESTION_MESSAGE,
						null,
						null,
						null);
                if(query != null){
                	Set<Entity> entities = repository.searchForEntities(query);
                	if(entities.isEmpty()){
                		JOptionPane.showMessageDialog(frame,"Search returned no results");
                	} else {
                		Collection<Entity> selected = Dialogs.selectMultipleEntitiesFrom(entities, frame);
                		if(selected.size() > 0){
                		    browseEntities(selected);
                		}
                	}
                }
			} catch(Throwable t) {
				new ExceptionDisplay(frame,t);
			}
		}
	};
    
	/**
	 * Browses the given entity.
	 * @param entity
	 */
	public void browseEntity(Entity entity) throws Exception{
        ModelBrowser browser = (ModelBrowser) app.getWindowCoordinator().getFrame("ModelBrowser");
        browser.browse(entity);
        browser.setVisible(true);
	}
	
	/**
	 * Browses the given entity.
	 * @param entity
	 * @throws Exception 
	 */
	public void browseEntities(Collection<Entity> entities) throws Exception{
        ModelBrowser browser = (ModelBrowser) app.getWindowCoordinator().getFrame("ModelBrowser");
        browser.browse(entities);
        browser.setVisible(true);
	}
	
    /** Model Explore action - shows a tree view of the model */
    private final Action actionModelExplore = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                showModel();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
                
        }
    };   

    /**
	 * Shows the model (tree view). 
     * @throws Exception 
	 */
	public void showModel() throws Exception {
        ModelExplorer me = (ModelExplorer) app.getWindowCoordinator().getFrame("ModelExplorer");
        me.refresh();
        me.show();
    }
    
    

    /** Model View action - shows a graphical view of the model. */
    private final Action actionModelView = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                viewModel();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
                
        }
    };   

    /**
	 * Views the model (graphical display).
     * @throws Exception 
	 */
	public void viewModel() throws Exception {
        ModelViewer v = (ModelViewer) app.getWindowCoordinator().getFrame("ModelViewer");
        v.refresh();
        v.show();
    }
    
    
    /** Model StandardDiagram action - displays diagram explorer */
    private final Action actionModelDiagram = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                showModelDiagrams();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
                
        }
    };   

    /**
     * show the diagram explorer for the model.
     * @throws Exception 
     */
    public void showModelDiagrams() throws Exception{
        DiagramExplorer dex = (DiagramExplorer) app.getWindowCoordinator().getFrame("DiagramExplorer");
        dex.setRepository(repository.getDiagramTypes(), repository.getDiagrams());
        dex.refresh();
        dex.show();
    }
    
	/** Model Matrix action - displays matrix explorer */
	private final Action actionModelMatrix = new AbstractAction() {
	    private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			try {
				MetaRelationship[] metaRelationships =
					repository.getMetaModel().getMetaRelationshipsAsArray();
				MetaRelationship selected =
					(MetaRelationship) JOptionPane.showInputDialog(
						frame,
						"Select relationship",
						"EATool",
						JOptionPane.QUESTION_MESSAGE,
						null,
						metaRelationships,
						null);
				if (selected != null) {
					WindowCoordinator wc = app.getWindowCoordinator();
					MatrixViewer viewer =
						(MatrixViewer) wc.getFrame(
							MatrixViewer.getWindowName(selected),
							new MatrixViewer.WindowFactory(
								repository.getModel(),
								selected, app));
					viewer.show();
				}
			} catch (Throwable t) {
					new ExceptionDisplay(frame, t);
				}
                
		}
	};   

	/** Model Time action - sets reference time */
	private final Action actionModelTime = new AbstractAction() {
	    private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			try {
				//TODO
			} catch (Throwable t) {
					new ExceptionDisplay(frame, t);
				}
                
		}
	};   

	/** Displays the images explorer */
	private final Action actionModelImages = new AbstractAction() {
	    private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                //Images images = repository.getImages();
                ImageExplorer ie = (ImageExplorer)app.getWindowCoordinator().getFrame("ImageExplorer");
                ie.show();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };
	
	/** Displays the HTMLProxy pages explorer */
	private final Action actionToolsPages = new AbstractAction() {
	    private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                //HTMLPages pages = repository.getPages();
                HTMLPagesExplorer pe = (HTMLPagesExplorer) app.getWindowCoordinator().getFrame("PageExplorer");
                pe.show();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };
	
	/** Displays the scripts explorer */
	private final Action actionToolsScripts = new AbstractAction() {
	    private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                showScripts();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };
	
    public void showScripts() throws Exception{
        //Scripts scripts = repository.getScripts();
        ScriptExplorer se = (ScriptExplorer) app.getWindowCoordinator().getFrame("ScriptExplorer");
        se.show();
    }
    
    private  final Action actionToolsEventMappings = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                EventMap eventMap = repository.getEventMap();
                Scripts scripts = repository.getScripts();
                EventMapDialog editor = new EventMapDialog(frame, "Edit Event Map", eventMap, scripts);
                editor.setVisible(true);
                if(editor.wasEdited()) {
                	repository.setEventMap(eventMap);
                }
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };
    
	/** Displays the import mappings explorer */
	private final Action actionToolsImportMappings = new AbstractAction() {
	    private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            //System.out.println("Tools Import Mappings called.");
            try {
                showImportMappings();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };

    /**
     * Shows the import mappings explorer. 
     * @throws Exception 
     */
    public void showImportMappings() throws Exception{
        //ImportMappings mappings = repository.getImportMappings();

        ImportMappingExplorer ime = (ImportMappingExplorer) app.getWindowCoordinator().getFrame("ImportMappingExplorer");
        ime.show();
    }
    
    /** Displays the export mappings explorer */
	private final Action actionToolsExportMappings = new AbstractAction() {
	    private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                showExportMappings();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };
    
    /**
     * Shows the export mappings 
     * @throws Exception 
     */
    public void showExportMappings() throws Exception{
        //ExportMappings mappings = repository.getExportMappings();
 	    
        ExportMappingExplorer eme = (ExportMappingExplorer) app.getWindowCoordinator().getFrame("ExportMappingExplorer");
        eme.show();
    }

    /** Tool Settings action - displays the current settings */
    private final Action actionToolsExportWebSite = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                SettingsManager.Element cfg = app.getSettings().getOrCreateElement("/Files/WebExport");
                String path = cfg.attribute("path");
                
                JFileChooser chooser = new JFileChooser();
                if(path != null) {
                    chooser.setCurrentDirectory(new File(path));
                }
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setDialogTitle("Select Folder to export to");
                chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
                if(chooser.showDialog(frame,"Export") == JFileChooser.APPROVE_OPTION){
                    File selected = chooser.getSelectedFile();
                    cfg.setAttribute("path", selected.getCanonicalPath());
                	frame.setCursor(Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
                    try {
	                    WebExport export = new WebExport();
	                    export.export(repository, selected.getAbsolutePath());
                    } finally {
                        frame.setCursor(Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR)); 
                    }
                }
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };
    
    /** Tool Settings action - displays the current settings */
    private final Action actionToolsSettings = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            //System.out.println("Tools Settings called.");
            try {
                SettingsExplorer se = (SettingsExplorer) app.getWindowCoordinator().getFrame("SettingsExplorer");
                se.show();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };

	/** Tools Config action - displays the current configuration */
    private final Action actionToolsConfig = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            //System.out.println("Tools Config called.");
            try {
                SettingsExplorer ce = (SettingsExplorer) app.getWindowCoordinator().getFrame("ConfigExplorer");
                ce.show();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };

    /** Test Exception action - throws a test exception */
    private final Action actionTestException = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            //System.out.println("Test Exception called.");
            try {
                
                try {
                    throw new IllegalArgumentException("Initial Test Exception");
                } catch(Throwable t) {
                    throw new InputException("Chaining Exception", t);
                }
                
            } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };
 
	/** Test Index Repository action - indexes the repository */
	private final Action actionIndexRepository = new AbstractAction() {
	    private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			try {
				repository.index(null);
			} catch(Throwable t) {
				new ExceptionDisplay(frame,t);
			}
		}
	};
	/** Test Search action - searches for entities */
	private final Action actionSearch = new AbstractAction() {
	    private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			//System.out.println("Search.");
			try {
				String query =
					(String) JOptionPane.showInputDialog(
						frame,
						"Please enter the query",
						"EATool",
						JOptionPane.QUESTION_MESSAGE,
						null,
						null,
						null);
                if(query != null){
                	Set<Entity> entities = repository.searchForEntities(query);
                	if(entities.isEmpty()){
                		JOptionPane.showMessageDialog(frame,"Search returned no results");
                	} else {
                		JOptionPane.showMessageDialog(frame,entities.toArray());
                	}
                	for(Entity entity : entities){
                		System.out.println(entity.toString());
                	}
                }
			} catch(Throwable t) {
				new ExceptionDisplay(frame,t);
			}
		}
	};
   
	/** Test Search action - searches for entities */
	private final Action actionTest = new AbstractAction() {
	    private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			try {
		        HTMLEditor editor = (HTMLEditor) app.getWindowCoordinator().getFrame("HTMLEditor");
		        editor.setVisible(true);

			} catch(Throwable t) {
				new ExceptionDisplay(frame,t);
			}
		}
	};
	
    /** WindowPLAFMetal action - displays Metal look and feel */
    private final Action actionWindowPLAFMetal = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
            	setPlaf("javax.swing.plaf.metal.MetalLookAndFeel");
           } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };   
    /** WindowPLAFMotif action - displays Motif look and feel */
    private final Action actionWindowPLAFMotif = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
            	setPlaf("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
           } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };   
    /** WindowPLAFWindows action - displays Windows look and feel */
    private final Action actionWindowPLAFWindows = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
            	setPlaf("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
           } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };   
    
    /** private helper to set Pluggable Look & Feel (PLAF)
     * @param name is the PLAF name
     * @throws one of ClassNotFoundException, 
     *	IllegalAccessException, 
     *	InstantiationException, 
	 *   javax.swing.UnsupportedLookAndFeelException
     */ 
    private void setPlaf(String name) 
	throws ClassNotFoundException, 
	IllegalAccessException, 
	InstantiationException, 
    javax.swing.UnsupportedLookAndFeelException{
		//System.out.println("Setting PLAF " + name);	    	
		JDesktopPane desktop = app.getWindowCoordinator().getDesktop();
    	javax.swing.UIManager.setLookAndFeel(name);
    	SwingUtilities.updateComponentTreeUI(desktop.getRootPane());
    }
    

    /** Help Contents action - displays help */
    private final Action actionHelpContents = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                HelpBrowser browser = (HelpBrowser) app.getWindowCoordinator().getFrame("HelpBrowser");
                browser.browse();
                browser.setVisible(true);
           } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };   

    /** Help About action - displays the about box */
    private final Action actionHelpAbout = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
                AboutBox about = new AboutBox();
                about.setVisible(true);
           } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };   
    
    /** Script help browser action - displays the function help browser */
    private final Action actionScriptHelpBrowser = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
            try {
			    FunctionHelpBrowser browser = (FunctionHelpBrowser)app.getWindowCoordinator().getFrame("FunctionHelpBrowser");
			    browser.browse(alvahouse.eatool.gui.scripting.proxy.ApplicationProxy.class);
			    browser.setVisible(true);
           } catch(Throwable t) {
                new ExceptionDisplay(frame,t);
            }
        }
    };   
    
}
