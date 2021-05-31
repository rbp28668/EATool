/*
 * PropertyMappingDialog.java
 * Project: EATool
 * Created on 22-Dec-2005
 *
 */
package alvahouse.eatool.gui.mappings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import alvahouse.eatool.gui.BasicDialog;
import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.repository.mapping.PropertyTranslation;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaProperty;

/**
 * PropertyMappingDialog
 * 
 * @author rbp28668
 */
public class PropertyMappingDialog extends BasicDialog {

    private static final long serialVersionUID = 1L;
    private PropertyTranslation mapping;
    private MetaEntity parentEntity;
    private MainPanel panel;

    /**
     * @param parent
     * @param title
     */
    public PropertyMappingDialog(JDialog parent, String title, MetaEntity parentEntity, PropertyTranslation mapping) throws Exception {
        super(parent, title);
        init(parentEntity, mapping);
    }

    /**
     * @param parent
     * @param title
     */
    public PropertyMappingDialog(Component parent, String title, MetaEntity parentEntity, PropertyTranslation mapping) throws Exception{
        super(parent, title);
        init(parentEntity, mapping);
    }

    /**
     * The main initialiser for the dialog.
     * @param mapping is the EntityTranslation to edit.
     */
    private void init(MetaEntity parentEntity, PropertyTranslation mapping) throws Exception{
        if(mapping == null){
            throw new NullPointerException("Can't edit null property translation");
        }
        this.parentEntity = parentEntity;
        this.mapping = mapping;
        
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
        mapping.setTypeName(panel.getTypeName());
        mapping.setKeyValue(panel.isKey());
        mapping.setMeta(panel.getMeta());
     }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#validateInput()
     */
    protected boolean validateInput() {
        return panel.validateInput();
    }

    private class MainPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        private JTextField txtName;
        private JCheckBox keySel;
        private JComboBox<MetaProperty> propertySel;

        /**
         * Create a new MainPanel.
         */
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

            label = new JLabel("Key Property ");
            c.gridwidth = GridBagConstraints.RELATIVE;
            gridbag.setConstraints(label,c);
            add(label);

            keySel = new JCheckBox();
            keySel.setSelected(mapping.isKeyValue());
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(keySel,c);
            add(keySel);
            
            label = new JLabel("Maps to Property ");
            c.gridwidth = GridBagConstraints.RELATIVE;
            gridbag.setConstraints(label,c);
            add(label);

            propertySel = new JComboBox<MetaProperty>(parentEntity.getMetaPropertiesAsArray());
            propertySel.setSelectedItem(mapping.getMeta());
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(propertySel,c);
            add(propertySel);
            
        }
        
        /**
         * Sees if the key checkbox is set.
         * @return true if this translation is marked as a key translation.
         */
        public boolean isKey() {
            return keySel.isSelected();
        }

        /**
         * Gets the type name entered by the user.
         * @return String type name.
         */
        public String getTypeName() {
            return txtName.getText().trim();
        }

        /**
         * Gets the currently selected MetaProperty.
         * @return the selected MetaProperty - may be null.
         */
        public MetaProperty getMeta() {
            return (MetaProperty)propertySel.getSelectedItem();
        }
        
        /**
         * Validates the current input: the type name must be entered and
         * the connected MetaEntity selected.
         * @return true if valid, false if not.
         */
        boolean validateInput() {
            String name = getTypeName(); 
            if(name.length()==0){
                Dialogs.warning(this,"Please enter the input property name");
                txtName.requestFocus();
                return false;
            }
            if(getMeta() == null){
                Dialogs.warning(this,"Please select the property this maps to");
                propertySel.requestFocus();
                return false;
            }
            return true;
        }
      
    }
}
