/*
 * DeleteDependenciesList.java
 *
 * Created on 07 February 2002, 20:51
 */

package alvahouse.eatool.repository.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.dto.DeleteProxyDto;
import alvahouse.eatool.repository.metamodel.MetaEntityDeleteProxy;
import alvahouse.eatool.repository.metamodel.MetaRelationshipDeleteProxy;
import alvahouse.eatool.repository.model.EntityDeleteProxy;
import alvahouse.eatool.repository.model.RelationshipDeleteProxy;
import alvahouse.eatool.util.UUID;
/**
 * A list of delete dependencies that describe what will be deleted if the parent object
 * is deleted.
 * @author  rbp28668
 */
public class DeleteDependenciesList {

    private final LinkedList<IDeleteDependenciesProxy> dependencies = new LinkedList<IDeleteDependenciesProxy>();
    private final Set<UUID> targets = new HashSet<>();
    private final static Map<String, Factory> factories = new HashMap<>();
    
    
    static {
    	factories.put(MetaEntityDeleteProxy.NAME, new Factory() {

			@Override
			public IDeleteDependenciesProxy create(DeleteProxyDto dao, Repository repository) {
				return new MetaEntityDeleteProxy(repository.getMetaModel(), dao );
			}
    		
    	});
    	factories.put(MetaRelationshipDeleteProxy.NAME, new Factory() {

			@Override
			public IDeleteDependenciesProxy create(DeleteProxyDto dao, Repository repository) {
				return new MetaRelationshipDeleteProxy(repository.getMetaModel(), dao );
			}
    		
    	});
    	factories.put(EntityDeleteProxy.NAME, new Factory() {

			@Override
			public IDeleteDependenciesProxy create(DeleteProxyDto dao, Repository repository) {
				return new EntityDeleteProxy(repository.getModel(), dao );
			}
    		
    	});
    	factories.put(RelationshipDeleteProxy.NAME, new Factory() {

			@Override
			public IDeleteDependenciesProxy create(DeleteProxyDto dao, Repository repository) {
				return new RelationshipDeleteProxy(repository.getModel(), dao );
			}
    		
    	});
    	
    	
    }
    /** Creates new DeleteDependenciesList */
    public DeleteDependenciesList() {
    }

    public void addDependency(IDeleteDependenciesProxy proxy) {
        if(targets.contains(proxy.getTargetKey()))
            throw new IllegalStateException("Delete dependency target already listed");
        
        dependencies.addLast(proxy);
        targets.add(proxy.getTargetKey());
    }

    public void addDependency(DeleteProxyDto proxy, Repository repository) {
        if(targets.contains(proxy.getItemKey()))
            throw new IllegalStateException("Delete dependency target already listed");
        
        Factory factory = factories.get(proxy.getItemType());
        if(factory == null) {
        	throw new IllegalArgumentException("Delete proxy item type not known (was) " + proxy.getItemType());
        }
        IDeleteDependenciesProxy delete = factory.create(proxy, repository);
        dependencies.addLast(delete);
        targets.add(proxy.getItemKey());
    }

    public void deleteDependencies() throws Exception{
        for(IDeleteDependenciesProxy proxy : dependencies){
            proxy.delete();
        }
    }
    
    public String[] getDependencyNames() {
        String[] names = new String[dependencies.size()];
        int idx = 0;
        for(IDeleteDependenciesProxy proxy : dependencies){
            names[idx++] = proxy.toString();
        }
        return names;
    }

    public UUID[] getDependencyTargets() {
        UUID[] targets = new UUID[dependencies.size()];
        int idx = 0;
        for(IDeleteDependenciesProxy proxy : dependencies){
            targets[idx++] = proxy.getTargetKey();
        }
        return targets;
    }
    
    public boolean containsTarget(UUID key) {
        return targets.contains(key);
    }
    
    private interface Factory {
    	IDeleteDependenciesProxy create(DeleteProxyDto dao, Repository repository);
    }
}
