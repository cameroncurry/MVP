import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class MDPractice {
	
	public static void main(String[] args) throws InterruptedException{
		
		
		JFrame f = new JFrame();
		f.setSize(700,600);
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(Color.white);
		
		JButton energyButton = new JButton("plot energy");
		header.add(energyButton,BorderLayout.EAST);
		contentPanel.add(header,BorderLayout.NORTH);
		
		
		double dt = 0.0001;
		double density = 1;
		double L = 10.;
		
		final MDPractice model = new MDPractice(dt,density,L);
		ColourParticle3DView view = new ColourParticle3DView(model.particles,model.length());
		
		
		energyButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				model.plotEnergy();
			}
		});
		
		contentPanel.add(view);
		f.getContentPane().add(contentPanel);
		f.setVisible(true);
		
		for(int i=0; true; i++){
			model.velocityVerlet();
			
			//Thread.sleep(1);
			view.repaint();
			
			if(i%100 == 0){
				model.updateEnergy();
			}
		}
		
	}
	
	
	private ColourParticle3D[] particles;
	private double dimension;
	private double sigma = 1;
	private double timeStep;
	
	private boolean periodic = true;
	private boolean NVE = true;
	
	private double friction = 1;
	private Random r = new Random();
	private double T = 10;
	
	private double criticalR = Math.pow(2, 1./6.)*sigma;
	
	
	private Observable energy;

	
	//given density and length of box, calculates number of particles
	public MDPractice(double timeStep, double density, double length) {
		int N = (int)(density*length*length / (Math.PI*sigma*sigma));
		particles = new ColourParticle3D[N];
		this.dimension = length;
		this.timeStep = timeStep;
		initialiseParticles(N,length);	
		
		energy = new Observable("Energy","time","Energy","kinetic","potential","total");
	}
	
	private void initialiseParticles(int N, double length){
		double dimension = length;
		double nPerD = Math.sqrt(N); //particles per dimension
		
		double delta = 1.2*sigma;
		double spacing = dimension / ((double)N*sigma);
		
		int index = 0;
		for(int i=0;i<nPerD;i++){
			for(int j=0;j<nPerD;j++){
				if(index < N){
					Vector3D position = new Vector3D(i*delta+spacing/2.,j*delta+spacing/2.,0);
					particles[index] = new ColourParticle3D(index>(N/2) ? Color.red : Color.blue,
							1,position,new Vector3D());
					index ++;
				}	
			}
		}
	}
	
	public ColourParticle3D[] particles(){
		return particles;
	}
	public double length(){
		return dimension;
	}
	
	int index = 0;
	public void plotEnergy(){
		energy.plot();
	}
	public void updateEnergy(){
		double ke = 0;
		double pe = 0;
		for(int i=0;i<particles.length;i++){
			ke += particles[i].kineticEnergy();
			
			for(int j=0;j<i;j++){
				Vector3D r = Particle3D.separation(particles[j], particles[i]);
				
				//minimum image
				if(periodic){
					//x
					if(r.x() > dimension/2.){
						r.setX(r.x() - dimension);
					}
					else if(r.x() < -dimension/2.){
						r.setX(r.x() + dimension);
					}
					//y
					if(r.y() > dimension/2.){
						r.setY(r.y() - dimension);
					}
					else if(r.y() < -dimension/2.){
						r.setY(r.y() + dimension);
					}
					//z
					if(r.z() > dimension/2.){
						r.setZ(r.z() - dimension);
					}
					else if(r.z() < -dimension/2.){
						r.setZ(r.z() + dimension);
					}
				}
				pe += potential(r,particles[i],particles[j]);
			}
		}
		
		energy.add(0, index);
		energy.add(1,ke);
		energy.add(2,pe);
		energy.add(3,ke+pe);
		index ++;
	}
	
	
	/*
	 * Verlet algorithm:
	 * updates each particles position and velocity
	 */
	
	Vector3D[] previousForce = null;
	
	public void velocityVerlet(){
		
		//forces acting on each particle
		//if first iteration calculate for from scratch
		//but if force from previous step has been set take that one
		//Vector3D[] forces = previousForce != null ? previousForce : forceArray();
		Vector3D[] forces = previousForce != null ? previousForce : optimizedForces();
		
		
		for(int i=0; i<particles.length; i++){
			Particle3D p = particles[i];
			
			//half step velocity
			if(NVE){
				p.setVelocity(Vector3D.add(p.velocity(), forces[i].multiply(timeStep/(2*p.mass()) ) ) );
			}
			else{ //NVT - add friction and stochastic term
				Vector3D forceTerm = forces[i].multiply(0.5*timeStep / p.mass());
				Vector3D frictionTerm = p.velocity().multiply(friction*timeStep*0.5);
				//Vector3D random = new Vector3D(rt3*(r.nextDouble()-0.5),rt3*(r.nextDouble()-0.5),rt3*(r.nextDouble()-0.5));
				Vector3D random = new Vector3D(r.nextGaussian(),r.nextGaussian(),r.nextGaussian());
				Vector3D randomTerm = random.multiply(Math.sqrt(timeStep*T*friction/p.mass()));
				Vector3D resultant = Vector3D.add(Vector3D.subtract(forceTerm, frictionTerm), randomTerm);
				p.setVelocity(Vector3D.add(p.velocity(),resultant));
			}
			
			
			//full step position
			Vector3D pos = Vector3D.add(p.position(), p.velocity().multiply(timeStep));
			if(periodic){
				
				//check x coordinate
				if(pos.x() > dimension){//outside box
					pos.setX(pos.x() - dimension);					
				}
				else if(pos.x() < 0.){
					pos.setX(pos.x() + dimension);
				}
				//y coordinate
				if(pos.y() > dimension){
					pos.setY(pos.y() - dimension);
				}
				else if(pos.y() < 0.){
					pos.setY(pos.y() + dimension);
				}
				//z
				if(pos.z() > dimension){
					pos.setZ(pos.z() - dimension);
				}
				else if(pos.z() < 0.){
					pos.setZ(pos.z() + dimension);
				}	
			}
			p.setPosition(pos);
		}
		
		//forces = forceArray();
		forces = optimizedForces();
		
		previousForce = forces;
		for(int i=0;i<particles.length;i++){
			Particle3D p = particles[i];
			//next half step velocity
			if(NVE){
				p.setVelocity(Vector3D.add(p.velocity(), forces[i].multiply(timeStep/(2*p.mass()) ) ) );
			}
			else{ //NVT - add friction
				Vector3D forceTerm = forces[i].multiply(0.5*timeStep / p.mass());
				Vector3D frictionTerm = p.velocity().multiply(friction*timeStep*0.5);
				Vector3D random = new Vector3D(r.nextDouble()-0.5,r.nextDouble()-0.5,r.nextDouble()-0.5);
				Vector3D randomTerm = random.multiply(Math.sqrt(timeStep*T*friction/p.mass()));
				Vector3D resultant = Vector3D.add(Vector3D.subtract(forceTerm, frictionTerm), randomTerm);
				p.setVelocity(Vector3D.add(p.velocity(),resultant));
			}
		}
		
	}
	
	private Vector3D[] optimizedForces(){
		Vector3D[] forces = new Vector3D[particles.length];
		
		for(int i=0;i<particles.length;i++){
			for(int j=0;j<i;j++){
				
				Vector3D r = Particle3D.separation(particles[j], particles[i]);
				
				//minimum image
				if(periodic){
					//x
					if(r.x() > dimension/2.){
						r.setX(r.x() - dimension);
					}
					else if(r.x() < -dimension/2.){
						r.setX(r.x() + dimension);
					}
					//y
					if(r.y() > dimension/2.){
						r.setY(r.y() - dimension);
					}
					else if(r.y() < -dimension/2.){
						r.setY(r.y() + dimension);
					}
					//z
					if(r.z() > dimension/2.){
						r.setZ(r.z() - dimension);
					}
					else if(r.z() < -dimension/2.){
						r.setZ(r.z() + dimension);
					}
				}
				
				
				Vector3D f = LJForce(r,particles[i], particles[j]);
				
				
				//add forces
				if(forces[i] != null){
					forces[i].add(f);
				}
				else {
					forces[i] = new Vector3D(f);
				}
				
				if(forces[j] != null){
					forces[j].subtract(f);
				}
				else{
					forces[j] = new Vector3D(f.multiply(-1.));
				}
			}
		}
		return forces;
	}
	
	
	private double potential(Vector3D r,ColourParticle3D a, ColourParticle3D b){
		
		if(a.colour == b.colour){
			if(r.mag() < 2.5*sigma){
				double epsilon = 1;
				double div = sigma / r.mag();
				return 4*epsilon*( Math.pow(div, 12) - Math.pow(div, 6)) + epsilon;
			}
			else{return 0.;}
		}
		else {
			if(r.mag() < criticalR){
				double epsilon = -0.05;
				double div = sigma / r.mag();
				return 4*epsilon*( Math.pow(div, 12) - Math.pow(div, 6)) + epsilon;
			}
			else{
				return 0.;
			}
		}
	}
	
	
	private Vector3D LJForce(Vector3D r, ColourParticle3D a, ColourParticle3D b){
		
		if(a.colour == b.colour){ // same colour - attractive
			
			if(r.mag() < 2.5*sigma){
				double epsilon = 1;
				double division = sigma/r.mag();
				double c = -24*epsilon*(2*Math.pow(division,13) - Math.pow(division, 7)) / (sigma*r.mag());
				return r.multiply(c);
			}
			else{return new Vector3D();}
			
			
		}
		else{ // opposite colours - repulsive
			if(r.mag() < criticalR){
				double epsilon = 1;
				double division = sigma/r.mag();
				double c = -24*epsilon*(2*Math.pow(division,13) - Math.pow(division, 7)) / (sigma*r.mag());
				if(r.mag()>criticalR) c=0.0;
				return r.multiply(c);
			}
			else { return new Vector3D();}
		}
	
		
	
	}
	
}

class ColourParticle3D extends Particle3D {
	
	public Color colour;
	
	public ColourParticle3D(Color c, double mass, Vector3D pos, Vector3D velocity){
		super(mass,pos,velocity);
		
		this.colour = c;
	}
}


class ColourParticle3DView extends Particle3DView<ColourParticle3D> {

	
	private static final long serialVersionUID = 1L;
	
	public ColourParticle3DView(ColourParticle3D[] particles, double dimension){
		super(particles,dimension);
	}
	
	void prepareParticle(ColourParticle3D p){
		super.setParticleColour(p.colour);
	}
}
