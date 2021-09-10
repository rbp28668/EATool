/**
 * 
 */
package alvahouse.eatool.gui;

import java.awt.Cursor;

import javax.swing.JFrame;

/**
 * @author bruce_porteous
 *
 */
public class WaitCursor implements AutoCloseable {

	private JFrame frame;
	/**
	 * 
	 */
	public WaitCursor(JFrame frame) {
		this.frame = frame;
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	/* (non-Javadoc)
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		frame.setCursor(Cursor.getDefaultCursor());
	}

}
