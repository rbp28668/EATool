/**
 * 
 */
package alvahouse.eatool.gui.scripting;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.JTextComponent;

import alvahouse.eatool.gui.ActionSet;

/* Note - text actions for JTextComponent are:
 * insert-content
	delete-previous
	delete-next
	set-read-only
	set-writable
	cut-to-clipboard
	copy-to-clipboard
	paste-from-clipboard
	page-up
	page-down
	selection-page-up
	selection-page-down
	selection-page-left
	selection-page-right
	insert-break
	beep
	caret-forward
	caret-backward
	selection-forward
	selection-backward
	caret-up
	caret-down
	selection-up
	selection-down
	caret-begin-word
	caret-end-word
	selection-begin-word
	selection-end-word
	caret-previous-word
	caret-next-word
	selection-previous-word
	selection-next-word
	caret-begin-line
	caret-end-line
	selection-begin-line
	selection-end-line
	caret-begin-paragraph
	caret-end-paragraph
	selection-begin-paragraph
	selection-end-paragraph
	caret-begin
	caret-end
	selection-begin
	selection-end
	default-typed
	insert-tab
	select-word
	select-line
	select-paragraph
	select-all
	unselect
	toggle-componentOrientation
	dump-model
 */

/**
 * Provides a global set of actions for use in a JEditorPane.
 * @author bruce_porteous
 *
 */
public class TextActions {
	private static Map<String,Action> editorActions = new HashMap<String,Action>();

	static {
		addTextActions(new JEditorPane());
	}

	private static void addTextActions(JTextComponent text){
		Action[] actions = text.getActions();
		for(int i=0; i<actions.length; ++i){
			String name = (String)actions[i].getValue(Action.NAME);
			//System.out.println(name);
			editorActions.put(name,actions[i]);
		}
	}
	
	public static void addActionsTo(ActionSet actions) {
	   for (Map.Entry<String,Action> entry : editorActions.entrySet()) {
		   actions.addAction(entry.getKey(),entry.getValue());
       }

	}
}