
public class IsingModelWithAverages extends IsingModel {

	private int sweeps; //number of times averages have been updated
	
	private double Msum;
	private double M2sum; //M squared
	
	private double Esum;
	private double E2sum;
	
	
	public IsingModelWithAverages(IsingSettings settings) {
		super(settings);
		//set variables to 0
		this.reset();
	}

	public IsingModelWithAverages(int width, int height, double J, double k,double T, double initialUpProbability, boolean glauber)
			throws IllegalArgumentException {
		super(width, height, J, k, T, initialUpProbability, glauber);
		
		this.reset();
	}
	
	public void reset(){
		this.sweeps = 0;
		this.Msum = 0;
		this.M2sum = 0;
		this.Esum = 0;
		this.E2sum = 0;
	}

	public void updateSums(){
		int M = 0;
		int E = 0;
		
		for(int i=0; i<width; i++){
			for(int j=0; j<height; j++){
				boolean spin = spins[i][j];
				
				//magnetisation
				if(spin) M++;
				else M--;
				
				//energy count right and bottom neighbours
				//bottom
				if(spin == spins[i][(j+1)%height]) E++;
				else E--;
				//right
				if(spin == spins[(i+1)%width][j]) E++;
				else E--;
			}
		}
		
		Msum += M;
		M2sum += M*M;
		Esum += E;
		E2sum += E*E;
		sweeps ++;
		
	}
	
	
	
	public double absMagnetisation(){
		return Math.abs(Msum) / (double)(sweeps*width*height);
	}
	public double susceptibility(){
		double averageMagSquared = Math.pow(Msum / (double)sweeps,2);
		double magSquaredAverage = M2sum / (double)sweeps;
		
		return (magSquaredAverage - averageMagSquared) / (k*T*width*height);	
	}
	
	public double averageE(){
		//is energy normalised to size of array?
		return Esum / (double)(sweeps*width*height);
	}
	public double heatCapacity(){
		double averageEsquared = Math.pow(averageE(),2);
		double EsquaredAverage = E2sum / (double)sweeps;
		
		return (EsquaredAverage - averageEsquared) / (k*T*width*height);
	}

}
