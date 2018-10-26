/*
 * ControlledListTypeDialog.java
 * Project: EATool
 * Created on 12-Jul-2006
 *
 */
package alvahouse.eatool.gui.types;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import alvahouse.eatool.gui.ButtonBox;
import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.gui.NamedRepositoryItemPanel;
import alvahouse.eatool.repository.metamodel.types.ControlledListType;
import alvahouse.eatool.repository.metamodel.types.ExtensibleMetaPropertyType;

/**
 * ControlledListTypeDialog is a dialog for setting up a ControlledListType.
 * 
 * @author rbp28668
 */
public class ControlledListTypeDialog extends ExtensibleTypeDialog {

    private ControlledListType type;
    private ListPanel listPanel;
    private NamedRepositoryItemPanel nriPanel;
    
    /**
     * @param parent
     * @param title
     */
    public ControlledListTypeDialog(JDialog parent, String title) {
        super(parent, title);
    }

    /**
     * @param parent
     * @param title
     */
    public ControlledListTypeDialog(Component parent, String title) {
        super(parent, title);
    }

    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.types.ExtensibleTypeDialog#setType(alvahouse.eatool.repository.metamodel.types.ExtensibleMetaPropertyType)
     */
    public void setMetaPropertyType(ExtensibleMetaPropertyType type) {
        this.type = (ControlledListType)type;
        init();
    }

