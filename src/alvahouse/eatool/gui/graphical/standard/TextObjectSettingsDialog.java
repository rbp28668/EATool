/*
 * TextObjectSettingsDialog.java
 * Created on 28-May-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.gui.graphical.standard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import alvahouse.eatool.gui.BasicDialog;
import alvahouse.eatool.gui.graphical.FontChooserPanel;
import alvahouse.eatool.repository.graphical.standard.TextObjectSettings;

/**
 * TextObjectSettingsDialog
 * @author Bruce.Porteous
 *
 */
public class TextObjectSettingsDialog extends BasicDialog {

	private TextObjectSettingsDialog thisDialog;
	private TextObjectSettings settings;
	private JButton backColourButton = new JButton("Background");	
	private JButton textColourButton = new JButton("Text");
	private JButton borderColourButton = new JButton("Border");
	private ColourDisplay backColourDisplay = new ColourDisplay();
	private ColourDisplay textColourDisplay = new ColourDisplay();
	private ColourDisplay borderColourDisplay = new ColourDisplay();
	private FontChooserPanel fontChooser = new FontChooserPanel();

	/**
	 * @param settings
	 * @param parent
	 * @param title
	 */
	public TextObjectSettingsDialog(TextObjectSettings settings, JDialog parent, String title) {
		super(parent, title);
		init(settings);
	}

	/**
	 * @param settings
	 * @param parent
	 * @param title
	 */
	public TextObjectSettingsDialog(TextObjectSettings settings, Component parent, String title) {
		super(parent, title);
		init(settings);
	}

	/**
	 * @param settings
	 */
	private void init(TextObjectSettings settings){
		thisDialog = this;
		this.settings = settings;
		
		JPanel colourPanel = new JPanel();
		colourPanel.setLayout(new GridLayout(3,2));
		colourPanel.add(backColourButton);
		colourPanel.add(backColourDisplay);
		colourPanel.add(textColourButton);
		colourPanel.add(textColourDisplay);
		colourPanel.add(borderColourButton);
		colourPanel.add(borderColourDisplay);
		
		backColourDisplay.setColour(settings.getBackColour());
		textColourDisplay.setColour(settings.getTextColour());
		borderColourDisplay.setColour(settings.getBorderColour());
		fontChooser.setSelectedFont(settings.getFont());
		
		backColourButton.addActionListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Color colour = JColorChooser.showDialog(thisDialog,"Background Colour", Color.blue);
				if(colour != null){
					backColourDisplay.setColour(colour);
				}
				
			}
		});
		textColourButton.addActionListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Color colour = JColorChooser.showDialog(thisDialog,"Background Colour", Color.blue);
				if(colour != null){
					textColourDisplay.setColour(colour);
				}
				
			}
		});
		
		borderColourButton.addActionListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Color colour = JColorChooser.showDialog(thisDialog,"Background Colour", Color.blue);
				if(colour != null){
					borderColourDisplay.setColour(colour);
				}
				
			}
		});
		
		getContentPane().add(colourPanel,BorderLayout.NORTH);
		getContentPane().add(fontChooser,BorderLayout.CENTER);
		getContentPane().add(getOKCancelPanel(),BorderLayout.EAST);
		pack();
	}
	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.BasicDialog#onOK()
	 */
	protected void onOK() {
		settings.setBackColour(backColourDisplay.getColour());
		settings.setTextColour(textColourDisplay.getColour());
		settings.setBorderColour(borderColourDisplay.getColour());
		
		Font font = fontChooser.getSelectedFont();
		if(font != null){
			settings.setFont(font);
		}

	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.BasicDialog#validateInput()
	 */
	protected boolean validateInput() {
		return true;
	}

	/**
	 * ColourDisplay
	 * @author Bruce.Porteous
	 *
	 */
	private static class ColourDisplay extends JLabel{
		
		/**
		 * 
		 */
		ColourDisplay(){
			setOpaque(true);
			setPreferredSize(new Dimension(20,20));
		}
		
		/**
		 * @param colour
		 */
		void setColour(Color colour){
			setBackground(colour);
		}
		
		/**
		 * @return
		 */
		Color getColour(){
			return getBackground();
		}

	}
}
