/*
 * NamedMetaItemPanel.java
 *
 * Created on 02 February 2002, 19:56
 */

package alvahouse.eatool.gui;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import alvahouse.eatool.repository.base.NamedItem;


/**
 *
 * @author  rbp28668
 */
public class NamedRepositoryItemPanel extends javax.swing.JPanel {

    /** Creates new form NamedMetaItemPanel */
    public NamedRepositoryItemPanel(NamedItem nri) {
        initComponents();
        
        lblUUID = new JLabel("uuid: " + nri.getKey().toString());
        lblUUID.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(2, 3, 2, 3)));
        add(lblUUID,BorderLayout.NORTH);
        
        add(new JLabel("Name"), BorderLayout.WEST);
        txtName = new JTextField(nri.getName());
        add(txtName,BorderLayout.CENTER);
        txtName.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e){
                System.out.println(e.getActionCommand() + "," + e.getID() + "," +e.getModifiers());
            }

        });
        
        //add(new JLabel("Description",BorderLayout.SOUTH));
        
        txtDescription = new WrappedTextArea(nri.getDescription());
        txtDescription.setRows(4);
        JScrollPane scroll = new JScrollPane();
        scroll.setBorder(new TitledBorder("Description"));
        scroll.setViewportView(txtDescription);
        add(scroll,BorderLayout.SOUTH);
    }

    /** validates values in the panel
     * @return true if input is valid, false if invalid
     **/
    public boolean validateInput() {
        if(getName().length() == 0) {
            JOptionPane.showMessageDialog(this,
                "Please enter a value for the name", 
                "EATool", JOptionPane.INFORMATION_MESSAGE);
            txtName.requestFocus();
            return false;
        }
        return true;
    }
    
    public String getName() {
        return txtName.getText();
    }

    public void setName(String name) {
        txtName.setText(name);
    }
    
    public String getDescription() {
        return txtDescription.getText();
    }
    
    public void getDescription(String desc) {
         txtDescription.setText(desc);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        setLayout(new java.awt.BorderLayout());

    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    private javax.swing.JLabel lblUUID;
    private javax.swing.JTextField txtName;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtDescription;

}
