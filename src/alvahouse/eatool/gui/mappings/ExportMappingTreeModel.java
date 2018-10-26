/*
 * ExportMappingTreeModel.java
 * Project: EATool
 * Created on 31-Dec-2005
 *
 */
package alvahouse.eatool.gui.mappings;

import java.util.Iterator;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import alvahouse.eatool.gui.ExplorerTreeModel;
import alvahouse.eatool.repository.mapping.ExportMapping;
import alvahouse.eatool.repository.mapping.ExportMappingChangeListener;
import alvahouse.eatool.repository.mapping.ExportMappings;
import alvahouse.eatool.repository.mapping.MappingChangeEvent;

/**
 * ExportMappingTreeModel
 * 
 * @author rbp28668
 */
public class ExportMappingTreeModel extends ExplorerTreeModel implements
        ExportMappingChangeListener {

	private MutableTreeNode root = null;
	private JTree tree = null;
	private ExportMappings mappings;

    /**
     * @param rootTitle
     */
    public ExportMappingTreeModel(String rootTitle, ExportMappings mappings) {
        super(rootTitle);
        this.mappings = mappings;
        root = (MutableTreeNode)getRoot();
		root.setUserObject( mappings);
		mappings.addChangeListener(this);
        initModel();
    }

    public void dispose(){
        if(mappings != null){
            mappings.removeChangeListener(this);
        }
    }
    
    public void setMappings(ExportMappings mappings){
        this.mappings = mappings;
        initModel();
    }
	/**
	 * Method initModel builds the tree model from the mappings.
	 */
    private void initModel() {
        int idx = 0;
        for(Iterator iter = mappings.getExportMappings().iterator();iter.hasNext();){
            ExportMapping mapping = (ExportMapping)iter.next();
            addExportMappingNode((MutableTreeNode)getRoot(),mapping,idx++);
        }
    }

    /**
     * @param parent
     * @param mapping
     * @param i
     */
    private int addExportMappingNode(MutableTreeNode parent, ExportMapping mapping, int idx) {
        DefaultMutableTreeNode tnExportMapping = new DefaultMutableTreeNode(mapping.getName());
        tnExportMapping.setUserObject(mapping);
        insertNodeInto(tnExportMapping,parent,idx);
        registerNode(tnExportMapping,mapping);
        return idx;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.mapping.ExportMappingChangeListener#MappingAdded(alvahouse.eatool.repository.mapping.MappingChangeEvent)
     */
    public void MappingAdded(MappingChangeEvent e) {
        ExportMapping mapping = (ExportMapping)e.getSource();
        int idx = ((MutableTreeNode)getRoot()).getChildCount();
        addExportMappingNode((MutableTreeNode)getRoot(), mapping, idx);
        
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.mapping.ExportMappingChangeListener#MappingEdited(alvahouse.eatool.repository.mapping.MappingChangeEvent)
     */
    public void MappingEdited(MappingChangeEvent e) {
        ExportMapping mapping = (ExportMapping)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(mapping);
        if(tn != null) {
            nodeChanged(tn);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.mapping.ExportMappingChangeListener#MappingDeleted(alvahouse.eatool.repository.mapping.MappingChangeEvent)
     */
    public void MappingDeleted(MappingChangeEvent e) {
        ExportMapping mapping = (ExportMapping)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(mapping);
        if(tn != null) {
            removeNodeFromParent(tn);
            removeNodeOf(mapping);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Export Mappings";
    }
}
