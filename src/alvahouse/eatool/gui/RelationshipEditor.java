package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.Relationship;

/**
 * Dialog class to allow editing of a relationship.  For editing the 
 * relationship is cloned and the copy is edited.  If the user clicks
 * on OK then the original relationship is updated from the (edited)
 * copy.
 * @author bruce.porteous
 */
public class RelationshipEditor extends BasicDialog {

    private static final long serialVersionUID = 1L;

    /** The original relationship to be edited */
    private Relationship originalRelationship;
    
    /** Panel for handling relationship edit - presented in the relationships tab */
    private RelationshipPanel relationshipPanel;
    
    /** */
    private Model model;
    
	/**
	 * Constructor for RelationshipEditor.
	 * @param parent
	 * @param title
	 */
    public RelationshipEditor(Component parent, Relationship r, Model model) {
		super(parent, "Edit Relationship");
		this.model = model;
		
        originalRelationship = r;
        
        JLabel label = new JLabel("uuid: " + r.getKey().toString());
        label.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        getContentPane().add(label, BorderLayout.NORTH);
        
        relationshipPanel = new RelationshipPanel(originalRelationship);
        
        getContentPane().add(relationshipPanel, BorderLayout.CENTER);
        getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
        pack();
	}

	/**
	 * @see alvahouse.eatool.GUI.BasicDialog#onOK()
	 */
	protected void onOK() {
    	relationshipPanel.onOK();
 	}

	/**
	 * @see alvahouse.eatool.GUI.BasicDialog#validateInput()
	 */
	protected boolean validateInput() {
		return relationshipPanel.validateInput();
	}

    /*=================================================================*/
    // RelationshipPanel is the editing panel for the Relationship.
    // Relationships are fairly constrained - all we can change is 
    // which entities the start or finish connections connect to.
    /*=================================================================*/
    private class RelationshipPanel extends JPanel{
		
        private static final long serialVersionUID = 1L;

        /** For selecting which entity the start of the relationship connects to.*/
		JComboBox cmbStart;

		/** For selecting which entity the finish of the relationship connects to.*/
		JComboBox cmbFinish;
		
		/** The relationship being edited */
		Relationship relationship;
		
		/**
		 * Method RelationshipsPanel.
		 * @param e
		 */
        RelationshipPanel(Relationship r) {

			relationship = r;
			
			Model model = originalRelationship.getModel();
			
            MetaEntity startEntityType = r.start().getMeta().connectsTo();
            MetaEntity finishEntityType = r.finish().getMeta().connectsTo();
        	
            cmbStart = new JComboBox();
            cmbStart.setBorder(new TitledBorder("Start Connects to"));
            
            for(Entity e : model.getEntitiesOfType(startEntityType)){
                cmbStart.addItem(e);
            }
            
            cmbStart.setSelectedItem(r.start().connectsTo());

            cmbFinish = new JComboBox();
            cmbFinish.setBorder(new TitledBorder("Finish Connects to"));
            
            for(Entity e : model.getEntitiesOfType(finishEntityType)){
                cmbFinish.addItem(e);
            }
            
            cmbFinish.setSelectedItem(r.finish().connectsTo());
        	
            Box box = Box.createVerticalBox();
            box.add(cmbStart);
            box.add(cmbFinish);
            box.add(Box.createVerticalGlue());
            add(box, BorderLayout.EAST);
        }
        
		/**
		 * Method onOK.
		 */
        void onOK() {
        	relationship.start().setConnection((Entity)cmbStart.getSelectedItem());
        	relationship.finish().setConnection((Entity)cmbFinish.getSelectedItem());
        }
        
		/**
		 * validateInput determines whether the input is valid and the user can
		 * safely click on OK - in this case, are both relationship ends
		 * connected to something.
		 * @return boolean
		 */
       	boolean validateInput() {

			return cmbStart.getSelectedItem() != null 
			&& cmbFinish.getSelectedItem() != null;
       	}

    }

}
