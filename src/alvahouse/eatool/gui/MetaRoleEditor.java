/*
 * MetaRoleEditor.java
 *
 * Created on 09 February 2002, 14:07
 */

package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.repository.metamodel.Multiplicity;

/**
 *
 * @author  rbp28668
 */
public class MetaRoleEditor extends BasicDialog {

    private static final long serialVersionUID = 1L;
    
    private MetaRole mrOriginal;
    private RolePanel rolePanel;
    private MetaPropertiesPanel propertiesPanel;
    
    /** Creates new form MetaRoleEditor */
    public MetaRoleEditor(Component parent, MetaRole mr, Repository repository)  throws Exception{
        super(parent,"Edit Meta-Role");
        mrOriginal = mr;
        setLocationRelativeTo(parent);
        init(mr,repository);
    }
  
    private void init(MetaRole mr, Repository repository)  throws Exception{
        getContentPane().add( getOKCancelPanel(), BorderLayout.EAST);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        
        rolePanel = new RolePanel(mr, repository);
        mainPanel.add(rolePanel, BorderLayout.CENTER);
        propertiesPanel = new MetaPropertiesPanel(mr, repository, this);
        mainPanel.add(propertiesPanel, BorderLayout.SOUTH);
        
        pack();
    }

    protected void onOK() {
        rolePanel.updateRole();
        mrOriginal.setDeclaredMetaProperties(propertiesPanel.getMetaProperties());
    }
    
    protected boolean validateInput() {
        return rolePanel.validateInput()
        		&& propertiesPanel.validateInput();
    }
    
    /*=================================================================*/
    /* RolePanel
    /*=================================================================*/
    public static class RolePanel extends JPanel{
        private static final long serialVersionUID = 1L;
        private MetaRole role;
        private JComboBox<Multiplicity> cmbMultiplicity;
        private JComboBox<MetaEntity> cmbConnects;
        private NamedRepositoryItemPanel nriPanel;
        
        RolePanel(MetaRole mr, Repository repository)  throws Exception{
            role = mr;
            setLayout(new BorderLayout());
            setBorder(new TitledBorder("Role"));

            nriPanel = new NamedRepositoryItemPanel(mr);
            add(nriPanel,BorderLayout.CENTER);
            
            cmbMultiplicity = new JComboBox<Multiplicity>();
            cmbMultiplicity.setBorder(new TitledBorder("Multiplicity"));
            for(Multiplicity multiplicity : Multiplicity.getValues()){
                cmbMultiplicity.addItem(multiplicity);
            }
            cmbMultiplicity.setSelectedItem(mr.getMultiplicity());
            
            cmbConnects = new JComboBox<MetaEntity>();
            cmbConnects.setBorder(new TitledBorder("Connects to"));
            
            Collection<MetaEntity> metaEntities = repository.getMetaModel().getMetaEntities();
            for(MetaEntity me : metaEntities){
                if(!me.isAbstract()) {
                    cmbConnects.addItem(me);
                }
            }
            cmbConnects.setSelectedItem(mr.connectsTo());
            
            cmbConnects.addItemListener( new ItemListener() {
                 public void itemStateChanged(ItemEvent e) {
                     if((e.getStateChange() == ItemEvent.SELECTED) &&
                         (nriPanel.getName().length() == 0)) {
                         MetaEntity me = (MetaEntity)e.getItem();
                         nriPanel.setName(me.getName());
                     }
                 }
           
            });
            
            Box box = Box.createVerticalBox();
            box.add(cmbMultiplicity);
            box.add(cmbConnects);
            box.add(Box.createVerticalGlue());
            add(box, BorderLayout.EAST);
        }
        
        public void updateRole() {
            role.setName(nriPanel.getName());
            role.setDescription(nriPanel.getDescription());
            role.setMultiplicity((Multiplicity)cmbMultiplicity.getSelectedItem());
            role.setConnection((MetaEntity)cmbConnects.getSelectedItem());
        }
        
        /** validates values in the panel
         * @return true if input is valid, false if invalid
         **/
        public boolean validateInput() {
            if(!nriPanel.validateInput())
                return false;

            if(cmbConnects.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select a value for the connected entity", 
                    "EATool", JOptionPane.INFORMATION_MESSAGE);
                cmbConnects.requestFocus();
                return false;
            }
            return true;
        }
        
        /** adds an item listener to the combo-box so that enclosing
         * classes can pick up when the connection is changed.  Intended
         * for auto-naming of relationships.
         * @param il is the listener to add
         */
        public void addConnectionChangeItemListener(ItemListener il) {
            cmbConnects.addItemListener(il);
        }
        
        /** gets the selected connection to enable auto-naming 
         * @return the selected connection (may be null)
         */
        public MetaEntity getSelectedConnection() {
            return (MetaEntity)cmbConnects.getSelectedItem();
        }
    }


}
