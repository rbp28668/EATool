/*
 * PropertiesEditPanel.java
 * Project: EATool
 * Created on 12-May-2006
 *
 */
package alvahouse.eatool.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * PropertiesEditPanel allows a properties set (key-value) to be edited.
 * 
 * @author rbp28668
 */
public class PropertiesEditPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Properties properties;
    private Vector<JLabel> labels;
    private Vector<JTextField> fields;
    /**
     * 
     */
    public PropertiesEditPanel(Properties properties) {
        super();
        this.properties = properties;
        
        GridBagLayout grid = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        
        setLayout(grid);
        
        c.anchor = GridBagConstraints.LINE_START;
        c.weighty = 1.0;
        c.insets = new Insets(5,10,5,10);
        
        
        int rows = properties.size();
        labels = new Vector<JLabel>(rows);
        fields = new Vector<JTextField>(rows);
        
        int idx = 0;
        for(Entry<Object, Object> entry : properties.entrySet()){
            JLabel label = new JLabel((String)entry.getKey());
            JTextField text = new JTextField((String)entry.getValue());
            text.setColumns(40);
            
            c.gridwidth = GridBagConstraints.RELATIVE;
            grid.setConstraints(label,c);
            add(label);
            labels.add(idx, label);
            
            c.gridwidth = GridBagConstraints.REMAINDER;
            grid.setConstraints(text,c);
            add(text);
            fields.add(idx,text);
            
            ++idx;
        }
    }
    
    public void onOK(){
        int rows = labels.size();
        assert(labels.size() == fields.size());
        
        for(int i=0; i<rows; ++i){
            String key = ((JLabel)labels.get(i)).getText();
            String value = ((JTextField)fields.get(i)).getText();
            properties.setProperty(key, value);
        }
    }

    public Properties getProperties() {
    	return properties;
    }
}
