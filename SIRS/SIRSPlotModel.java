
public class SIRSPlotModel extends SIRSModel {

	private double Isum;
	private double I2sum;
	
	public SIRSPlotModel(SIRSPlotSettings settings) {
		super(settings);
	}
	
	public void updateSums(){//update this after every sweep
		double I = totalInfected();
		Isum += I;
		I2sum += I*I;
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
