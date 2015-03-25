import java.util.Random;

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
	
	//length width and height of the box particles are within
	private double dimension;
	
	//defines distance for which LJ potential is 0
	private double criticalR;
	
	private boolean LJ; //use either Lennard-Jones or WCA potential
	private boolean soft; // use soft potential for initialisation of random positions
	private boolean periodic;
	
	public MDModel(int N,double timeStep,double sigma,double epsilon,double density,
			boolean LJ,boolean random, boolean twoD,boolean periodic) {
		particles = new Particle3D[N];
		
		//initialise particle's position and velocity and create dimensions of box 
		dimension = initialise(N,density,sigma,random,twoD);
		
		this.timeStep = timeStep;
		this.sigma = sigma;
		this.epsilon = epsilon;
		this.LJ = LJ;
		this.periodic = periodic;
		this.criticalR = sigma*Math.pow(2, 1./6.);
		
		
	}
	
	
	//private double initialise(int N, double density, double sigma, boolean random, boolean twoD){
		
//	}
	
	
	
	//returns dimension of box in a Vector3D
	private double initialise(int N, double density, double sigma,boolean random, boolean twoD) {
			
		//density = particles / volume
		//double V = (double)N*Math.PI / (6*Math.pow(sigma, 3)*density);
		//double dimension = 0;
		
		//soft = true;
		if(random){
			soft = true;
			Random r = new Random();
			
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
				
				
				double delta = sigma;
				double spacing = dimension / ((double)N*sigma);
				
				//double delta = dimension / nPerD;
				//double spacing = dimension-(nPerD-1)*delta; //space left at the end of lattice
				
				
				
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
				return 0.;
			}
		}
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
			p.setVelocity(Vector3D.add(p.velocity(), forces[i].multiply(timeStep/(2*p.mass()) ) ) );
			
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
			
			//next half step velocity
			particles[i].setVelocity(Vector3D.add(particles[i].velocity(), forces[i].multiply(timeStep/(2*particles[i].mass()) ) ) );
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
	 * Lennard-Jones and WCA forces
	 */
	
	private Vector3D LJForce(Vector3D r){
		double division = sigma/r.mag();
		double c = -24*epsilon*(2*Math.pow(division,13) - Math.pow(division, 7)) / (sigma*r.mag());
		return r.multiply(c);
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
	 * soft potential for random initialisation
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
