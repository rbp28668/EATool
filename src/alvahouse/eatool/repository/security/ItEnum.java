package alvahouse.eatool.repository.security;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * ItEnum is an adapter class 
 * 
 * @author rbp28668
 */
class ItEnum<T> implements Enumeration<T>{

    private Iterator<T> iter;
    
    public ItEnum(Iterator<T> iterator){
        this.iter = iterator;
    }
    
    @Override
    public boolean hasMoreElements() {
        return iter.hasNext();
    }

    @Override
    public T nextElement() {
        return iter.next();
    }
    
}