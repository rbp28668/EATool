/*
 * WebExport.java
 * Project: EATool
 * Created on 10-May-2006
 *
 */
package alvahouse.eatool.webexport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.stream.StreamResult;

import org.apache.bsf.BSFException;

import alvahouse.eatool.gui.graphical.EventErrorHandler;
import alvahouse.eatool.gui.graphical.time.TimeDiagramType;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.RepositoryProperties;
import alvahouse.eatool.repository.exception.OutputException;
import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.DiagramTypeFamily;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramType;
import alvahouse.eatool.repository.html.HTMLPage;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.repository.scripting.ScriptContext;
import alvahouse.eatool.repository.scripting.ScriptManager;
import alvahouse.eatool.scripting.proxy.ScriptWrapper;
import alvahouse.eatool.util.XMLWriter;
import alvahouse.eatool.util.XMLWriterSAX;
import alvahouse.eatool.util.XSLTTransform;

/**
 * WebExport
 * 
 * @author rbp28668
 */
public class WebExport {

    private final static String NAMESPACE = "http://alvagem.co.uk/eatool/export";

    private static final String INDEX_PAGE = "index.html";
    private static final String INDEX_XFORM = "transform/index.xslt";
    private static final String META_ENTITY_XFORM = "transform/metaEntity.xslt";
    private static final String META_ENTITY_TABLE_XFORM = "transform/metaEntityTable.xslt";
    private static final String ENTITY_XFORM = "transform/entity.xslt";
    private static final String DIAGRAM_XFORM = "transform/diagram.xslt";
    private static final String STYLESHEET = "transform/ea.css";
    

    private String title = "Repository Export";
    
    /**
     * 
     */
    public WebExport() {
        super();
    }

    /**
     * Exports the repository as HTMLProxy pages.
     * @param repository is the repository to export
     * @param path is the path to the output folder.
     * @throws OutputException
     */
    public void export(Repository repository, String path) throws OutputException{
        
        MetaModel meta = repository.getMetaModel();
        Model model = repository.getModel();

        try {

	        // Always start with a clean folder.
	        File outputFolder = new File(path);
	        if(outputFolder.exists()){
	            outputFolder.delete();
	        }
	        outputFolder.mkdirs();
	        
	        // Try and get page titles from repository properties.
	        title = "Repository Export";
	        Properties props = repository.getProperties().get(); 
	        String t2 = props.getProperty(RepositoryProperties.NAME);
	        if(t2 != null && t2.length() > 0){
	            title = t2;
	        }
        
            
            writeIndex(repository, outputFolder);
            writeMetaEntities(meta, model, outputFolder);
            writeMetaEntityTables(meta,model, outputFolder);
            writeEntities(model,outputFolder);
            writeDiagrams(repository, outputFolder);
            writePages(repository, outputFolder);
            copyFile(outputFolder,STYLESHEET);
            
        } catch (FileNotFoundException e) {
            throw new OutputException("Unable to generate web output",e);
        } catch (Exception e) {
            throw new OutputException("Unable to generate web output",e);
        }

    }



    /**
     * @param model
     * @param outputFolder
     * @throws IOException
     */
    private void writeEntities(Model model, File outputFolder) throws Exception {
        File subFolder = new File(outputFolder,"entity");
        subFolder.mkdirs();
        
        EntityExportProxy proxy = new EntityExportProxy();
        for(Entity entity : model.getEntities()){
            
            MetaEntity me = entity.getMeta();
            if(!me.isAbstract() && me.getDisplayHint() != null){

	            String outputFile = entity.getKey().toString() + ".html";
		        XMLWriter writer = getWriter(subFolder,outputFile, ENTITY_XFORM);
		        try {
		            writePreamble(writer);
		            proxy.setEntity(entity);
		            proxy.export(writer);
		            writePostamble(writer);
		        } finally {
		            writer.close();
		        }
            }
        }
   }

