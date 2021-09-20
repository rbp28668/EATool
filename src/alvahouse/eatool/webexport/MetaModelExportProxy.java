/*
 * MetaModelExportProxy.java
 * Project: EATool
 * Created on 10-May-2006
 *
 */
package alvahouse.eatool.webexport;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.util.XMLWriter;

/**
 * MetaModelExportProxy
 * 
 * @author rbp28668
 */
public class MetaModelExportProxy implements ExportProxy {

    private MetaModel meta;
    
    /**
     * 
     */
    public MetaModelExportProxy(MetaModel meta) {
        super();
        this.meta = meta;
    }

    public void export(XMLWriter out) throws Exception{
        List<MetaEntity> metaList = new LinkedList<>();
        metaList.addAll(meta.getMetaEntities());
        Collections.sort(metaList, new MetaEntity.Compare());
        
        for(MetaEntity me : metaList) {
            if(!me.isAbstract() && me.getDisplayHint() != null){
                out.startEntity("MetaEntity");
                out.textEntity("UUID",me.getKey().toString());
                out.textEntity("Name",me.getName());
                out.textEntity("Description",me.getDescription());
                out.stopEntity();
            }
        }
    }
}
