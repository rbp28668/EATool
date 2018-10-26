package alvahouse.eatool.repository.model;

import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.repository.base.NamedRepositoryItem;
import alvahouse.eatool.util.UUID;

/**
 * A scenario allows different views of a model to be managed.  Scenarios are hierarchical - changes in a base scenario are visible
 * in its children but not vice-versa.
 * @author bruce.porteous
 *
 */
public class Scenario extends NamedRepositoryItem{

	private Scenario base = null;
	private List<Scenario> derived = new LinkedList<Scenario>();
	
	public Scenario(){
		this(new UUID());
	}
	public Scenario(UUID uuid) {
		super(uuid);
	}
	/**
	 * @return the base
	 */
	public Scenario getBase() {
		return base;
	}
	/**
	 * @param base the base to set
	 */
	public void setBase(Scenario base) {
		if(base == null){
			throw new NullPointerException("Can't set null base scenario");
		}
		this.base = base;
	}
	
	public void disconnect(){
		this.base = null;
	}
	
	/**
	 * @return the derived
	 */
	public List<Scenario> getDerived() {
		return derived;
	}
	/**
	 * @param derived the derived to set
	 */
	public void setDerived(List<Scenario> derived) {
		this.derived = derived;
	}

	
}
