import java.util.Random;

public class IsingModel {

	private boolean[][] spins;
	private Random random;
	
	public IsingModel(int width, int height, double upProbability) throws IllegalArgumentException {
		if(upProbability > 1. || upProbability < 0.) throw new IllegalArgumentException("initial up spin probability must be between 0-1");
		
		spins = new boolean[width][height];
		random = new Random();
		
		for(int i=0; i<width; i++){
			for(int j=0; j<height; j++){
				double rand = random.nextDouble();
				//set each element of spin array
				spins[i][j] = rand < upProbability ? true : false;
			}
		}
	}
	
	public boolean[][] get(){
		return spins;
	}
}
