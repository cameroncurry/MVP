import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class SIRSView extends JPanel{

	private static final long serialVersionUID = 1L;

	private int[][] array;
	
	public SIRSView() {
		setBackground(Color.white);
	}
	
	public void set(int[][] array){
		this.array = array;
	}
	
	
	//coords must be {x,y}
	public void paintCoords(int[] coords){
		repaint();
		/*
		Dimension d = getSize();
		double boxWidth = (double)d.width / (double)array.length;
		double boxHeight = (double)d.height / (double)array[0].length;
		repaint((int)(coords[0]*boxWidth),(int)(coords[1]*boxHeight),(int)boxWidth,(int)boxHeight);
		*/
	}
	
	public void paintComponent(Graphics g){
		
		//only paint if array has been set
		if(array != null){
			Dimension d = getSize(); //must call getSize here because dimensions aren't initialized in constructor
			
			double boxWidth = (double)d.width / (double)array.length;
			double boxHeight = (double)d.height / (double)array[0].length;
					
					
			for(int i=0; i<array.length; i++){
				for(int j=0; j<array[i].length; j++){
					paintBox(g,i,j,boxWidth,boxHeight);	
				}
			}	
		}
		else {
			super.paintComponent(g);
		}
	}
	
	private void paintBox(Graphics g, int i, int j, double boxWidth, double boxHeight){
		if(array[i][j] < 0) g.setColor(Color.yellow);
		else if(array[i][j] == 0) g.setColor(Color.red);
		else if(array[i][j] == 1) g.setColor(Color.green);
		else g.setColor(Color.black);
		
		int x = (int)(i*boxWidth) + (int)(boxWidth);
		int y = (int)(j*boxHeight) + (int)boxHeight;
		
		int nextX = (int)((i+1)*boxWidth);
		int nextY = (int)((j+1)*boxHeight);
		
		int xGap = nextX-x;
		int yGap = nextY-y;
		
		g.fillRect((int)(i*boxWidth),(int)(j*boxHeight), (int)boxWidth+xGap, (int)boxHeight+yGap);	
	}
		
	

}
