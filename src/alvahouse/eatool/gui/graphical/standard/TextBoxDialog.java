/*
 * TextObjectSettingsDialog.java
 * Created on 28-May-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.gui.graphical.standard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import alvahouse.eatool.gui.BasicDialog;
import alvahouse.eatool.repository.graphical.standard.TextBox;

/**
 * TextBoxDialog
 * 
 * @author rbp28668
 */
public class TextBoxDialog extends BasicDialog {

	private TextBoxDialog thisDialog;
	private TextBox textBox;
	
	private JTextArea text;
	private JTextField url;
	
	
	/**
	 * @param textBox
	 * @param parent
	 * @param title
	 */
	public TextBoxDialog(TextBox textBox, JDialog parent, String title) {
		super(parent, title);
		init(textBox);
	}

	/**
	 * @param textBox
	 * @param parent
	 * @param title
	 */
	public TextBoxDialog(TextBox textBox, Component parent, String title) {
		super(parent, title);
		init(textBox);
	}

	/**
	 * @param settings
	 */
	private void init(TextBox textBox){
		thisDialog = this;
		this.textBox = textBox;
		
		JPanel mainPanel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		mainPanel.setLayout(layout);
		
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridy = GridBagConstraints.RELATIVE;
		
		JLabel label = new JLabel("Text");
		layout.setConstraints(label,c);
		mainPanel.add(label);
		
		text = new JTextArea();
		text.setText(textBox.getText());
		text.setColumns(40);
		text.setRows(5);
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(text,c);
		mainPanel.add(text);
		
		label = new JLabel("URL");
		c.gridx = GridBagConstraints.RELATIVE;
		layout.setConstraints(label,c);
		mainPanel.add(label);
	
		url = new JTextField();
		url.setText(textBox.getUrl());
		url.setColumns(40);
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(url,c);
		mainPanel.add(url);
		
		
		getContentPane().add(mainPanel,BorderLayout.CENTER);
		getContentPane().add(getOKCancelPanel(),BorderLayout.EAST);
		pack();
	}
	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.BasicDialog#onOK()
	 */
	protected void onOK() {
	    String value = text.getText();
	    textBox.setText(value);
	    
	    value = url.getText();
	    textBox.setUrl(value);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.BasicDialog#validateInput()
	 */
	protected boolean validateInput() {
		return true;
	}

}
