/*
 * MetaEntityEditor.java
 *
 * Created on 29 January 2002, 08:03
 */

package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.impl.MetaPropertyImpl;
import alvahouse.eatool.util.UUID;
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
    private PropertiesPanel propPanel;

    /** Creates new form MetaEntityEditor */
    public MetaEntityEditor(Component parent, MetaEntity me, Repository repository) {
        super(parent ,"Edit Meta-Entity");

        meOriginal = me;
        this.repository = repository;

        nriPanel = new NamedRepositoryItemPanel(me);
        getContentPane().add(nriPanel,BorderLayout.NORTH);
        
        inhPanel = new InheritancePanel(me);
        getContentPane().add(inhPanel,BorderLayout.CENTER);

        propPanel = new PropertiesPanel(me);
        
        JPanel pnlSouth = new JPanel();
        pnlSouth.setLayout(new BorderLayout());
        pnlSouth.add(propPanel, BorderLayout.CENTER);
        pnlSouth.add(getOKCancelPanel(), BorderLayout.EAST);

        getContentPane().add(pnlSouth, BorderLayout.SOUTH);
        pack();
    }
    
    private MetaEntityEditor getDialog() {
        return this;
    }
    
    protected void onOK() {
        meOriginal.setName(nriPanel.getName());
        meOriginal.setDescription(nriPanel.getDescription());
        meOriginal.setAbstract(inhPanel.isAbstract());
        if(inhPanel.isExtending())
            meOriginal.setBase(inhPanel.getExtends());
        else
            meOriginal.setBase(null);
        meOriginal.setMetaProperties(propPanel.getMetaProperties());
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
        InheritancePanel(MetaEntity me) {
            setLayout(new BorderLayout());
            setBorder(new TitledBorder("Inheritance"));
            
            chkAbstract = new JCheckBox("Abstract");
            chkAbstract.setSelected(me.isAbstract());
            add(chkAbstract,BorderLayout.WEST);
            
            chkExtends = new JCheckBox("Extends");
            chkExtends.setSelected(me.getBase() != null);
            add(chkExtends, BorderLayout.CENTER);
            
            cmbExtends = new JComboBox();
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
        private javax.swing.JComboBox cmbExtends;
    }
    
    /*=================================================================*/
    // Properties Panel
    /*=================================================================*/
    private class PropertiesPanel extends JPanel {
        
        private static final long serialVersionUID = 1L;
        private JList lstProperties;
        private JButton btnEditProperty;
        private JButton btnNewProperty;
        private JButton btnDeleteProperty;
        private JButton btnMoveUp;
        private JButton btnMoveDown;

        PropertiesPanel(MetaEntity me) {
        
            setBorder(new TitledBorder("Properties"));
            setLayout(new BorderLayout());
            
            btnEditProperty = new JButton("Edit");
            btnNewProperty = new JButton("New");
            btnDeleteProperty = new JButton("Delete");
            btnMoveUp = new JButton("Move up");
            btnMoveDown = new JButton("Move down");
            
            btnEditProperty.setEnabled(false);
            btnDeleteProperty.setEnabled(false);
            btnMoveUp.setEnabled(false);
            btnMoveDown.setEnabled(false);
            
            ButtonBox box = new ButtonBox();
            box.add(btnEditProperty);
            box.add(btnNewProperty);
            box.add(btnDeleteProperty);
            box.add(btnMoveUp);
            box.add(btnMoveDown);
            
            DefaultListModel lm = new DefaultListModel();
            for(MetaProperty mp : me.getDeclaredMetaProperties()){
                lm.addElement(mp);
            }
            lstProperties = new JList(lm);
 
            add(lstProperties,BorderLayout.CENTER);
            add(box, BorderLayout.EAST);
            
            // Edit Property
            btnEditProperty.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    MetaProperty mp = (MetaProperty)lstProperties.getSelectedValue();
                    new MetaPropertyEditor(getDialog(), mp, repository).setVisible(true);
                }
            });

            // New Property
            btnNewProperty.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    MetaProperty mp = new MetaPropertyImpl(new UUID());
                    MetaPropertyEditor editor;
                    (editor = new MetaPropertyEditor(getDialog(), mp, repository)).setVisible(true);
                    if(editor.wasEdited()) {
                        DefaultListModel model = (DefaultListModel)lstProperties.getModel();
                        model.addElement(mp);
                    }
                }
            });

            // Delete Property
            btnDeleteProperty.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    MetaProperty mp = (MetaProperty)lstProperties.getSelectedValue();
                    if(JOptionPane.showConfirmDialog(null,"Delete " + mp.getName() + "?","EATool",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                        int idx = lstProperties.getSelectedIndex();
                        DefaultListModel listModel = (DefaultListModel)lstProperties.getModel(); 
                        listModel.remove(idx);    
                    }
                }
            });
            
            // Move property up list.
            btnMoveUp.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent arg0) {
                    int index = lstProperties.getSelectedIndex();
                    DefaultListModel listModel = (DefaultListModel)lstProperties.getModel();
                    if(index > 0){
                        MetaProperty entry = (MetaProperty)listModel.elementAt(index);
                        listModel.removeElementAt(index);
                        listModel.add(index-1,entry);
                        lstProperties.setSelectedIndex(index-1);
                    }
                }
                
            });

            // Move property down list.
            btnMoveDown.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent arg0) {
                    int index = lstProperties.getSelectedIndex();
                    DefaultListModel listModel = (DefaultListModel)lstProperties.getModel();
                    if(index < listModel.getSize()-1){
                        MetaProperty entry = (MetaProperty)listModel.elementAt(index);
                        listModel.removeElementAt(index);
                        listModel.add(index+1,entry);
                        lstProperties.setSelectedIndex(index+1);
                    }
                   
                }
                
            });
            

            lstProperties.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
                public void valueChanged(ListSelectionEvent evt) {
                    if(!evt.getValueIsAdjusting()) {
                        btnEditProperty.setEnabled(true);
                        btnDeleteProperty.setEnabled(true);
                        
                        int idx = lstProperties.getSelectedIndex();
                        DefaultListModel listModel = (DefaultListModel)lstProperties.getModel();
                        btnMoveUp.setEnabled(idx > 0);
                        btnMoveDown.setEnabled(idx < listModel.size()-1);
                        
                    }
                }

            });
        }
        
        public boolean validateInput() {
            return true;
        }

        /**
         * Gets the list of MetaProperties held in the list model.
         * @return an array of MetaProperty with the list of MetaProperties.
         */
        public MetaProperty[] getMetaProperties(){
            DefaultListModel listModel = (DefaultListModel)lstProperties.getModel();
            MetaProperty[] metaProperties = new MetaProperty[listModel.getSize()];
            listModel.copyInto(metaProperties);
            return metaProperties;
        }
    }

    
}
