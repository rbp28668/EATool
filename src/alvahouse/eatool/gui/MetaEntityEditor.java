/*
 * MetaEntityEditor.java
 *
 * Created on 29 January 2002, 08:03
 */

package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
/**
 *
 * @author  rbp28668
 */
public class MetaEntityEditor extends BasicDialog {

    private static final long serialVersionUID = 1L;

    private MetaEntity meOriginal;  // original MetaEntity
    private Repository repository;
    
    private NamedRepositoryItemPanel nriPanel;
    private InheritancePanel inhPanel;
    private MetaPropertiesPanel propPanel;

    /** Creates new form MetaEntityEditor */
    public MetaEntityEditor(Component parent, MetaEntity me, Repository repository)  throws Exception{
        super(parent ,"Edit Meta-Entity");

        meOriginal = me;
        this.repository = repository;

        nriPanel = new NamedRepositoryItemPanel(me);
        getContentPane().add(nriPanel,BorderLayout.NORTH);
        
        inhPanel = new InheritancePanel(me);
        getContentPane().add(inhPanel,BorderLayout.CENTER);

        propPanel = new MetaPropertiesPanel(me, repository, this);
        
        JPanel pnlSouth = new JPanel();
        pnlSouth.setLayout(new BorderLayout());
        pnlSouth.add(propPanel, BorderLayout.CENTER);
        pnlSouth.add(getOKCancelPanel(), BorderLayout.EAST);

        getContentPane().add(pnlSouth, BorderLayout.SOUTH);
        pack();
    }
    
    protected void onOK() {
        meOriginal.setName(nriPanel.getName());
        meOriginal.setDescription(nriPanel.getDescription());
        meOriginal.setAbstract(inhPanel.isAbstract());
        if(inhPanel.isExtending())
            meOriginal.setBase(inhPanel.getExtends());
        else
            meOriginal.setBase(null);
        meOriginal.setDeclaredMetaProperties(propPanel.getMetaProperties());
    }
    
    protected boolean validateInput() {
        if(!nriPanel.validateInput()) {
            return false;
        }
        if(!inhPanel.validateInput())
            return false;
        if(!propPanel.validateInput())
            return false;
        return true;    
    }
    
    /*=================================================================*/
    /*=================================================================*/
    private class InheritancePanel extends JPanel{
         private static final long serialVersionUID = 1L;
         
        InheritancePanel(MetaEntity me)  throws Exception{
            setLayout(new BorderLayout());
            setBorder(new TitledBorder("Inheritance"));
            
            chkAbstract = new JCheckBox("Abstract");
            chkAbstract.setSelected(me.isAbstract());
            add(chkAbstract,BorderLayout.WEST);
            
            chkExtends = new JCheckBox("Extends");
            chkExtends.setSelected(me.getBase() != null);
            add(chkExtends, BorderLayout.CENTER);
            
            cmbExtends = new JComboBox<MetaEntity>();
            MetaModel mm = repository.getMetaModel();
            for(MetaEntity temp : mm.getMetaEntities()){
                cmbExtends.addItem(temp);
            }
            cmbExtends.setEnabled(me.getBase() != null);
            if(me.getBase() != null) {
                cmbExtends.setSelectedItem(me.getBase());
            }
            add(cmbExtends,BorderLayout.EAST);
            
            chkExtends.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent evt) {
                    cmbExtends.setEnabled(chkExtends.isSelected());
                }
            });

        }
        public boolean isAbstract() {
            return chkAbstract.isSelected();
        }

        public boolean isExtending() {
            return chkExtends.isSelected();
        }

        public MetaEntity getExtends() {
            return (MetaEntity)cmbExtends.getSelectedItem();
        }

        public boolean validateInput() {
            if(isExtending()) {
                if(getExtends() == null) {
                    JOptionPane.showMessageDialog(this,
                        "Please select a value for the base meta-entity", 
                        "EATool", JOptionPane.INFORMATION_MESSAGE);
                    cmbExtends.requestFocus();
                    return false;
                }
            }
            return true;
        }
        private javax.swing.JCheckBox chkAbstract;
        private javax.swing.JCheckBox chkExtends;
        private javax.swing.JComboBox<MetaEntity> cmbExtends;
    }

    
}
