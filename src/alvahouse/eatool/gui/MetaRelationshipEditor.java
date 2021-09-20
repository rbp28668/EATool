/*
 * MetaRelationshipEditor.java
 *
 * Created on 03 February 2002, 08:21
 */

package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JPanel;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;

/**
 * Editor for a MetaRelationship. This edits the meta relationship "in place" and does
 * not explicitly update the meta model.
 * @author  rbp28668
 */
public class MetaRelationshipEditor extends BasicDialog {

    private MetaRelationship mrOriginal; // and the original to be updated if ok
    private NamedRepositoryItemPanel nriPanel;
    private RolesPanel rolesPanel;
    private MetaPropertiesPanel propertiesPanel;

    private static final long serialVersionUID = 1L;
    private class AutoName implements ItemListener {
         public void itemStateChanged(ItemEvent e) {
             if((e.getStateChange() == ItemEvent.SELECTED) &&
                 (nriPanel.getName().length() == 0)) {
                 MetaEntity meStart = rolesPanel.getStartConnection();
                 MetaEntity meFinish = rolesPanel.getFinishConnection();
                 if((meStart != null) && (meFinish != null)) {
                    nriPanel.setName(meStart.getName() + "2" + meFinish.getName());
                 }
             }
         }
    }
           
    /** Creates new form MetaRelationshipEditor */
    public MetaRelationshipEditor(Component parent, MetaRelationship mr , Repository repository)  throws Exception{
        super(parent,"Edit Meta-Relationship"); 

        mrOriginal = mr;

        getContentPane().add(getOKCancelPanel(),BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        
        nriPanel = new NamedRepositoryItemPanel(mr);
        mainPanel.add(nriPanel,BorderLayout.NORTH);


        rolesPanel = new RolesPanel(mr, repository);
        mainPanel.add(rolesPanel,BorderLayout.CENTER);
        rolesPanel.addConnectionChangeListener(new AutoName());
        
        propertiesPanel = new MetaPropertiesPanel(mr, repository, this);
        mainPanel.add(propertiesPanel,BorderLayout.SOUTH);
        
        pack();
    }

    protected void onOK() {
        mrOriginal.setName(nriPanel.getName());
        mrOriginal.setDescription(nriPanel.getDescription());
        rolesPanel.updateRoles();
        mrOriginal.setDeclaredMetaProperties(propertiesPanel.getMetaProperties());
    }
    
    protected boolean validateInput() {
        return nriPanel.validateInput()
        	&& rolesPanel.validateInput()
        	&& propertiesPanel.validateInput();
    }
    
    /*=================================================================*/
    // RolesPanel
    /*=================================================================*/
    private class RolesPanel extends JPanel{
        private static final long serialVersionUID = 1L;
        private MetaRoleEditor.RolePanel rpStart;
        private MetaRoleEditor.RolePanel rpFinish;

        public RolesPanel(MetaRelationship mr, Repository repository)  throws Exception{
            setLayout(new GridLayout(2,1));
            rpStart = new MetaRoleEditor.RolePanel(mr.start(),repository);
            rpFinish = new MetaRoleEditor.RolePanel(mr.finish(),repository);
            add(rpStart);
            add(rpFinish);
        }
        
        public void updateRoles() {
            rpStart.updateRole();
            rpFinish.updateRole();
        }
        
        public boolean validateInput() {
            return rpStart.validateInput() && rpFinish.validateInput();
        }
        
        public MetaEntity getStartConnection() {
            return rpStart.getSelectedConnection();
        }
        
        public MetaEntity getFinishConnection() {
            return rpFinish.getSelectedConnection();
        }
        
        public void addConnectionChangeListener(ItemListener il) {
            rpStart.addConnectionChangeItemListener(il);
            rpFinish.addConnectionChangeItemListener(il);
        }
        
    }

    
 }
