/*
 * ModelViewerActionSet.java
 * Project: EATool
 * Created on 17-Feb-2006
 *
 */
package alvahouse.eatool.gui.graphical.standard.model;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.DeleteConfirmationDialog;
import alvahouse.eatool.gui.EntityEditor;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.gui.ItemSelectionDialog;
import alvahouse.eatool.gui.ModelBrowser;
import alvahouse.eatool.gui.graphical.layout.Node;
import alvahouse.eatool.gui.graphical.standard.StandardDiagramViewer;
import alvahouse.eatool.gui.graphical.standard.StandardDiagramViewerActionSet;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.base.DeleteDependenciesList;
import alvahouse.eatool.repository.graphical.standard.Connector;
import alvahouse.eatool.repository.graphical.standard.ConnectorType;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramType;
import alvahouse.eatool.repository.graphical.standard.Symbol;
import alvahouse.eatool.repository.graphical.standard.SymbolType;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Relationship;

/**
 * ModelViewerActionSet is the ActionSet for a ModelViewer.
 * 
 * @author rbp28668
 */
public class ModelViewerActionSet extends StandardDiagramViewerActionSet {

    private Application app;
    private Repository repository;
    
    /**
     * @param viewer
     */
    public ModelViewerActionSet(StandardDiagramViewer viewer, Application app, Repository repository) {
        super(viewer, app, repository);
        this.app = app;
        this.repository = repository;

        // Edit
		addAction("SearchAndAdd", actionSearchAndAdd);
		addAction("AddConnectors", actionAddConnectors);
		addAction("AddSelectedConnectors", actionAddSelectedConnectors);

		// Entity popup
		addAction("EntityBrowse", actionEntityBrowse);
		addAction("EntityEdit", actionEntityEdit);
        addAction("EntityRemove", actionEntityRemove);
        addAction("EntityDelete", actionEntityDelete);
        
        // Relationship popup
        addAction("RelationshipRemove", actionRelationshipRemove);
        addAction("RelationshipDelete", actionRelationshipDelete);

    }


    /** Add selected connectors allows the user to select which type of relationships
	 * are added to the diagram. */
	private final Action actionAddSelectedConnectors = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
		    
