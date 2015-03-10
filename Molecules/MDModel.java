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
	
	//use either Lennard-Jones or WCA potential
	private boolean LJ;
	
	public MDModel(int N, double timeStep, double sigma, double epsilon, double density, boolean random, boolean twoD) {
		particles = new Particle3D[N];
		//initialise particle's position and velocity and create dimensions of box
		dimension = initialise(N,density,random,twoD); 
		this.timeStep = timeStep;
		this.sigma = sigma;
		this.epsilon = epsilon;
		this.criticalR = sigma*Math.pow(2, 1./6.);
	}
	
	//returns dimension of box in a Vector3D
	private double initialise(int N, double density, boolean random, boolean twoD) {
		//density = particles / volume
		double V = (double)N / density;
		double dimension = 0;
		
		if(twoD){
			dimension = Math.sqrt(V);
			int particlesPerDimension = (int)Math.sqrt(N) + 1;
			
			for(int i=0; i<particles.length; i++){
				//particles[i].setPosition(new Vecto);
			}
		}
		else{
			dimension = Math.cbrt(V);
		}
		
		return dimension;
	}

	//updates each particles position and velocity using the velocity verlet algorithm
	public void velocityVerlet(){
		for(int i=0; i<particles.length; i++){
			Particle3D p = particles[i]; //pointer to particle being updated
			
			//find force from all other particles
			Vector3D force = totalForce(i);
			
			//half step velocity
			p.setVelocity(Vector3D.add(p.velocity(), force.multiply(timeStep/(2*p.mass()) ) ) );
			//full step position
			p.setPosition(Vector3D.add(p.position(), p.velocity().multiply(timeStep)));
			//compute force again
			Vector3D force2 = totalForce(i);
			//next half step velocity
			p.setVelocity(Vector3D.add(p.velocity(), force2.multiply(timeStep/(2*p.mass()) ) ) );
		}
	}
	
	//the total force acting on particle of index i
	private Vector3D totalForce(int i){
		Vector3D force = new Vector3D();
		for(int j=0; j<particles.length; j++){
			if(i != j){
				if(LJ) force.add(LJForce(particles[i],particles[j]));
				else force.add(WCAForce(particles[i],particles[j]));
			}
		}
		return force;
	}
	
	//Lennard-Jones force exerted on particle a from particle b
	private Vector3D LJForce(Particle3D a, Particle3D b){
		Vector3D r = Particle3D.separation(b, a);
		double division = sigma/r.mag();
		double c = -24*epsilon*(2*Math.pow(division,13) - Math.pow(division, 7)) / (sigma*r.mag());
		return r.multiply(c);
	}
	
	private Vector3D WCAForce(Particle3D a, Particle3D b){
		if(Particle3D.separation(b, a).mag() < criticalR){
			return LJForce(a,b);
		}
		else{
			return new Vector3D(); //return 0
		}
	}

}
