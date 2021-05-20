/*
 * ScriptEditorActionSet.java
 * Project: EATool
 * Created on 05-Mar-2006
 *
 */
package alvahouse.eatool.gui.scripting;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.repository.scripting.Script;
import alvahouse.eatool.repository.scripting.ScriptContext;
import alvahouse.eatool.repository.scripting.ScriptErrorListener;
import alvahouse.eatool.repository.scripting.ScriptManager;

/**
 * ScriptEditorActionSet
 * 
 * @author rbp28668
 */
public class ScriptEditorActionSet extends ActionSet {

	private ScriptEditor editor;
	private Application app;

	/**
	 * 
	 */
	public ScriptEditorActionSet(ScriptEditor editor, Application app) {
		super();
		this.editor = editor;
		this.app = app;

		addAction("ScriptUpdate", actionScriptUpdate);
		addAction("ScriptProperties", actionScriptProperties);
		addAction("ScriptClose", actionScriptClose);
		addAction("ScriptRun", actionScriptRun);
		addAction("ScriptObjectBrowser", actionScriptObjectBrowser);
		addAction("ScriptGotoLine", actionScriptGotoLine);
		TextActions.addActionsTo(this);
	}

	private final Action actionScriptUpdate = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
				editor.updateScript();
				editor.dispose();
			} catch (Throwable t) {
				new ExceptionDisplay(editor, t);
			}
		}
	};

	private final Action actionScriptProperties = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
				Script script = editor.getScript();
				if (script != null) {
					ScriptAttributesDialog dlg = new ScriptAttributesDialog(editor, "Edit Script Properties", script);
					dlg.setVisible(true);
					if (dlg.wasEdited()) {
						editor.updateTitle();
						editor.setModified();
					}
				}
			} catch (Throwable t) {
				new ExceptionDisplay(editor, t);
			}
		}
	};

	private final Action actionScriptClose = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
				editor.dispose();
			} catch (Throwable t) {
				new ExceptionDisplay(editor, t);
			}
		}
	};

	private final Action actionScriptRun = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
				editor.updateScript();
				editor.showMessage("");
				editor.setModified();

				ScriptContext context = new ScriptContext(editor.getScript());
				context.setErrorHandler(new ScriptErrorListener() {

					@Override
					public void scriptError(String err) {
						editor.showMessage(err);
					}

				});
				ScriptManager.getInstance().runScript(context);
			} catch (Throwable t) {
				editor.showMessage(t.getMessage());
				// new ExceptionDisplay(editor,t);
			}
		}
	};

	private final Action actionScriptObjectBrowser = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
				FunctionHelpBrowser browser = (FunctionHelpBrowser) app.getWindowCoordinator()
						.getFrame("FunctionHelpBrowser");
				browser.browse(alvahouse.eatool.gui.scripting.proxy.ApplicationProxy.class);
				browser.setVisible(true);
			} catch (Throwable t) {
				new ExceptionDisplay(editor, t);
			}
		}

	};

	private final Action actionScriptGotoLine = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
				JEditorPane editPane = editor.getEditPane();
				PlainDocument doc = (PlainDocument) editPane.getDocument();
				Element root = doc.getDefaultRootElement();

				String caption = "Enter line number between 1 and " + Integer.toString(root.getElementCount());
				String lineText = Dialogs.input(editor, caption);
				if (lineText != null) {
					int lineNumber = Integer.parseInt(lineText);
					if (lineNumber < 1) {
						lineNumber = 1;
					} else if (lineNumber > root.getElementCount()) {
						lineNumber = root.getElementCount();
					}
					Element line = root.getElement(lineNumber - 1);
					int offset = line.getStartOffset();
					editPane.setCaretPosition(offset);
				}

			} catch (Throwable t) {
				new ExceptionDisplay(editor, t);
			}
		}

	};

}
