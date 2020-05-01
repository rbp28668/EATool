/*
 * ScriptEditor.java
 * Project: EATool
 * Created on 05-Mar-2006
 *
 */
package alvahouse.eatool.gui.scripting;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

import org.apache.bsf.BSFException;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.GUIBuilder;
import alvahouse.eatool.gui.WrappedTextArea;
import alvahouse.eatool.repository.scripting.Script;
import alvahouse.eatool.repository.scripting.ScriptErrorListener;
import alvahouse.eatool.repository.scripting.ScriptManager;
import alvahouse.eatool.util.SettingsManager;


/**
 * ScriptEditor provides a simple editor for writing scripts.
 * 
 * @author rbp28668
 */
public class ScriptEditor extends JInternalFrame implements ScriptErrorListener{

    private EditorPane display;
    private JTextArea messages;
    private ScriptEditorActionSet actions; 
    private Script script;
    private Application app;
    
    private final static String WINDOW_SETTINGS = "/Windows/ScriptEditor";
	private static final String MENU_CONFIG = "/ScriptEditor/menus";

    /**
     * 
     */
    public ScriptEditor(Application app) {
        super();
        init(app);
    }

    /**
     * @param arg0
     */
    public ScriptEditor(String arg0,Application app) {
        super(arg0);
        init(app);
    }

    private void init(Application app){
        this.app = app;
        
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        actions = new ScriptEditorActionSet(this,app);
        
        GUIBuilder.loadBounds(this,WINDOW_SETTINGS,app);

        SettingsManager config = app.getConfig();

        display = new EditorPane();
        
        JMenuBar menuBar = new JMenuBar();
        SettingsManager.Element cfg = config.getElement(MENU_CONFIG);
        GUIBuilder.buildMenuBar(menuBar, actions, cfg);
        setJMenuBar(menuBar);

        JScrollPane scrollPane = new JSizedScroll(display);
        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
        
        messages = new WrappedTextArea(4,60);
        messages.setEditable(false);
        getContentPane().add(messages, java.awt.BorderLayout.SOUTH);
        
        updateKeymap(display);
        
        try {
            ScriptManager.getInstance().addListener(this);        
        } catch (BSFException e) {
            // NOP
        }
    }
    
    /**
     * @param display
     */
    private void updateKeymap(EditorPane display) {
        Keymap map = JTextComponent.addKeymap("NextPrevMap",display.getKeymap());
        bindKey(map, KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK, DefaultEditorKit.nextWordAction);
        bindKey(map, KeyEvent.VK_LEFT,  InputEvent.CTRL_MASK, DefaultEditorKit.previousWordAction);
        bindKey(map, KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK|InputEvent.SHIFT_MASK, DefaultEditorKit.selectionNextWordAction);
        bindKey(map, KeyEvent.VK_LEFT,  InputEvent.CTRL_MASK|InputEvent.SHIFT_MASK, DefaultEditorKit.selectionPreviousWordAction);
    }

    /**
     * Binds an Action to a keystroke in the given Keymap.
     * @param map is the Keymap to add the binding to.
     * @param key is the virtual key code to make up the keystroke.
     * @param mask is the keymask to make up the keystroke.
     * @param actionName is the name of the action to bind.
     */
    private void bindKey(Keymap map, int key, int mask, String actionName) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(key,mask, false);
        map.addActionForKeyStroke(keyStroke,actions.getAction(actionName));
    }

    public void edit(Script script){
        this.script = script;
        display.setText(script.getScript());
        updateTitle();
        setVisible(true);
    }
    
    /**
     * Gets the script currently being edited.
     * @return
     */
    public Script getScript(){
        return script;
    }

    /**
     * This updates the script from the editor pane.
     */
    public void updateScript() {
        String scriptText = display.getText(); 
        script.setScript(scriptText);
    }

    public void dispose() {
        GUIBuilder.saveBounds(this,WINDOW_SETTINGS,app);
        app.getWindowCoordinator().removeFrame(this);
        try {
            ScriptManager.getInstance().removeListener(this);
        } catch (BSFException e) {
            // NOP
        }
        super.dispose();
    }

    JEditorPane getEditPane(){
        return display;
    }
    
    private class EditorPane extends JEditorPane{
        public EditorPane() {
            super();
            setEditable(true);
            setContentType("text/plain");
            
            Font font = new Font("Lucida Console", Font.PLAIN,12);
            setFont(font);
        }
        
    }

    private class JSizedScroll extends JScrollPane {
        
        JSizedScroll(JComponent component){
            super(component);
            Dimension d = new Dimension(200,300);
            super.setPreferredSize(d);
            
        }
    }
    
    /**
     * Updates the editor title with the script name.
     */
    public void updateTitle() {
        setTitle("Script: " + script.getName());
     }

     /**
      * Shows a message.
     * @param text
     */
    public void showMessage(String text){
         messages.setText(text);
     }

    /* (non-Javadoc)
     * @see alvahouse.eatool.scripting.ScriptErrorListener#scriptError(java.lang.String)
     */
    public void scriptError(String err) {
        showMessage(err);
    }

}