    /**
     * @param meta
     * @param model
     * @param outputFolder
     * @throws IOException
     */
    private void writeMetaEntityTables(MetaModel meta, Model model, File outputFolder) throws Exception {
        File subFolder = new File(outputFolder,"metaEntityTable");
        subFolder.mkdirs();
        
        EntityListExportProxy proxy = new EntityListExportProxy(model);
        for(MetaEntity me : meta.getMetaEntities()){
            String outputFile = me.getKey().toString() + ".html";
	        XMLWriter writer = getWriter(subFolder,outputFile, META_ENTITY_TABLE_XFORM);
	        try {
	            writePreamble(writer);
	            proxy.setMetaEntity(me);
	            proxy.export(writer);
	            writePostamble(writer);
	        } finally {
	            writer.close();
	        }
        }
    }

    /**
     * @param meta
     * @param model
     * @param outputFolder
     * @throws IOException
     */
    private void writeMetaEntities(MetaModel meta, Model model, File outputFolder) throws Exception {

        File subFolder = new File(outputFolder,"metaEntity");
        subFolder.mkdirs();
        
        EntityListExportProxy proxy = new EntityListExportProxy(model);
        for(MetaEntity me : meta.getMetaEntities()){
            String outputFile = me.getKey().toString() + ".html";
	        XMLWriter writer = getWriter(subFolder,outputFile, META_ENTITY_XFORM);
	        try {
	            writePreamble(writer);
	            proxy.setMetaEntity(me);
	            proxy.export(writer);
	            writePostamble(writer);
	        } finally {
	            writer.close();
	        }
        }
    }

    /**
     * @param repository
     * @param outputFolder
     * @throws IOException
     */
    private void writeIndex(Repository repository, File outputFolder) throws Exception {
        XMLWriter writer = getWriter(outputFolder,INDEX_PAGE, INDEX_XFORM);
        MetaModel meta = repository.getMetaModel();
        try {
            writePreamble(writer);
            MetaModelExportProxy proxy = new MetaModelExportProxy(meta);
            proxy.export(writer);
            DiagramListExportProxy diagramProxy = new DiagramListExportProxy(repository);
            diagramProxy.export(writer);
            PageListExportProxy pagesProxy = new PageListExportProxy(repository.getPages());
            pagesProxy.export(writer);
            writePostamble(writer);
        } finally {
            writer.close();
        }
    }

    
    /**
     * @param writer
     * @throws IOException
     */
    private void writePreamble(XMLWriter writer) throws IOException {
        writer.startXML();
        writer.startEntity("Export");
        writer.textEntity("Title",title);
    }

    /**
     * @param writer
     * @throws IOException
     */
    private void writePostamble(XMLWriter writer) throws IOException {
        writer.stopEntity();
        writer.stopXML();
    }
    
    /**
     * @param outputFolder
     * @param page
     * @param xslt
     * @return
     * @throws IOException
     */
    private XMLWriter getWriter(File outputFolder, String page, String xslt) throws IOException {
        
        File outputFile = new File(outputFolder,page);
        StreamResult result = new StreamResult(new FileOutputStream(outputFile));
        result.setSystemId(outputFile.getAbsolutePath());

        XMLWriter writer;
        if(xslt != null) {
            URL xform = getClass().getResource(xslt);
            XSLTTransform transform = new XSLTTransform(xform.toString());
            transform.setResult(result);
            writer = new XMLWriterSAX(transform.asContentHandler());
        } else {
            writer = new XMLWriterSAX(result.getOutputStream());
        }
        writer.setNamespace("ex",NAMESPACE);
        return writer;
    }

