/*
 * EntityMappingDialog.java
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
import java.util.Collection;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import alvahouse.eatool.gui.BasicDialog;
import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.repository.mapping.EntityTranslation;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;

/**
 * EntityMappingDialog edits an EntityTranslation import mapping.  Note that this
 * does not edit any child property mappings.
 * 
 * @author rbp28668
 */
public class EntityMappingDialog extends BasicDialog {

    private static final long serialVersionUID = 1L;
    private EntityTranslation mapping;
    private MainPanel panel;
    
    /**
     * Creates a new EntityMappingDialog with another JDialog as parent.
     * @param parent is the parent dialog.
     * @param title is the title for the dialog.
     * @param mapping is the EntityTranslation to edit.
     */
    public EntityMappingDialog(JDialog parent, String title, EntityTranslation mapping, MetaModel metaModel) throws Exception {
        super(parent, title);
        init(mapping, metaModel);
    }

    /**
     * Creates a new EntityMappingDialog with a Component as parent.
     * @param parent is the parent Component.
     * @param title is the title for the dialog.
     * @param mapping is the EntityTranslation to edit.
     */
    public EntityMappingDialog(Component parent, String title, EntityTranslation mapping, MetaModel metaModel)  throws Exception{
        super(parent, title);
        init(mapping, metaModel);
    }
    
    /**
     * The main initialiser for the dialog.
     * @param mapping is the EntityTranslation to edit.
     */
    private void init(EntityTranslation mapping, MetaModel metaModel)  throws Exception{
        if(mapping == null){
            throw new NullPointerException("Can't edit null entity translation");
        }
        this.mapping = mapping;
        
        setLayout(new java.awt.BorderLayout());
        
        panel = new MainPanel(metaModel);
        add(panel,BorderLayout.CENTER);
        
        add(getOKCancelPanel(),BorderLayout.EAST);
        pack();
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#onOK()
     */
    protected void onOK() {
        mapping.setType(panel.getType());
        mapping.setMeta(panel.getSelectedMetaEntity());
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#validateInput()
     */
    protected boolean validateInput() {
        return panel.validateInput();
    }

    /**
     * MainPanel is an inner class to display the main panel of the dialog.
     * 
     * @author rbp28668
     */
    private class MainPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        private JTextField txtName;
        private JComboBox entitySel;

        /**
         * Create a new MainPanel.
         */
        MainPanel(MetaModel metaModel) throws Exception{
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

            label = new JLabel("Maps to Entity ");
            c.gridwidth = GridBagConstraints.RELATIVE;
            gridbag.setConstraints(label,c);
            add(label);

            Collection<MetaEntity> mes = metaModel.getMetaEntities();
            entitySel = new JComboBox(mes.toArray(new MetaEntity[mes.size()]));
            entitySel.setSelectedItem(mapping.getMeta());
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(entitySel,c);
            add(entitySel);
            
        }
        
        /**
         * Validates the current input: the type name must be entered and
         * the connected MetaEntity selected.
         * @return true if valid, false if not.
         */
        boolean validateInput() {
            String name = txtName.getText().trim(); 
            if(name.length()==0){
                Dialogs.warning(this,"Please enter the input entity name");
                txtName.requestFocus();
                return false;
            }
            if(entitySel.getSelectedItem() == null){
                Dialogs.warning(this,"Please select the entity this is mapped to");
                entitySel.requestFocus();
                return false;
            }
            return true;
        }
        
        /**
         * Gets the entered type name.
         * @return the type name.
         */
        String getType() {
            return txtName.getText().trim(); 
        }
        
        /**
         * Gets the selected MetaEntity.
         * @return the selected MetaEntity, may be null if none selected.
         */
        MetaEntity getSelectedMetaEntity(){
            return (MetaEntity)entitySel.getSelectedItem();
        }
    }
}
