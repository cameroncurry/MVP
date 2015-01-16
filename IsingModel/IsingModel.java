import java.util.Random;

public class IsingModel {

	//true = +1spin
	//false = -1spin
	private boolean[][] spins;
	
	//keep dimensions of array here for quick access
	private int width;
	private int height;
	
	//random number generator
	private Random random;
	
	//simulation constants
	private double J;
	private double k;
	private double T;
	
	
	//construct simulation by setting variables and filling array with spins
	public IsingModel(int width, int height, double J, double k, double T, double initialUpProbability) throws IllegalArgumentException {
		if(initialUpProbability > 1. || initialUpProbability < 0.) throw new IllegalArgumentException("initial spin up probability must be between 0-1");
		
		spins = new boolean[width][height];
		random = new Random();
		this.J = J;
		this.k = k;
		this.T = T;
		this.width = width;
		this.height = height;
		
		
		//fill array randomly with initial spin up prob.
		for(int i=0; i<width; i++){
			for(int j=0; j<height; j++){
				spins[i][j] = random.nextDouble() < initialUpProbability ? true : false;
			}
		}
	}
	
	public boolean[][] get(){
		return spins;
	}
	
	//Glauber dynamics change in energy if spin[i][j] is flipped 
	private double glauberDeltaE(int i, int j){
		int sum = 0;
		
		if(spins[(i+1)%width][j]) sum++;
		else sum--;
		
		if(spins[(i-1+width)%width][j]) sum++;
		else sum--;
		
		if(spins[i][(j+1)%height]) sum++;
		else sum--;
		
		if(spins[i][(j-1+height)%height]) sum++;
		else sum--;
		
		return 2*J*sum;
	}
	
	//one iteration of the simulation
	public void updateGlauber(){
		//pick a random spin, consider delta E
		int randX = random.nextInt(width); //random between 0 and wdith-1
		int randY = random.nextInt(height);
		
		double deltaE = glauberDeltaE(randX,randY);
		
		//if delta E is less than 0, flip spin
		//otherwise randomly with prob. min(exp(deltaE / kT), 1)
		//can be accomplished in one step using Math.min(a,b)
		
		
		if(random.nextDouble() < Math.min( Math.exp(-1*deltaE/(k*T)) / (k*T) , 1.)){
			spins[randX][randY] = !spins[randX][randY];
		}
		
		
	}
	
	
	
	
	
	
	
	
	
}
