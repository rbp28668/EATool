/*
 * EAToolWindowCoordinator.java
 *
 * Created on 06 March 2002, 20:32
 */

package alvahouse.eatool.gui;
import javax.swing.JInternalFrame;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.graphical.standard.metamodel.GraphicalMetaModel;
import alvahouse.eatool.gui.graphical.standard.metamodel.MetaModelDiagramType;
import alvahouse.eatool.gui.graphical.standard.metamodel.MetaModelViewer;
import alvahouse.eatool.gui.graphical.standard.model.GraphicalModel;
import alvahouse.eatool.gui.graphical.standard.model.ModelDiagramType;
import alvahouse.eatool.gui.graphical.standard.model.ModelViewer;
import alvahouse.eatool.gui.html.HTMLDisplay;
import alvahouse.eatool.gui.html.HTMLEditor;
import alvahouse.eatool.gui.html.HTMLPagesExplorer;
import alvahouse.eatool.gui.images.ImageExplorer;
import alvahouse.eatool.gui.mappings.ExportMappingExplorer;
import alvahouse.eatool.gui.mappings.ImportMappingExplorer;
import alvahouse.eatool.gui.scripting.FunctionHelpBrowser;
import alvahouse.eatool.gui.scripting.ScriptEditor;
import alvahouse.eatool.gui.scripting.ScriptExplorer;
import alvahouse.eatool.gui.types.TypesExplorer;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramType;
import alvahouse.eatool.util.UUID;

/**
 * EAToolWindowCoordinator provides an EATool specific windows
 * coordinate.
 * @author  rbp28668
 */
public class EAToolWindowCoordinator extends WindowCoordinator {

    private Application app;
    private Repository rep;
    
    /** Creates new EAToolWindowCoordinator */
    public EAToolWindowCoordinator(Application application, Repository repository) {
        
        this.app = application;
        this.rep = repository;
        
        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() {
                return new MetaModelExplorerFrame(app, rep);
            }
        },"MetaModelExplorer");

        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() {
                return new ModelExplorer(app, rep);
            }
        },"ModelExplorer");
        
        addFactory( new WindowFactory () {
            public JInternalFrame createFrame()  throws Exception{
                StandardDiagramType diagramType = MetaModelDiagramType.getInstance();
                GraphicalMetaModel gmm = new GraphicalMetaModel(rep, rep.getMetaModel(),diagramType, new UUID());
                rep.getMetaModelViewerEvents().cloneTo(gmm.getEventMap());
                return new MetaModelViewer(gmm, app, rep);
            }
        },"MetaModelViewer");

        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() throws Exception{
				StandardDiagramType diagramType = new ModelDiagramType(rep.getMetaModel());
				GraphicalModel gm = new GraphicalModel(rep, rep.getModel(),diagramType,new UUID());
				rep.getModelViewerEvents().cloneTo(gm.getEventMap());
                return new ModelViewer(gm, app, rep);
            }
        },"ModelViewer");
        
        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() {
                return new SettingsExplorer(app.getSettings().getRoot(), app);
            }
        },"SettingsExplorer");
        
        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() {
                return new SettingsExplorer(app.getConfig().getRoot(), app);
            }
        },"ConfigExplorer");

        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() {
                return new DiagramExplorer("Diagrams","/Windows/DiagramExplorer","Diagrams","/DiagramExplorer/popups",app,rep);
            }
        },"DiagramExplorer");

        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() {
                return new DiagramExplorer("Meta-Model Diagrams","/Windows/MetaDiagramExplorer","Diagrams","/MetaDiagramExplorer/popups",app,rep);
            }
        },"MetaDiagramExplorer");
        
        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() {
                return new ImportMappingExplorer(app, rep);
            }
        },"ImportMappingExplorer");

        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() {
                return new ExportMappingExplorer(app, rep);
            }
        },"ExportMappingExplorer");

        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() {
                return new ModelBrowser(app, rep);
            }
        },"ModelBrowser");

        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() {
                return new HelpBrowser(app);
            }
        },"HelpBrowser");

        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() {
                return new ScriptEditor(app);
            }
        },"ScriptEditor");

        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() {
                return new ScriptExplorer(app, rep);
            }
        },"ScriptExplorer");
        
        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() {
                return new FunctionHelpBrowser(app);
            }
        },"FunctionHelpBrowser");

        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() throws Exception {
                return new TypesExplorer(rep.getExtensibleTypes(), app);
            }
        },"TypesExplorer");

        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() {
                return new HTMLEditor(app,rep);
            }
        },"HTMLEditor");

        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() {
                return new HTMLDisplay(app,rep);
            }
        },"HTMLDisplayProxy");

        
        addFactory( new WindowFactory() {
           public JInternalFrame createFrame(){
               return new HTMLPagesExplorer(rep.getPages(),app, rep);
           }
        },"PageExplorer");
        
        addFactory( new WindowFactory () {
            public JInternalFrame createFrame() {
                return new ImageExplorer(app, rep);
            }
        },"ImageExplorer");
        
    }

//            addFrame(new MetaModelExplorerFrame(m_repository.getMetaModel()), "MetaModelExplorer");
//            addFrame(new ModelExplorer(m_repository.getModel(), m_repository.getMetaModel()),"ModelExplorer");
//            addFrame(new MetaModelViewer(m_repository.getMetaModel()), "MetaModelViewer");
//            addFrame(new SettingsExplorer(settings.getRoot()),"SettingsExplorer");
//            addFrame(new SettingsExplorer(config.getRoot()),"ConfigExplorer");

}
