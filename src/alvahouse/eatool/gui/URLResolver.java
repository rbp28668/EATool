/*
 * URLResolver.java
 * Project: EATool
 * Created on 03-May-2007
 *
 */
package alvahouse.eatool.gui;

import java.awt.Component;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.graphical.DiagramViewer;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.Diagrams;
import alvahouse.eatool.repository.images.Image;
import alvahouse.eatool.repository.images.Images;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.scripting.Script;
import alvahouse.eatool.repository.scripting.ScriptManager;
import alvahouse.eatool.repository.scripting.Scripts;
import alvahouse.eatool.util.UUID;

/**
 * URLResolver resolves system-wide URLs to allow functionality to be 
 * fired by use of hyperlinks in HTML pages.  Note - singleton class.
 * 
 * @author rbp28668
 */
public class URLResolver {

    private Map<String,Resolver> resolvers = new HashMap<String,Resolver>();
    // private static URLResolver instance = null;
    private Application app;
    private Repository repository;
    
//    /**
//     * Gets the singleton instance. 
//     * @return the singleton instance.
//     */
//    public static URLResolver getInstance(){
//        if(instance == null){
//            instance = new URLResolver();
//        }
//        return instance;
//    }
    
    /**
     * Creates the URLResolver populated with resolvers.
     */
    public URLResolver(Application app, Repository repository) {
        super();
    
        this.app = app;
        this.repository = repository;
        
        resolvers.put("entity", new EntityResolver());
        resolvers.put("metaEntity", new MetaEntityResolver());
        resolvers.put("metaEntityTable", new MetaEntityTableResolver());
        resolvers.put("metaModel",new MetaModelResolver());
        resolvers.put("script", new ScriptResolver());
        resolvers.put("diagram", new DiagramResolver());
        resolvers.put("image", new ImageResolver());

    }

    /**
     * Resolves the given URL.
     * @param url is the URL to resolve.
     * @throws Exception
     */
    public void resolve(URL url) throws Exception{
        resolve(url,null);
    }
    
    /**
     * Resolves a given URL while re-using the launching viewer (if possible).
     * @param url is the URL to resolve.
     * @param source is the launching viewer - if of the same type (and not null)
     * as the viewer needed to display the contents of the URL then this will
     * re-use the viewer.
     * @throws Exception
     */
    public void resolve(URL url, Component source) throws Exception{
        String type = null;
        UUID uuid = null;
        StringTokenizer toks = new StringTokenizer(url.getPath(),"/");
        if(toks.hasMoreTokens()){
            type = toks.nextToken();
        }
        
        if(toks.hasMoreTokens()){
            String key = toks.nextToken();
            uuid = new UUID(key);
        }
        
        Resolver resolver = resolvers.get(type);
        if(resolver == null){
            throw new IllegalArgumentException("No URL resolver for " + type);
        }
        
        resolver.resolve(url,source,uuid);

    }
    
    /**
     * Resolver resolves a given URL.
     * 
     * @author rbp28668
     */
    private abstract class Resolver {
        
        /**
         * Resolves a URL.
         * @param url is the source URL.
         * @param source is the launching viewer etc. If source is not null, and is of
         * the correct type then the launching viewer will be re-used.
         * @param uuid is the key to the item to be viewed/displayed/run etc.
         * @throws Exception
         */
        public abstract void resolve(URL url, Component source, UUID uuid) throws Exception;
    }
    
    /**
     * EntityResolver resolves a given entity using the model browser.
     * 
     * @author rbp28668
     */
    private class EntityResolver extends Resolver {

        /* (non-Javadoc)
         * @see alvahouse.eatool.gui.URLResolver.Resolver#resolve(java.net.URL, java.awt.Component)
         */
        public void resolve(URL url, Component source, UUID uuid)  throws Exception{
            if(uuid == null){
                throw new IllegalArgumentException("URL for entity browsing does not include a UUID");
            }
            
            Model model = repository.getModel();
            Entity e = model.getEntity(uuid);
            if(e == null){
                throw new IllegalArgumentException("No entity has key " + uuid.toString());
            }
            
            ModelBrowser browser;
            if(source != null && source instanceof ModelBrowser){
                browser = (ModelBrowser)source;
            } else {
                browser = (ModelBrowser) app.getWindowCoordinator().getFrame("ModelBrowser");
            }
            browser.browse(e);
            browser.setVisible(true);
        }
        
    }
    
    /**
     * MetaEntityResolver resolves a given meta-entity using the model browser.
     * 
     * @author rbp28668
     */
    private class MetaEntityResolver extends Resolver {

        /* (non-Javadoc)
         * @see alvahouse.eatool.gui.URLResolver.Resolver#resolve(java.net.URL, java.awt.Component)
         */
        public void resolve(URL url, Component source, UUID uuid)  throws Exception{
            if(uuid == null){
                throw new IllegalArgumentException("URL for meta-entity browsing does not include a UUID");
            }

            MetaModel metaModel = repository.getMetaModel();
            Model model = repository.getModel();

            MetaEntity me = metaModel.getMetaEntity(uuid);
            if(me == null){
                throw new IllegalArgumentException("No meta-entity has key " + uuid.toString());
            }

            ModelBrowser browser;
            if(source != null && source instanceof ModelBrowser){
                browser = (ModelBrowser)source;
            } else {
                browser = (ModelBrowser) app.getWindowCoordinator().getFrame("ModelBrowser");
            }
            browser.browse(me,model);
            browser.setVisible(true);
        }
        
    }
    
