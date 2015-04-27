/*
 * abstract class that draws an NxM on a JPanel
 * able to repaint entire grid by calling repaint() (inherited by JPanel)
 * also able to repaint a single grid element by calling repaintAgent(i,j)
 * 
 * method drawBox() must be implemented by subclass
 * according to type T used by subclass
 */

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

abstract class GridView<T> extends JPanel {

	//something to do with Java serialization
	private static final long serialVersionUID = 1L;

	//grid of agents of arbitrary type
	T[][] agents;
	
	public GridView(T[][] agents) {
		this.agents = agents;
	}
	
	//instantiates a grid of null agents (paintComponent can handle this)
	public GridView(){
		this(null);
	}
	
	public void set(T[][] agents){
		this.agents = agents;
	}
	
	//repaints a single box
	public void repaintAgent(int i, int j){
		//is agent i,j in array
		if(i>=0 && i<agents.length && j>=0 && j<agents[i].length){
			Dimension d = getSize();
			double boxWidth = d.getWidth() / (double)agents.length;
			double boxHeight = d.getHeight() / (double)agents[0].length;
			int widthGap = (int)((i+1)*boxWidth) - (int)(i*boxWidth);
			int hGap = (int)((j+1)*boxHeight) - (int)(j*boxHeight);
			this.repaint((int)(i*boxWidth),(int)(j*boxHeight),(int)boxWidth + widthGap,(int)boxHeight + hGap);
		}
	}
	
	public void paintComponent(Graphics g){
		
		if(agents != null){
			Dimension d = getSize();
			if(agents.length > d.width || agents[0].length > d.height){
				super.paintComponent(g);
			}
			double boxWidth = d.getWidth() / (double)agents.length;
			double boxHeight = d.getHeight() / (double)agents[0].length;
			
			for(int i=0;i<agents.length;i++){
				for(int j=0;j<agents[i].length;j++){
					int widthGap = (int)((i+1)*boxWidth) - (int)(i*boxWidth);
					int hGap = (int)((j+1)*boxHeight) - (int)(j*boxHeight);
					drawBox(agents[i][j], g,(int)(i*boxWidth),(int)(j*boxHeight),(int)boxWidth + widthGap,(int)boxHeight + hGap);
				}
			}			
		}
		else{
			super.paintComponent(g);
		}
	}
	
	//repaint a single element using the graphics object
	//x and y coordinates are given along with element width and height
	abstract void drawBox(T agent, Graphics g, int x, int y, int width, int height);

}
