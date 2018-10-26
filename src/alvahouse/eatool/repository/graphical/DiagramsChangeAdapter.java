/*
 * DiagramsChangeAdapter.java
 * Created on 20-Jan-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.graphical;

/**
 * DiagramsChangeAdapter provides an adapter class for 
 * DiagramsChangeListener.  All the methods of this class
 * are stubs that do nothing hence subclasses only need to over-ride
 * those methods they are interested in.
 * @author Bruce.Porteous
 *
 */
public class DiagramsChangeAdapter implements DiagramsChangeListener {

	/**
	 * 
	 */
	public DiagramsChangeAdapter() {
		super();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.DiagramsChangeListener#typesUpdated(alvahouse.eatool.gui.graphical.DiagramsChangeEvent)
	 */
	public void typesUpdated(DiagramsChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.DiagramsChangeListener#diagramTypeAdded(alvahouse.eatool.gui.graphical.DiagramsChangeEvent)
	 */
	public void diagramTypeAdded(DiagramsChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.DiagramsChangeListener#diagramTypeChanged(alvahouse.eatool.gui.graphical.DiagramsChangeEvent)
	 */
	public void diagramTypeChanged(DiagramsChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.DiagramsChangeListener#diagramTypeDeleted(alvahouse.eatool.gui.graphical.DiagramsChangeEvent)
	 */
	public void diagramTypeDeleted(DiagramsChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.DiagramsChangeListener#diagramsUpdated(alvahouse.eatool.gui.graphical.DiagramsChangeEvent)
	 */
	public void diagramsUpdated(DiagramsChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.DiagramsChangeListener#diagramAdded(alvahouse.eatool.gui.graphical.DiagramsChangeEvent)
	 */
	public void diagramAdded(DiagramsChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.DiagramsChangeListener#diagramChanged(alvahouse.eatool.gui.graphical.DiagramsChangeEvent)
	 */
	public void diagramChanged(DiagramsChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.DiagramsChangeListener#diagramDeleted(alvahouse.eatool.gui.graphical.DiagramsChangeEvent)
	 */
	public void diagramDeleted(DiagramsChangeEvent e) {
	}

}
