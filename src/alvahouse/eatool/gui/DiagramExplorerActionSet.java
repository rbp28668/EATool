package alvahouse.eatool.gui;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.WindowCoordinator.WindowFactory;
import alvahouse.eatool.gui.graphical.DiagramViewer;
import alvahouse.eatool.gui.graphical.standard.StandardDiagramViewer;
import alvahouse.eatool.gui.graphical.standard.metamodel.MetaModelViewer;
import alvahouse.eatool.gui.scripting.EventMapDialog;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.DiagramTypeFamily;
import alvahouse.eatool.repository.graphical.DiagramTypes;
import alvahouse.eatool.repository.graphical.Diagrams;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramType;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.scripting.Scripts;

/**
 * DiagramExplorerActionSet has the actions to take in response to UI
 * commands for the DiagramExplorer.
 * @author bruce.porteous
 *
 */
public class DiagramExplorerActionSet extends ActionSet {

	private DiagramExplorer explorer;
	private Repository repository;
	private Application app;
	

	/**
	 * Constructor for DiagramExplorerActionSet.
	 */
	public DiagramExplorerActionSet(DiagramExplorer dex, Repository repository, Application app) {
		super();
		explorer = dex;
		this.repository = repository;
		this.app = app;
		
		addAction("DiagramTypeNew",actionDiagramTypeNew);
		addAction("DiagramTypeEdit",actionDiagramTypeEdit);
		addAction("DiagramTypeDelete",actionDiagramTypeDelete);
		addAction("DiagramTypeEventMappings",actionDiagramTypeEventMappings);
		addAction("MetaModelDiagramNew",actionMetaModelDiagramNew);
		addAction("DiagramNew",actionDiagramNew);
		addAction("MetaModelDiagramEdit",actionMetaModelDiagramEdit);
		addAction("DiagramEdit",actionDiagramEdit);
		addAction("DiagramInformation",actionDiagramInformation);
		addAction("DiagramDelete",actionDiagramDelete);
		addAction("DiagramEventMappings",actionDiagramEventMappings);
		addAction(CopyKeyAction.NAME, new CopyKeyAction(dex,dex));
	}

 
	/**
     * @param metaModel
     * @param diagramTypes
     * @param diagramType
     * @return
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws Exception
     */
    private DiagramTypeEditor getTypeEditor(MetaModel metaModel, DiagramTypes diagramTypes, DiagramType diagramType) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, Exception {
        DiagramTypeEditor editor = DiagramViewer.getTypeEditor(diagramType,explorer);
        editor.init( diagramType, metaModel, diagramTypes);
        return editor;
    }


