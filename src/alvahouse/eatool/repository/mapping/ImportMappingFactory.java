/*
 * ImportMappingFactory.java
 * Project: EATool
 * Created on 10-Dec-2005
 *
 */
package alvahouse.eatool.repository.mapping;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.ProgressCounter;
import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;

/**
 * ImportMappingFactory is an IXMLContentHandler for reading the import mappings
 * from an XML file. This handles the complete set of Translation  objects.
 * 
 * @author rbp28668
 */
public class ImportMappingFactory extends FactoryBase implements
        IXMLContentHandler {

    private ImportMappings mappings;
    private MetaModel metaModel;
    private ImportMapping mapping = null;
    private EntityTranslation currentEntityTranslation = null;
    private RelationshipTranslation currentRelationshipTranslation = null;
    private RoleTranslation currentRoleTranslation = null;
    private PropertyTranslationCollection currentPTC = null;
    private StringBuffer text = new StringBuffer();
    private MetaEntity propertyParent = null;
    private int roleCount = 0;
    private ProgressCounter counter;

    
    /**
     * @param counter
     * @param mappings
     * @param meta
     */
    public ImportMappingFactory(ProgressCounter counter, ImportMappings mappings, MetaModel meta) {
        super();
        this.mappings = mappings;
        this.metaModel = meta;
        this.counter = counter;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String local, Attributes attrs)
            throws InputException {

        uri=null; // remove if we prefix attributes with namepsace

        if(local.equals("ImportTranslation")){
            if(mapping != null) {
                throw new IllegalStateException("Nested import mappings");
            }

            mapping = new ImportMapping();
            String attr = attrs.getValue("name");
            if(attr == null) {
                throw new InputException("Missing name attribute on ImportTranslation");
            }
            mapping.setName(attr);

            attr = attrs.getValue("description");
            if(attr != null) {
            	mapping.setDescription(attr);
            }

        } else if (local.equals("Description")) {
            text.delete(0,text.length());

        } else if (local.equals("Parser")) {
            text.delete(0,text.length());
            
        } else if (local.equals("Transform")) {
            text.delete(0,text.length());

        } else if (local.equals("EntityTranslation")){
        	try {
        		startEntityTranslation(attrs);
        	}catch (Exception e) {
        		throw new InputException("Unable to start entity translation",e);
        	}
           
        } else if (local.equals("RelationshipTranslation")){
        	try {
        		startRelationshipTranslation(attrs);
        	} catch (Exception e) {
        		throw new InputException("Unable to start relationship translation",e);
        	}
            
        } else if (local.equals("RoleTranslation")){
        	try {
        		startRoleTranslation(attrs);
        	} catch (Exception e) {
        		throw new InputException("Unable to start role translation",e);
        	}
            
        } else if (local.equals("PropertyTranslation")){
        	try {
        		startPropertyTranslation(attrs);
        	} catch (Exception e) {
        		throw new InputException("Unable to start property translation",e);
        	}
        } else if (local.equals("Version")) {
        	VersionImpl.readXML(attrs, mapping);
        }

    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local) throws InputException {
        if(local.equals("ImportTranslation")){
        	try {
        		mappings._add(mapping);
        	} catch (Exception e) {
        		throw new InputException("Unable to load import mapping",e);
        	}
            mapping = null;
            counter.count("Import Mapping");
        } else if (local.equals("Description")) {
            mapping.setDescription(text.toString());
     
        } else if (local.equals("Parser")) {
            mapping.setParserName(text.toString());
     
        } else if (local.equals("Transform")) {
            mapping.setTransformPath(text.toString());

        } else if (local.equals("EntityTranslation")) {
            mapping.add(currentEntityTranslation);
            currentEntityTranslation = null;
            currentPTC = null;
            propertyParent = null;
        } else if (local.equals("RelationshipTranslation")) {
            if(roleCount < 2){
                throw new InputException("Too few RoleTranslations in RelationshipTranslation");
            }
            mapping.add(currentRelationshipTranslation);
            currentRelationshipTranslation = null;

        } else if (local.equals("RoleTranslation")) {
            switch(roleCount){
            	case 0:
            	    currentRelationshipTranslation.setStart(currentRoleTranslation);
            	    break;
            	case 1:
            	    currentRelationshipTranslation.setFinish(currentRoleTranslation);
            	    break;
            	default:
            	    throw new InputException("Too many RoleTranslations for RelationshipTranslation");
            } 	
            ++roleCount;
            currentRoleTranslation = null;
            currentPTC = null;
            propertyParent = null;
            
        } else if (local.equals("PropertyTranslation")) {
            
        }

    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
     */
    public void characters(String str) throws InputException {
        text.append(str);
    }
    
    /**
     * @param uri
     * @param attrs
     */
    private void startPropertyTranslation(Attributes attrs) throws Exception{
        if(currentPTC == null){
            throw new InputException("PropertyTranslation outside its container");
        }
        
        PropertyTranslation pt = new PropertyTranslation();
        
        String attr = attrs.getValue("type");
        if(attr == null) {
            throw new InputException("Missing type attribute on PropertyTranslation");
        }
        pt.setTypeName(attr);

        attr = attrs.getValue("uuid");
        if(attr == null) {
            throw new InputException("Missing uuid attribute on PropertyTranslation");
        }
        
        MetaProperty mp = propertyParent.getMetaProperty(new UUID(attr));  
        if(mp == null) {
            throw new InputException("UUID does not identify a known MetaProperty");
        }
        pt.setMeta(mp);

        boolean isKey = false;
        attr = attrs.getValue("key");
        if(attr != null) {
            isKey = attr.equals("true");
        }
        pt.setKeyValue(isKey);
        
        currentPTC.addProperty(pt);
    }

    /**
     * @param uri
     * @param attrs
     */
    private void startRoleTranslation(Attributes attrs) throws Exception{
        if(currentRelationshipTranslation == null){
            throw new InputException("RoleTranslation outside RelationshipTranslation");
        }
        
        currentRoleTranslation = new RoleTranslation();
        currentPTC = currentRoleTranslation;
        
        String attr = attrs.getValue("type");
        if(attr == null) {
            throw new InputException("Missing type attribute on RoleTranslation");
        }
        currentRoleTranslation.setType(attr);
        
        attr = attrs.getValue("uuid");
        if(attr == null) {
            throw new InputException("Missing uuid attribute on RelationshipTranslation");
        }
        MetaRole mr = currentRelationshipTranslation.getMeta(metaModel).getMetaRole(new UUID(attr));  
        if(mr == null) {
            throw new InputException("UUID does not identify a known MetaRelationship");
        }
        currentRoleTranslation.setMeta(mr);
        
        propertyParent = mr.connectsTo();
    }

    /**
     * @param uri
     * @param attrs
     */
    private void startRelationshipTranslation(Attributes attrs)  throws Exception{
        if(mapping == null) {
            throw new InputException("RelationshipTranslation outside ImportMapping");
        }
        if(currentRelationshipTranslation != null){
            throw new InputException("Nested RelationshipTranslation");
        }
        
        currentRelationshipTranslation = new RelationshipTranslation();
        
        String attr = attrs.getValue("type");
        if(attr == null) {
            throw new InputException("Missing type attribute on RelationshipTranslation");
        }
        currentRelationshipTranslation.setType(attr);
        
        attr = attrs.getValue("uuid");
        if(attr == null) {
            throw new InputException("Missing uuid attribute on RelationshipTranslation");
        }
        MetaRelationship mr = metaModel.getMetaRelationship(new UUID(attr));
        if(mr == null) {
            throw new InputException("UUID does not identify a known MetaRelationship");
        }
        currentRelationshipTranslation.setMeta(mr);
        roleCount = 0;
    }

    /**
     * @param uri
     * @param attrs
     */
    private void startEntityTranslation(Attributes attrs)  throws Exception{
        if(mapping == null) {
            throw new InputException("EntityTranslation outside ImportMapping");
        }
        if(currentEntityTranslation != null){
            throw new InputException("Nested EntityTranslation");
        }
        
        currentEntityTranslation = new EntityTranslation();
        currentPTC = currentEntityTranslation;
        
        String attr = attrs.getValue("type");
        if(attr == null) {
            throw new InputException("Missing type attribute on EntityTranslation");
        }
        currentEntityTranslation.setType(attr);
        
        attr = attrs.getValue("uuid");
        if(attr == null) {
            throw new InputException("Missing uuid attribute on EntityTranslation");
        }
        MetaEntity me = metaModel.getMetaEntity(new UUID(attr));
        if(me == null) {
            throw new InputException("UUID does not identify a known MetaEntity");
        }
        currentEntityTranslation.setMeta(me);
        
        propertyParent = me;
    }


}
