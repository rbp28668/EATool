/*
 * MetaModelActionSet.java
 *
 * Created on 28 January 2002, 12:43
 */

package alvahouse.eatool.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import alvahouse.eatool.Application;
import alvahouse.eatool.Main;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.base.DeleteDependenciesList;
import alvahouse.eatool.repository.metamodel.MetaEntityDisplayHint;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.util.UUID;

/**
 *
 * @author  rbp28668
 */
public class MetaModelActionSet extends ActionSet {

    private Application app;
    private Repository repository;
    
    /** Creates new MetaModelActionSet
     * @param mme Parent meta-model explorer frame
     */
    public MetaModelActionSet(MetaModelExplorerFrame mme, Application app, Repository repository) {
        super();
        explorer = mme;
        this.app = app;
        this.repository = repository;
        
        addAction("MetaEntityNew",actionMetaEntityNew);
        addAction("MetaEntityEdit",actionMetaEntityEdit);
        addAction("MetaEntityDisplayHintEdit",actionMetaEntityDisplayHintEdit);
        addAction("MetaEntityDelete",actionMetaEntityDelete);
        addAction("MetaEntityRename",actionMetaEntityRename);
        addAction("MetaEntityAddProperty",actionMetaEntityAddProperty);
        addAction("MetaEntityAddRelationship",actionMetaEntityAddRelationship);
        addAction("MetaRelationshipNew",actionMetaRelationshipNew);
        addAction("MetaRelationshipEdit",actionMetaRelationshipEdit);
        addAction("MetaRelationshipDelete",actionMetaRelationshipDelete);
        addAction("MetaPropertyEdit",actionMetaPropertyEdit);
        addAction("MetaRoleEdit",actionMetaRoleEdit);
        addAction("Test",actionTest);
        addAction(CopyKeyAction.NAME, new CopyKeyAction(mme,mme));
    }

        
    /**
     * MetaEntity New action
     */
    public final Action actionMetaEntityNew = new AbstractAction() {
 		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            try {
                MetaEntity me = new MetaEntity(new UUID());
                MetaEntityEditor editor;
                (editor = new MetaEntityEditor(explorer, me, repository)).setVisible(true);
                if(editor.wasEdited()) {
                    repository.getMetaModel().addMetaEntity(me);
                }
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   
    
    /**
     * MetaEntity Edit action
     */
    public final Action actionMetaEntityEdit = new AbstractAction() {
 		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            try {
                MetaEntity me = (MetaEntity)explorer.getSelectedNode().getUserObject();
                MetaEntityEditor editor;
                (editor = new MetaEntityEditor(explorer, me, repository)).setVisible(true);
                if(editor.wasEdited()) {
                	repository.getMetaModel().updateMetaEntity(me);
                }
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   

    /**
     * MetaEntityDisplayHint Edit action
     */
    public final Action actionMetaEntityDisplayHintEdit = new AbstractAction() {
 		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            try {
                MetaEntity me = (MetaEntity)explorer.getSelectedNode().getUserObject();
                MetaEntityDisplayHint dh = me.getDisplayHint();
                if(dh == null){
                    dh = new MetaEntityDisplayHint(me);
                }
                MetaEntityDisplayHintEditor editor = new MetaEntityDisplayHintEditor(explorer, dh, me);
                editor.setVisible(true);
                if(editor.wasEdited()){
                    me.setDisplayHint(dh);
                    repository.getMetaModel().updateMetaEntity(me);
                }
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   

    /**
     * MetaEntity Delete action
     */
    public final Action actionMetaEntityDelete = new AbstractAction() {
 		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            //System.out.println("MetaEntityDelete called.");
            try {
                DefaultMutableTreeNode node = explorer.getSelectedNode();
                MetaEntity me = (MetaEntity)node.getUserObject();

                DeleteDependenciesList dependencies = repository.getDeleteDependencies(me);

                DeleteConfirmationDialog dlg = new DeleteConfirmationDialog(explorer, dependencies);
                dlg.setVisible(true);
                
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   

    /**
     * MetaEntity Rename action
     */
    public final Action actionMetaEntityRename = new AbstractAction() {
 		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            try {
                DefaultMutableTreeNode node = explorer.getSelectedNode();
                MetaEntity me = (MetaEntity)node.getUserObject();
                String newName = JOptionPane.showInputDialog(null,
                    "Rename Meta-entity " + me.getName(),"EATool",
                    JOptionPane.QUESTION_MESSAGE);
                if(newName != null) {
                    me.setName(newName);
                    repository.getMetaModel().updateMetaEntity(me);
                }
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   

    /**
     * MetaEntity add Property action
     */
    public final Action actionMetaEntityAddProperty = new AbstractAction() {
 		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            //System.out.println("MetaEntityAddProperty called.");
            try {
                DefaultMutableTreeNode node = explorer.getSelectedNode();
                MetaEntity me = (MetaEntity)node.getUserObject();

                MetaProperty mp = new MetaProperty(new UUID());
                MetaPropertyEditor editor;
                (editor = new MetaPropertyEditor(explorer, mp, repository)).setVisible(true);
                if(editor.wasEdited()) {
                    me.addMetaProperty(mp);
                    repository.getMetaModel().updateMetaEntity(me);
                }
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   

    /**
     * MetaRelationship Add action
     */
    public final Action actionMetaEntityAddRelationship = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            //System.out.println("MetaEntityAddRelationship called.");
            try {
                MetaEntity me = (MetaEntity)explorer.getSelectedNode().getUserObject();
                MetaRelationship mr = new MetaRelationship(new UUID());
                mr.getMetaRole(new UUID()).setConnection(me);
                mr.getMetaRole(new UUID());
                MetaRelationshipEditor editor;
                (editor = new MetaRelationshipEditor(explorer, mr, repository)).setVisible(true);
                if(editor.wasEdited()) {
                    repository.getMetaModel().addMetaRelationship(mr);
                }
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   

    /**
     * MetaRelationship New action
     */
    public final Action actionMetaRelationshipNew = new AbstractAction() {
 		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            try {
                MetaRelationship mr = new MetaRelationship(new UUID());
                mr.getMetaRole(new UUID());
                mr.getMetaRole(new UUID());
                MetaRelationshipEditor editor;
                (editor = new MetaRelationshipEditor(explorer, mr, repository)).setVisible(true);
                if(editor.wasEdited()) {
                    repository.getMetaModel().addMetaRelationship(mr);
                }
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   
    
    /**
     * MetaRelationship Edit action
     */
    public final Action actionMetaRelationshipEdit  = new AbstractAction() {
 		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            try {
                MetaRelationship mr = (MetaRelationship)explorer.getSelectedNode().getUserObject();
                MetaRelationshipEditor editor;
                (editor = new MetaRelationshipEditor(explorer, mr, repository)).setVisible(true);
                if(editor.wasEdited()) {
                	repository.getMetaModel().updateMetaRelationship(mr);
                }
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   
   
    public final Action actionMetaPropertyEdit = new AbstractAction() {
 		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            try {
                MetaProperty mp = (MetaProperty)explorer.getSelectedNode().getUserObject();
                MetaPropertyEditor editor;
                (editor = new MetaPropertyEditor(explorer, mp, repository)).setVisible(true);
                if(editor.wasEdited()) {
                	if(mp.getContainer() instanceof MetaEntity) {
                		MetaEntity me = (MetaEntity)mp.getContainer();
                		repository.getMetaModel().updateMetaEntity(me);
                	} else if(mp.getContainer() instanceof MetaRelationship) {
                		MetaRelationship mr = (MetaRelationship)mp.getContainer();
                		repository.getMetaModel().updateMetaRelationship(mr);
                	} else if(mp.getContainer() instanceof MetaRole) {
                		MetaRole mr = (MetaRole)mp.getContainer();
                		repository.getMetaModel().updateMetaRelationship(mr.getMetaRelationship());
                 	}
                }
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   

    
    /**
     * MetaRelationship Delete action
     */
    public final Action actionMetaRelationshipDelete = new AbstractAction() {
 		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            try {
                DefaultMutableTreeNode node = explorer.getSelectedNode();
                MetaRelationship mr = (MetaRelationship)node.getUserObject();
                DeleteDependenciesList dependencies = repository.getDeleteDependencies(mr);
                DeleteConfirmationDialog dlg = new DeleteConfirmationDialog(explorer, dependencies);
                dlg.setVisible(true);
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   
    
    public final Action actionMetaRoleEdit = new AbstractAction() {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            try {
                MetaRole mr = (MetaRole)explorer.getSelectedNode().getUserObject();
                MetaRoleEditor editor;
                (editor = new MetaRoleEditor(explorer, mr, repository)).setVisible(true);
                if(editor.wasEdited()) {
                	repository.getMetaModel().updateMetaRelationship(mr.getMetaRelationship());
                }
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   

    public final Action actionTest = new AbstractAction() {
 		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            System.out.println("Test called from " 
                + e.getSource().getClass().getName()
                + ", " + e.getActionCommand());
        }
    };   

    
    private MetaModelExplorerFrame explorer;
}
