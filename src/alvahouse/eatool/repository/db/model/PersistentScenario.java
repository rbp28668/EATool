package alvahouse.eatool.repository.db.model;

import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.repository.base.NamedRepositoryItem;
import alvahouse.eatool.util.UUID;

public class PersistentScenario extends NamedRepositoryItem{

	private PersistentScenario base = null;
	private List<PersistentScenario> derived = new LinkedList<PersistentScenario>();
	
	public PersistentScenario(){
		this(new UUID());
	}
	public PersistentScenario(UUID uuid) {
		super(uuid);
	}
	/**
	 * @return the base
	 */
	public PersistentScenario getBase() {
		return base;
	}
	/**
	 * @param base the base to set
	 */
	public void setBase(PersistentScenario base) {
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
	public List<PersistentScenario> getDerived() {
		return derived;
	}
	/**
	 * @param derived the derived to set
	 */
	public void setDerived(List<PersistentScenario> derived) {
		this.derived = derived;
	}

	
}
