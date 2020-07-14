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

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.CopyKeyAction;
import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.gui.ExceptionDisplay;
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
		public void actionPerformed(ActionEvent e) {
			try {
			    Script script = new Script(new UUID());
		        ScriptAttributesDialog editor = new ScriptAttributesDialog(explorer, "Edit Script Properties", script);
		        editor.setVisible(true);
		        if(editor.wasEdited()){
		            scripts.add(script);
			        ScriptEditor se = (ScriptEditor)app.getWindowCoordinator().getFrame("ScriptEditor");
			        se.edit(script);
		        }

			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   

    private final Action actionScriptEdit = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
			    Script script = (Script)explorer.getSelectedNode().getUserObject();
			    if(script != null){
			        ScriptEditor editor = (ScriptEditor)app.getWindowCoordinator().getFrame("ScriptEditor");
			        editor.edit(script);
			    }
			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   
    private final Action actionScriptProperties = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
			    Script script = (Script)explorer.getSelectedNode().getUserObject();
			    if(script != null){
			        ScriptAttributesDialog editor = new ScriptAttributesDialog(explorer, "Edit Script Properties", script);
			        editor.setVisible(true);
			        if(editor.wasEdited()){
			            scripts.fireScriptChanged(script);
			        }
			    }
			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   
    
	private final Action actionScriptDelete = new AbstractAction() {
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
	
}
