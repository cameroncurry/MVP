import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;


/*
 * class that draws a 2d boolean grid in squares
 */

public class IsingView extends JPanel {

	private static final long serialVersionUID = 1L;
	
	
	//array set to null at construction
	//must e set later in simulation
	private boolean[][] array;
	
	
	public IsingView(){
		setBackground(Color.white);
	}
	
	public void setAndRepaint(boolean[][] array){
		this.array = array;
		repaint();
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
		if(array[i][j]) g.setColor(Color.blue);
		else g.setColor(Color.white);
	
		g.fillRect((int)(i*boxWidth), (int)(j*boxHeight), (int)boxWidth, (int)boxHeight);
	}

}
