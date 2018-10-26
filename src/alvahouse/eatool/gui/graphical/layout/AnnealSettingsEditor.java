/**
 * @author rbp28668
 * File AnnealSettingsEditor.java
 * Created on 22-Nov-02
 * 
  */
package alvahouse.eatool.gui.graphical.layout;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;

import alvahouse.eatool.gui.BasicDialog;
/**
 * AnnealSettingsEditor provides a simple editor for adjusting settings
 * for simulated annealing.
 * @author Bruce.Porteous
 *
 */
//import EATool.GUI.Graphical.GraphicalViewer.ViewerPane;


public class AnnealSettingsEditor extends BasicDialog {

    /** Creates new form MetaEntityEditor */
    public AnnealSettingsEditor(Component parent, 
    SimulatedAnnealingLayoutStrategy a) {
        super(parent ,"Control Annealing Schedule");
        
        anneal = a;
        
        JPanel pnlSettings = new JPanel();
        pnlSettings.setLayout(new GridLayout(5,2));

		String[] fieldNames = {
			"Temperature",
			"Anneal Schedule",
			"Temp Steps",
			"Max configs Per Step",
			"Changes Per Step"
			};        

		fields = new JTextField[fieldNames.length];
							
		for(int i=0; i<fieldNames.length; ++i) {
			JLabel l = new JLabel(fieldNames[i]);
			fields[i] = new JTextField(20);	        
			l.setLabelFor(fields[i]);
	        pnlSettings.add(l);
	        pnlSettings.add(fields[i]);
		}

		fields[0].setText(Double.toString(anneal.getTemperature()));
		fields[1].setText(Double.toString(anneal.getAnnealSchedule()));
		fields[2].setText(Integer.toString(anneal.getTempSteps()));
		fields[3].setText(Integer.toString(anneal.getMaxConfigsPerStep()));
		fields[4].setText(Integer.toString(anneal.getChangesPerStep()));
		
        getContentPane().add(pnlSettings, BorderLayout.NORTH);
        getContentPane().add(getOKCancelPanel(), BorderLayout.SOUTH);
        pack();
        
    }

	/**
	 * @see alvahouse.eatool.GUI.BasicDialog#onOK()
	 */
	protected void onOK() {
	}

	/**
	 * @see alvahouse.eatool.GUI.BasicDialog#validateInput()
	 */
	protected boolean validateInput() {
		try {
			float temperature = Float.parseFloat(fields[0].getText());
			float schedule = Float.parseFloat(fields[1].getText());
			int tempSteps = Integer.parseInt(fields[2].getText());
			int maxConfigsPerStep = Integer.parseInt(fields[3].getText());
			int changesPerStep = Integer.parseInt(fields[4].getText());
			
			anneal.setTemperature(temperature);
			anneal.setAnnealSchedule(schedule);
			anneal.setTempSteps(tempSteps);
			anneal.setMaxConfigsPerStep(maxConfigsPerStep);
			anneal.setChangesPerStep(changesPerStep);
			
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	private SimulatedAnnealingLayoutStrategy anneal;
	private JTextField[] fields;
}
