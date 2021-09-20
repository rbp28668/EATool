/*
 * ImportMappingActionSet.java
 * Project: EATool
 * Created on 03-Dec-2005
 *
 */
package alvahouse.eatool.gui.mappings;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.mapping.EntityTranslation;
import alvahouse.eatool.repository.mapping.ImportMapping;
import alvahouse.eatool.repository.mapping.ImportMappings;
import alvahouse.eatool.repository.mapping.PropertyTranslation;
import alvahouse.eatool.repository.mapping.PropertyTranslationCollection;
import alvahouse.eatool.repository.mapping.RelationshipTranslation;
import alvahouse.eatool.repository.mapping.RoleTranslation;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRole;

/**
 * ImportMappingActionSet provides action handlers for the import
 * mapping explorer.
 * 
 * @author rbp28668
 */
public class ImportMappingActionSet extends ActionSet {

    	private ImportMappingExplorer explorer;
    	private ImportMappings mappings;
    	private Application app;
    	private Repository repository;
    	
        /**
         * Creates a mapping set for the explorer.
         * @param explorer is the ImportMappingExplorer used for displaying and editing the mappings.
         * @param mappings are the mappings being edited.
         * @param metaModel is the meta model the mappings map to.
         */
        public ImportMappingActionSet(ImportMappingExplorer explorer, ImportMappings mappings, Application app, Repository repository) {
            super();
            if(explorer == null) {
                throw new NullPointerException("Can't create ImportMappingActionSet with null explorer");
            }
            if(mappings == null) {
                throw new NullPointerException("Can't explore null mappings");
            }
            if(app == null){
                throw new NullPointerException("Can't edit mappings with null application");
            }
            if(repository == null){
                throw new NullPointerException("Can't edit mappings with null repository");
            }
            
            this.explorer = explorer;
            this.mappings = mappings;
            this.app = app;
            this.repository = repository;
            
            
    		addAction("ImportMappingNew",actionImportMappingNew);
    		addAction("ImportMappingEdit",actionImportMappingEdit);
    		addAction("ImportMappingDelete",actionImportMappingDelete);
    		addAction("EntityTranslationNew",actionEntityTranslationNew);
    		addAction("RelationshipTranslationNew",actionRelationshipTranslationNew);
    		addAction("EntityTranslationEdit",actionEntityTranslationEdit);
    		addAction("EntityTranslationDelete",actionEntityTranslationDelete);
    		addAction("RelationshipTranslationEdit",actionRelationshipTranslationEdit);
    		addAction("RelationshipTranslationDelete",actionRelationshipTranslationDelete);
    		addAction("PropertyTranslationNew",actionPropertyTranslationNew);
    		addAction("PropertyTranslationEdit",actionPropertyTranslationEdit);
    		addAction("PropertyTranslationDelete",actionPropertyTranslationDelete);
    		addAction("RoleTranslationEdit",actionRoleTranslationEdit);
    		
    		
        }
        
    	/**
         * @param parentNode
         * @return
         */
        private MetaEntity getControllingMetaEntity(DefaultMutableTreeNode parentNode) throws Exception{
            Object parent = parentNode.getUserObject();
            MetaEntity me = null;
            if(parent instanceof EntityTranslation){
                EntityTranslation parentTranslation = (EntityTranslation)parent;
            	me = parentTranslation.getMeta(repository.getMetaModel());
            } else if (parent instanceof RoleTranslation){
                RoleTranslation parentTranslation = (RoleTranslation)parent;
                MetaRole mr = parentTranslation.getMeta(repository.getMetaModel());
                me = mr.connectsTo();
            }
            if(me == null){
                throw new IllegalStateException("PropertyTranslation parent is of invalid type " + parent.getClass().getName());
            }
            return me;
        }

