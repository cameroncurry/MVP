import java.util.Random;
import java.util.ArrayList;

/*
 * Model for molecular dynamics
 * contains N-particles and capable of updating
 * position and velocities with the velocity verlet algorithm
 * using the LJ potential and WCA potential
 */
public class MDModel {

	
	private Particle3D[] particles;
	private double timeStep;
	private double sigma;
	private double epsilon;
	private Random r;
	
	//NVT variables
	private double friction = 1;
	private double T = 10;
	//private double rt3 = Math.sqrt(3.);
	
	//length width and height of the box particles are within
	private double dimension;
	
	//defines distance for which LJ potential is 0
	private double criticalR;
	
	private boolean LJ; //use either Lennard-Jones or WCA potential
	private boolean soft; // use soft potential for initialisation of random positions
	private boolean periodic;
	private boolean NVE;
	private boolean twoD;
	
	//arrays for kinetic energy, potential energy and temperature
	ArrayList<Double> ke, pe, temp, r2Av;
	Vector3D[] initialPositions; 
	
	
	public MDModel(int N,double timeStep,double sigma,double epsilon,double density,
			boolean LJ,boolean random, boolean twoD,boolean periodic,boolean NVE) {
		particles = new Particle3D[N];
		r = new Random();
		//initialise particle's position and velocity and create dimensions of box 
		dimension = initialise(N,density,sigma,random,twoD);
		
		
		//make copy of positions for mean square displacement
		initialPositions = new Vector3D[particles.length];
		for(int i=0;i<particles.length;i++){
			//make deep copy of initial position so difference isn't 0 every time
			initialPositions[i] = new Vector3D(particles[i].position());
		}
		
		
		this.timeStep = timeStep;
		this.sigma = sigma;
		this.epsilon = epsilon;
		this.LJ = LJ;
		this.periodic = periodic;
		this.NVE = NVE;
		this.criticalR = sigma*Math.pow(2, 1./6.);
		this.twoD = twoD;
		
		ke = new ArrayList<Double>(100);
		pe = new ArrayList<Double>(100);
		temp = new ArrayList<Double>(100);
		r2Av = new ArrayList<Double>(100);
	}
	
	public MDModel(MDSettings settings){
		this(settings.N,settings.timeStep,settings.sigma,settings.epsilon,settings.density,settings.LJ,
				settings.random,settings.twoD,settings.periodic,settings.NVE);
	}
	
	
	//returns dimension of box in a Vector3D
	private double initialise(int N, double density, double sigma,boolean random, boolean twoD) {
		
		//soft = true;
		if(random){
			soft = true;
			//r = new Random();
			if(twoD){
				double area = (double)N*Math.PI/(4*sigma*sigma*density);
				double dimension = Math.sqrt(area);
				for(int i=0; i<N; i++){
					Vector3D position = new Vector3D(r.nextDouble()*dimension,r.nextDouble()*dimension,0);
					particles[i] = new Particle3D(1,position,new Vector3D());//start with 0 velocity
				}
				return dimension;
			}
			else {
				double volume = (double)N*Math.PI / (6*Math.pow(sigma, 3)*density);
				double dimension = Math.cbrt(volume);
				for(int i=0; i<N; i++){
					Vector3D position = new Vector3D(r.nextDouble()*dimension,r.nextDouble()*dimension,dimension*r.nextDouble());
					particles[i] = new Particle3D(1,position,new Vector3D());
				}
				return dimension;
			}
		}
		else{//lattice
			if(twoD){
				double area = (double)N*Math.PI / (4*sigma*sigma*density);
				double dimension = Math.sqrt(area);
				double nPerD = Math.sqrt(N); //particles per dimension
				
				double delta = 1.2*sigma;
				double spacing = dimension / ((double)N*sigma);
				
				int index = 0;
				for(int i=0;i<nPerD;i++){
					for(int j=0;j<nPerD;j++){
						if(index < N){
							Vector3D position = new Vector3D(i*delta+spacing/2.,j*delta+spacing/2.,0);
							particles[index] = new Particle3D(1,position,new Vector3D());
							index ++;
						}	
					}
				}
				return dimension;
			}
			else {
				double volume = N*Math.PI / (6*sigma*sigma*sigma*density);
				double sideLength = Math.cbrt(volume);
				double nPerD = Math.cbrt(N);
				
				double delta = sideLength / nPerD;
				
				int index = 0;
				for(int i=0;i<nPerD;i++){
					for(int j=0;j<nPerD;j++){
						for(int k=0;k<nPerD;k++){
							if(index < N){
								Vector3D pos = new Vector3D(i*delta,j*delta,k*delta);
								particles[index] = new Particle3D(1,pos,new Vector3D());
								index++;
							}
						}
					}
					
				}
				return sideLength;
			}
		}
		
		
	}
	
