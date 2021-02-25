/*
 * IDeleteDependenciesProxy.java
 *
 * Created on 07 February 2002, 08:38
 */

package alvahouse.eatool.repository.base;

/**
 * IDeleteDependenciesProxy should be implemented by objects
 * that may be deleted as a consequence of a parent object
 * being deleted.  This allows the system to create a list 
 * of all objects that will be deleted in a single transaction
 * which can be displayed to the user, and deleted as a group.
 * @author  rbp28668
 */
public interface IDeleteDependenciesProxy {

    public String toString();
    public void delete() throws Exception;
    public Object getTarget();
}