    /**
     * MetaEntityTableResolver resolves a given meta-entity as a meta-entity table.
     * 
     * @author rbp28668
     */
    private class MetaEntityTableResolver extends Resolver {

        /* (non-Javadoc)
         * @see alvahouse.eatool.gui.URLResolver.Resolver#resolve(java.net.URL, java.awt.Component)
         */
        public void resolve(URL url, Component source, UUID uuid)  throws Exception{
            if(uuid == null){
                throw new IllegalArgumentException("URL for meta-entity browsing does not include a UUID");
            }

            MetaModel metaModel = repository.getMetaModel();
            Model model = repository.getModel();

            MetaEntity me = metaModel.getMetaEntity(uuid);
            if(me == null){
                throw new IllegalArgumentException("No meta-entity has key " + uuid.toString());
            }

            ModelBrowser browser;
            if(source != null && source instanceof ModelBrowser){
                browser = (ModelBrowser)source;
            } else {
                browser = (ModelBrowser) app.getWindowCoordinator().getFrame("ModelBrowser");
            }
            browser.browseAsTable(me,model);
            browser.setVisible(true);
        }
        
    }
    
    /**
     * MetaModelResolver resolves the meta-model using the model browser.
     * 
     * @author rbp28668
     */
    private class MetaModelResolver extends Resolver {

        /* (non-Javadoc)
         * @see alvahouse.eatool.gui.URLResolver.Resolver#resolve(java.net.URL, java.awt.Component)
         */
        public void resolve(URL url, Component source, UUID uuid) throws Exception {
            MetaModel metaModel = repository.getMetaModel();

            ModelBrowser browser;
            if(source != null && source instanceof ModelBrowser){
                browser = (ModelBrowser)source;
            } else {
                browser = (ModelBrowser) app.getWindowCoordinator().getFrame("ModelBrowser");
            }
            browser.browse(metaModel);
            browser.setVisible(true);
         }
        
    }
    
    /**
     * ScriptResolver resolves a script which is then run.
     * 
     * @author rbp28668
     */
    private class ScriptResolver extends Resolver {

        /* (non-Javadoc)
         * @see alvahouse.eatool.gui.URLResolver.Resolver#resolve(java.net.URL, java.awt.Component, alvahouse.eatool.util.UUID)
         */
        public void resolve(URL url, Component source, UUID uuid) throws Exception {
            if(uuid == null){
                throw new IllegalArgumentException("URL for script does not include a UUID");
            }
            Scripts scripts = repository.getScripts();
            Script script = scripts.lookupScript(uuid);
            if(script == null){
                throw new IllegalArgumentException("No script with a key of " + uuid.toString());
            }
		    ScriptManager.getInstance().runScript(script);
        }
        
    }
    
    /**
     * DiagramResolver resolves a diagram which is displayed.
     * 
     * @author rbp28668
     */
    private class DiagramResolver extends Resolver {

        /* (non-Javadoc)
         * @see alvahouse.eatool.gui.URLResolver.Resolver#resolve(java.net.URL, java.awt.Component, alvahouse.eatool.util.UUID)
         */
        public void resolve(URL url, Component source, UUID uuid) throws Exception{
            if(uuid == null){
                throw new IllegalArgumentException("URL for diagram browsing does not include a UUID");
            }
            Diagrams diagrams = repository.getDiagrams();
            Diagram diagram = diagrams.lookup(uuid);
            if(diagram == null){
                throw new IllegalArgumentException("No diagram with a key of " + uuid.toString());
            }
			
			DiagramViewer editor;
			if(source != null && source instanceof DiagramViewer){
			    editor = (DiagramViewer)source;
			} else {
				DiagramViewerWindowFactory windowFactory = DiagramViewer.getWindowFactory(diagram.getType()); 
				windowFactory.init(diagram, app, repository);
				WindowCoordinator wc = app.getWindowCoordinator();
				editor = (DiagramViewer)wc.getFrame(diagram.getKey().toString(), windowFactory);
			}
				
			editor.refresh();
			editor.show();
            
        }
    }

    /**
     * ImageResolver resolves an image which is displayed.
     * 
     * @author rbp28668
     */
    private class ImageResolver extends Resolver {

        /* (non-Javadoc)
         * @see alvahouse.eatool.gui.URLResolver.Resolver#resolve(java.net.URL, java.awt.Component, alvahouse.eatool.util.UUID)
         */
        public void resolve(URL url, Component source, UUID uuid) throws Exception{
            if(uuid == null){
                throw new IllegalArgumentException("URL for image does not include a UUID");
            }
            Images images = repository.getImages();
            Image image = images.lookupImage(uuid);
            if(image == null){
                throw new IllegalArgumentException("No image with a key of " + uuid.toString());
            }
	        ImageIcon icon = new ImageIcon(image.getImage());
	        JOptionPane.showMessageDialog(source, icon, image.toString(), JOptionPane.PLAIN_MESSAGE);
        }
    }

}
