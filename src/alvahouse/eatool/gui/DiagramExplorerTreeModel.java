package alvahouse.eatool.gui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.DiagramTypeFamily;
import alvahouse.eatool.repository.graphical.DiagramTypes;
import alvahouse.eatool.repository.graphical.Diagrams;
import alvahouse.eatool.repository.graphical.DiagramsChangeEvent;
import alvahouse.eatool.repository.graphical.DiagramsChangeListener;

/**
 * Builds the tree model that represents diagram types and their associated
 * diagrams (if any). Instances of this class is used as the model for a JTree or
 * its sub-classes (@see ExplorerTree).
 * @author bruce.porteous
 */
public class DiagramExplorerTreeModel extends ExplorerTreeModel 
implements DiagramsChangeListener{

    private static final long serialVersionUID = 1L;
    private MutableTreeNode root = null;
	private DiagramTypes diagramTypes = null;
	private Diagrams diagrams = null;
	
	/**
	 * Constructor for DiagramExplorerTreeModel.
	 * @param root
	 */
	public DiagramExplorerTreeModel(String rootTitle) {
        super(rootTitle);
        root = (MutableTreeNode)getRoot();
		root.setUserObject( new DiagramTypesRoot(rootTitle));
    }

	/**
	 * Sets the DiagramTypes and Diagrams for manipulation by this explorer.
	 * @param diagramTypes gives the types.
	 * @param diagrams gives the diagrams.
	 */
	public void setDiagrams(DiagramTypes diagramTypes, Diagrams diagrams) {
		this.diagramTypes = diagramTypes;
		this.diagrams = diagrams;
		diagramTypes.addChangeListener(this);
		diagrams.addChangeListener(this);
	}
	
	/**
	 * Method dispose.
	 */
    public void dispose() {
    	diagrams.removeChangeListener(this);
    	diagramTypes.removeChangeListener(this);
    }
    
    /** Call when the diagram types or set of diagrams changes to get the explorer to re-build
     * it's internal representation
     */
    public void refresh() {
       ((DefaultMutableTreeNode)getRoot()).removeAllChildren();
       reload();
       initModel();
     }

	/**
	 * Method initModel builds the tree model from the current set of
	 * diagram types and diagrams.
	 */
    private void initModel() {
 
        int idx = 0;
        for(DiagramTypeFamily dtf : diagramTypes.getDiagramTypeFamilies()) {
            addDiagramTypeFamilyNode((MutableTreeNode)getRoot(),dtf,idx++);
        }
    }
    
    
    /**
     * Inserts a node in the tree for a DiagramTypeFamily and its children.
     * @param parent is the parent node to insert under.
     * @param dtf is the DiagramTypeFactory to insert.
     * @param idx is the child position of the parent to insert the DiagramTypeFactory node at.
     */
    private void addDiagramTypeFamilyNode(MutableTreeNode parent, DiagramTypeFamily dtf, int idx) {
        DefaultMutableTreeNode tnFamily = new DefaultMutableTreeNode(dtf);
        insertNodeInto(tnFamily,parent,idx);
        tnFamily.setUserObject(dtf);
        registerNode(tnFamily,dtf);
        setFamilyNodeChildren(tnFamily, dtf);
    }
    
	/**
	 * Inserts the children of a DiagramTypeFactory node into the tree.
	 * @param parent is the parent node to add the children to.
	 * @param dtf is the parent DiagramTypeFactory.
	 */
	private void setFamilyNodeChildren(MutableTreeNode parent, DiagramTypeFamily dtf) {
        int idx = 0;
        
        for(DiagramType dt : dtf.getDiagramTypes()) {
            addDiagramTypeNode(parent,dt,idx++);
        }
    }

    
    /**
     * Adds a node for a DiagramType. 
     * @param parent is the parent node to add this DiagramType to. This node should reference the parent
     * DiagramTypeFamily.
     * @param dt is the DiagramType to add.
     * @param idxType is the position where this child node should be inserted in the parent.
     */
    private void addDiagramTypeNode(MutableTreeNode parent, DiagramType dt, int idxType) {
        DefaultMutableTreeNode tnDiagramType = new DefaultMutableTreeNode(dt);
        insertNodeInto(tnDiagramType,parent,idxType);
        tnDiagramType.setUserObject(dt);
        registerNode(tnDiagramType,dt);
        setDiagramTypeNodeChildren(tnDiagramType, dt);
    }

	/**
	 * Sets all the child nodes of a DiagramType node.
	 * @param parent is the parent node to add the children to. This should reference
	 * the parent DiagramType.
	 * @param dt is the DiagramType whose children should be added.
	 */
	private void setDiagramTypeNodeChildren(MutableTreeNode parent, DiagramType dt) {
        int idx = 0;
        
        String description = dt.getDescription();
        if(description != null && description.length() > 0){
            insertNodeInto(new DefaultMutableTreeNode("description: " + description),parent,idx++);
        }
        
        for(Diagram diagram : diagrams.getDiagramsOfType(dt)) {
        	addDiagramNode(parent,diagram,idx++);
        }
	}
	
    /**
     * Adds a node for a diagram.
     * @param parent is the parent node to add this under. The parent node should
     * reference the diagram type.
     * @param diagram is the Diagram to add.
     * @param idxType is the position to add this Diagram's node under its parent.
     */
    private void addDiagramNode(MutableTreeNode parent, Diagram diagram, int idxType) {
        DefaultMutableTreeNode tnDiagram = new DefaultMutableTreeNode(diagram.getName());
        insertNodeInto(tnDiagram,parent,idxType);
        tnDiagram.setUserObject(diagram);
		diagram.addChangeListener(this);
        registerNode(tnDiagram,diagram);
        
        int idx = 0;
        
        String description = diagram.getDescription();
        if(description != null && description.length() > 0){
            insertNodeInto(new DefaultMutableTreeNode("description: " + description), tnDiagram, idx++);
        }
        
        if(diagram.isDynamic()){
            insertNodeInto(new DefaultMutableTreeNode("dynamic: true"), tnDiagram, idx++);
        }

    }

	/**
	 * Refreshes a node corresponding to a DiagramType after the DiagramType
	 * has been edited or its children changed.
	 * @param node is the node corresponding to the given DiagramType.
	 * @param dt is the DiagramType that has been changed.
	 */
	private void refreshNode(DefaultMutableTreeNode node, DiagramType dt) {
		node.removeAllChildren();
		setDiagramTypeNodeChildren(node, dt);
		nodeStructureChanged(node);
	}

	/**
	 * Refreshes a node corresponding to a Diagram after the Diagram
	 * has been edited or its children changed.
	 * @param node is the node corresponding to the given Diagram.
	 * @param diag is the Diagram that has been changed.
	 */
	private void refreshNode(DefaultMutableTreeNode node, Diagram diag) {
		nodeChanged(node);
	}
	
	/** Class to form the root of the tree */
	private static class DiagramTypesRoot{
		private String title;
		DiagramTypesRoot(String title) {
			this.title = title;
		}
		
		public String toString() {
			return title;
		}
	}
	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.DiagramsChangeListener#typesUpdated(alvahouse.eatool.gui.graphical.DiagramsChangeEvent)
	 */
	public void typesUpdated(DiagramsChangeEvent e) {
		refresh();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.DiagramsChangeListener#diagramTypeAdded(alvahouse.eatool.gui.graphical.DiagramsChangeEvent)
	 */
	public void diagramTypeAdded(DiagramsChangeEvent e) {
		DiagramType dt = (DiagramType)e.getSource();
		DiagramTypeFamily family = dt.getFamily();
		DefaultMutableTreeNode parent = lookupNodeOf(family);
		if(parent != null) {
			int idxType = parent.getChildCount();
			addDiagramTypeNode(parent, dt, idxType); 
		}
		
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.DiagramsChangeListener#diagramTypeChanged(alvahouse.eatool.gui.graphical.DiagramsChangeEvent)
	 */
	public void diagramTypeChanged(DiagramsChangeEvent e) {
		DiagramType dt = (DiagramType)e.getSource();
		DefaultMutableTreeNode tn = lookupNodeOf(dt);
		if(dt != null) {
		    refreshNode(tn,dt);
		}
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.DiagramsChangeListener#diagramTypeDeleted(alvahouse.eatool.gui.graphical.DiagramsChangeEvent)
	 */
	public void diagramTypeDeleted(DiagramsChangeEvent e) {
		DiagramType dt = (DiagramType)e.getSource();
		DefaultMutableTreeNode tn = lookupNodeOf(dt);
		if(tn != null) {
			removeNodeFromParent(tn);
			removeNodeOf(dt);
		}
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.DiagramsChangeListener#diagramsUpdated(alvahouse.eatool.gui.graphical.DiagramsChangeEvent)
	 */
	public void diagramsUpdated(DiagramsChangeEvent e) {
		refresh();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.DiagramsChangeListener#diagramAdded(alvahouse.eatool.gui.graphical.DiagramsChangeEvent)
	 */
	public void diagramAdded(DiagramsChangeEvent e) {
		Diagram diag = (Diagram)e.getSource();
		DiagramType dt = diag.getType();
		MutableTreeNode parent = lookupNodeOf(dt); 
		if(parent != null) {
			int idxType = parent.getChildCount();
			addDiagramNode(parent, diag, idxType); 
		}
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.DiagramsChangeListener#diagramChanged(alvahouse.eatool.gui.graphical.DiagramsChangeEvent)
	 */
	public void diagramChanged(DiagramsChangeEvent e) {
		Diagram diag = (Diagram)e.getSource();
		DefaultMutableTreeNode tn = lookupNodeOf(diag);
		if(tn != null) refreshNode(tn,diag);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.DiagramsChangeListener#diagramDeleted(alvahouse.eatool.gui.graphical.DiagramsChangeEvent)
	 */
	public void diagramDeleted(DiagramsChangeEvent e) {
		Diagram diag = (Diagram)e.getSource();
		DefaultMutableTreeNode tn = lookupNodeOf(diag);
		if(tn != null) {
			removeNodeFromParent(tn);
			removeNodeOf(diag);
		}
	};
}
