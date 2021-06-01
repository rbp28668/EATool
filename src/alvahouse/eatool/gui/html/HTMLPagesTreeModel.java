/*
 * HTMLPagesTreeModel.java
 * Project: EATool
 * Created on 04-May-2007
 *
 */
package alvahouse.eatool.gui.html;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import alvahouse.eatool.gui.ExplorerTreeModel;
import alvahouse.eatool.repository.html.HTMLPage;
import alvahouse.eatool.repository.html.HTMLPages;
import alvahouse.eatool.repository.html.PageChangeEvent;
import alvahouse.eatool.repository.html.PagesChangeListener;

/**
 * HTMLPagesTreeModel is the tree model for the HTMLPages
 * explorer.
 * 
 * @author rbp28668
 */
public class HTMLPagesTreeModel extends ExplorerTreeModel implements PagesChangeListener {

	private static final long serialVersionUID = 1L;
	private DefaultMutableTreeNode root = null;
    private HTMLPages pages;

    /**
     */
    public HTMLPagesTreeModel(String rootTitle,HTMLPages pages) throws Exception {
        super(rootTitle);
        this.pages = pages;
        
        root = (DefaultMutableTreeNode)getRoot();
		root.setUserObject( pages );

		initModel();
		
		pages.addChangeListener(this);
        
    }

    public void dispose(){
        pages.removeChangeListener(this);
        pages = null;
    }
    

    /**
	 * Method initModel builds the tree model from the pages.
	 */
    private void initModel() throws Exception {
        int idx = 0;
        for(HTMLPage page : pages.getPages()){
            addPageNode((MutableTreeNode)getRoot(),page,idx++);
        }
    }

    /**
     * @param node
     * @param page
     * @param i
     */
    private int addPageNode(MutableTreeNode parent, HTMLPage page, int idx) {
        DefaultMutableTreeNode tnPage = new DefaultMutableTreeNode(page.getName());
        tnPage.setUserObject(page);
        insertNodeInto(tnPage,parent,idx);
        registerNode(tnPage,page);
        addPageChildren(tnPage,page);
        return idx;
    }

    /**
     * Adds the properties of the page to the tree.
     * @param parent is the parent page node to add properties to.
     * @param page is the HTMLPage whose properties we want.
     * @return the last child index used.
     */
    private int addPageChildren(MutableTreeNode parent, HTMLPage page) {
        int idx = 0;
        
        if(page.getDescription() != null)
            insertNodeInto(new DefaultMutableTreeNode("description: " + page.getDescription()), parent, idx++);
        
        return idx;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.html.PagesChangeListener#pageAdded(alvahouse.eatool.repository.html.PageChangeEvent)
     */
    public void pageAdded(PageChangeEvent event) {
        HTMLPage page = (HTMLPage)event.getSource();
        int pos = root.getChildCount();
        addPageNode(root,page,pos);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.html.PagesChangeListener#pageEdited(alvahouse.eatool.repository.html.PageChangeEvent)
     */
    public void pageEdited(PageChangeEvent event) {
        HTMLPage page = (HTMLPage)event.getSource();
        DefaultMutableTreeNode node = lookupNodeOf(page);
        if(node != null){
            nodeChanged(node);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.html.PagesChangeListener#pageRemoved(alvahouse.eatool.repository.html.PageChangeEvent)
     */
    public void pageRemoved(PageChangeEvent event) {
        HTMLPage page = (HTMLPage)event.getSource();
        DefaultMutableTreeNode node = lookupNodeOf(page);
        if(node != null){
            node.removeFromParent();
        }
    }

}
