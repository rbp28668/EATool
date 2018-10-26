/*
 * MetaModelViewerActionSet.java
 * Project: EATool
 * Created on 17-Feb-2006
 *
 */
package alvahouse.eatool.gui.graphical.standard.metamodel;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.DeleteConfirmationDialog;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.gui.MetaEntityEditor;
import alvahouse.eatool.gui.graphical.layout.Node;
import alvahouse.eatool.gui.graphical.standard.StandardDiagramViewer;
import alvahouse.eatool.gui.graphical.standard.StandardDiagramViewerActionSet;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.base.DeleteDependenciesList;
import alvahouse.eatool.repository.graphical.standard.Connector;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramType;
import alvahouse.eatool.repository.graphical.standard.Symbol;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;

/**
 * MetaModelViewerActionSet is an ActionSet that provides the actions for
 * the menus and popups in a MetaModelViewer.
 * 
 * @author rbp28668
 */
public class MetaModelViewerActionSet extends StandardDiagramViewerActionSet {

    private Repository repository;
    /**
     * @param viewer
     */
    public MetaModelViewerActionSet(StandardDiagramViewer viewer, Application app, Repository repository) {
        super(viewer, app, repository);
       
        this.repository = repository;
        
        addAction("AddConnectors", actionAddConnectors);
        
        addAction("MetaEntityEdit", actionMetaEntityEdit);
        addAction("MetaEntityRemove", actionMetaEntityRemove);
        addAction("MetaEntityDelete", actionMetaEntityDelete);
        
        
        addAction("MetaRelationshipRemove", actionMetaRelationshipRemove);
        addAction("MetaRelationshipDelete", actionMetaRelationshipDelete);
    }

	/** Add connectors for any meta-relationships not already on the diagram */
	private final Action actionAddConnectors = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
           try {
				StandardDiagram diagram = (StandardDiagram)getViewer().getDiagram();
				StandardDiagramType dt = (StandardDiagramType)diagram.getType();
				
				for(Iterator iter = diagram.getNodes().iterator(); iter.hasNext();){
					Symbol symbol = (Symbol)iter.next();
					
					MetaEntity me = (MetaEntity)symbol.getItem();
					Set connected = me.getModel().getMetaRelationshipsFor(me);
					
					for(Iterator ir = connected.iterator(); ir.hasNext(); ){
						MetaRelationship r = (MetaRelationship)ir.next();
						MetaEntity farMetaEntity = null;
						if(r.start().connectsTo() == me){
						    farMetaEntity = r.finish().connectsTo();
						}
						if(r.finish().connectsTo() == me){
						    farMetaEntity = r.start().connectsTo();
						}
						
						Node near = diagram.lookupNode(me);
						Node far = diagram.lookupNode(farMetaEntity);
						
						// Need both ends to do anything with and connector type must be supported
						if(near != null && far != null ){ 
							if(diagram.lookupArc(r) == null){
								diagram.addArcForObject(r, me, farMetaEntity);
							}
						}
					}
					
				}
           } catch(Throwable t) {
                new ExceptionDisplay(getViewer(),t);
            }
		}
	};
	
	/** Edit a meta-entity */
	private final Action actionMetaEntityEdit = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
           try {
               StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
               Symbol symbol = viewer.getSelectedSymbol();
               if(symbol != null) {
                   MetaEntity me = (MetaEntity)symbol.getItem();
                   MetaEntityEditor editor;
	               (editor = new MetaEntityEditor( getViewer(),me, repository)).setVisible(true);
	               editor.dispose();
               }
            } catch(Throwable t) {
                new ExceptionDisplay(getViewer(),t);
            }
		}
	};
	/** Remove a meta-entity from the diagram */
	private final Action actionMetaEntityRemove = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
           try {
               StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();

               Symbol symbol = viewer.getSelectedSymbol();
               if(symbol != null) {
                   StandardDiagram diagram = (StandardDiagram)viewer.getDiagram();
                   diagram.deleteNode(symbol);
                   viewer.refresh();
               }
            } catch(Throwable t) {
                new ExceptionDisplay(getViewer(),t);
            }
		}
	};
	/** Delete a meta-entity from the meta-model and diagram */
	private final Action actionMetaEntityDelete = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
           try {
               StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
               Symbol symbol = viewer.getSelectedSymbol();
               if(symbol != null) {
                   MetaEntity me = (MetaEntity)symbol.getItem();

                   DeleteDependenciesList dependencies = repository.getDeleteDependencies(me);

                   DeleteConfirmationDialog dlg;
                   (dlg = new DeleteConfirmationDialog(getViewer(), dependencies)).setVisible(true);
                   if(dlg.wasEdited()){
	                   StandardDiagram diagram = (StandardDiagram)viewer.getDiagram();
	                   diagram.deleteNode(symbol);
	                   viewer.refresh();
                   }
                   dlg.dispose();
               }
          } catch(Throwable t) {
                new ExceptionDisplay(getViewer(),t);
            }
		}
	};
	/** Remove a MetaRelationship from the diagram */
	private final Action actionMetaRelationshipRemove = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
           try {
               StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
               Connector con = viewer.getSelectedConnector();
               if(con != null) {
                   StandardDiagram diagram = (StandardDiagram)viewer.getDiagram();
                   diagram.deleteArc(con);
                   viewer.refresh();
               }
               
            } catch(Throwable t) {
                new ExceptionDisplay(getViewer(),t);
            }
		}
	};
	/** Delete a MetaRelationship from the meta-model and diagram */
	private final Action actionMetaRelationshipDelete = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
           try {
               StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
               Connector con = viewer.getSelectedConnector();
               if(con != null) {

                   MetaRelationship mr = (MetaRelationship)con.getItem();

                   DeleteDependenciesList dependencies = repository.getDeleteDependencies(mr);

                   DeleteConfirmationDialog dlg;
                   (dlg = new DeleteConfirmationDialog(getViewer(), dependencies)).setVisible(true);
                   if(dlg.wasEdited()){
	                   StandardDiagram diagram = (StandardDiagram)viewer.getDiagram();
	                   diagram.deleteArc(con);
	                   viewer.refresh();
                   }
                   dlg.dispose();
                   
               }
            } catch(Throwable t) {
                new ExceptionDisplay(getViewer(),t);
            }
		}
	};

}
