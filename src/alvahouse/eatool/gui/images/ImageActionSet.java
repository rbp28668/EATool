/*
 * ImageActionSet.java
 * Project: EATool
 * Created on 05-Mar-2006
 *
 */
package alvahouse.eatool.gui.images;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.CopyKeyAction;
import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.repository.images.Image;
import alvahouse.eatool.repository.images.Images;
import alvahouse.eatool.util.SettingsManager;
import alvahouse.eatool.util.UUID;

/**
 * ImageActionSet is the action set for script explorer.
 * 
 * @author rbp28668
 */
public class ImageActionSet extends ActionSet {

    private ImageExplorer explorer;
    private Images images;
    private Application app;
    /**
     * 
     */
    public ImageActionSet(ImageExplorer explorer, Images images, Application app) {
        super();
        this.explorer = explorer;
        this.images = images;
        this.app = app;
        
		addAction("ImageImport",actionImageImport);
		addAction("ImageProperties", actionImageProperties);
		addAction("ImageDelete",actionImageDelete);
		addAction("ImagePreview", actionImagePreview);
        addAction(CopyKeyAction.NAME, new CopyKeyAction(explorer,explorer));
    }

    /** Action to import an image from disk */
    private final Action actionImageImport = new AbstractAction() {
       private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
			try {
			       	SettingsManager.Element cfg = app.getSettings().getOrCreateElement("/Files/Image");
			       	String path = cfg.attribute("path");
			        
			        JFileChooser chooser = new JFileChooser();
			        if(path == null){
			            chooser.setCurrentDirectory( new File("."));
			        } else {
			            chooser.setSelectedFile(new File(path));
			        }
			        
			        chooser.setFileFilter(new FileFilter(){

                        public boolean accept(File file) {
                            if(file.isDirectory()){
                                return true;
                            }
                            
                            String[] formats = ImageIO.getWriterFormatNames();
                            String name = file.getName();
                            
                            for(int i=0; i<formats.length; ++i){
                                if(name.endsWith(formats[i])){
                                    return true;
                                }
                            }
                            return false;
                        }

                        public String getDescription() {
                            return "Image Files";
                        }
			            
			        });

			        if( chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			        	explorer.setCursor(Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
						try {
							path = chooser.getSelectedFile().getPath();
							cfg.setAttribute("path",path);
							
						    Image image = new Image(new UUID());
						    FileInputStream fis = new FileInputStream(path);
						    image.importImage(fis);

						    String name = chooser.getSelectedFile().getName();
						    image.setName(name);
						    image.setDescription(path);
						    
						    String[] formats = ImageIO.getReaderFormatNames();
						    for(int i=0; i<formats.length; ++i){
						        if(name.endsWith( "." + formats[i])){
						            image.setFormat(formats[i]);
						            break;
						        }
						    }
					        
						    ImageAttributesDialog editor = new ImageAttributesDialog(explorer, "Edit Image Properties", image);
					        editor.setVisible(true);
						    
			                images.addImage(image);
			                
						} finally {
							explorer.setCursor(Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
						}
			        }
			        
			    
			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   

    /**
     * Action to edit an image's properties
     */
    private final Action actionImageProperties = new AbstractAction() {
        private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			try {
			    Image image = (Image)explorer.getSelectedNode().getUserObject();
			    if(image != null){
			        ImageAttributesDialog editor = new ImageAttributesDialog(explorer, "Edit Image Properties", image);
			        editor.setVisible(true);
			        if(editor.wasEdited()){
			            images.updateImage(image);
			        }
			    }
			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   
    
	private final Action actionImageDelete = new AbstractAction() {
	       private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			try {
			    Image image = (Image)explorer.getSelectedNode().getUserObject();
			    if(image != null){
			        if(Dialogs.question(explorer, "Delete image " + image.getName())){
			            images.removeImage(image);
			        }
			    }
			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};
	
    private final Action actionImagePreview = new AbstractAction() {
        private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			try {
			    Image image = (Image)explorer.getSelectedNode().getUserObject();
			    if(image != null){
			        ImageIcon icon = new ImageIcon(image.getImage());
			        JOptionPane.showMessageDialog(explorer, icon, image.toString(), JOptionPane.PLAIN_MESSAGE);
			    }
			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   
	
}
