/*
 * MetaRole.java
 * Project: EATool
 * Created on 24-Apr-2007
 *
 */
package alvahouse.eatool.scripting.proxy;

/**
 * MetaRole describes the role of an end of a MetaRelationship.  This
 * describes what type of thing (i.e. MetaEntity) the relationship can
 * connect to and its allowed multiplicity.
 * 
 * @author rbp28668
 */
@Scripted(description="MetaRole describes the role of an end of a MetaRelationship."
		+ " This describes what type of thing (i.e. MetaEntity) the relationship can" + 
		" connect to and its allowed multiplicity.")
public class MetaRoleProxy {

    private alvahouse.eatool.repository.metamodel.MetaRole metaRole;
    
    /**
     * 
     */
    MetaRoleProxy(
            alvahouse.eatool.repository.metamodel.MetaRole metaRole) {
        super();
        this.metaRole = metaRole;
    }
    
    /**
     * Get the MetaEntity that this MetaRole connects to.
     * @return the connected MetaEntity.
     */
    @Scripted(description="Gets the type of meta-entity this role connects to.")
    public MetaEntityProxy connectsTo(){
        return new MetaEntityProxy( metaRole.connectsTo());
    }

    /**
     * Gets the name of the role.
     * @return the role name.
     */
    @Scripted(description="Gets the name of this meta-role.")
    public String getName(){
        return metaRole.getName();
    }
    
    /**
     * Gets the description for the role.
     * @return the role description.
     */
    @Scripted(description="Gets the description of this meta-role.")
    public String getDescription(){
        return metaRole.getDescription();
    }
    
    /**
     * Gets the lower bound for multiplicity for this role.  
     * 0 implies this relationship is optional.
     * @return the lower bound.
     */
    @Scripted(description="Gets the lower bound for multiplicity for this role." + 
    		" 0 implies this relationship is optional. ")
    public int getLowerBound(){
        return metaRole.getMultiplicity().getLower();
    }
    
    /**
     * Gets the upper bound for multiplicity for this role.
     * @return the upper bound.
     */
    @Scripted(description="Gets the upper bound for multiplicity for this role.")
    public int getUpperBound(){
        return metaRole.getMultiplicity().getUpper();
    }
}
