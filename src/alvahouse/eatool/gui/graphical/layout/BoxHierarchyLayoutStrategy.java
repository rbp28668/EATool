/*
 * BoxHierarchyLayoutStrategy.java
 * Project: EATool
 * Created on 15-Feb-2006
 *
 */
package alvahouse.eatool.gui.graphical.layout;


/**
 * BoxHierarchyLayoutStrategy lays out a hierarchy of nodes as a set of nested
 * boxes where each box contains its children (and so on).
 * 
 * @author rbp28668
 */
public class BoxHierarchyLayoutStrategy implements IGraphicalLayoutStrategy {

    /**
     * 
     */
    public BoxHierarchyLayoutStrategy() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.layout.IGraphicalLayoutStrategy#layout(alvahouse.eatool.gui.graphical.NodeGraph)
     */
    public void layout(NodeGraph graph) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.layout.IGraphicalLayoutStrategy#reset()
     */
    public void reset() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.layout.IGraphicalLayoutStrategy#isComplete()
     */
    public boolean isComplete() {
        // TODO Auto-generated method stub
        return false;
    }

}
