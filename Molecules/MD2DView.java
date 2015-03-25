import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class MD2DView extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private Particle3D[] particles;
	private double boxDimension;
	
	private int particleSize = 20;

	public MD2DView() {
		setBackground(Color.white);
	}
	
	public void set(Particle3D[] particles,double dimension){
		this.particles = particles;
		this.boxDimension = dimension;
	}
	
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.setColor(Color.PINK);
		
		//paints x-y coordinates of particles
		if(particles != null){
			Dimension d = getSize();
			
			for(int i=0; i<particles.length; i++){
				double x = (particles[i].position().x() / boxDimension) * d.getWidth();
				double y = (particles[i].position().y() / boxDimension) * d.getHeight();
				
				g.fillOval((int)(x-particleSize/2), (int)(y-particleSize/2), particleSize, particleSize);
			}
			
		}
		
	}

}
