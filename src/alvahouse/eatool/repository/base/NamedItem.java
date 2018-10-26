/*
 * NamedItem.java
 * Project: EATool
 * Created on 23 Dec 2007
 *
 */
package alvahouse.eatool.repository.base;

public interface NamedItem extends KeyedItem{

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getDescription();

    public abstract void setDescription(String desc);

}