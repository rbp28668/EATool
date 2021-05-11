/*
 * RelationshipMappingDialog.java
 * Project: EATool
 * Created on 10-Dec-2005
 *
 */
package alvahouse.eatool.gui.mappings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import alvahouse.eatool.gui.BasicDialog;
import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.repository.mapping.RelationshipTranslation;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaRelationship;

/**
 * RelationshipMappingDialog
 * 
 * @author rbp28668
 */
public class RelationshipMappingDialog extends BasicDialog {

    private static final long serialVersionUID = 1L;
    private RelationshipTranslation mapping;
    private MainPanel panel;
    private MetaModel metaModel;

    
    /**
     * @param parent
     * @param title
     */
    public RelationshipMappingDialog(JDialog parent, String title, RelationshipTranslation mapping, MetaModel metaModel)  throws Exception{
        super(parent, title);
        init(mapping, metaModel);
    }

    /**
     * @param parent
     * @param title
     */
    public RelationshipMappingDialog(Component parent, String title, RelationshipTranslation mapping, MetaModel metaModel)  throws Exception{
        super(parent, title);
        init(mapping,metaModel);
    }

    /**
     * @param mapping
     */
    private void init(RelationshipTranslation mapping, MetaModel metaModel)  throws Exception{
        if(mapping == null) {
            throw new NullPointerException("Can't edit a null RelationshipTranslation");
        }
        this.mapping = mapping;
        this.metaModel = metaModel;
        
        setLayout(new java.awt.BorderLayout());
        
        panel = new MainPanel();
        add(panel,BorderLayout.CENTER);
        
        add(getOKCancelPanel(),BorderLayout.EAST);
        pack();

    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#onOK()
     */
    protected void onOK() {
        MetaRelationship selected = panel.getSelectedMetaRelationship(); 
        mapping.setMeta(selected);
        mapping.setType(panel.getType());
        
        mapping.getStart().setMeta(selected.start());
        mapping.getStart().setType(panel.getStartType());

        mapping.getFinish().setMeta(selected.finish());
        mapping.getFinish().setType(panel.getFinishType());
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#validateInput()
     */
    protected boolean validateInput() {
        return panel.validateInput();
    }

    /**
     * MainPanel provides the main editing panel for the dialog.
     * 
     * @author rbp28668
     */
    private class MainPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        private JTextField txtName;
        private JComboBox relationshipSel;
        private JLabel startLabel;
        private JLabel finishLabel;
        private JTextField txtStart;
        private JTextField txtFinish;

        MainPanel() throws Exception{
	        GridBagLayout gridbag = new GridBagLayout();
	        setLayout(gridbag);
	        GridBagConstraints c = new GridBagConstraints();
	        c.gridwidth = GridBagConstraints.REMAINDER; //end row
	        c.anchor = GridBagConstraints.WEST;//LINE_START;
	        c.insets = new Insets(5,5,5,5);
	        
	        JLabel label;

            label = new JLabel("Import Name");
            c.gridwidth = GridBagConstraints.RELATIVE;
            gridbag.setConstraints(label,c);
            add(label);
            
            txtName = new JTextField(mapping.getTypeName());
            txtName.setColumns(40);
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(txtName,c);
            add(txtName);

            label = new JLabel("Maps to Relationship");
            c.gridwidth = GridBagConstraints.RELATIVE;
            gridbag.setConstraints(label,c);
            add(label);

            relationshipSel = new JComboBox(metaModel.getMetaRelationshipsAsArray());
            relationshipSel.setSelectedItem(mapping.getMeta());
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(relationshipSel,c);
            add(relationshipSel);
	        
            // Do roles.
	        startLabel = new JLabel("Name for start role");
            c.gridwidth = GridBagConstraints.RELATIVE;
            gridbag.setConstraints(startLabel,c);
            add(startLabel);
            
            txtStart = new JTextField(mapping.getStart().getTypeName());
            txtStart.setColumns(40);
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(txtStart,c);
            add(txtStart);

            finishLabel = new JLabel("Name for finish role");
            c.gridwidth = GridBagConstraints.RELATIVE;
            gridbag.setConstraints(finishLabel,c);
            add(finishLabel);
            
            txtFinish = new JTextField(mapping.getFinish().getTypeName());
            txtFinish.setColumns(40);
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(txtFinish,c);
            add(txtFinish);
            
            updateRoles();
            
            relationshipSel.addActionListener( new ActionListener() {

                public void actionPerformed(ActionEvent arg0) {
                    txtStart.setText("");
                    txtFinish.setText("");
                    updateRoles();
                }
                
            });
        }

        private void updateRoles(){
            MetaRelationship mr = (MetaRelationship)relationshipSel.getSelectedItem();
            if(mr != null){
                startLabel.setText("Name for start " + mr.start().getName());
                finishLabel.setText("Name for finish " + mr.finish().getName());
            }
            pack();
        }
        
        String getType(){
            return txtName.getText().trim();
        }
        
        MetaRelationship getSelectedMetaRelationship(){
            return (MetaRelationship)relationshipSel.getSelectedItem();
        }
        
        String getStartType() {
            return txtStart.getText().trim();
        }

        String getFinishType() {
            return txtFinish.getText().trim();
        }

        boolean validateInput(){
            if(getType().length() == 0){
                Dialogs.warning(this, "Please enter the input type for the relationship");
                txtName.requestFocus();
                return false;
            }

            if(getSelectedMetaRelationship() == null){
                Dialogs.warning(this, "Please select the relationship type this maps to");
                relationshipSel.requestFocus();
                return false;
            }
            
            if(getStartType().length() == 0){
                Dialogs.warning(this, "Please enter the input type for the start role");
                txtStart.requestFocus();
                return false;
            }

            if(getFinishType().length() == 0){
                Dialogs.warning(this, "Please enter the input type for the finish role");
                txtFinish.requestFocus();
                return false;
            }
            
            return true;
        }
    }
}
