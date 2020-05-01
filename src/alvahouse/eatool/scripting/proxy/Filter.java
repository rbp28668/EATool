/*
 * Filter.java
 * Project: EATool
 * Created on 10-Mar-2006
 *
 */
package alvahouse.eatool.scripting.proxy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alvahouse.eatool.repository.metamodel.types.MetaPropertyType;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Property;
import alvahouse.eatool.util.UUID;

/**
 * Filter describes a filter that can be used to filter sets of entities.
 * Filters can be combined to form complex expressions before being applied
 * to a MetaEntitySet.
 * 
 * @author rbp28668
 */
@Scripted(description="A filter that can be used to filter sets of entities.  See EntitySet.applyFilter().")
public class Filter {

    private static final Term MATCH_ALL_TERM = new MatchAllTerm();
    private Term term = MATCH_ALL_TERM;
    
    /**
     * Creates a new, empty filter that matches anything.
     */
    Filter() {
        super();
    }

    /**
     * Sets the filter to a regular expression on a given property.
     * @param propertyKey is the key for the property to check.
     * @param regexp is the regular expression to match that property.
     * @return the updated filter.
     */
    @Scripted(description="Sets a filter so that the given property has to match a regular expression. "
    		+"First parameter is the UUID key of the property, the second is the regular expression")
    public Filter setRegexp(String propertyKey, String regexp ){
        term = new RegexpTerm(propertyKey, regexp);
        return this;
    }
    
    /**
     * Sets the filter to a range check on a given property.
     * @param propertyKey is the key for the property to check.
     * @param lower is the lower inclusive bound of the range, 
     * upperis the upper inclusive bound of the range.
     * @return the updated filter.
     */
    @Scripted(description="Sets a filter so that the given property has to lie within 2 inclusive bounds. "+
    		"First parameter is the UUID key of the property, the second and third the lower and upper inclusive bounds" +
    		"of the range")
    public Filter setRange(String propertyKey, String lower, String upper){
        term = new RangeTerm(propertyKey, lower, upper);
        return this;
    }
    
    /**
     * Sets the filter to only pass those entities whose types match those
     * in the given type set.
     * @param types are the set of types to check against.
     * @return the updated filter.
     */
    @Scripted(description="Sets a filter to only match entities of the given type(s).")
    public Filter ofType(MetaEntitySet types){
        term = new TypeTerm(types);
        return this;
    }
    
    /**
     * Sets the filter to be a logical OR between the original state of
     * this filter and another filter.
     * @param other is the other filter to OR with.
     * @return the updated filter.
     */
    @Scripted(description="Sets the filter to be a logical OR between the original state of this filter and another filter.")
    public Filter or(Filter other){
        term = new OrTerm(term, other.getTerm());
        return this;
    }
    
    /**
     * Sets the filter to be a logical AND between the original state of
     * this filter and another filter.
     * @param other is the other filter to AND with.
     * @return the updated filter.
     */
    @Scripted(description="Sets the filter to be a logical AND between the original state of this filter and another filter.")
    public Filter and(Filter other){
        term = new AndTerm(term, other.getTerm());
        return this;
    }
    
    /**
     * Sets the filter to be a logical NOT of its original state.
     * @return the updated filter.
     */
    @Scripted(description="Sets the filter to be a logical NOT of its original state.")
    public Filter not(){
        term = new NotTerm(term);
        return this;
    }
    
    /**
     * Gets the underlying Term for this filter.
     * @return the underlying Term.
     */
    private Term getTerm(){
        return term;
    }

    /**
     * Determines whether the filter matches a given entity.
     * @param e is the Entity to match.
     * @return true if it matches, false otherwise.
     */
    public boolean matches(Entity e) {
        return term.matches(e);
    }
    
    
    /**
     * Term is an abstract class for matching entities.  The (complex)
     * matching rules may be built up from a graph of Term or its
     * sub-classes.
     * 
     * @author rbp28668
     */
    private static abstract class Term {
        /**
         * Match an entity.
         * @param e is the entity to match.
         * @return true if the entity matches, false otherwise.
         */
        abstract boolean matches(Entity e);
    }
    
    /**
     * MatchAllTerm matches any entity.  Gives a safe default.
     * 
     * @author rbp28668
     */
    private static class MatchAllTerm extends Term {

        /* (non-Javadoc)
         * @see alvahouse.eatool.scripting.proxy.Filter.Term#matches(alvahouse.eatool.repository.model.Entity)
         */
        boolean matches(Entity e) {
            return true;
        }
    }
    
    /**
     * RegexpTerm matches a property against a regular expression
     * 
     * @author rbp28668
     */
    private static class RegexpTerm extends Term {
        
        private UUID key;
        private Pattern pattern;
        
