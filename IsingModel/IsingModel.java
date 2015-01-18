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
	
	//type of dynamics
	//true = glauber
	//false = kawasaki
	private boolean glauber;
	
	
	//construct simulation by setting variables and filling array with spins
	public IsingModel(int width, int height, double J, double k, double T, double initialUpProbability, boolean glauber) throws IllegalArgumentException {
		if(initialUpProbability > 1. || initialUpProbability < 0.) throw new IllegalArgumentException("initial spin up probability must be between 0-1");
		
		spins = new boolean[width][height];
		random = new Random();
		this.J = J;
		this.k = k;
		this.T = T;
		this.width = width;
		this.height = height;
		this.glauber = glauber;
		
		
		//fill array randomly with initial spin up prob.
		for(int i=0; i<width; i++){
			for(int j=0; j<height; j++){
				spins[i][j] = random.nextDouble() < initialUpProbability ? true : false;
			}
		}
	}
	
	public boolean[][] getSpins(){
		return spins;
	}
	
	public void update(){
		if(glauber)updateGlauber();
		else updateKawasaki();
	}
	
	
	//one iteration of the simulation
	private void updateGlauber(){
		//pick a random spin, consider delta E
		int randX = random.nextInt(width); //random between 0 and width-1
		int randY = random.nextInt(height);
			
		double deltaE = glauberDeltaE(randX,randY);
		
		
		//if delta E is less than 0, flip spin
		//otherwise randomly with prob. exp(deltaE / kT)
		
		if(deltaE <= 0 || random.nextDouble() < Math.exp(-1*deltaE/(k*T))){
			spins[randX][randY] = !spins[randX][randY];
		}
		
	}
	
	private void updateKawasaki(){
		int randX1 = random.nextInt(width);
		int randY1 = random.nextInt(height);
		
		int randX2;
		int randY2;
		
		//random points must be different
		do{
			randX2 = random.nextInt(width);
			randY2 = random.nextInt(height);
		}while(randX1 == randX2 && randY1 == randY2);
		
		double deltaE = kawasakiDeltaE(randX1,randY1,randX2,randY2);
		
		//exhange spins
		if(deltaE <= 0 || random.nextDouble() < Math.exp(-1*deltaE/(k*T))){
			boolean tmp = spins[randX1][randY1];
			spins[randX1][randY1] = spins[randX2][randY2];
			spins[randX2][randY2] = tmp;
		}
		
	}	
	
	//Glauber dynamics change in energy considering spin[i][j] 
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
			
		if(spins[i][j]) return 2*J*sum;
		else return -2*J*sum;
	}
	
	
	//considers spins[i][j] and spins[k][l]
	private double kawasakiDeltaE(int i, int j, int k,int l){
		//if spins are the same, exchange doesn't do anything so 0 delta E
		if(spins[i][j] == spins[k][l]){
			return 0.;
		}
		else{
			double flip1 = glauberDeltaE(i,j);
			//flip spin[i][j] so if points are neighbours there is no spin double count
			spins[i][j] = !spins[i][j];
			double flip2 = glauberDeltaE(k,l);
			//flip first spin back
			spins[i][j] = !spins[i][j];
			
			//return sum of delta E
			return flip1 + flip2;
		}
		
		 
	}
	
	
}
