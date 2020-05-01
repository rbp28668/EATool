/*
 * HTMLPagesActionSet.java
 * Project: EATool
 * Created on 04-May-2007
 *
 */
package alvahouse.eatool.gui.html;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.CopyKeyAction;
import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.html.HTMLPage;
import alvahouse.eatool.repository.html.HTMLPages;
import alvahouse.eatool.repository.html.PageChangeEvent;
import alvahouse.eatool.util.UUID;


/**
 * HTMLPagesActionSet provides Actions for the HTMLPagesExplorer.
 * 
 * @author rbp28668
 */
public class HTMLPagesActionSet extends ActionSet {

    private HTMLPagesExplorer explorer;
    private HTMLPages pages;
    private Repository repository;
    private Application app;
    
    HTMLPagesActionSet(HTMLPagesExplorer explorer, HTMLPages pages, Application app, Repository repository){
        super();
        this.explorer = explorer;
        this.pages = pages;
        this.app = app;
        this.repository = repository;
        
		addAction("PageNew",actionPageNew);
		addAction("PageEdit",actionPageEdit);
		addAction("PageProperties",actionPageProperties);
		addAction("PageDelete",actionPageDelete);
		addAction("PageShow", actionPageShow);
        addAction(CopyKeyAction.NAME, new CopyKeyAction(explorer,explorer));
    }
    
    
    /**
     * Comment for <code>actionPageNew</code>
     */
    private final Action actionPageNew = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
			try {
			    HTMLPage page = new HTMLPage(new UUID());
		        HTMLPagePropertiesEditor propertiesDialog = new HTMLPagePropertiesEditor(explorer,page);
		        propertiesDialog.setVisible(true);
		        if(propertiesDialog.wasEdited()) {
				    pages.add(page);
				    explorer.pageAdded(new PageChangeEvent(page));
				    
				    // and edit it...
			        HTMLEditor editor = (HTMLEditor)app.getWindowCoordinator().getFrame("HTMLEditor");
			        editor.setPage(page);
			        editor.show();
       				}

			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   

    /**
     * Comment for <code>actionPageEdit</code>
     */
    private final Action actionPageEdit = new AbstractAction() {
        private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			try {
			    HTMLPage page = (HTMLPage)explorer.getSelectedNode().getUserObject();
			    if(page != null){
			        HTMLEditor editor = (HTMLEditor)app.getWindowCoordinator().getFrame("HTMLEditor");
			        editor.setPage(page);
			        editor.show();
			    }
			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   

    /**
     * Comment for <code>actionPageProperties</code>
     */
    private final Action actionPageProperties = new AbstractAction() {
        private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			try {
			    HTMLPage page = (HTMLPage)explorer.getSelectedNode().getUserObject();
			    if(page != null){
			        HTMLPagePropertiesEditor editor = new HTMLPagePropertiesEditor(explorer,page);
			        editor.setVisible(true);
			        if(editor.wasEdited()) {
					    explorer.pageEdited(new PageChangeEvent(page));
			        }
			    }
			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   

    /**
     * Comment for <code>actionPageDelete</code>
     */
    private final Action actionPageDelete = new AbstractAction() {
        private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			try {
			    HTMLPage page = (HTMLPage)explorer.getSelectedNode().getUserObject();
			    if(page != null){
			        if(Dialogs.question(explorer, "Delete page " + page.getName())){
			            pages.delete(page);
					    explorer.pageRemoved(new PageChangeEvent(page));
			        }
			    }

			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   
	
    /**
     * Comment for <code>actionPageShow</code>
     */
    private final Action actionPageShow = new AbstractAction() {
        private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			try {
			    HTMLPage page = (HTMLPage)explorer.getSelectedNode().getUserObject();
			    if(page != null){
			        HTMLDisplay display = (HTMLDisplay)app.getWindowCoordinator().getFrame("HTMLDisplayProxy");
			        display.showPage(page);
			    }


			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   
	

}
