/*
 * HTMLEditorActionSet.java
 * Project: EATool
 * Created on 07-May-2007
 *
 */
package alvahouse.eatool.gui.html;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.text.JTextComponent;

import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.repository.html.HTMLPage;

/**
 * HTMLEditorActionSet is the set of actions for the HTML editor.
 * 
 * @author rbp28668
 */
public class HTMLEditorActionSet extends ActionSet {
    
    private HTMLEditor editor;
    private static Map textActions = new HashMap(); 
    
    /**
     * 
     */
    public HTMLEditorActionSet(HTMLEditor editor) {
        super();
        this.editor = editor;
        
		addAction("HTMLUpdate",actionHTMLUpdate);
		addAction("HTMLProperties",actionHTMLProperties);
		addAction("HTMLClose", actionHTMLClose);
        
    }
    
    public void addTextActions(JTextComponent text){
        // First time through, create an initial map with the action names as 
        // menus will change action names subsequently.
        synchronized(textActions){
            if(textActions.isEmpty()){
                Action[] actions = text.getActions();
                for(int i=0; i<actions.length; ++i){
                    String name = (String)actions[i].getValue(Action.NAME);
                    //System.out.println(name);
                    textActions.put(name,actions[i]);
                }
            }
        }
        addAll(textActions);
    }

	private final Action actionHTMLUpdate = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
			    editor.updatePage();
			    editor.dispose();
			} catch(Throwable t) {
				new ExceptionDisplay(editor,t);
			}
		}
	};

    private final Action actionHTMLProperties = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
			    HTMLPage page = editor.getPage();
			    if(page != null){
//			        ScriptAttributesDialog dlg = new ScriptAttributesDialog(editor, "Edit Script Properties", script);
//			        dlg.setVisible(true);
//			        if(dlg.wasEdited()){
//			            editor.updateTitle();
//			        }
			    }
			} catch(Throwable t) {
				new ExceptionDisplay(editor,t);
			}
		}
	};   
    
	private final Action actionHTMLClose = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
			    editor.dispose();
			} catch(Throwable t) {
				new ExceptionDisplay(editor,t);
			}
		}
	};
	


}