			try {
				StandardDiagram diagram = (StandardDiagram)getViewer().getDiagram();
				StandardDiagramType dt = (StandardDiagramType)diagram.getType();
				
				List<MetaRelationship> allowable = new LinkedList<MetaRelationship>();
				for(ConnectorType ct : dt.getConnectorTypes()){
				    allowable.add(ct.getRepresents(repository.getMetaModel()));
				}
				
				// Possible that there are no connectors that can be displayed.
				if(allowable.isEmpty()){
				    return;
				}
				
				ItemSelectionDialog<MetaRelationship> dlg = new ItemSelectionDialog<>(getViewer(),"Select relationship type(s)", allowable);
				dlg.setVisible(true);
				if(dlg.wasEdited()){
				    Set<MetaRelationship> selected = dlg.getSelectedSet();
				    
					for(Iterator<? extends Node> iter = diagram.getNodes().iterator(); iter.hasNext();){
						Symbol symbol = (Symbol)iter.next();
						
						Entity entity = (Entity)symbol.getItem();
						Set<Relationship> connected = entity.getConnectedRelationships();
						
						for(Relationship r : connected){
							
							if(selected.contains(r.getMeta())){
								Entity farEntity = null;
								if(r.start().connectsTo() == entity){
									farEntity = r.finish().connectsTo();
								}
								if(r.finish().connectsTo() == entity){
									farEntity = r.start().connectsTo();
								}
								
								Node near = diagram.lookupNode(entity);
								Node far = diagram.lookupNode(farEntity);
								
								// Need both ends to do anything with and connector type must be supported
								if(near != null && far != null 
								    && dt.hasConnectorTypeFor(r.getMeta())){ 
									if(diagram.lookupArc(r) == null){
										diagram.addArcForObject(r,entity,farEntity);
									}
								}
							}
						}
						
					}
				    
				}
				dlg.dispose();
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(),ex);
			}
		    
		}
	};

	/** Add Connectors action */
	private final Action actionAddConnectors = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
				StandardDiagram diagram = (StandardDiagram)getViewer().getDiagram();
				diagram.addConnectors();
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(),ex);
			}
		}
	};   

	
	
	/** Search and Add action */
	private final Action actionSearchAndAdd = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
				String query =
					(String) JOptionPane.showInputDialog(
						getViewer(),
						"Please enter the query",
						"EATool",
						JOptionPane.QUESTION_MESSAGE,
						null,
						null,
						null);
				if(query != null){
					Set<Entity> entities = repository.searchForEntities(query);
					if(entities.isEmpty()){
						JOptionPane.showMessageDialog(getViewer(),"Search returned no results");
					} else {
						StandardDiagram diagram = (StandardDiagram)getViewer().getDiagram();
						StandardDiagramType type = (StandardDiagramType)diagram.getType();
						
						int size = (int)Math.sqrt(entities.size());
						int ix = 0;
						int iy = 0;
						for(Entity entity : entities){
							
							float x = (1 + ix) * 20;
							float y = (1 + iy) * 20;
							if(type.hasSymbolTypeFor(entity.getMeta())){
								SymbolType st = type.getSymbolTypeFor(entity.getMeta());
								Symbol node = st.newSymbol(entity,x,y);
								diagram.addSymbol(node);
	
								++ix;
								if(ix > size){
									++iy;
									ix = 0;
								}
							}
						}
					}
				}

			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(),ex);
			}
		}
	};   


	/** Edit an entity*/
	private final Action actionEntityEdit = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
           try {
               StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
               Symbol symbol = viewer.getSelectedSymbol();
               //System.out.println("actionEntityEdit: " + symbol.toString());
               if(symbol != null) {
	               Entity entity = (Entity)symbol.getItem();
	                EntityEditor editor;
	                (editor = new EntityEditor(getViewer(), entity, repository)).setVisible(true);
	                if(editor.wasEdited()){
	                	repository.getModel().updateEntity(entity);
	                    viewer.refresh();
	                }
               }
            } catch(Throwable t) {
                new ExceptionDisplay(getViewer(),t);
            }
		}
	};

	/** Browse a symbol's attached entity */
	private final Action actionEntityBrowse = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
           try {
               StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
               Symbol symbol = viewer.getSelectedSymbol();
               if(symbol != null) {
                   Object obj = symbol.getItem();
                   if(obj instanceof Entity){
                       ModelBrowser browser = (ModelBrowser) app.getWindowCoordinator().getFrame("ModelBrowser");
                       browser.browse((Entity)obj);
                       browser.setVisible(true);
                   }
               }
            } catch(Throwable t) {
                new ExceptionDisplay(getViewer(),t);
            }
		}
	};


	/** Remove selected Symbol from diagram */
	private final Action actionEntityRemove = new AbstractAction() {
		private static final long serialVersionUID = 1L;

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
	
	/** Remove selected Sybmol from diagram and delete attached Entity from model */
	private final Action actionEntityDelete = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
           try {
               StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
               Symbol symbol = viewer.getSelectedSymbol();
               if(symbol != null) {
                   Entity entity = (Entity)symbol.getItem();

                   DeleteDependenciesList dependencies = repository.getDeleteDependencies(entity);

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


	/** Remove selected connector from diagram */
	private final Action actionRelationshipRemove = new AbstractAction() {
		private static final long serialVersionUID = 1L;

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
	
	/** Remove selected connector from diagram and delete underlying relationship from model. */
	private final Action actionRelationshipDelete = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
	           try {
	               StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
	               Connector con = viewer.getSelectedConnector();
	               if(con != null) {

	                   Relationship relationship = (Relationship)con.getItem();

	                   DeleteDependenciesList dependencies = repository.getDeleteDependencies(relationship);

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
