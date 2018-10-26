/*
 * ScriptTreeModel.java
 * Project: EATool
 * Created on 05-Mar-2006
 *
 */
package alvahouse.eatool.gui.scripting;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import alvahouse.eatool.gui.ExplorerTreeModel;
import alvahouse.eatool.repository.scripting.Script;
import alvahouse.eatool.repository.scripting.ScriptChangeEvent;
import alvahouse.eatool.repository.scripting.Scripts;
import alvahouse.eatool.repository.scripting.ScriptsChangeListener;

/**
 * ScriptTreeModel reprepresents the scripts and their properties as a hierarchy
 * for use in the ScriptExplorer.
 * 
 * @author rbp28668
 */
public class ScriptTreeModel extends ExplorerTreeModel implements ScriptsChangeListener{

    private static final long serialVersionUID = 1L;
    private DefaultMutableTreeNode root = null;
    private Scripts scripts;

    /**
     * @param rootTitle
     */
    public ScriptTreeModel(String rootTitle, Scripts scripts) {
        super(rootTitle);
        this.scripts = scripts;
        
        root = (DefaultMutableTreeNode)getRoot();
		root.setUserObject( scripts );

		scripts.addChangeListener(this);
		initModel();
        
    }

    public void dispose(){
        if(scripts != null) {
            scripts.removeChangeListener(this);
        }
        scripts = null;
    }
    

    /**
	 * Method initModel builds the tree model from the mappings.
	 */
    private void initModel() {
        int idx = 0;
        for (Script script : scripts.getScripts()) {
            addScriptNode((MutableTreeNode)getRoot(),script,idx++);
        }
    }

    /**
     * @param node
     * @param script
     * @param i
     */
    private int addScriptNode(MutableTreeNode parent, Script script, int idx) {
        DefaultMutableTreeNode tnScript = new DefaultMutableTreeNode(script.getName());
        tnScript.setUserObject(script);
        insertNodeInto(tnScript,parent,idx);
        registerNode(tnScript,script);
        addScriptChildren(tnScript,script);
        return idx;
    }

    /**
     * Adds the properties of the script to the tree.
     * @param parent is the parent script node to add properties to.
     * @param script is the script whose properties we want.
     * @return the last child index used.
     */
    private int addScriptChildren(MutableTreeNode parent, Script script) {
        int idx = 0;
        
        insertNodeInto(new DefaultMutableTreeNode("language: " + script.getLanguage()),parent,idx++);
        
        if(script.getDescription() != null)
            insertNodeInto(new DefaultMutableTreeNode("description: " + script.getDescription()), parent, idx++);
        
        return idx;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.scripting.ScriptsChangeListener#updated(alvahouse.eatool.scripting.ScriptChangeEvent)
     */
    public void updated(ScriptChangeEvent e) {
        root.removeAllChildren();
        initModel();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.scripting.ScriptsChangeListener#scriptAdded(alvahouse.eatool.scripting.ScriptChangeEvent)
     */
    public void scriptAdded(ScriptChangeEvent e) {
        Script script = (Script)e.getSource();
        addScriptNode(root,script,root.getChildCount());
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.scripting.ScriptsChangeListener#scriptChanged(alvahouse.eatool.scripting.ScriptChangeEvent)
     */
    public void scriptChanged(ScriptChangeEvent e) {
        Script script = (Script)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(script);
        if(tn != null) {
            nodeChanged(tn);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.scripting.ScriptsChangeListener#scriptDeleted(alvahouse.eatool.scripting.ScriptChangeEvent)
     */
    public void scriptDeleted(ScriptChangeEvent e) {
        Script script = (Script)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(script);
        if(tn != null) {
            removeNodeFromParent(tn);
            removeNodeOf(script);
        }
    }

}
