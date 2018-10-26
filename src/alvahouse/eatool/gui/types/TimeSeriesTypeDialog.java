/*
 * TimeSeriesTypeDialog.java
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

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import alvahouse.eatool.gui.ButtonBox;
import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.gui.NamedRepositoryItemPanel;
import alvahouse.eatool.repository.metamodel.types.ExtensibleMetaPropertyType;
import alvahouse.eatool.repository.metamodel.types.TimeSeriesType;

/**
 * TimeSeriesTypeDialog
 * 
 * @author rbp28668
 */
public class TimeSeriesTypeDialog extends ExtensibleTypeDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TimeSeriesType type = null;
    private ListPanel listPanel;
    private NamedRepositoryItemPanel nriPanel;
    
    /**
     * @param parent
     * @param title
     */
    public TimeSeriesTypeDialog(JDialog parent, String title) {
        super(parent, title);
    }

    /**
     * @param parent
     * @param title
     */
    public TimeSeriesTypeDialog(Component parent, String title) {
        super(parent, title);
     }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.types.ExtensibleTypeDialog#setType(alvahouse.eatool.repository.metamodel.types.ExtensibleMetaPropertyType)
     */
    public void setMetaPropertyType(ExtensibleMetaPropertyType type) {
        this.type = (TimeSeriesType)type;
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
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JList<String> list = new JList<String>();
        private JButton addButton = new JButton("Add");
        private JButton editButton = new JButton("Edit");
        private JButton delButton = new JButton("Delete");
        private JButton upButton = new JButton("Move Up");
        private JButton downButton = new JButton("Move Down");
        
        private DefaultListModel<String> listModel = new DefaultListModel<String>();
        
        /**
         * 
         */
        ListPanel(){

            Collection<String> values = type.getIntervals();
            for(String value : values) {
            	listModel.addElement(value);
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
                    String value = JOptionPane.showInputDialog(TimeSeriesTypeDialog.this, "Please enter a value", "EATool", JOptionPane.PLAIN_MESSAGE);
                    if(value != null){
                        listModel.addElement(value);
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
                        JOptionPane option = new JOptionPane("Please enter a value");
                        option.setMessageType(JOptionPane.PLAIN_MESSAGE);
                        option.setInitialSelectionValue(value);
                        option.setWantsInput(true);
                        JDialog dlg = option.createDialog(TimeSeriesTypeDialog.this, "EATool");
                        dlg.setVisible(true);
                        value = (String)option.getInputValue();
                        if(value != null){
                            listModel.removeElementAt(index);
                            listModel.add(index,value);
                        }
                    }
                    
                }
	        });
	        box.add(editButton);
	        
        
	        delButton.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent arg0) {
                    int index = list.getSelectedIndex();
                    if(index != -1){
                        //String value = (String)listModel.elementAt(index);
                        listModel.remove(index);
                        enableButtons();
                    }
                }
	        });
	        box.add(delButton);
	        
	        
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
            if(listModel.size() < 2){
                Dialogs.message(this, "Must have at least 2 intervals in list");
                return false;
            }
            return true;
        }

        
        /**
         * 
         */
        private void enableButtons(){
            int idx = list.getSelectedIndex();
            editButton.setEnabled(idx != -1);
            delButton.setEnabled(idx != -1);
            upButton.setEnabled(idx > 0);
            downButton.setEnabled(idx < listModel.size()-1);
        }
    }
    
     

}
