package nz.co.iswe.mediamanager.ui.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	private static final long serialVersionUID = 5240722558269603191L;

	protected BufferedImage image;
	protected Dimension size = new Dimension();

    public ImagePanel() {
        
    }

    public void setImage(BufferedImage image){
    	this.image = image;
        size.setSize(image.getWidth(), image.getHeight());
        repaint();
    }
    
    /**
     * Drawing an image can allow for more
     * flexibility in processing/editing.
     */
    protected void paintComponent(Graphics g) {
    	if(image == null){
    		super.paintComponent(g);
    	}
    	
        // Center image in this component.
        int x = (getWidth() - size.width)/2;
        int y = (getHeight() - size.height)/2;
        g.drawImage(image, x, y, this);
    }

    public Dimension getPreferredSize() { return size; }

}
