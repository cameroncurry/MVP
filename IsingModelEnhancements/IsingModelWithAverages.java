import java.util.ArrayList;

public class IsingModelWithAverages extends IsingModel {

	private int sweeps; //number of times averages have been updated
	
	private double MsumAbs;
	
	private double Msum;
	private double M2sum; //M squared
	
	private double Esum;
	private double E2sum;
	
	//arrays for error calculation
	private ArrayList<Double> m;
	private ArrayList<Double> e;
	
	
	
	public IsingModelWithAverages(IsingSettings settings) {
		super(settings);
		//set variables to 0
		this.reset();
	}

	
	public void reset(){
		this.sweeps = 0;
		this.MsumAbs = 0;
		this.Msum = 0;
		this.M2sum = 0;
		this.Esum = 0;
		this.E2sum = 0;
		this.m = new ArrayList<Double>();
		this.e = new ArrayList<Double>();
	}

	public void updateSums(){
		double M = 0;
		double E = 0;
		
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
		MsumAbs += Math.abs(M);
		Esum += E;
		E2sum += E*E;
		
		m.add(M);
		e.add(E);
		sweeps ++;
		
	}
	
	
	//average of absolute value of M
	public double averageAbsM(){
		return MsumAbs / (double)(sweeps*width*height);
	}
	/*
	public double errorAbsM(){
		double averageAbsM = averageAbsM();
		double mSquaredAverage = M2sum / (double)(sweeps*sweeps*width*height);
		return Math.sqrt((mSquaredAverage-(averageAbsM*averageAbsM)) / (double)(sweeps-1));
	}
	*/
	
	public double susceptibility(){
		double averageM = Msum / (double)sweeps;
		double MSquaredAverage = M2sum / (double)(sweeps);
		
		return (MSquaredAverage - averageM*averageM) / (k*T*width*height);	
	}
	
	//using bootstrap method
	public double errorSusceptibility(){
		int measurements = 100;
		double chiSum = 0;
		double chi2Sum = 0;
		
		for(int j=0; j<measurements; j++){
			//find average and average squared susceptibility from random measurements of M
			double mSum = 0;
			double m2Sum = 0;
			for(int i=0;i<sweeps;i++){
				double q = m.get(super.random.nextInt(sweeps));
				mSum += q;
				m2Sum += q*q;
			}
			
			double averageM = mSum / (double)sweeps;
			double averageM2 = m2Sum / (double)(sweeps);
			
		
			double chi = (averageM2 - averageM*averageM) / (k*T*width*height);
			
			chiSum += chi;
			chi2Sum += chi*chi;
		}
		
		double averageChi = chiSum / (double)measurements;
		double averageChi2 = chi2Sum / (double)(measurements);
		
		return Math.sqrt(averageChi2 - averageChi*averageChi);
	}
	
	
	
	public double averageE(){
		return Esum / (double)(sweeps*width*height);
	}
	
	
	public double heatCapacity(){
		double averageE = Esum / (double)sweeps;
		double EsquaredAverage = E2sum / (double)(sweeps);
		
		return (EsquaredAverage - averageE*averageE) / (k*T*T*width*height);
	}
	public double errorHearCapacity(){
		int measurements = 100;
		double cSum = 0;
		double c2Sum = 0;
		
		for(int j=0;j<measurements;j++){
			double eSum = 0;
			double e2Sum = 0;
			for(int i=0;i<sweeps;i++){
				double q = e.get(super.random.nextInt(sweeps));
				eSum += q;
				e2Sum += q*q;
			}
			
			double averageE = eSum / (double)sweeps;
			double averageE2 = e2Sum / (double)sweeps;
			
			double c = (averageE2 - averageE*averageE) / (k*T*T*width*height);
			cSum += c;
			c2Sum += c*c;
		}
		
		double averageC = cSum / (double)measurements;
		double averageC2 = c2Sum / (double)measurements;
		
		return Math.sqrt(averageC2 - averageC*averageC);
	}

}
