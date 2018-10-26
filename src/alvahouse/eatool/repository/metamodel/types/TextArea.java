/*
 * TextArea.java
 * Project: EATool
 * Created on 09-Jul-2006
 *
 */
package alvahouse.eatool.repository.metamodel.types;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * TextArea provides an editor component for the Text MetaPropertyType.
 * 
 * @author rbp28668
 */
public class TextArea extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextArea text;
    /**
     * 
     */
    public TextArea(String value)  {
        super();
        text = new JTextArea(value);
        JScrollPane scroll = new JScrollPane(text);
        add(scroll);
     }
    
    public void setRows(int rows){
        text.setRows(rows);
    }
    
    public void setColumns(int columns){
        text.setColumns(columns);
    }
    
    public String getText() {
        return text.getText();
    }

    
}