	public boolean isTwoD(){
		return twoD;
	}
	
	public double getBoxDimension(){
		return dimension;
	}
	
	public Particle3D[] getParticles(){
		return particles;
	}
	public void setSoft(boolean soft){
		this.soft = soft;
	}
	public boolean isSoft(){
		return soft;
	}
	
	public double[] kineticEnergy(){
		return listToArray(ke);
	}
	public double[] potentialEnergy(){
		return listToArray(pe);
	}
	public double[] temperature(){
		return listToArray(temp);
	}
	public double[] rSqaured(){
		return listToArray(r2Av);
	}
	
	private double[] listToArray(ArrayList<Double> a){
		int size = a.size();
		double[] array = new double[size];
		for(int i=0;i<size;i++){
			array[i] = a.get(i);
		}
		return array;
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
				Vector3D initial = initialPositions[i];
				//check x coordinate
				if(pos.x() > dimension){//outside box
					pos.setX(pos.x() - dimension);
					//adjust initial position of particle if moved to other sider of the box
					initial.setX(initial.x()-dimension);
				}
				else if(pos.x() < 0.){
					pos.setX(pos.x() + dimension);
					initial.setX(initial.x() + dimension);
				}
				//y coordinate
				if(pos.y() > dimension){
					pos.setY(pos.y() - dimension);
					initial.setY(initial.y() - dimension);
				}
				else if(pos.y() < 0.){
					pos.setY(pos.y() + dimension);
					initial.setY(initial.y() + dimension);
				}
				//z
				if(pos.z() > dimension){
					pos.setZ(pos.z() - dimension);
					initial.setZ(initial.z() - dimension);
				}
				else if(pos.z() < 0.){
					pos.setZ(pos.z() + dimension);
					initial.setZ(initial.z() + dimension);
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
				
				
				Vector3D f = new Vector3D();
				if(soft){
					f = softForce(r);	
				}
				else if(LJ){
					f = LJForce(r);
				}
				else {
					f = WCAForce(r);
				}
				
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
	
	/*
	 * update methods for energy and temperature array
	 */
	
	public void updateArrays(){
	
		
		
		double keSum = 0;
		double peSum = 0;
		double r2Sum = 0;
		for(int i=0;i<particles.length;i++){
			
			keSum += particles[i].mass()*Vector3D.dot(particles[i].velocity(), particles[i].velocity());
			double deltaR = Vector3D.subtract(particles[i].position(),initialPositions[i]).mag();
			r2Sum += deltaR*deltaR;
			
			for(int j=0;j<i;j++){
				Vector3D r = Particle3D.separation(particles[i], particles[j]);
				
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
				
				if(LJ){
					peSum += LJPotential(r);
				}
				else{
					peSum += WCAPotential(r);
				}
			}
		}
		
		ke.add(0.5*keSum);
		pe.add(peSum);
		r2Av.add(r2Sum / particles.length);
		
		//equipartition - boltzmann constant = 1
		if(twoD){
			temp.add(keSum / (2*(particles.length-1)));
		}
		else{
			temp.add(keSum / (3*(particles.length-1)));
		}
		
	}
	
	
	
	/*
	 * Lennard-Jones and WCA forces
	 */
	
	private double LJPotential(Vector3D r){
		double div = sigma / r.mag();
		return 4*epsilon*( Math.pow(div, 12) - Math.pow(div, 6));
	}
	
	private Vector3D LJForce(Vector3D r){
		double division = sigma/r.mag();
		double c = -24*epsilon*(2*Math.pow(division,13) - Math.pow(division, 7)) / (sigma*r.mag());
		return r.multiply(c);
	}
	
	private double WCAPotential(Vector3D r){
		if(r.mag() < criticalR){
			return LJPotential(r) + epsilon;
		}
		else{
			return 0.;
		}
	}
	
	private Vector3D WCAForce(Vector3D r){
		if(r.mag() < criticalR){
			return LJForce(r);
		}
		else{
			return new Vector3D(); //return 0
		}
	}
	
	
	/*
	 * soft potential for random initialization
	 */
	private double A = 10;
	
	private Vector3D softForce(Vector3D r){
		if(r.mag() < criticalR){
			double c = -A*Math.PI*Math.sin(Math.PI*r.mag()/criticalR)/(criticalR*r.mag());
			return r.multiply(c);
		}
		else{
			return new Vector3D();
		}
	}

}
