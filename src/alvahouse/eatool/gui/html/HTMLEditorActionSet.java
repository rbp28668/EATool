/*
 * HTMLEditorActionSet.java
 * Project: EATool
 * Created on 07-May-2007
 *
 */
package alvahouse.eatool.gui.html;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.gui.scripting.EventMapDialog;
import alvahouse.eatool.gui.scripting.TextActions;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.html.HTMLPage;
import alvahouse.eatool.repository.scripting.Scripts;

/**
 * HTMLEditorActionSet is the set of actions for the HTMLProxy editor.
 * 
 * @author rbp28668
 */
public class HTMLEditorActionSet extends ActionSet {
    
    private final HTMLEditor editor;
    private final Repository repository;
	
    
    /**
     * 
     */
    public HTMLEditorActionSet(HTMLEditor editor, Repository repository) {
        super();
        this.editor = editor;
        this.repository = repository;
        
		addAction("HTMLUpdate",actionHTMLUpdate);
		addAction("HTMLProperties",actionHTMLProperties);
		addAction("HTMLClose", actionHTMLClose);
		addAction("HTMLEventMappings", actionEditEventMappings);
        TextActions.addActionsTo(this);
    }
    

	private final Action actionHTMLUpdate = new AbstractAction() {
		private static final long serialVersionUID = 1L;

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
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
			    HTMLPage page = editor.getPage();
			    if(page != null){
			    	HTMLPagePropertiesEditor dlg = new HTMLPagePropertiesEditor(editor,page);
			        dlg.setVisible(true);
			        if(dlg.wasEdited()){
			            editor.updateTitle();
			        }
			    }
			} catch(Throwable t) {
				new ExceptionDisplay(editor,t);
			}
		}
	};   
    
	private final Action actionHTMLClose = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
			    editor.dispose();
			} catch(Throwable t) {
				new ExceptionDisplay(editor,t);
			}
		}
	};
	
	/**
	 * Comment for <code>actionEditEventMappings</code>
	 */
	public final Action actionEditEventMappings = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
			    HTMLPage page = editor.getPage();
			    Scripts scripts = repository.getScripts();
                EventMapDialog dialog = new EventMapDialog(editor, "Edit Page Event Mapping", page.getEventMap(), scripts);
                dialog.setVisible(true);
                if(dialog.wasEdited()) {
                	page.scriptsUpdated();
                }
			} catch(Throwable t) {
				new ExceptionDisplay(editor,t);
			}
		}

	};

	


}
