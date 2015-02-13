import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

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
		Dimension d = getSize();
		double boxWidth = (double)d.width / (double)array.length;
		double boxHeight = (double)d.height / (double)array[0].length;
		repaint((int)(coords[0]*boxWidth),(int)(coords[1]*boxHeight),(int)boxWidth,(int)boxHeight);
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
		else if(array[i][j] == 1) g.setColor(Color.white);
		else g.setColor(Color.black);
	
		g.fillRect((int)(i*boxWidth), (int)(j*boxHeight), (int)boxWidth, (int)boxHeight);
	}
		
	

}