    /**
     * 
     */
    private void init(){
        getContentPane().setLayout(new BorderLayout());
        nriPanel = new NamedRepositoryItemPanel(type);
        getContentPane().add(nriPanel,BorderLayout.NORTH);
        
        listPanel = new ListPanel();
        getContentPane().add(listPanel,BorderLayout.CENTER);

        getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
        pack();

    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#onOK()
     */
    protected void onOK() {
        type.setName(nriPanel.getName());
        type.setDescription(nriPanel.getDescription());
        
        type.clear();
        for(int i=0; i<listPanel.listModel.size(); ++i){
            type.add((String)listPanel.listModel.getElementAt(i));
        }
        type.setDefault(listPanel.defaultValue);
        
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#validateInput()
     */
    protected boolean validateInput() {
        return nriPanel.validateInput() && listPanel.validateInput();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.types.ExtensibleTypeDialog#getType()
     */
    public ExtensibleMetaPropertyType getMetaPropertyType() {
        return type;
    }

    /**
     * ListPanel
     * 
     * @author rbp28668
     */
    private class ListPanel extends JPanel{
        private JLabel deflt = new JLabel();
        private String defaultValue = null;
        private JList list = new JList();
        private JButton addButton = new JButton("Add");
        private JButton editButton = new JButton("Edit");
        private JButton delButton = new JButton("Delete");
        private JButton dfltButton = new JButton("Set Default");
        private JButton upButton = new JButton("Move Up");
        private JButton downButton = new JButton("Move Down");
        
        private DefaultListModel listModel = new DefaultListModel();
        
        /**
         * 
         */
        ListPanel(){

            Collection values = type.getValues();
            if(values.size() == 0){
                setDefault(null);
            } else {
                for(Iterator iter = values.iterator(); iter.hasNext();){
                    String value = (String)iter.next();
                    listModel.addElement(value);
                }
                setDefault(type.initialise());
            }

	        GridBagLayout layout = new GridBagLayout();
	        GridBagConstraints c = new GridBagConstraints();
	        c.fill = GridBagConstraints.BOTH;
	        c.gridheight = 1;
	        c.gridwidth = 1;
	        c.weightx = 1.0f;
	        c.insets = new Insets(20,10,20,10);
	        
	        c.anchor = GridBagConstraints.LINE_START;
	        
            setLayout(layout);
            
            c.gridwidth = GridBagConstraints.REMAINDER;
	        layout.setConstraints(deflt,c);
	        add(deflt);

	        list.setModel(listModel);
	        list.setVisibleRowCount(6);
	        
	        c.gridwidth = 1;
	        c.weightx = 0.1;
	        list.setBorder(BorderFactory.createLineBorder(Color.black));
	        layout.setConstraints(list,c);
	        add(list);

	        list.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent event) {
                    enableButtons();
                }
	            
	        });
	        
	        ButtonBox box = new ButtonBox();
	        addButton.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent arg0) {
                    String value = JOptionPane.showInputDialog(ControlledListTypeDialog.this, "Please enter a value", "EATool", JOptionPane.PLAIN_MESSAGE);
                    if(value != null){
                        listModel.addElement(value);
                        if(listModel.size() == 1){
                            setDefault(value);
                        }
                        enableButtons();
                    }
                    
                }
	        });
	        box.add(addButton);
	        
	        editButton.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent arg0) {
                    int index = list.getSelectedIndex();
                    if(index != -1){
                        String value = (String)listModel.elementAt(index);
                        boolean wasDefault = value == defaultValue;
                        JOptionPane option = new JOptionPane("Please enter a value");
                        option.setMessageType(JOptionPane.PLAIN_MESSAGE);
                        option.setInitialSelectionValue(value);
                        option.setWantsInput(true);
                        JDialog dlg = option.createDialog(ControlledListTypeDialog.this, "EATool");
                        dlg.setVisible(true);
                        value = (String)option.getInputValue();
                        if(value != null){
                            listModel.removeElementAt(index);
                            listModel.add(index,value);
                            if(wasDefault){
                                setDefault(value);
                            }
                        }
                    }
                    
                }
	        });
	        box.add(editButton);
	        
        
	        delButton.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent arg0) {
                    int index = list.getSelectedIndex();
                    if(index != -1){
                        String value = (String)listModel.elementAt(index);
                        listModel.remove(index);
                        if(listModel.size() == 0){
                            setDefault(null);
                        } else if (value == defaultValue){
                            setDefault((String)listModel.elementAt(0));
                        }
                        enableButtons();
                    }
                }
	        });
	        box.add(delButton);
	        
	        dfltButton.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent arg0) {
                    int index = list.getSelectedIndex();
                    if(index != -1){
                        setDefault((String)listModel.elementAt(index));
                    }
                    
                }
	        });
	        box.add(dfltButton);
	        
	        upButton.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent arg0) {
                    int index = list.getSelectedIndex();
                    if(index > 0){
                        String value = (String)listModel.elementAt(index);
                        listModel.removeElementAt(index);
                        listModel.add(index-1,value);
                        list.setSelectedIndex(index-1);
                    }
                }
	        });
	        box.add(upButton);
	        
	        downButton.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent arg0) {
                    int index = list.getSelectedIndex();
                    if(index < listModel.getSize()-1){
                        String value = (String)listModel.elementAt(index);
                        listModel.removeElementAt(index);
                        listModel.add(index+1,value);
                        list.setSelectedIndex(index+1);
                    }
                }
	        });
	        box.add(downButton);
	        
	        c.fill = GridBagConstraints.VERTICAL;
	        c.gridheight = 1;
	        c.gridwidth = GridBagConstraints.REMAINDER;
	        c.anchor = GridBagConstraints.LINE_END;
	        c.weightx = 0.0;
	        
	        layout.setConstraints(box,c);
	        add(box);
	        
	        enableButtons();
        }
        
        /**
         * @return
         */
        public boolean validateInput() {
            if(listModel.size() == 0){
                Dialogs.message(this, "No values in list");
                return false;
            }
            if(defaultValue == null){
                Dialogs.message(this, "No default value set for list");
                return false;
            }
            return true;
        }

        /**
         * @param value
         */
        private void setDefault(String value){
            defaultValue = value;
            if(value == null){
                value = "No default set";
            } else {
                value = "Default: " + value;
            }
	        deflt.setText(value);
        }
        
        /**
         * 
         */
        private void enableButtons(){
            int idx = list.getSelectedIndex();
            editButton.setEnabled(idx != -1);
            delButton.setEnabled(idx != -1);
            dfltButton.setEnabled(idx != -1);
            upButton.setEnabled(idx > 0);
            downButton.setEnabled(idx < listModel.size()-1);
        }
    }
    
        
}
