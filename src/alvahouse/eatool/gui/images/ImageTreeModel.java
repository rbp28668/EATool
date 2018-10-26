/*
 * ImageTreeModel.java
 * Project: EATool
 * Created on 05-Mar-2006
 *
 */
package alvahouse.eatool.gui.images;

import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import alvahouse.eatool.gui.ExplorerTreeModel;
import alvahouse.eatool.repository.images.Image;
import alvahouse.eatool.repository.images.ImageChangeEvent;
import alvahouse.eatool.repository.images.Images;
import alvahouse.eatool.repository.images.ImagesChangeListener;

/**
 * ImageTreeModel reprepresents the scripts and their properties as a hierarchy
 * for use in the ImageExplorer.
 * 
 * @author rbp28668
 */
public class ImageTreeModel extends ExplorerTreeModel implements ImagesChangeListener{

	private DefaultMutableTreeNode root = null;
    private Images images;

    /**
     * @param rootTitle
     */
    public ImageTreeModel(String rootTitle, Images images) {
        super(rootTitle);
        this.images = images;
        
        root = (DefaultMutableTreeNode)getRoot();
		root.setUserObject( images );

		images.addChangeListener(this);
		initModel();
        
    }

    /**
     * 
     */
    public void dispose(){
        if(images != null) {
            images.removeChangeListener(this);
        }
        images = null;
    }
    

    /**
	 * Method initModel builds the tree model from the images.
	 */
    private void initModel() {
        int idx = 0;
        for(Iterator iter = images.getImages().iterator();iter.hasNext();){
            Image image = (Image)iter.next();
            addImageNode((MutableTreeNode)getRoot(),image,idx++);
        }
    }

    /**
     * @param node
     * @param image
     * @param i
     */
    private int addImageNode(MutableTreeNode parent, Image image, int idx) {
        DefaultMutableTreeNode tnImage = new DefaultMutableTreeNode(image.getName());
        tnImage.setUserObject(image);
        insertNodeInto(tnImage,parent,idx);
        registerNode(tnImage,image);
        addImageChildren(tnImage,image);
        return idx;
    }

    /**
     * Adds the properties of the image to the tree.
     * @param parent is the parent image node to add properties to.
     * @param image is the image whose properties we want.
     * @return the last child index used.
     */
    private int addImageChildren(MutableTreeNode parent, Image image) {
        int idx = 0;
        
        insertNodeInto(new DefaultMutableTreeNode("format: " + image.getFormat()),parent,idx++);
        
        if(image.getDescription() != null){
            insertNodeInto(new DefaultMutableTreeNode("description: " + image.getDescription()), parent, idx++);
        }
        
        return idx;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.scripting.ImagesChangeListener#updated(alvahouse.eatool.scripting.ImageChangeEvent)
     */
    public void updated(ImageChangeEvent e) {
        root.removeAllChildren();
        initModel();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.scripting.ImagesChangeListener#scriptAdded(alvahouse.eatool.scripting.ImageChangeEvent)
     */
    public void imageAdded(ImageChangeEvent e) {
        Image script = (Image)e.getSource();
        addImageNode(root,script,root.getChildCount());
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.scripting.ImagesChangeListener#scriptChanged(alvahouse.eatool.scripting.ImageChangeEvent)
     */
    public void imageEdited(ImageChangeEvent e) {
        Image script = (Image)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(script);
        if(tn != null) {
            nodeChanged(tn);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.scripting.ImagesChangeListener#scriptDeleted(alvahouse.eatool.scripting.ImageChangeEvent)
     */
    public void imageRemoved(ImageChangeEvent e) {
        Image script = (Image)e.getSource();
        DefaultMutableTreeNode tn = lookupNodeOf(script);
        if(tn != null) {
            removeNodeFromParent(tn);
            removeNodeOf(script);
        }
    }

}
