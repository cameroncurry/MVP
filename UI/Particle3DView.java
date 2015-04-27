import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class Particle3DView<T extends Particle3D> extends JPanel {
	
	//remove
	public static void main(String[] args){
		
		JFrame f = new JFrame();
		f.setSize(500,500);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		
		ColourParticle3D[] p = new ColourParticle3D[10];
		Random r = new Random();
		for(int i=0; i<p.length;i++){
			Vector3D pos = new Vector3D(r.nextDouble(),r.nextDouble(),r.nextDouble());
			p[i] = new ColourParticle3D(r.nextBoolean() ? Color.RED : Color.BLUE
					,1,pos,new Vector3D());
		}
		
		ColourParticle3DView view = new ColourParticle3DView(p,1.);
		view.set3D(true);
		f.getContentPane().add(view);
		f.setVisible(true);
		
	}
	
	
	private static final long serialVersionUID = 1L;
	
	
	private T[] particles;
	private double boxDimension;
	private Color particleColour;
	private boolean threeD;
	
	private int particleSize = 30;
	private double minimumAlpha = 0.1;
	private double minimumSize = 10;
	
	public Particle3DView(T[] particles, double boxDimension){
		setBackground(Color.white);
		this.particles = particles;
		this.boxDimension = boxDimension;
		particleColour = Color.black;
		this.threeD = false;
	}
	public Particle3DView() {
		this(null,Double.NaN);
	}
	
	public void setParticles(T[] particles, double dimension){
		this.particles = particles;
		this.boxDimension = dimension;
	}
	
	public void setParticleColour(Color colour){
		this.particleColour = colour;
	}
	public void setParticleSize(int size){
		this.particleSize = size;
	}
	public void set3D(boolean b){
		this.threeD = b;
	}
	
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		if(particles != null){
			Dimension d = getSize();
				
			for(T p: particles){
				prepareParticle(p);
				double size = particleSize;
				
				if(threeD){
					double alpha = (1.-minimumAlpha)*(p.position().z() / boxDimension) + minimumAlpha;
					size = (particleSize-minimumSize)*(p.position().z() / boxDimension) + minimumSize;
					g.setColor(new Color(particleColour.getRed(),
							particleColour.getGreen(),
							particleColour.getBlue(),
							(int)(255*alpha)));
				}
				else {
					g.setColor(particleColour);
				}
				double x = (p.position().x() / boxDimension) * d.getWidth();
				double y = (p.position().y() / boxDimension) * d.getHeight();
				
				
				g.fillOval((int)(x-size/2), (int)(y-size/2), (int)size, (int)size);
			}
		}
	}
	
	void prepareParticle(T particle){return;}
	
}
