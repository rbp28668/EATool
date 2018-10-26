/*
 * RegexpCheckedTypeDialog.java
 * Project: EATool
 * Created on 12-Jul-2006
 *
 */
package alvahouse.eatool.gui.types;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.gui.NamedRepositoryItemPanel;
import alvahouse.eatool.repository.metamodel.types.ExtensibleMetaPropertyType;
import alvahouse.eatool.repository.metamodel.types.RegexpCheckedType;

/**
 * RegexpCheckedTypeDialog
 * 
 * @author rbp28668
 */
public class RegexpCheckedTypeDialog extends ExtensibleTypeDialog {

    private RegexpCheckedType type = null;
    private NamedRepositoryItemPanel nriPanel;
    
    private JTextField regexp = new JTextField();
    private JTextField defaultValue = new JTextField();
    private JTextField fieldLength = new JTextField();

    /**
     * @param parent
     * @param title
     */
    public RegexpCheckedTypeDialog(JDialog parent, String title) {
        super(parent, title);
    }

    /**
     * @param parent
     * @param title
     */
    public RegexpCheckedTypeDialog(Component parent, String title) {
        super(parent, title);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#onOK()
     */
    protected void onOK() {
        type.setName(nriPanel.getName());
        type.setDescription(nriPanel.getDescription());
        type.setPattern(regexp.getText());
        type.setDefaultValue(defaultValue.getText());
        type.setFieldLength(Integer.parseInt(fieldLength.getText()));
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#validateInput()
     */
    protected boolean validateInput() {
        if(!nriPanel.validateInput()){
            return false;
        }
        
        if(!type.isPatternValid(regexp.getText())){
            Dialogs.message(this, "Invalid regular expression pattern");
            regexp.requestFocus();
            return false;
        }

        if(!type.isValueValid(regexp.getText(),defaultValue.getText())){
            Dialogs.message(this, "Invalid default value");
            defaultValue.requestFocus();
            return false;
        }
        
        boolean valid = true;
        try {
            Integer.parseInt(fieldLength.getText());
        } catch (Exception e) {
            Dialogs.message(this, "Field length must be a valid integer");
            fieldLength.requestFocus();
            valid = false;
        }
        return valid;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.types.ExtensibleTypeDialog#setType(alvahouse.eatool.repository.metamodel.types.ExtensibleMetaPropertyType)
     */
    public void setMetaPropertyType(ExtensibleMetaPropertyType type) {
        this.type = (RegexpCheckedType)type;
        init();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.types.ExtensibleTypeDialog#getType()
     */
    public ExtensibleMetaPropertyType getMetaPropertyType() {
        return type;
    }
    /**
     * 
     */
    private void init(){
        getContentPane().setLayout(new BorderLayout());
        nriPanel = new NamedRepositoryItemPanel(type);
        getContentPane().add(nriPanel,BorderLayout.NORTH);
        
        getContentPane().add(getOKCancelPanel(),BorderLayout.EAST);
        
        JPanel panel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1.0f;
        c.insets = new Insets(20,10,20,10);
        c.anchor = GridBagConstraints.LINE_START;
        
        JLabel label = new JLabel("Regular Expression");
        layout.setConstraints(label,c);
        panel.add(label);
        
        regexp.setColumns(40);
        regexp.setText(type.getPattern());
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(regexp,c);
        panel.add(regexp);

        label = new JLabel("Default Value");
        c.gridwidth = 1;
        layout.setConstraints(label,c);
        panel.add(label);
        
        defaultValue.setColumns(40);
        defaultValue.setText(type.getDefaultValue());
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(defaultValue,c);
        panel.add(defaultValue);

        label = new JLabel("Field Length");
        c.gridwidth = 1;
        layout.setConstraints(label,c);
        panel.add(label);
        
        fieldLength.setColumns(3);
        fieldLength.setText(Integer.toString(type.getDisplayLength()));
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(fieldLength,c);
        panel.add(fieldLength);
        
        getContentPane().add(panel,BorderLayout.CENTER);
        
        pack();
        
    }

}
