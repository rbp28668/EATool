/*
 * ImageAttributesDialog.java
 * Project: EATool
 * Created on 05-Mar-2006
 *
 */
package alvahouse.eatool.gui.images;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JDialog;

import alvahouse.eatool.gui.BasicDialog;
import alvahouse.eatool.gui.NamedRepositoryItemPanel;
import alvahouse.eatool.repository.images.Image;

/**
 * ImageAttributesDialog allows the user to edit the attributes of
 * an image.
 * 
 * @author rbp28668
 */
public class ImageAttributesDialog extends BasicDialog {

    private NamedRepositoryItemPanel nriPanel;
    private JComboBox format;
    private Image image;
    
    
    /**
     * @param parent
     * @param title
     */
    public ImageAttributesDialog(JDialog parent, String title, Image image) {
        super(parent, title);
        init(image);
    }

    /**
     * @param parent
     * @param title
     */
    public ImageAttributesDialog(Component parent, String title, Image image) {
        super(parent, title);
        init(image);
    }

    private void init(Image image){
        
        this.image = image;
        
        nriPanel = new NamedRepositoryItemPanel(image);
        getContentPane().add(nriPanel, BorderLayout.NORTH);
        
        String[] formats = image.getOutputFormats();
        
        format = new JComboBox(formats);
        
        String currentFormat = image.getFormat();
        for(int i=0; i<formats.length; ++i){
            if(formats[i].equals(currentFormat)){
                format.setSelectedItem(formats[i]);
                break;
            }
        }
        
        getContentPane().add(format);

		getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
		pack();
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#onOK()
     */
    protected void onOK() {
        image.setName(nriPanel.getName());
        image.setDescription(nriPanel.getDescription());
        image.setFormat((String)format.getSelectedItem());
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#validateInput()
     */
    protected boolean validateInput() {
        return nriPanel.validateInput() && format.getSelectedItem() != null;
    }

}
