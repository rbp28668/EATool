/*
 * MetaPropertyEditor.java
 *
 * Created on 03 February 2002, 08:20
 */

package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyType;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyTypes;

/**
 * Edit dialog to edit a Meta-property.
 * @author  rbp28668
 */
public class MetaPropertyEditor extends BasicDialog {

    private static final long serialVersionUID = 1L;

    /** The MetaProperty being edited */
    private MetaProperty mpEdit;

    /** Panel to edit the standard properties */
    private NamedRepositoryItemPanel nriPanel;
    
    /** Panel to edit meta-property specific properties */
    private PropertyPanel propPanel;

    /** Creates new form MetaPropertyEditor */
    public MetaPropertyEditor(Component parent, MetaProperty mp, Repository repository) throws Exception{
        super(parent,"Edit Meta-Property");
        init(mp, repository);
    }

    /** Creates new form MetaPropertyEditor */
    public MetaPropertyEditor(javax.swing.JDialog parent, MetaProperty mp, Repository repository) throws Exception{
        super(parent,"Edit Meta-Property"); 
        init(mp, repository);
    }

    
    /**
     * Initialises the editor.
     * @param mp is the MetaProperty to be edited.
     */
    private void init(MetaProperty mp, Repository repository) throws Exception {
        mpEdit = mp;

        nriPanel = new NamedRepositoryItemPanel(mp);
        getContentPane().add(nriPanel,BorderLayout.NORTH);
        
        getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);

        propPanel = new PropertyPanel(mp, repository);
        getContentPane().add(propPanel, BorderLayout.CENTER);
        
        pack();
        
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#onOK()
     */
    protected void onOK() {
        mpEdit.setName(nriPanel.getName());
        mpEdit.setDescription(nriPanel.getDescription());
        mpEdit.setMetaPropertyType(propPanel.getSelectedType());
        mpEdit.setMandatory(propPanel.isMandatory());
        mpEdit.setReadOnly(propPanel.isReadOnly());
        mpEdit.setSummary(propPanel.isSummary());
        mpEdit.setDefaultValue(propPanel.getDefault());
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#validateInput()
     */
    protected boolean validateInput() {
        return nriPanel.validateInput();
    }    
    
    /*=================================================================*/
    // PropertyPanel
    /*=================================================================*/
    private class PropertyPanel extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		PropertyPanel(MetaProperty mp, Repository repository) throws Exception {
            setLayout(new GridBagLayout());
            GridBagConstraints c;
            
            c = getConstraints();
            add(new JLabel("Type: "), c);
            
            c = getConstraints();
            c.gridx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            cmbTypes = new JComboBox();
            MetaPropertyTypes types = repository.getTypes();
            for(MetaPropertyType type : types.getTypes()){
                cmbTypes.addItem(type);
            }
            cmbTypes.setSelectedItem(mp.getMetaPropertyType());      
            
            // If a type is selected, we want to magic up a suitable default
            // value for this property from the type.
            cmbTypes.addItemListener( new ItemListener(){

                public void itemStateChanged(ItemEvent event) {
                    if(event.getStateChange() == ItemEvent.SELECTED){
                        MetaPropertyType type = (MetaPropertyType)cmbTypes.getSelectedItem();
                        String def = type.initialise();
                        txtDefault.setText(def);
                    }
                }
                
            });
            add(cmbTypes,c);
            
            c = getConstraints();
            c.gridy = 1;
            c.fill = GridBagConstraints.NONE;
            chkMandatory = new JCheckBox("Mandatory");
            chkMandatory.setSelected(mp.isMandatory());
            add(chkMandatory,c);

            c = getConstraints();
            c.gridy = 2;
            c.fill = GridBagConstraints.NONE;
            chkReadOnly = new JCheckBox("Read Only");
            chkReadOnly.setSelected(mp.isReadOnly());
            add(chkReadOnly,c);

            c = getConstraints();
            c.gridy = 3;
            c.fill = GridBagConstraints.NONE;
            chkSummary = new JCheckBox("Summary");
            chkSummary.setSelected(mp.isSummary());
            add(chkSummary,c);

            c = getConstraints();
            c.gridy = 4;
            add(new JLabel("Default: "), c);

            c = getConstraints();
            c.gridx = 1;
            c.gridy = 4;
            c.fill = GridBagConstraints.HORIZONTAL;
            txtDefault = new JTextField();
            txtDefault.setText(mp.getDefaultValue());
            add(txtDefault,c);
        }
        
        private GridBagConstraints getConstraints(){
            GridBagConstraints c = new GridBagConstraints();
            c.gridwidth = 1;
            c.gridheight = 1;
            c.gridx = 0;
            c.gridy = 0;
            return c;
        }
        
        public MetaPropertyType getSelectedType() {
            MetaPropertyType selected = (MetaPropertyType)cmbTypes.getSelectedItem();
            return selected;
        }
        
        public boolean isMandatory() {
            return chkMandatory.isSelected();
        }
        
        public boolean isReadOnly(){
            return chkReadOnly.isSelected();
        }
        
        public boolean isSummary(){
        	return chkSummary.isSelected();
        }
        
        public String getDefault() {
            return txtDefault.getText();
        }
        
        private JComboBox cmbTypes;
        private JCheckBox chkMandatory;
        private JCheckBox chkSummary;
        private JCheckBox chkReadOnly;
        private JTextField txtDefault;
    }
    
    
}
