/*
 * Application.java
 * Project: EATool
 * Created on 06-Mar-2006
 *
 */
package alvahouse.eatool.gui.scripting.proxy;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import javax.swing.Action;

import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.CommandActionSet;
import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.gui.ModelBrowser;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.scripting.proxy.EntitySet;
import alvahouse.eatool.scripting.proxy.ImageProxy;
import alvahouse.eatool.scripting.proxy.ImagesProxy;
import alvahouse.eatool.scripting.proxy.MetaEntitySet;
import alvahouse.eatool.scripting.proxy.MetaRelationshipSet;
import alvahouse.eatool.scripting.proxy.StandardDiagramProxy;



/**
 * Application is a proxy object for the main application that
 * provides a facade for scripting.
 * 
 * @author rbp28668
 */
public class Application {

    private alvahouse.eatool.Application app;
    private Repository repository;
    
    public Application(alvahouse.eatool.Application app,Repository repository){
        this.app = app;
        this.repository = repository;
    }
    /**
     * Get the command action set that provides much of the 
     * applications functionality.
     * @return the CommandActionSet of the main frame.
     */
    private CommandActionSet getActions() {
        return (CommandActionSet)app.getCommandFrame().getActions();
    }

    /**
     * Displays an informational message in a popup.
     * @param text is the message to display.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public void message(String text) throws InterruptedException, InvocationTargetException{
        EventQueue.invokeAndWait( new MessageRunner(text));
    }
    
    private class MessageRunner implements Runnable {

        private String message;
        
        MessageRunner(String message){
            this.message = message;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            Dialogs.message(app.getCommandFrame(), message);
        }
    }

    /**
     * Displays a warning message in a popup.
     * @param text is the message to display.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public void warning(String text) throws InterruptedException, InvocationTargetException{
        EventQueue.invokeAndWait( new WarningRunner(text));
    }

    private class WarningRunner implements Runnable {

        private String message;
        
        WarningRunner(String message){
            this.message = message;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            Dialogs.warning(app.getCommandFrame(), message);
        }
    }

    /**
     * Provides the user with a yes/no popup.
     * @param text is the question to display in the popup.
     * @return true if the user presses yes, false if presses no.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public boolean question(String text) throws InterruptedException, InvocationTargetException{
        QuestionRunner runner = new QuestionRunner(text);
        EventQueue.invokeAndWait( runner);
        return runner.getResult();
    }
    
    private class QuestionRunner implements Runnable {

        private String message;
        private boolean result = false;
        
        QuestionRunner(String message){
            this.message = message;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            result = Dialogs.question(app.getCommandFrame(), message);
        }
        
        boolean getResult(){
            return result;
        }
    }

    /**
     * Allows the user to enter text.
     * @param text is the question to display in the popup.
     * @return the entered text
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public String input(String text) throws InterruptedException, InvocationTargetException{
        InputRunner runner = new InputRunner(text);
        EventQueue.invokeAndWait( runner);
        return runner.getResult();
    }
    
    private class InputRunner implements Runnable {

        private String message;
        private String result = "";
        
        InputRunner(String message){
            this.message = message;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            result = Dialogs.input(app.getCommandFrame(), message);
        }
        
        String getResult(){
            return result;
        }
    }
    

     
    /**
     * Runs a named action of the application. These will run as if the
     * user had selected them from the menu.
     * @param actionName is the name of the action to run.
     */
    public void runAction(String actionName){
        ActionSet actions = app.getCommandFrame().getActions();
        Action action = actions.getAction(actionName);
        if(action != null){
            ActionEvent evt = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,actionName);
            action.actionPerformed(evt);
        }
    }
    
    /**
     * Provides access to the top level menu bar of the application.
     * @return the top level menu-bar.
     */
    public MenuBar getMenuBar(){
        return new MenuBar(app.getCommandFrame().getJMenuBar(),app,repository);
    }
    
    /**
     * Clears out the repository.
     * @throws IOException
     */
    public void fileNew() throws IOException{
        getActions().fileNew();
    }
    

    /**
     * Loads a file from disk.  Unless fileNew is called first
     * to empty the repository this will merge the new data with
     * the existing repository.
     * @param path is the location of the data on disk.
     * @throws IOException
     */
    public void loadFile(String path) throws IOException{
        getActions().loadFile(path);
    }
    
    /**
     * Saves the repository to the given path.
     * @param path is the location on disk to save to.
     */
    public void saveFile(String path){
        getActions().saveFile(path);
    }
    
    /**
     * Exits the application. This will not ask for confirmation.
     */
    public void exit(){
        getActions().fileExit();
    }
    
    /**
     * Display the meta-model explorer.
     */
    public void showMetaModel(){
        getActions().showMetaModel();
    }
    
    /**
     * Display a diagram of the metamodel.
     */
    public void viewMetaModel(){
        getActions().viewMetaModel();
    }
    
    /**
     * Show the diagram explorer that contains all the meta-model
     * diagrams.
     */
    public void showMetaModelDiagrams(){
        getActions().showMetaModelDiagrams();
    }
    
    /**
     * Display the model explorer.
     */
    public void showModel(){
        getActions().showModel();
    }
    
    /**
     * Display a diagram of the complete model.
     */
    public void viewModel(){
        getActions().viewModel();
    }
    
    /**
     * Display the explorer for the model's diagrams.
     */
    public void showModelDiagrams(){
        getActions().showModelDiagrams();
    }
    
    /**
     * Display the model browser.
     */
    public void browseModel(){
        getActions().browseModel();
    }
    
    
    /**
     * Show the scripts explorer.
     */
    public void showScripts(){
        getActions().showScripts();
    }
    
    /**
     * Show the import mappings explorer.
     */
    public void showImportMappings(){
        getActions().showImportMappings();
    }
    
    /**
     * Show the export mappings explorer.
     */
    public void showExportMappings(){
        getActions().showExportMappings();
    }
    
    /**
     * Pause for a given number of seconds.
     * @param seconds is the number of seconds to pause for.
     */
    public void pause(int seconds){
        try {
            Thread.interrupted();
            Thread.sleep(1000l * seconds);
        } catch (InterruptedException e) {
            // NOP
        }
    }
    
    /**
     * Gets a HTML object for writing HTML to file or HTMLDisplay.
     * @return a new HTML object.
     */
    public HTML getHTML(){
        return new HTML( new alvahouse.eatool.gui.html.HTML());
    }
    
    /**
     * Creates a new HTMLDisplay for reports etc.
     * @return a new HTMLDisplay.
     */
    public HTMLDisplay createDisplay(){
        return new HTMLDisplay(app,repository);
    }
    
    /**
     * Opens an output file for writing.
     * @param path is the path of the file.
     * @return an open file.
     * @throws FileNotFoundException
     */
    public File openOutputFile(String path) throws FileNotFoundException{
        File file = new File(app);
        file.open(path);
        return file;
    }
    
    /**
     * Allows the user to select an output file and returns the opened file.
     * @param title is the title to use in the file open dialog.
     * @return a file - test with isOpen().
     * @throws FileNotFoundException
     * @throws InterruptedException
     * @throws InvocationTargetException
     */
    public File chooseOutputFile(String title) throws FileNotFoundException, InterruptedException, InvocationTargetException{
        File file = new File(app);
        file.choose(title);
        return file;
    }
    
    /**
     * Gets the set of images known to the system.
     * @return the image collection.
     */
    public ImagesProxy getImages(){
        return new ImagesProxy(repository.getImages());
    }
    
    /**
     * Changes the application background image.  The image is tiled across
     * the main window.
     * @param image is the image to display.
     * @param greyed, if true, forces a grey version of the image.  If false, 
     * the image is displayed unmodified.
     */
    public void tileBackground(ImageProxy image, boolean greyed){
        app.getCommandFrame().tileBackgroundImage(image.getImage(), greyed);
    }

    /**
     * Changes the application background image.  The image is stretched across
     * the main window.
     * @param image is the image to display.
     * @param greyed, if true, forces a grey version of the image.  If false, 
     * the image is displayed unmodified.
     */
    public void stretchBackground(ImageProxy image, boolean greyed){
        app.getCommandFrame().stretchBackgroundImage(image.getImage(), greyed);
    }

    /**
     * Browse a given entity.
     * @param entities is the set of Entity to browse.  If it contains more than
     * one Entity then each entity will be displayed in a different window.
     */
    public void browseEntity(EntitySet entities){
        for(Entity e : entities.getContents()){
            getActions().browseEntity(e);
        }
    }

    /**
     * Displays this set of meta-entities in a browser. 
     * @param set
     */
    public void browseMetaEntities(MetaEntitySet set){
        ModelBrowser browser = (ModelBrowser) app.getWindowCoordinator().getFrame("ModelBrowser");
        Set<alvahouse.eatool.repository.metamodel.MetaEntity> contents = set.getContents();
        MetaEntity[] entities = new MetaEntity[contents.size()];
        browser.browse((MetaEntity[])contents.toArray(entities));
        browser.setVisible(true);
    }

    /**
     * Select a single entity from the contents of an EntitySet set.  If one
     * is selected the contents of this set are replaced by the single
     * selected entity. If one is not selected this set will then become
     * empty.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public void selectEntity(EntitySet set) throws InterruptedException, InvocationTargetException {
        EntitySelectRunner runner = new EntitySelectRunner(set);
        EventQueue.invokeAndWait( runner);
    }
    
    private class EntitySelectRunner implements Runnable {
        private EntitySet set;
        
        EntitySelectRunner(EntitySet set){
            this.set = set;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            Set<Entity> contents = set.getContents();
            alvahouse.eatool.repository.model.Entity selected = Dialogs.selectEntityFrom(contents,app.getCommandFrame());
            contents.clear();
            if(selected != null){
                contents.add(selected);
            }
        }
        
    }


    /**
     * Select a single meta-entity from the contents of this set.  If one
     * is selected the contents of this set are replaced by the single
     * selected meta-entity. If one is not selected this set will then become
     * empty.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public void selectMetaEntity(MetaEntitySet set) throws InterruptedException, InvocationTargetException {
        MetaEntitySelectRunner runner = new MetaEntitySelectRunner(set);
        EventQueue.invokeAndWait( runner);
    }
    
    private class MetaEntitySelectRunner implements Runnable {

        private MetaEntitySet set; 
        
        MetaEntitySelectRunner(MetaEntitySet set){
            this.set = set;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            Set<MetaEntity> contents = set.getContents();
            MetaEntity selected = (MetaEntity)Dialogs.selectNRIFrom(
                    contents,
                    "Select Meta-Entity",
                    app.getCommandFrame());
            contents.clear();
            if(selected != null){
                contents.add(selected);
            }
        }
    }
    
    /**
     * Select a single MetaRelationship from the contents of this set.  If one
     * is selected the contents of this set are replaced by the single
     * selected MetaRelationship. If one is not selected this set will then become
     * empty.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public void selectMetaRelationship(MetaRelationshipSet set) throws InterruptedException, InvocationTargetException {
        MetaRelationshipSelectRunner runner = new MetaRelationshipSelectRunner(set);
        EventQueue.invokeAndWait( runner);
    }
    
    private class MetaRelationshipSelectRunner implements Runnable {

        private MetaRelationshipSet set;
        MetaRelationshipSelectRunner(MetaRelationshipSet set){
            this.set = set;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            Set<MetaRelationship> contents = set.getContents();
            MetaRelationship selected = (MetaRelationship)Dialogs.selectNRIFrom(contents,"Select Meta-Relationship", app.getCommandFrame());
            contents.clear();
            if(selected != null){
                contents.add(selected);
            }
        }
    }

    public StandardDiagramViewer getDiagramViewer(StandardDiagramProxy diagram){
        StandardDiagramViewer viewer = new StandardDiagramViewer(diagram.getDiagram(), app, repository);
        return viewer;
    }
    
}