/*
 * IGraphicalLayoutStrategy.java
 *
 * Created on 12 February 2002, 18:02
 */

package alvahouse.eatool.gui.graphical.layout;

/**
 * Interface for auto-layout strategies.
 * @author  rbp28668
 */
public interface IGraphicalLayoutStrategy {

    /**
     * Does a single layout pass.  Layout should be called repeatedly
     * until isComplete() returns true.
	 * @param graph is the graph to layout.
	 */
	public void layout(NodeGraph graph);
    
    /**
	 * resets the internal state of the strategy.
	 */
	public void reset();
	
    /**
     * Determines whether the layout process has finished.  Call layout(AbstractNodeGraph)
     * repeatedly until this returns true to perform a full layout.
	 * @return true if complete.
	 */
	public boolean isComplete();
}

