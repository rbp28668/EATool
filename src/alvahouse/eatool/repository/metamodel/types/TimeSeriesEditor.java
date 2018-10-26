/*
 * TimeSeriesEditor.java
 * Project: EATool
 * Created on 18-Jul-2006
 *
 */
package alvahouse.eatool.repository.metamodel.types;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * TimeSeriesEditor is an editor panel that edits a time-series type.
 * 
 * @author rbp28668
 */
public class TimeSeriesEditor extends JPanel {

    private static final long serialVersionUID = 1L;
    private TimeSeriesType type;
    private JTextField values[];
    /**
     * 
     */
    public TimeSeriesEditor(TimeSeriesType type, String value) {
        super();
        this.type = type;
    
        Collection<String> intervals = type.getIntervals();
        if(intervals.size() < 1){
            throw new IllegalStateException("Time series can't have less than 1 interval");
        }
        
        setBorder(BorderFactory.createLineBorder(Color.black));

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1.0f;
        c.weighty = 0.0f;
        //c.insets = new Insets(20,10,20,10);
        c.insets = new Insets(5,2,5,2);
        c.anchor = GridBagConstraints.LINE_START;
        
        setLayout(layout);
        
        values = new JTextField[intervals.size()+1];
        
        Iterator<String> iter = intervals.iterator();
        String labelText = "Start";
        StringTokenizer toks = new StringTokenizer(value,"|");
        String start = iter.next();
        for(int idx = 0; idx<values.length; ++idx){
            
            JLabel label = new JLabel(labelText);
            c.gridwidth = 1;
            c.weightx = 0.1;
            layout.setConstraints(label,c);
            add(label);
            
            String tok = toks.nextToken();
            values[idx]= new JTextField();
            values[idx].setText(tok);
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.weightx = 0.9;
            layout.setConstraints(values[idx],c);
            add(values[idx],c);
            
            if(iter.hasNext()){
                String end = iter.next();
                labelText = start + " - " + end; 
                start = end; // of next interval
            } else {
                labelText = "Finish";
            }
            
        }
    }
    
    /**
     * @return
     */
    public String getValue() {
        StringBuffer buff = new StringBuffer(256);
        for(int i=0; i<values.length; ++i){
            if(buff.length() > 0){
                buff.append('|');
            }
            buff.append(values[i].getText());
        }
        return buff.toString();
    }

    
}