        /**
         * Creates a RegexpTerm tied to a given property and regexp.
         * @param propertyKey is the string representation of a UUID that
         * identifies the property to match against.
         * @param regexp is the regular expression to test the property against.
         */
        RegexpTerm(String propertyKey, String regexp){
            key = new UUID(propertyKey);
            pattern = Pattern.compile(regexp);
        }
        
        /* (non-Javadoc)
         * @see alvahouse.eatool.scripting.proxy.Filter.Term#matches(alvahouse.eatool.repository.model.Entity)
         */
        boolean matches(Entity e){
            Property p = e.getPropertyByMeta(key);
            boolean matched = true;
            if(p != null){
                Matcher m = pattern.matcher(p.getValue());
                matched = m.matches();
            }
            return matched;
        }
    }
    
    /**
     * RangeTerm matches entities against a lower and upper bound.
     * 
     * @author rbp28668
     */
    private static class RangeTerm extends Term {

        private UUID key;
        private String lower;
        private String higher;
        private Comparable low = null;
        private Comparable high = null;
        
        /**
         * @param propertyKey is the string representation of a UUID that
         * identifies the property to match against.
         * @param lower is the lower bound to match against.
         * @param higher is the higher bound to match against.
         */
        RangeTerm(String propertyKey, String lower, String higher){
            key = new UUID(propertyKey);
            this.lower = lower;
            this.higher = higher;
        }
        
        /* (non-Javadoc)
         * @see alvahouse.eatool.scripting.proxy.Filter.Term#matches(alvahouse.eatool.repository.model.Entity)
         */
        boolean matches(Entity e) {
            Property p = e.getPropertyByMeta(key);
            
            boolean matched = true;
            if(p != null){
                
                MetaPropertyType type = p.getMeta().getMetaPropertyType();
                
                // Want to get comparable objects to over-ride string comparision
                // as appropriate.  These are cached after first use.
                if(low == null || high == null){
                    low = type.getComparable(lower);
                    high = type.getComparable(higher);
                }
                
                Comparable value = type.getComparable(p.toString());
                matched = low.compareTo(value) <= 0 && high.compareTo(value) >= 0;
            }
            return matched;
        }
        
    }

    /**
     * TypeTerm filters entities on their type (MetaEntity).
     * 
     * @author rbp28668
     */
    private static class TypeTerm extends Term {
        
        private MetaEntitySet types;
        
        /**
         * Creates a new TypeTerm against a given set of types.
         * @param types is the MetaEntitySet containing the types
         * to match against.
         */
        TypeTerm(MetaEntitySet types){
            this.types = types;
        }

        /* (non-Javadoc)
         * @see alvahouse.eatool.scripting.proxy.Filter.Term#matches(alvahouse.eatool.repository.model.Entity)
         */
        boolean matches(Entity e) {
            return types.contains(e.getMeta());
        }
        
    }
    
    /**
     * AndTerm does a logical and of 2 other terms.
     * 
     * @author rbp28668
     */
    private static class AndTerm extends Term{
        
        private Term t1;
        private Term t2;

        /**
         * Create a new AndTerm tied to 2 other terms.
         * @param t1 is the first term to And
         * @param t2 is the second term to and.
         */
        AndTerm(Term t1, Term t2){
            this.t1 = t1;
            this.t2 = t2;
        }
        
        /* (non-Javadoc)
         * @see alvahouse.eatool.scripting.proxy.Filter.Term#matches(alvahouse.eatool.repository.model.Entity)
         */
        boolean matches(Entity e) {
            return t1.matches(e) && t2.matches(e);
        }
    }

    /**
     * OrTerm performs a logical OR between 2 other terms.
     * 
     * @author rbp28668
     */
    private static class OrTerm extends Term{
        
        private Term t1;
        private Term t2;

        /**
         * Creates a new OrTerm with 2 other terms as operands.
         * @param t1 is the first term to OR.
         * @param t2 is the second term to OR.
         */
        OrTerm(Term t1, Term t2){
            this.t1 = t1;
            this.t2 = t2;
        }
        
        /* (non-Javadoc)
         * @see alvahouse.eatool.scripting.proxy.Filter.Term#matches(alvahouse.eatool.repository.model.Entity)
         */
        boolean matches(Entity e) {
            return t1.matches(e) || t2.matches(e);
        }
    }
    
    /**
     * NotTerm is a Term that gives the logical NOT of another term.
     * 
     * @author rbp28668
     */
    private static class NotTerm extends Term{
        private Term term;
        
        /**
         * Creates a new term that is the logical NOT of an existing term.
         * @param term is the existing term to invert.
         */
        NotTerm(Term term){
            this.term = term;
        }

        /* (non-Javadoc)
         * @see alvahouse.eatool.scripting.proxy.Filter.Term#matches(alvahouse.eatool.repository.model.Entity)
         */
        boolean matches(Entity e) {
            return !term.matches(e);
        }
    }

}
