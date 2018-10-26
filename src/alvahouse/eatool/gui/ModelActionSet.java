/*
 * ModelActionSet.java
 *
 * Created on 21 February 2002, 08:31
 */

package alvahouse.eatool.gui;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import alvahouse.eatool.Application;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.base.DeleteDependenciesList;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.Relationship;

/**
 * This is the set of actions for the model explorer.  It allows these
 * actions to be invoked by name which allows the menus to be configured
 * from a configuration file.
 * @author  rbp28668
 */
public class ModelActionSet extends ActionSet {
    /** the explorer that these actions are for*/
    private ModelExplorer explorer;
    private Application app;
    private Repository repository;

    /** Creates new ModelActionSet 
     * @param me is the model explorer these actions are for.
     */
    public ModelActionSet(ModelExplorer me, Application app, Repository repository) {
        super();
        explorer = me;
        this.app = app;
        this.repository = repository;
        
        addAction("EntityNew", actionEntityNew);
        addAction("MetaEntityBrowse",actionMetaEntityBrowse);
        addAction("MetaEntityTable", actionMetaEntityTable);
        addAction("EntityNewFromMeta",actionEntityNewFromMeta);
        addAction("EntityBrowse", actionEntityBrowse);
        addAction("EntityEdit", actionEntityEdit);
        addAction("EntityDelete", actionEntityDelete);
        addAction("RelationshipNew", actionRelationshipNew);
        addAction("RelationshipNewFromMeta", actionRelationshipNewFromMeta);
        addAction("RelationshipEdit", actionRelationshipEdit);
        addAction("RelationshipDelete", actionRelationshipDelete);
        addAction("Test",actionTest);
        addAction(CopyKeyAction.NAME, new CopyKeyAction(me,me));

    }

    private final Action actionEntityNew = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
            	// Get a list of all non-abstract entities (i.e.ones we
            	// can create entities for.
                List metaList = new LinkedList();
                for(Iterator iter = repository.getMetaModel().getMetaEntities().iterator();
                iter.hasNext();) {
                    MetaEntity me = (MetaEntity)iter.next();
                    if(!me.isAbstract())
                        metaList.add(me);
                }
                MetaEntity meta = (MetaEntity)JOptionPane.showInputDialog(
                explorer,  "Select Entity Type", "EATool",
                JOptionPane.QUESTION_MESSAGE, null, metaList.toArray(), null);
                
                if(meta != null) {
                    Entity entity = new Entity(meta);
                    EntityEditor editor;
                    (editor = new EntityEditor(explorer, entity, repository)).setVisible(true);
                    if(editor.wasEdited()) {
                        repository.getModel().addEntity(entity);
                    }
                }
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
            
        }
    };   

    private final Action actionMetaEntityBrowse = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
               MetaEntity meta = (MetaEntity)explorer.getSelectedNode().getUserObject();
               if(meta != null) {
                   Model model = repository.getModel();
                   ModelBrowser browser = (ModelBrowser) app.getWindowCoordinator().getFrame("ModelBrowser");
                   browser.browse(meta, model);
                   browser.setVisible(true);
               }
                
           } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
            
        }
    };   

    /**
     * Comment for <code>actionMetaEntityTable</code>
     */
    private final Action actionMetaEntityTable = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
                MetaEntity meta = (MetaEntity)explorer.getSelectedNode().getUserObject();
                 
                if(meta != null) {
                    Model model = explorer.getModel();
                    
					WindowCoordinator wc = app.getWindowCoordinator();
					TabularEntityEditor editor =
						(TabularEntityEditor) wc.getFrame(
							TabularEntityEditor.getWindowName(meta),
							new TabularEntityEditor.WindowFactory(
								model,
								meta,
								app));
					editor.show();
               }
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
         }
        
    };
    
    private final Action actionEntityNewFromMeta = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
                MetaEntity meta = (MetaEntity)explorer.getSelectedNode().getUserObject();
                 
                if(meta != null) {
                    Entity entity = new Entity(meta);
                    EntityEditor editor;
                    (editor = new EntityEditor(explorer, entity,repository)).setVisible(true);
                    if(editor.wasEdited()) {
                  		repository.getModel().addEntity(entity);
                    }
                }
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
         }
    };   
    
    private final Action actionEntityBrowse = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
                Entity entity = (Entity)explorer.getSelectedNode().getUserObject();
                ModelBrowser browser = (ModelBrowser) app.getWindowCoordinator().getFrame("ModelBrowser");
                browser.browse(entity);
                browser.setVisible(true);
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };
    
    private final Action actionEntityEdit = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
                Entity entity = (Entity)explorer.getSelectedNode().getUserObject();
                EntityEditor editor;
                (editor = new EntityEditor(explorer, entity,repository)).setVisible(true);
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   
    
    private final Action actionEntityDelete = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
                DefaultMutableTreeNode node = explorer.getSelectedNode();
                Entity entity = (Entity)node.getUserObject();

                DeleteDependenciesList dependencies = repository.getDeleteDependencies(entity);

                DeleteConfirmationDialog dlg;
                (dlg = new DeleteConfirmationDialog(explorer, dependencies)).setVisible(true);
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   
    
    private final Action actionRelationshipNew = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
        	try {
	            MetaRelationship meta = Dialogs.selectMetaRelationship(explorer, repository);
	            if(meta != null) {
	                Relationship r = new Relationship(meta);
//	                System.out.println("New relationship created: " + r.getKey());
	               	RelationshipEditor editor;
	                (editor = new RelationshipEditor(explorer, r, repository.getModel())).setVisible(true);
	                if(editor.wasEdited()) {
//	                	System.out.println("Relationship added: " 
//	                		+ r.getKey()
//	                		+ " connects "
//	                		+ r.start().connectsTo()
//	                		+ " to "
//	                		+ r.finish().connectsTo()
//	                	);
	                	
	              		repository.getModel().addRelationship(r);
	                }
	                
	            }
                
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };
    
    private final Action actionRelationshipNewFromMeta = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
        	try {
	            MetaRelationship meta = (MetaRelationship)explorer.getSelectedNode().getUserObject();
	            if(meta != null) {
	                Relationship r = new Relationship(meta);
	               	RelationshipEditor editor;
	                (editor = new RelationshipEditor(explorer, r, repository.getModel())).setVisible(true);
	                if(editor.wasEdited()) {
	              		repository.getModel().addRelationship(r);
	                }
	                
	            }
                
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   
    
    private final Action actionRelationshipEdit = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
             try {
                Relationship relationship = (Relationship)explorer.getSelectedNode().getUserObject();
                RelationshipEditor editor;
                (editor = new RelationshipEditor(explorer, relationship, repository.getModel())).setVisible(true);
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
       }
    };   
    private final Action actionRelationshipDelete = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
                DefaultMutableTreeNode node = explorer.getSelectedNode();
                Relationship r = (Relationship)node.getUserObject();

                DeleteDependenciesList dependencies = repository.getDeleteDependencies(r);

                DeleteConfirmationDialog dlg;
                (dlg = new DeleteConfirmationDialog(explorer, dependencies)).setVisible(true);
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   
    
    private final Action actionTest = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            System.out.println("Test called from " 
                + e.getSource().getClass().getName()
                + ", " + e.getActionCommand());
        }
    };   
    
}
