/*
 * ScriptActionSet.java
 * Project: EATool
 * Created on 05-Mar-2006
 *
 */
package alvahouse.eatool.gui.scripting;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JInternalFrame;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.CopyKeyAction;
import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.gui.WindowCoordinator.WindowFactory;
import alvahouse.eatool.repository.scripting.Script;
import alvahouse.eatool.repository.scripting.ScriptContext;
import alvahouse.eatool.repository.scripting.ScriptErrorListener;
import alvahouse.eatool.repository.scripting.ScriptManager;
import alvahouse.eatool.repository.scripting.Scripts;
import alvahouse.eatool.util.UUID;

/**
 * ScriptActionSet is the action set for script explorer.
 * 
 * @author rbp28668
 */
public class ScriptActionSet extends ActionSet {

    private ScriptExplorer explorer;
    private Scripts scripts;
    private Application app;
    
    /**
     * 
     */
    public ScriptActionSet(ScriptExplorer explorer, Scripts scripts, Application app) {
        super();
        this.explorer = explorer;
        this.scripts = scripts;
        this.app = app;
        
		addAction("ScriptNew",actionScriptNew);
		addAction("ScriptEdit",actionScriptEdit);
		addAction("ScriptProperties", actionScriptProperties);
		addAction("ScriptDelete",actionScriptDelete);
		addAction("ScriptRun",actionScriptRun);
        addAction(CopyKeyAction.NAME, new CopyKeyAction(explorer,explorer));
		
    }

    private final Action actionScriptNew = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
			    final Script script = new Script(new UUID());
		        ScriptAttributesDialog editor = new ScriptAttributesDialog(explorer, "Edit Script Properties", script);
		        editor.setVisible(true);
		        if(editor.wasEdited()){
			    	ScriptEditor se = getEditorFor(script);
			        se.onCompletion(new ScriptEditor.Completion() {
						@Override
						public void onCompletion(Script s) {
							try {
								scripts.add(script);
							}catch(Exception e) {
								new ExceptionDisplay(explorer,e);
							}
						}
					});
			        
			        se.edit(script);
		        }

			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   

    private final Action actionScriptEdit = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
			    final Script script = (Script)explorer.getSelectedNode().getUserObject();
			    if(script != null){
			    	ScriptEditor editor = getEditorFor(script);
			        editor.onCompletion(new ScriptEditor.Completion() {
						@Override
						public void onCompletion(Script s) {
							try {
								scripts.update(script);
							}catch(Exception e) {
								new ExceptionDisplay(explorer,e);
							}
						}
					});
			        
			        editor.edit(script);
			    }
			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};
	
    private final Action actionScriptProperties = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
			    Script script = (Script)explorer.getSelectedNode().getUserObject();
			    if(script != null){
			        ScriptAttributesDialog editor = new ScriptAttributesDialog(explorer, "Edit Script Properties", script);
			        editor.setVisible(true);
			        if(editor.wasEdited()){
			            scripts.update(script);
			        }
			    }
			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   
    
	private final Action actionScriptDelete = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
			    Script script = (Script)explorer.getSelectedNode().getUserObject();
			    if(script != null){
			        if(Dialogs.question(explorer, "Delete script " + script.getName())){
			            scripts.delete(script);
			        }
			    }
			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};
	
    private final Action actionScriptRun = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
			    Script script = (Script)explorer.getSelectedNode().getUserObject();
			    if(script != null){
			    	ScriptContext context = new ScriptContext(script);
			    	context.setErrorHandler( new ScriptErrorListener() {
						
						@Override
						public void scriptError(String err) {
							Exception e = new Exception(err);
							new ExceptionDisplay(explorer,e);
						}
					});
			        ScriptManager.getInstance().runScript(context);
			    }
			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};

	/**
	 * @param script
	 * @return
	 * @throws Exception
	 */
	private ScriptEditor getEditorFor(final Script script) throws Exception {
		String windowKey = "ScriptEditor" + script.getKey().toString();
		if(!app.getWindowCoordinator().hasFactory(windowKey)) {
			app.getWindowCoordinator().addFactory( new WindowFactory () {
		        public JInternalFrame createFrame() {
		            return new ScriptEditor(app);
		        }
		    },windowKey);
		}

		ScriptEditor editor = (ScriptEditor)app.getWindowCoordinator().getFrame(windowKey);
		return editor;
	}   
	
}
