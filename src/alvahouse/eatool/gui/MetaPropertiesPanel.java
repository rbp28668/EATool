/**
 * 
 */
package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaPropertyContainer;
import alvahouse.eatool.repository.metamodel.impl.MetaPropertyImpl;
import alvahouse.eatool.util.UUID;

/*=================================================================*/
// Meta Properties Panel
/*=================================================================*/
public class MetaPropertiesPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    private JList<MetaProperty> lstProperties;
    private JButton btnEditProperty;
    private JButton btnNewProperty;
    private JButton btnDeleteProperty;
    private JButton btnMoveUp;
    private JButton btnMoveDown;

    MetaPropertiesPanel(MetaPropertyContainer me, final Repository repository, final javax.swing.JDialog parentDialog) {
    
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
        
        DefaultListModel<MetaProperty> lm = new DefaultListModel<MetaProperty>();
        for(MetaProperty mp : me.getDeclaredMetaProperties()){
            lm.addElement(mp);
        }
        lstProperties = new JList<MetaProperty>(lm);

        add(lstProperties,BorderLayout.CENTER);
        add(box, BorderLayout.EAST);
        
        // Edit Property
        btnEditProperty.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MetaProperty mp = lstProperties.getSelectedValue();
                new MetaPropertyEditor(parentDialog, mp, repository).setVisible(true);
            }
        });

        // New Property
        btnNewProperty.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MetaProperty mp = new MetaPropertyImpl(new UUID());
                MetaPropertyEditor editor;
                (editor = new MetaPropertyEditor(parentDialog, mp, repository)).setVisible(true);
                if(editor.wasEdited()) {
                    DefaultListModel<MetaProperty> model = (DefaultListModel<MetaProperty>)lstProperties.getModel();
                    model.addElement(mp);
                }
            }
        });

        // Delete Property
        btnDeleteProperty.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MetaProperty mp = lstProperties.getSelectedValue();
                if(JOptionPane.showConfirmDialog(null,"Delete " + mp.getName() + "?","EATool",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                    int idx = lstProperties.getSelectedIndex();
                    DefaultListModel<MetaProperty> listModel = (DefaultListModel<MetaProperty>)lstProperties.getModel(); 
                    listModel.remove(idx);    
                }
            }
        });
        
        // Move property up list.
        btnMoveUp.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                int index = lstProperties.getSelectedIndex();
                DefaultListModel<MetaProperty> listModel = (DefaultListModel<MetaProperty>)lstProperties.getModel();
                if(index > 0){
                    MetaProperty entry = listModel.elementAt(index);
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
                DefaultListModel<MetaProperty> listModel = (DefaultListModel<MetaProperty>)lstProperties.getModel();
                if(index < listModel.getSize()-1){
                    MetaProperty entry = listModel.elementAt(index);
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
                    DefaultListModel<MetaProperty> listModel = (DefaultListModel<MetaProperty>)lstProperties.getModel();
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
        DefaultListModel<MetaProperty> listModel = (DefaultListModel<MetaProperty>)lstProperties.getModel();
        MetaProperty[] metaProperties = new MetaProperty[listModel.getSize()];
        listModel.copyInto(metaProperties);
        return metaProperties;
    }
}