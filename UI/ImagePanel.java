import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
	}
	public void setImage(BufferedImage image){
		this.image = image;
	}
	
	public void paintComponent(Graphics g){
		Dimension d = getSize();
		if(image != null){
			//resize image to fit panel
			BufferedImage resized = new BufferedImage(d.width,d.height,image.getType());
			Graphics2D g2 = resized.createGraphics();
			g2.drawImage(image, 0, 0, d.width, d.height, null);
			g2.dispose();
			
			g.drawImage(resized,0,0,null);
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
