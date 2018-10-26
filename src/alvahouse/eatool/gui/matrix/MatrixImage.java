/*
 * MatrixImage.java
 * Project: EATool
 * Created on 07-Aug-2005
 *
 */
package alvahouse.eatool.gui.matrix;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

/**
 * MatrixImage builds a black and white image from the contents of a matrix to
 * give a thumbnail display of the matrix layout.
 * 
 * @author rbp28668
 */
public class MatrixImage extends BufferedImage implements Icon, MatrixTableModelListener {

    private MatrixTableModel model;
    /**
     * @param arg0
     * @param arg1
     * @param arg2
     */
    public MatrixImage(MatrixTableModel model) {
        super(model.getColumnCount(), model.getRowCount(), 	TYPE_BYTE_GRAY );
        this.model = model;
        buildImage();
    }

    private void buildImage(){
         for(int row = 0; row < model.getRowCount(); ++row){
            for(int col = 0; col < model.getColumnCount(); ++col){
                updatePixel(model.booleanValueAt(row,col), row, col);
            }
        }
    }
    
    private void updatePixel(boolean value, int row, int col){
        int intensity = (value) ? 255 : 0;
        int rgb = (intensity << 16) | (intensity << 8) | intensity;
        setRGB(col,row,rgb);
    }

    /* (non-Javadoc)
     * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.drawImage(this,x,y,null);
    }

    /* (non-Javadoc)
     * @see javax.swing.Icon#getIconWidth()
     */
    public int getIconWidth() {
         return model.getColumnCount();
    }

    /* (non-Javadoc)
     * @see javax.swing.Icon#getIconHeight()
     */
    public int getIconHeight() {
        return model.getRowCount();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.matrix.MatrixTableModelListener#columnOrderChanged()
     */
    public void columnOrderChanged() {
        buildImage();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.matrix.MatrixTableModelListener#rowOrderChanged()
     */
    public void rowOrderChanged() {
        buildImage();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.matrix.MatrixTableModelListener#valueChanged(int, int, boolean)
     */
    public void valueChanged(int rowIndex, int columnIndex, boolean linked) {
        updatePixel(linked, rowIndex, columnIndex);
    }
}
