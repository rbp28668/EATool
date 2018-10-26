/*
 * MetaEntityFactory.java
 *
 * Created on 16 January 2002, 22:07
 */

package alvahouse.eatool.repository.metamodel.factory;


import org.xml.sax.Attributes;

import alvahouse.eatool.repository.ProgressCounter;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.impl.MetaEntityImpl;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyTypes;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;
/**
 * MetaEntityFactory class is a content handler for deserialising MetaEntity
 * (and MetaProperty) from XML.
 * @author  rbp28668
 */
public class MetaEntityFactory extends MetaPropertyContainerFactory implements IXMLContentHandler {

    /** The MetaModel the MetaEntities will be added to */
    private MetaModel m_metaModel;
    
    /** The MetaEntity currently being read in */
    private MetaEntity m_currentMetaEntity = null;
    
    /** Whether a MetaEntity is new or previously exists in the meta model */
    private boolean isNewMetaEntity;

    private ProgressCounter counter;

    /** Creates new MetaEntityFactory. 
     * @param counter
     * @param mm is the MetaModel to add new MetaEntities to.
     * */
    public MetaEntityFactory(ProgressCounter counter, MetaPropertyTypes types, MetaModel mm) {
        super(types);
        m_metaModel = mm;
        this.counter = counter;
    }

    // IXMLContentHandler
    public void startElement(String uri, String local, Attributes attrs) throws InputException{
        // Expect MetaEntity followed by a number of MetaProperty
        if(local.equals("MetaEntity")) {
            if(m_currentMetaEntity != null) { // oops, nested meta entities
                throw new InputException("Nested Meta Entry loading XML into repository");
            }
            
            UUID uuid = getUUID(attrs);
            
            isNewMetaEntity = false;
            m_currentMetaEntity = m_metaModel.getMetaEntity(uuid);
            if(m_currentMetaEntity == null){
                m_currentMetaEntity = new MetaEntityImpl(uuid);
                isNewMetaEntity = true;
            }
            
            getCommonFields(m_currentMetaEntity, attrs);
            
            String attr = attrs.getValue("name");
            if(attr != null) {
                m_currentMetaEntity.setName(attr);
            }
            
            attr = attrs.getValue("abstract");
            if(attr != null) {
                m_currentMetaEntity.setAbstract(attr.equals("true"));
            }
            
            attr = attrs.getValue("extends");
            if(attr != null) {
                UUID uuidBase = new UUID(attr);
                
                MetaEntity meBase = m_metaModel.getMetaEntity(uuidBase);
                if(meBase == null){ // not yet read in
                    meBase = new MetaEntityImpl(uuidBase);
                    m_metaModel.addMetaEntity(meBase);
                }
                m_currentMetaEntity.setBase(meBase);
            } 
            
        } else {
            startMetaProperty(m_currentMetaEntity,uri,local,attrs);
        }
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local){
        if(local.equals("MetaEntity")) {
            if(isNewMetaEntity){
                m_metaModel.addMetaEntity(m_currentMetaEntity);
                //System.out.println("Adding meta-entity " + m_currentMetaEntity.getName() + ", " + m_currentMetaEntity.getKey().toString());
                counter.count("Meta Entity");
            }
            m_currentMetaEntity = null;
        } else  {
            endMetaProperty(m_currentMetaEntity,uri,local);
        }
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
     */
    public void characters(String str){
        if (m_currentMetaEntity != null){
            m_currentMetaEntity.setDescription(str);
        } else {
            setPropertyDescription(str);
        }
    }

}