    /**
     * Copies a file from a resource in the code to the output folder.
     * @param outputFolder
     * @throws IOException
     */
    private void copyFile(File outputFolder, String resource) throws IOException{

        InputStream is = getClass().getResourceAsStream(resource);
        try {
	        int idx = resource.lastIndexOf('/');
	        if(idx >= 0){
	            resource = resource.substring(idx);
	        }
	        
	        File outputFile = new File(outputFolder, resource);
	        FileOutputStream fos = new FileOutputStream(outputFile);
	        try {
		        byte[] buffer = new byte[4096];
		        int bytes;
		        while((bytes = is.read(buffer)) != -1){
		            fos.write(buffer,0,bytes);
		        }
	        } finally {
	            fos.close();
	        }
        } finally {
            is.close();
        }
    }
    
    private void writeDiagrams(Repository repository, File outputFolder) throws Exception {
        
        File imageDir = new File(outputFolder,"diagrams");
        imageDir.mkdirs();
        Map<Class<? extends DiagramType>, DiagramExportProxy> proxies = new HashMap<>();
        
        proxies.put(StandardDiagramType.class, new StandardDiagramExportProxy());
        proxies.put(TimeDiagramType.class, new TimeDiagramExportProxy());
        
        for(DiagramTypeFamily family : repository.getDiagramTypes().getDiagramTypeFamilies()){
            for(DiagramType type : family.getDiagramTypes()){
                DiagramExportProxy proxy = (DiagramExportProxy)proxies.get(type.getClass());
                if(proxy == null){
                    throw new IOException("No diagram proxy for diagram type " + type.getClass().getName());
                }
                writeDiagramsOfType(repository, imageDir, proxy, type);
            }
        }
    }

    /**
     * @param repository
     * @param imageDir
     * @param proxy
     * @param type
     * @throws IOException
     */
    private void writeDiagramsOfType(Repository repository, File imageDir, DiagramExportProxy proxy, DiagramType type) throws Exception {
        Collection<Diagram> diagrams = repository.getDiagrams().getDiagramsOfType(type);
        
        for(Diagram diagram : diagrams){
            File path = new File(imageDir, diagram.getKey().toString() + ".png");
            diagram.export(path,"png");

            String outputFile = diagram.getKey().toString() + ".html";
            XMLWriter writer = getWriter(imageDir,outputFile, DIAGRAM_XFORM);
            try {
                writePreamble(writer);
                proxy.setDiagram(diagram);
                proxy.export(writer);
                writePostamble(writer);
            } finally {
                writer.close();
            }
        }
    }

    /**
     * Writes all predefined HTMLProxy pages from the repository.  Slightly tricky if there's a script
     * that generates the page dynamically.  These are run on their own thread so they don't block
     * the user. In this case, the PageExportProxy also acts as a script proxy for the viewer and
     * its refresh method writes the output file. All html page scripts should call this at the end.
     * @param repository
     * @param outputFolder
     */
    private void writePages(Repository repository, File outputFolder) throws Exception {
    	File pageDir = new File(outputFolder,"pages");
    	pageDir.mkdirs();

    	for(HTMLPage page : repository.getPages().getPages()){
    		PageExportProxy proxy = new PageExportProxy(page);
    		String file =  page.getKey().toString() + ".html";
    		
    		EventMap eventMap = page.getEventMap();
    		if(eventMap.hasHandler(HTMLPage.ON_DISPLAY_EVENT)) {
    			ScriptContext context = page.getEventMap().getContextFor(HTMLPage.ON_DISPLAY_EVENT);
    			try {
    				proxy.setDestination(pageDir, file);
    				Object wrapped = ScriptWrapper.wrap(page);
    				context.addObject("page", wrapped, wrapped.getClass());

    				context.addObject("viewer", proxy, proxy.getClass());

    				EventErrorHandler errHandler = new EventErrorHandler(null); // null: no parent
    				context.setErrorHandler(errHandler);
    				ScriptManager.getInstance().runScript(context);
    			} catch (BSFException e) {
    				throw new IOException("Unable to export page due to script error " + e.getMessage());
    			}
    		} else {
    			// No script to generate output so just write it.
    			proxy.export(pageDir, file); // custom output as don't want to transform xml as already html
    		}
    	}

    }
    
}
