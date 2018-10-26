/*
 * SimulatedAnnealingLayoutStrategy.java
 *
 * Created on 23 February 2002, 06:04
 */

package alvahouse.eatool.gui.graphical.layout;



/**
 * Base class for all simulated annealing derived layout strategies.  This 
 * manages the key parts of the algorithm.  Derived classes need to consider
 * a putative change which returns the change in energy if that change takes
 * place.  This then uses the Metropolis algorithm to accept or reject
 * the change.
 * @author  rbp28668
 */
public abstract class SimulatedAnnealingLayoutStrategy implements IGraphicalLayoutStrategy {

    /** Creates new SimulatedAnnealingLayoutStrategy */
    public SimulatedAnnealingLayoutStrategy() {
    }

//    protected abstract float getEnergy();
    protected abstract float change();
    protected abstract void acceptChange();
    protected abstract void rejectChange();
    
    protected float estimateIntialTemp() {
        float delta = 0;
        final int count = 10;
        for(int i=0; i<count; ++i) { // 10 - guess as trade off between good estimate & time
            float de = change();
            delta += Math.abs(de);
        }
        temperature = delta / count;  // for de = T the probability of accepting a worse change is 0.37
        						 // so probably not a bad starting point.
        System.out.println();
        System.out.println("Estimated initial Temp is " + temperature);
        return temperature;
    }
    
    public float getTemperature() {
        return temperature;
    }
    
    public void setTemperature(float t) {
        temperature = t;
    }
    
    protected NodeGraph getGraph() {
    	return graph;
    }
    
    /** anneal does a pass at a single temperature & resets the 
     * temperature for the next path.
     * @return true if any changes were made.
     */
    protected boolean anneal() {
        int successCount = 0;
        System.out.println("Annealing at T=" + temperature);
        int cNodes = graph.nodeCount();
        if(cNodes > 2) {
	        for(int i = 0; i < cNodes; ++i) {
	            float de = change();
	            //System.out.println("DE: " + de);
	            if( metropolis(de, temperature)) {
	                acceptChange();
	                ++successCount;
	            } else {
	                rejectChange();
	            }
	        }
        }
        temperature *= annealSchedule;
        ++tempSteps;
        System.out.println("At step " + tempSteps + " swapped " + successCount);
        return successCount > 0;
    }
    /** Metropolis algorithm returns a boolean which issues a verdict on whether 
     * to accept a reconfiguration which leads to a change de in the objective function e.
     * if de < 0 metropolis always returns true, while if de > 0 metropolis is only true with
     * propability exp(-de/t), where t is the temperature determined by the annealing 
     * schedule.
     * @param de is the change in the objective function.
     * @param t is the annealing temperature.
     * @return whether to accept the change de or not.
     */
    private boolean metropolis(float de, float t) {
        return de < 0.0 || Math.random() < Math.exp(-de/t);
    }
    
	/**
	 * @see alvahouse.eatool.GUI.Graphical.IGraphicalLayoutStrategy#reset()
	 */
    public void reset() {
        temperature = 100;
        tempSteps = 0;
        complete = false;
        initialised = false;
    }    
    
	/**
	 * @see alvahouse.eatool.GUI.Graphical.IGraphicalLayoutStrategy#isComplete()
	 */
    public boolean isComplete() {
        return complete || (tempSteps > 100); // magic (as usual)
    }
    
	/**
	 * @see alvahouse.eatool.GUI.Graphical.IGraphicalLayoutStrategy#layout(NodeGraph)
	 */
    public void layout(NodeGraph model) {
        graph = model;

        if(!initialised) {
            float temp = estimateIntialTemp();
            setTemperature(temp);
            initialised = true;
        }
        
        complete = !anneal();
        if(complete) System.out.println("Layout complete at T=" + temperature);
    }

	/**
	 * Returns the annealSchedule.
	 * @return float
	 */
	public float getAnnealSchedule() {
		return annealSchedule;
	}

	/**
	 * Returns the changesPerStep.
	 * @return int
	 */
	public int getChangesPerStep() {
		return changesPerStep;
	}

	/**
	 * Returns the maxConfigsPerStep.
	 * @return int
	 */
	public int getMaxConfigsPerStep() {
		return maxConfigsPerStep;
	}

	/**
	 * Returns the tempSteps.
	 * @return int
	 */
	public int getTempSteps() {
		return tempSteps;
	}

	/**
	 * Sets the annealSchedule.
	 * @param annealSchedule The annealSchedule to set
	 */
	public void setAnnealSchedule(float annealSchedule) {
		this.annealSchedule = annealSchedule;
	}

	/**
	 * Sets the changesPerStep.
	 * @param changesPerStep The changesPerStep to set
	 */
	public void setChangesPerStep(int changesPerStep) {
		this.changesPerStep = changesPerStep;
	}

	/**
	 * Sets the maxConfigsPerStep.
	 * @param maxConfigsPerStep The maxConfigsPerStep to set
	 */
	public void setMaxConfigsPerStep(int maxConfigsPerStep) {
		this.maxConfigsPerStep = maxConfigsPerStep;
	}

	/**
	 * Sets the tempSteps.
	 * @param tempSteps The tempSteps to set
	 */
	public void setTempSteps(int tempSteps) {
		this.tempSteps = tempSteps;
	}

	/** The graph being optimised */
    private NodeGraph graph = null;
    
    /** the current annealing temperature */
    private float temperature = 100;
    
    /** the amount to drop the annealing temperature each run */
    private float annealSchedule = 0.9f;
    
    /** count of temperature steps during this annealing schedule */
    private int tempSteps = 0;
    
    /** maximum number of changes per temperature step to try */
    private int maxConfigsPerStep = 300;
    
    /** maximum number of successful changes at a temp step before dropping temp */
    private int changesPerStep = 30;

	/** true iff initialised */    
    private boolean initialised = false;
    
    /** true if complete */
    private boolean  complete = false;
    
    

}
