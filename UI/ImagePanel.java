import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


public class ImagePanel extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;

	private BufferedImage image;
	
	private int animIndex;
	private int animMaxIndex;
	private int circleSize;
	private int spacing;
	private int animWidth;
	
	private boolean threadFlag;
	
	
	public ImagePanel(){
		setBackground(Color.white);
		animIndex = 0;
		animMaxIndex = 5;
		circleSize = 13;
		spacing = 5;
		animWidth = animMaxIndex*(circleSize+spacing);
		
		threadFlag = false;
	}
	
	public void startAnimation(){
		threadFlag = false;
		new Thread(this).start();
	}
	public void stopAnimation(){
		threadFlag = true;
		animIndex = 0;
	}
	public void setImage(BufferedImage image){
		this.image = image;
	}
	
	public void paintComponent(Graphics g){
		Dimension d = getSize();
		if(image != null){
			//resize image to fit panel
			BufferedImage img = resize(this.image,d.width,d.height);
			g.drawImage(img,0,0,null);
			
			
		}
		else{
			super.paintComponent(g);
			int startx = d.width/2 - animWidth/2;
			int starty = d.height/2 - circleSize/2;
			
			for(int i=0;i<animMaxIndex;i++){
				if(i == animIndex){
					g.fillOval(startx + i*(circleSize+spacing), starty, circleSize, circleSize);
				}
				else{
					g.drawOval(startx + i*(circleSize+spacing), starty, circleSize, circleSize);
				}	
			}
			
		}
	}
	
	private BufferedImage resize(BufferedImage img, int newW, int newH){
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}
	
	public void run(){
		while(image == null && !threadFlag){
			animIndex = (animIndex+1)%animMaxIndex;
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			repaint();
		}
	}
	
}
