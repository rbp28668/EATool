/*
 * DeleteDependenciesList.java
 *
 * Created on 07 February 2002, 20:51
 */

package alvahouse.eatool.repository.base;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
/**
 * A list of delete dependencies that describe what will be deleted if the parent object
 * is deleted.
 * @author  rbp28668
 */
public class DeleteDependenciesList {

    private LinkedList<IDeleteDependenciesProxy> dependencies = new LinkedList<IDeleteDependenciesProxy>();
    private Set<Object> targets = new HashSet<Object>();

    /** Creates new DeleteDependenciesList */
    public DeleteDependenciesList() {
    }

    public void addDependency(IDeleteDependenciesProxy proxy) {
        if(targets.contains(proxy.getTarget()))
            throw new IllegalStateException("Delete dependency target already listed");
        
        dependencies.addLast(proxy);
        targets.add(proxy.getTarget());
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

    public Object[] getDependencyTargets() {
        Object[] targets = new Object[dependencies.size()];
        int idx = 0;
        for(IDeleteDependenciesProxy proxy : dependencies){
            targets[idx++] = proxy.getTarget();
        }
        return targets;
    }
    
    public boolean containsTarget(Object o) {
        return targets.contains(o);
    }
    
}
