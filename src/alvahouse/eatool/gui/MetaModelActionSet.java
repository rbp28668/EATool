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
import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.repository.metamodel.impl.MetaEntityImpl;
import alvahouse.eatool.repository.metamodel.impl.MetaPropertyImpl;
import alvahouse.eatool.repository.metamodel.impl.MetaRelationshipImpl;
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
        public void actionPerformed(ActionEvent e) {
            try {
                MetaEntityImpl me = new MetaEntityImpl(new UUID());
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
        public void actionPerformed(ActionEvent e) {
            try {
                MetaEntityImpl me = (MetaEntityImpl)explorer.getSelectedNode().getUserObject();
                MetaEntityEditor editor;
                (editor = new MetaEntityEditor(explorer, me, repository)).setVisible(true);
                if(editor.wasEdited()) {
                	repository.getMetaModel().fireMetaEntityChanged(me);
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
        public void actionPerformed(ActionEvent e) {
            try {
                MetaEntityImpl me = (MetaEntityImpl)explorer.getSelectedNode().getUserObject();
                MetaEntityDisplayHint dh = me.getDisplayHint();
                if(dh == null){
                    dh = new MetaEntityDisplayHint(me);
                }
                MetaEntityDisplayHintEditor editor = new MetaEntityDisplayHintEditor(explorer, dh, me);
                editor.setVisible(true);
                if(editor.wasEdited()){
                    me.setDisplayHint(dh);
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
        public void actionPerformed(ActionEvent e) {
            //System.out.println("MetaEntityDelete called.");
            try {
                DefaultMutableTreeNode node = explorer.getSelectedNode();
                MetaEntityImpl me = (MetaEntityImpl)node.getUserObject();

                DeleteDependenciesList dependencies = repository.getDeleteDependencies(me);

                DeleteConfirmationDialog dlg;
                (dlg = new DeleteConfirmationDialog(explorer, dependencies)).setVisible(true);
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   

    /**
     * MetaEntity Rename action
     */
    public final Action actionMetaEntityRename = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
                DefaultMutableTreeNode node = explorer.getSelectedNode();
                MetaEntityImpl me = (MetaEntityImpl)node.getUserObject();
                String newName = JOptionPane.showInputDialog(null,
                    "Rename Meta-entity " + me.getName(),"EATool",
                    JOptionPane.QUESTION_MESSAGE);
                if(newName != null) {
                    me.setName(newName);
                    repository.getMetaModel().fireMetaEntityChanged(me);
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
        public void actionPerformed(ActionEvent e) {
            //System.out.println("MetaEntityAddProperty called.");
            try {
                DefaultMutableTreeNode node = explorer.getSelectedNode();
                MetaEntityImpl me = (MetaEntityImpl)node.getUserObject();

                MetaPropertyImpl mp = new MetaPropertyImpl(new UUID());
                MetaPropertyEditor editor;
                (editor = new MetaPropertyEditor(explorer, mp, repository)).setVisible(true);
                if(editor.wasEdited()) {
                    me.addMetaProperty(mp);
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
        public void actionPerformed(ActionEvent e) {
            //System.out.println("MetaEntityAddRelationship called.");
            try {
                MetaEntityImpl me = (MetaEntityImpl)explorer.getSelectedNode().getUserObject();
                MetaRelationshipImpl mr = new MetaRelationshipImpl(new UUID());
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
        public void actionPerformed(ActionEvent e) {
            try {
                MetaRelationshipImpl mr = new MetaRelationshipImpl(new UUID());
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
        public void actionPerformed(ActionEvent e) {
            try {
                MetaRelationshipImpl mr = (MetaRelationshipImpl)explorer.getSelectedNode().getUserObject();
                MetaRelationshipEditor editor;
                (editor = new MetaRelationshipEditor(explorer, mr, repository)).setVisible(true);
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   
   
    public final Action actionMetaPropertyEdit = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
                MetaPropertyImpl mp = (MetaPropertyImpl)explorer.getSelectedNode().getUserObject();
                MetaPropertyEditor editor;
                (editor = new MetaPropertyEditor(explorer, mp, repository)).setVisible(true);
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   

    
    /**
     * MetaRelationship Delete action
     */
    public final Action actionMetaRelationshipDelete = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
                DefaultMutableTreeNode node = explorer.getSelectedNode();
                MetaRelationshipImpl mr = (MetaRelationshipImpl)node.getUserObject();
                DeleteDependenciesList dependencies = repository.getDeleteDependencies(mr);
                DeleteConfirmationDialog dlg = new DeleteConfirmationDialog(explorer, dependencies);
                dlg.setVisible(true);
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   
    
    public final Action actionMetaRoleEdit = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
                MetaRole mr = (MetaRole)explorer.getSelectedNode().getUserObject();
                MetaRoleEditor editor;
                (editor = new MetaRoleEditor(explorer, mr, repository)).setVisible(true);
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
        }
    };   

    public final Action actionTest = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            System.out.println("Test called from " 
                + e.getSource().getClass().getName()
                + ", " + e.getActionCommand());
        }
    };   

    
    private MetaModelExplorerFrame explorer;
}
