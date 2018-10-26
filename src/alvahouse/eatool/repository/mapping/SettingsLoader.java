/*
 * SettingsLoader.java
 * Project: EATool
 * Created on 24-Dec-2005
 *
 */
package alvahouse.eatool.repository.mapping;

import java.util.Iterator;

import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.util.SettingsManager;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.SettingsManager.Element;

/**
 * SettingsLoader creates ImportMappings from the settings tree.
 * @deprecated
 * @author rbp28668
 */
public class SettingsLoader {

    /**
     * 
     */
    public SettingsLoader(ImportMappings mappings) {
        super();
//        SettingsManager settings = Main.getApp().getSettings();
//        SettingsManager.Element importCfg = settings.getOrCreateElement("/Import");
//        MetaModel metaModel = Main.getApp().getRepository().getMetaModel();
//        loadImportMappings(mappings, metaModel, importCfg);
    }

    private void loadImportMappings(ImportMappings mappings, MetaModel metaModel, SettingsManager.Element cfg) {
        for(SettingsManager.Element e : cfg.getChildren()){
            ImportMapping mapping = new ImportMapping();
            build(mapping,metaModel,e);
            mappings.add(mapping);
        }
    }

    private void build(ImportMapping mapping, MetaModel metaModel, SettingsManager.Element cfg){
        mapping.setName(cfg.attributeRequired("name"));
        buildChildren(mapping,metaModel, cfg);
    }

    
    /**
     * Build the child entity and relationship translations.
     * @param cfg is the settings element at the root of the ImportMapping.
     */
    private void buildChildren(ImportMapping mapping, MetaModel metaModel, Element cfg) {
        SettingsManager.Element cfgEntities = cfg.findChild("EntityTranslationSet");
        
        // Add entities (optional)
        if(cfgEntities != null){
	        for(SettingsManager.Element etCfg : cfgEntities.getChildren()){
	            EntityTranslation et = new EntityTranslation();
	            build(et, metaModel, etCfg);
	            mapping.add(et);
	        }
        }
        
        // Add relationships (optional).
        SettingsManager.Element cfgRelationships = cfg.findChild("RelationshipTranslationSet");
        if(cfgRelationships != null){
            for(SettingsManager.Element rtCfg : cfgRelationships.getChildren()){
	            RelationshipTranslation rt = new RelationshipTranslation();
	            build(rt,metaModel, rtCfg);
	            mapping.add(rt);
	        }
        }
    }
    /**
     * Creates an EntityTranslation tied to a given metaModel and configured
     * from the given settings element.
     * @param metaModel is the metaModel that is being mapped against.
     * @param cfg is the settings manager element to configure this EntityTranslation.
     */
    private void build (EntityTranslation mapping, MetaModel metaModel, SettingsManager.Element cfg) {
        
        if(cfg.getName().compareTo("EntityTranslation") != 0)
            throw new IllegalArgumentException("Invalid Entity Translation " + cfg.getName());
        
        mapping.setType(cfg.attributeRequired("type"));
        UUID uuid = new UUID(cfg.attributeRequired("uuid"));
        MetaEntity meta = metaModel.getMetaEntity(uuid);
        if(meta == null)
            throw new IllegalArgumentException("Unable to find Meta-entity with uuid: " + uuid);
        mapping.setMeta(meta);

        addPropertiesFromConfig(mapping, meta, cfg);
    }

    
    /**
     * Builds a new RelationshipTranslation for a given meta-model based on 
     * the configuration information in cfg.
     * @param metaModel is the metaModel this is being built upon.
     * @param cfg defines the RelationshipTranslation.
     */
    private void build(RelationshipTranslation mapping, MetaModel metaModel, SettingsManager.Element cfg) {

        if(cfg.getName().compareTo("RelationshipTranslation") != 0)
            throw new IllegalArgumentException("Invalid Relationship Translation " + cfg.getName());

        mapping.setType(cfg.attributeRequired("type"));
        UUID uuid = new UUID(cfg.attributeRequired("uuid"));
        MetaRelationship meta = metaModel.getMetaRelationship(uuid);
        if(meta == null)
            throw new IllegalArgumentException("Unable to find meta-relationship with uuid: " + uuid);
        mapping.setMeta(meta);
        
        Iterator<SettingsManager.Element> iter = cfg.getChildren().iterator();

        SettingsManager.Element firstRoleConfig = iter.next();
        build(mapping.getStart(),meta, firstRoleConfig);
        
        SettingsManager.Element secondRoleConfig = iter.next();
        build(mapping.getFinish(), meta, secondRoleConfig);
    }
    
    /**
     * Create a new RoleTranslation attached to the given MetaRelatinship and
     * initialised from the given configuratin.
     * @param parent is the parent MetaRelationship this role translation bind to.
     * @param cfg defines the configuration of this RoleTranslation.
     */
    private void build(RoleTranslation mapping, MetaRelationship parent, SettingsManager.Element cfg) {
        if(cfg.getName().compareTo("RoleTranslation") != 0)
            throw new IllegalArgumentException("Invalid Role Translation " + cfg.getName());

        mapping.setType(cfg.attributeRequired("type"));
        UUID uuid = new UUID(cfg.attributeRequired("uuid"));
        MetaRole meta = parent.getMetaRole(uuid);
        if(meta == null)
            throw new IllegalArgumentException("Unable to find meta-role with uuid: " + uuid);
        mapping.setMeta(meta);
        
        addPropertiesFromConfig(mapping,meta.connectsTo(), cfg); // form key for "connectsTo" part of role
        if(mapping.getPropertyCount() == 0) 
            throw new InputException("RoleTranslation must have one or more properties to define key");
    }
    
    
    private void addPropertiesFromConfig(PropertyTranslationCollection mapping, MetaEntity meta, SettingsManager.Element cfg) {
        for(SettingsManager.Element child : cfg.getChildren()){
            PropertyTranslation pt = new PropertyTranslation();
            build(pt, meta, child);
            mapping.addProperty(pt);
        }
    }

    private void build(PropertyTranslation mapping, MetaEntity me, SettingsManager.Element cfg) {

        if(cfg.getName().compareTo("PropertyTranslation") != 0)
            throw new IllegalArgumentException("Invalid Property Translation " + cfg.getName());

        mapping.setTypeName(cfg.attributeRequired("type"));
        UUID uuid = new UUID(cfg.attributeRequired("uuid"));
        MetaProperty meta = me.getMetaProperty(uuid);

        if(meta == null)
            throw new IllegalArgumentException("Unable to find meta-property with uuid " + uuid + " for meta-entity " + me.getName());
        mapping.setMeta(meta);
        
        String key = cfg.attribute("key");
        if(key != null)
            mapping.setKeyValue(key.equals("true"));
    }


    
}
