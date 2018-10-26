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
import alvahouse.eatool.repository.metamodel.impl.MultiplicityImpl;

/**
 *
 * @author  rbp28668
 */
public class MetaRoleEditor extends BasicDialog {

    private static final long serialVersionUID = 1L;
    
    private RolePanel rolePanel;

    /** Creates new form MetaRoleEditor */
    public MetaRoleEditor(Component parent, MetaRole mr, Repository repository) {
        super(parent,"Edit Meta-Role");
        setLocationRelativeTo(parent);
        init(mr,repository);
    }
  
    private void init(MetaRole mr, Repository repository) {
        getContentPane().add( getOKCancelPanel(), BorderLayout.EAST);
        rolePanel = new RolePanel(mr, repository);
        getContentPane().add(rolePanel, BorderLayout.CENTER);
        pack();
    }

    protected void onOK() {
        rolePanel.updateRole();
    }
    
    protected boolean validateInput() {
        return rolePanel.validateInput();
    }
    
    /*=================================================================*/
    /* RolePanel
    /*=================================================================*/
    public static class RolePanel extends JPanel{
        private static final long serialVersionUID = 1L;
        private MetaRole role;
        private JComboBox cmbMultiplicity;
        private JComboBox cmbConnects;
        private NamedRepositoryItemPanel nriPanel;
        
        RolePanel(MetaRole mr, Repository repository) {
            role = mr;
            setLayout(new BorderLayout());
            setBorder(new TitledBorder("Role"));

            nriPanel = new NamedRepositoryItemPanel(mr);
            add(nriPanel,BorderLayout.CENTER);
            
            cmbMultiplicity = new JComboBox();
            cmbMultiplicity.setBorder(new TitledBorder("Multiplicity"));
            for(Multiplicity multiplicity : MultiplicityImpl.getValues()){
                cmbMultiplicity.addItem(multiplicity);
            }
            cmbMultiplicity.setSelectedItem(mr.getMultiplicity());
            
            cmbConnects = new JComboBox();
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