        private final Action actionImportMappingNew = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
    			try {
    			    //System.out.println("ImportMappingNew");
    			    ImportMapping mapping = new ImportMapping();
    			    ImportMappingDialog dialog = new ImportMappingDialog(explorer,"New Import Mapping",mapping,app,repository);
    			    dialog.setVisible(true);
    			    if(dialog.wasEdited()){
    			        mappings.add(mapping);
    			    }
    			} catch(Throwable t) {
    				new ExceptionDisplay(explorer,t);
    			}
    		}
    	};   

    	private final Action actionImportMappingEdit = new AbstractAction() {
            private static final long serialVersionUID = 1L;
    		public void actionPerformed(ActionEvent e) {
    			try {
    			    //System.out.println("ImportMappingEdit");
                    ImportMapping mapping = (ImportMapping)explorer.getSelectedNode().getUserObject();
                    ImportMappingDialog dialog = new ImportMappingDialog(explorer,"New Import Mapping",mapping,app,repository);
                    dialog.setVisible(true);
                    if(dialog.wasEdited()){
                        mappings.update(mapping);
                    }
                } catch(Throwable t) {
    				new ExceptionDisplay(explorer,t);
    			}
    		}
    	};   

    	private final Action actionImportMappingDelete = new AbstractAction() {
            private static final long serialVersionUID = 1L;
    		public void actionPerformed(ActionEvent e) {
    			try {
    			    //System.out.println("ImportMappingDelete");
                    ImportMapping mapping = (ImportMapping)explorer.getSelectedNode().getUserObject();
                    if(JOptionPane.showConfirmDialog(null,"Delete mapping " + mapping.getName() + "?","EATool",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                        mappings.remove(mapping);
                    }
    			} catch(Throwable t) {
    				new ExceptionDisplay(explorer,t);
    			}
    		}
    	};   
    	
        private final Action actionEntityTranslationNew = new AbstractAction() {
            private static final long serialVersionUID = 1L;
    		public void actionPerformed(ActionEvent e) {
    			try {
    			    //System.out.println("EntityTranslationNew");
    			    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)explorer.getSelectedNode().getParent();
                    ImportMapping mapping = (ImportMapping)parentNode.getUserObject();
                    EntityTranslation translation = new EntityTranslation();
                    
                    EntityMappingDialog dialog = new EntityMappingDialog(explorer, "New Entity Mapping", 
                            translation, repository.getMetaModel());
                    dialog.setVisible(true);
                    if(dialog.wasEdited()){
                        mapping.add(translation);
                        mappings.fireEntityMappingAdded(mapping);
                    }
    			} catch(Throwable t) {
    				new ExceptionDisplay(explorer,t);
    			}
    		}
    	};   




        private final Action actionRelationshipTranslationNew = new AbstractAction() {
            private static final long serialVersionUID = 1L;
    		public void actionPerformed(ActionEvent e) {
    			try {
    			    //System.out.println("RelationshipTranslationNew");

    			    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)explorer.getSelectedNode().getParent();
                    ImportMapping mapping = (ImportMapping)parentNode.getUserObject();
                    RelationshipTranslation translation = new RelationshipTranslation();
                    
                    RelationshipMappingDialog dialog = new RelationshipMappingDialog(explorer, "New Relationship Mapping", 
                            translation,repository.getMetaModel());
                    dialog.setVisible(true);
                    if(dialog.wasEdited()){
                        mapping.add(translation);
                        mappings.fireRelationshipMappingAdded(mapping);
                    }
    			} catch(Throwable t) {
    				new ExceptionDisplay(explorer,t);
    			}
    		}
    	};   



        private final Action actionEntityTranslationEdit = new AbstractAction() {
            private static final long serialVersionUID = 1L;
    		public void actionPerformed(ActionEvent e) {
    			try {
    			    //System.out.println("EntityTranslationEdit");
    			    DefaultMutableTreeNode node = (DefaultMutableTreeNode)explorer.getSelectedNode();
                    EntityTranslation translation = (EntityTranslation)node.getUserObject();
                    
                    EntityMappingDialog dialog = new EntityMappingDialog(explorer, "Edit Entity Mapping", 
                            translation, repository.getMetaModel());
                    dialog.setVisible(true);
                    if(dialog.wasEdited()){
                        mappings.fireEntityMappingEdited(translation);
                    }
    			} catch(Throwable t) {
    				new ExceptionDisplay(explorer,t);
    			}
    		}
    	};   



        private final Action actionEntityTranslationDelete = new AbstractAction() {
            private static final long serialVersionUID = 1L;
    		public void actionPerformed(ActionEvent e) {
    			try {
    			    //System.out.println("EntityTranslationDelete");
    			    DefaultMutableTreeNode node = (DefaultMutableTreeNode)explorer.getSelectedNode();
                    EntityTranslation translation = (EntityTranslation)node.getUserObject();
                    if(Dialogs.question(explorer,"Delete entity translation " + translation.toString() + "?")){
        			    DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent().getParent();
                        ImportMapping mapping = (ImportMapping)parent.getUserObject();
                        mapping.remove(translation);
                        mappings.fireEntityMappingDeleted(translation);
                    }
    			} catch(Throwable t) {
    				new ExceptionDisplay(explorer,t);
    			}
    		}
    	};   



        private final Action actionRelationshipTranslationEdit = new AbstractAction() {
            private static final long serialVersionUID = 1L;
    		public void actionPerformed(ActionEvent e) {
    			try {
    			    //System.out.println("RelationshipTranslationEdit");
       			    DefaultMutableTreeNode node = (DefaultMutableTreeNode)explorer.getSelectedNode();
                    RelationshipTranslation translation = (RelationshipTranslation)node.getUserObject();
                    
                    RelationshipMappingDialog dialog = new RelationshipMappingDialog(explorer, "Edit Relationship Mapping", 
                            translation, repository.getMetaModel());
                    dialog.setVisible(true);
                    if(dialog.wasEdited()){
                        mappings.fireRelationshipMappingEdited(translation);
                    }
     			} catch(Throwable t) {
    				new ExceptionDisplay(explorer,t);
    			}
    		}
    	};   



        private final Action actionRelationshipTranslationDelete = new AbstractAction() {
            private static final long serialVersionUID = 1L;
    		public void actionPerformed(ActionEvent e) {
    			try {
    			    //System.out.println("RelationshipTranslationDelete");
    			    DefaultMutableTreeNode node = (DefaultMutableTreeNode)explorer.getSelectedNode();
                    RelationshipTranslation translation = (RelationshipTranslation)node.getUserObject();
                    if(Dialogs.question(explorer,"Delete Relationship translation " + translation.toString() + "?")){
        			    DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent().getParent();
                        ImportMapping mapping = (ImportMapping)parent.getUserObject();
                        mapping.remove(translation);
                        mappings.fireRelationshipMappingDeleted(translation);
                    }
    			} catch(Throwable t) {
    				new ExceptionDisplay(explorer,t);
    			}
    		}
    	};   



        private final Action actionPropertyTranslationEdit = new AbstractAction() {
            private static final long serialVersionUID = 1L;
    		public void actionPerformed(ActionEvent e) {
    			try {
    			    //System.out.println("PropertyTranslationEdit");
    			    DefaultMutableTreeNode node = (DefaultMutableTreeNode)explorer.getSelectedNode();
    			    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
    			    
                    PropertyTranslation translation = (PropertyTranslation)node.getUserObject();
                    
                    MetaEntity me = getControllingMetaEntity(parentNode);
    				
                    PropertyMappingDialog dialog = new PropertyMappingDialog(explorer, "Edit Property Mapping", me, translation);
                    dialog.setVisible(true);
                    if(dialog.wasEdited()){
                        mappings.firePropertyMappingEdited(translation);
                    }
    			} catch(Throwable t) {
    				new ExceptionDisplay(explorer,t);
    			}
    		}
    	};   

        private final Action actionPropertyTranslationNew = new AbstractAction() {
            private static final long serialVersionUID = 1L;
    		public void actionPerformed(ActionEvent e) {
    			try {
    			    //System.out.println("PropertyTranslationNew");
    			    DefaultMutableTreeNode node = (DefaultMutableTreeNode)explorer.getSelectedNode();
                    PropertyTranslationCollection translation = (PropertyTranslationCollection)node.getUserObject();
                    
                    MetaEntity me = getControllingMetaEntity(node);
                    
                    PropertyTranslation pt = new PropertyTranslation();
                    PropertyMappingDialog dialog = new PropertyMappingDialog(explorer, "New Property Mapping", me, pt);
	                dialog.setVisible(true);
	                if(dialog.wasEdited()){
	                    translation.addProperty(pt);
	                    mappings.firePropertyMappingAdded(translation);
	                }
    			} catch(Throwable t) {
    				new ExceptionDisplay(explorer,t);
    			}
    		}
    	};   



        private final Action actionPropertyTranslationDelete = new AbstractAction() {
            private static final long serialVersionUID = 1L;
    		public void actionPerformed(ActionEvent e) {
    			try {
    			    //System.out.println("PropertyTranslationDelete");
    			    DefaultMutableTreeNode node = (DefaultMutableTreeNode)explorer.getSelectedNode();
                    PropertyTranslation translation = (PropertyTranslation)node.getUserObject();
                    if(Dialogs.question(explorer,"Delete Property translation " + translation.toString() + "?")){
        			    DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
                        PropertyTranslationCollection mapping = (PropertyTranslationCollection)parent.getUserObject();
                        mapping.remove(translation);
                        mappings.firePropertyMappingDeleted(translation);
                    }
    			} catch(Throwable t) {
    				new ExceptionDisplay(explorer,t);
    			}
    		}
    	};   



        private final Action actionRoleTranslationEdit = new AbstractAction() {
            private static final long serialVersionUID = 1L;
    		public void actionPerformed(ActionEvent e) {
    			try {
    			    //System.out.println("RoleTranslationEdit");
    			    DefaultMutableTreeNode node = (DefaultMutableTreeNode)explorer.getSelectedNode();
                    RoleTranslation translation = (RoleTranslation)node.getUserObject();
    			    String type = JOptionPane.showInputDialog(explorer,translation.getTypeName(),"EATool",JOptionPane.PLAIN_MESSAGE);
    			    if(type != null){
    			        translation.setType(type);
    			        mappings.fireRoleMappingChanged(translation);
    			    }
    			} catch(Throwable t) {
    				new ExceptionDisplay(explorer,t);
    			}
    		}
    	};   



}