    public final Action actionDiagramTypeNew = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
			try {
				MetaModel metaModel = repository.getMetaModel();
				DiagramTypes diagramTypes = repository.getDiagramTypes();

				DiagramTypeFamily family = (DiagramTypeFamily)explorer.getSelectedNode().getUserObject();
                DiagramType diagramType = family.newDiagramType();
                DiagramTypeEditor editor = getTypeEditor(metaModel, diagramTypes, diagramType);
                editor.setVisible(true);
                if(editor.wasEdited()) {
                	family.add(diagramType);
                }
			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   

	public final Action actionDiagramTypeEdit = new AbstractAction() {
        private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			try {
				MetaModel metaModel = repository.getMetaModel();
				DiagramTypes diagramTypes = repository.getDiagramTypes();
				
				DiagramType diagramType = (DiagramType)explorer.getSelectedNode().getUserObject();
                DiagramTypeEditor editor = getTypeEditor(metaModel, diagramTypes, diagramType);
                editor.setVisible(true);
				if(editor.wasEdited()){
					diagramTypes.signalEdited(diagramType);
				}
			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   

	public final Action actionDiagramTypeDelete = new AbstractAction() {
        private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			try {
				DiagramTypes diagramTypes = repository.getDiagramTypes();
				
				DiagramType diagramType = (DiagramType)explorer.getSelectedNode().getUserObject();
				if(Dialogs.question(explorer, "Delete diagram type " + diagramType.getName() + "?")){
				    diagramTypes.delete(diagramType);
				}

			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   

	/**
	 * Comment for <code>actionDiagramTypeEventMappings</code>
	 */
	public final Action actionDiagramTypeEventMappings = new AbstractAction() {
        private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			try {
			    DiagramType type = (DiagramType)explorer.getSelectedNode().getUserObject();
			    Scripts scripts = repository.getScripts();
                EventMapDialog dialog = new EventMapDialog(explorer, "Edit Diagram Type Event Mapping", type.getEventMap(), scripts);
                dialog.setVisible(true);
			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}

	};
	
	public final Action actionMetaModelDiagramNew = new AbstractAction() {
        private static final long serialVersionUID = 1L;
		 public void actionPerformed(ActionEvent e) {
			 try {
				
				StandardDiagramType diagramType = (StandardDiagramType)explorer.getSelectedNode().getUserObject();
				
				String inputValue = JOptionPane.showInputDialog(explorer, "Diagram Name");
				if(inputValue != null) {
					Diagrams diagrams = repository.getMetaModelDiagrams();
					Diagram diagram = diagrams.newDiagramOfType(diagramType);
					diagram.setName(inputValue); 

					WindowCoordinator wc = app.getWindowCoordinator();
					WindowFactory factory = new MetaModelViewer.WindowFactory(diagram,app, repository);
					StandardDiagramViewer editor = (StandardDiagramViewer)wc.getFrame(diagram.getKey().toString(), factory);				
					editor.refresh();
					editor.show();
				}
          	 } catch(Throwable t) {
				 new ExceptionDisplay(explorer,t);
			 }
		 }
	    
	};
	
	public final Action actionDiagramNew = new AbstractAction() {
        private static final long serialVersionUID = 1L;
		 /* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			 try {
				DiagramType diagramType = (DiagramType)explorer.getSelectedNode().getUserObject();
				
				String inputValue = JOptionPane.showInputDialog(explorer, "Diagram Name");
				if(inputValue != null) {
					Diagrams diagrams = repository.getDiagrams();
					Diagram diagram = diagrams.newDiagramOfType(diagramType);
					diagram.setName(inputValue); 

					WindowCoordinator wc = app.getWindowCoordinator();
					
					DiagramViewerWindowFactory windowFactory = DiagramViewer.getWindowFactory(diagramType);
					windowFactory.init(diagram, app, repository);
					
					DiagramViewer editor = (DiagramViewer)wc.getFrame(diagram.getKey().toString(), windowFactory);
					editor.refresh();
					editor.show();
				}
				
          	 } catch(Throwable t) {
				 new ExceptionDisplay(explorer,t);
			 }
		 }
	 };   

	 public final Action actionDiagramEdit = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
			 try {
                Diagram diagram = (Diagram)explorer.getSelectedNode().getUserObject();
				WindowCoordinator wc = app.getWindowCoordinator();
				
				DiagramViewerWindowFactory windowFactory = DiagramViewer.getWindowFactory(diagram.getType());
				windowFactory.init(diagram, app, repository);
				
				DiagramViewer editor = (DiagramViewer)wc.getFrame(diagram.getKey().toString(), windowFactory);
				editor.refresh();
				editor.show();

			 } catch(Throwable t) {
				 new ExceptionDisplay(explorer,t);
			 }
		 }
	 };   

	 
	 /**
	 * Allow the user to edit diagram information.
	 */
	public final Action actionDiagramInformation = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
			 try {
                Diagram diagram = (Diagram)explorer.getSelectedNode().getUserObject();
                DiagramDetailsEditor editor = new DiagramDetailsEditor(explorer, diagram);
                editor.setVisible(true);
			 } catch(Throwable t) {
				 new ExceptionDisplay(explorer,t);
			 }
		 }
	 };   

	 /**
	 * Comment for <code>actionMetaModelDiagramEdit</code>
	 */
	public final Action actionMetaModelDiagramEdit = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
			 try {
                Diagram diagram = (Diagram)explorer.getSelectedNode().getUserObject();
				WindowCoordinator wc = app.getWindowCoordinator();
				
				MetaModelViewer.WindowFactory windowFactory = new MetaModelViewer.WindowFactory(diagram, app, repository);

				DiagramViewer editor = (DiagramViewer)wc.getFrame(diagram.getKey().toString(), windowFactory);
				editor.refresh();
				editor.show();

			 } catch(Throwable t) {
				 new ExceptionDisplay(explorer,t);
			 }
		 }
	 };   

	 public final Action actionDiagramDelete = new AbstractAction() {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e) {
			 try {
                Diagram diagram = (Diagram)explorer.getSelectedNode().getUserObject();
                if(Dialogs.question(explorer,"Delete diagram " + diagram.getName() + " ?")){
                    Diagrams diagrams = repository.getDiagrams();
                    diagrams.removeDiagram(diagram);
                }
			     
			 } catch(Throwable t) {
				 new ExceptionDisplay(explorer,t);
			 }
		 }
	 };   

		public final Action actionDiagramEventMappings = new AbstractAction() {
	        private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				try {
				    Diagram diagram = (Diagram)explorer.getSelectedNode().getUserObject();
				    Scripts scripts = repository.getScripts();
	                EventMapDialog dialog = new EventMapDialog(explorer, "Edit StandardDiagram Event Mapping", diagram.getEventMap(), scripts);
	                dialog.setVisible(true);
				} catch(Throwable t) {
					new ExceptionDisplay(explorer,t);
				}
			}

		};

}
