
public class SIRSPlotModel extends SIRSModel {

	private int sweeps;
	
	private double Isum;
	private double I2sum;
	
	public SIRSPlotModel(SIRSPlotSettings settings) {
		super(settings);
		sweeps = settings.sweeps;
	}
	
	public void updateSums(){//update this after every sweep
		double I = totalInfected();
		Isum += I;
		I2sum += I*I;
	}
	
	public void reset(){
		Isum = 0;
		I2sum = 0;
	}
	
	public double averageInfected(){
		return Isum / (double)(sweeps*width*height);
	}
	//TODO
	public double varianceInfected(){
		return (I2sum - Isum*Isum/(double)(sweeps))/(double)(sweeps*width*height);
	}
	
	public int totalInfected(){
		int n = 0;
		for(int i=0; i<width; i++){
			for(int j=0;j<height; j++){
				if(super.agents[i][j] == 0){
					n++;
				}
			}
		}
		
		return n;
	}
	
	public double normedTotalInfected(){
		double n = totalInfected();
		return n / (double)(width*height);
	}

}
