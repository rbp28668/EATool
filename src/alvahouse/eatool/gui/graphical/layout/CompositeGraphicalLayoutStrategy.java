/*
 * CompositeGraphicalLayoutStrategy.java
 *
 * Created on 06 March 2002, 09:40
 */

package alvahouse.eatool.gui.graphical.layout;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;


/**
 * this strategy is a composite of other strategies that are used, until each
 * of them is complete, do layout the nodes.  This allows multiple strategies
 * to be used so that a simple coarse technique does gross layout followed
 * by a "polishing" stage.
 * @author  rbp28668
 */
public class CompositeGraphicalLayoutStrategy implements IGraphicalLayoutStrategy {

    private List<IGraphicalLayoutStrategy> strategies = new LinkedList<IGraphicalLayoutStrategy>();
    private Iterator<IGraphicalLayoutStrategy> iter = null;
    private IGraphicalLayoutStrategy currentStrategy = null;

    /** Creates new CompositeGraphicalLayoutStrategy */
    public CompositeGraphicalLayoutStrategy() {
    }

    public void add(IGraphicalLayoutStrategy strat) {
        strategies.add(strat);
    }
    
    public boolean isComplete() {
        return currentStrategy == null;
    }
    
    public void reset() {
        iter = strategies.iterator();
        nextStrategy();
    }
    
    public void layout(NodeGraph model) {
        if(currentStrategy != null) {
            currentStrategy.layout(model);
        
            if(currentStrategy.isComplete()) {
                nextStrategy();
            }
        }
    }

    private void nextStrategy() {
        currentStrategy = null;
        if(iter.hasNext()) {
            currentStrategy = (IGraphicalLayoutStrategy)iter.next();
            currentStrategy.reset();
            //System.out.println("Using strategy " + currentStrategy.getClass().getName());
        }
    }
    
}
