/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.repository.persist.ScriptPersistence;
import alvahouse.eatool.repository.scripting.Script;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class ScriptPersistenceMemory implements ScriptPersistence {

	private Map<UUID, Script> scripts = new HashMap<>();
	
	/**
	 * 
	 */
	public ScriptPersistenceMemory() {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#dispose()
	 */
	@Override
	public void dispose() {
		scripts.clear();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#getScript(alvahouse.eatool.util.UUID)
	 */
	@Override
	public Script getScript(UUID key) throws Exception {
		Script s = scripts.get(key);
		if(s == null) {
			throw new IllegalArgumentException("Script with key " + key + " not found in repository");
		}
		return s;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#addScript(alvahouse.eatool.repository.scripting.Script)
	 */
	@Override
	public void addScript(Script s) throws Exception {
	   	UUID key = s.getKey();
    	if(scripts.containsKey(key)) {
    		throw new IllegalArgumentException("Duplicate script -" + s.getName());
    	} 
    	scripts.put(key, (Script)s.clone());
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#updateScript(alvahouse.eatool.repository.scripting.Script)
	 */
	@Override
	public void updateScript(Script s) throws Exception {
	   	UUID key = s.getKey();
    	if(!scripts.containsKey(key)) {
    		throw new IllegalArgumentException("Can't update a script " + s.getName() + " not in the repository.");
    	} 
    	scripts.put(key, (Script)s.clone());
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#deleteScript(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteScript(UUID key) throws Exception {
    	if(!scripts.containsKey(key)) {
    		throw new IllegalArgumentException("Can't delete script with key " + key + ": not in repository");
    	}
    	scripts.remove(key);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#getScripts()
	 */
	@Override
	public Collection<Script> getScripts() {
		List<Script> copy = new ArrayList<>(scripts.size());
		for(Script s : scripts.values()) {
			copy.add( (Script) s.clone());
		}
		return copy;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#getScriptCount()
	 */
	@Override
	public int getScriptCount() {
		return scripts.size();
	}

}
