

public class IsingPlotController implements Runnable {

	private IsingModelWithAverages ising;
	private double startT, endT;
	
	private int burnIn;
	private int plotPoints, sweeps, updatesPerSweep;
	
	private double[] temp, magnetisation, susceptibility;
	
	//burn in is the number of sweeps to discard at start of simulation
	//sweeps is the number of sweeps that will occur for every plot point
	//1 sweep wil update the model width*height times
	public IsingPlotController(IsingModelWithAverages ising, double T1, double T2, int plotPoints, int burnIn, 
			 int sweeps,int updatesPerSweep){
		
		//start with highest temperature and work down
		if(T1 > T2){
			startT = T1;
			endT = T2;
		}
		else{
			startT = T2;
			endT = T1;
		}
		
		this.ising = ising;
		this.burnIn = burnIn;
		this.plotPoints = plotPoints;
		this.sweeps = sweeps;
		this.updatesPerSweep = updatesPerSweep;
		
		this.temp = new double[plotPoints];
		this.magnetisation = new double[plotPoints];
		this.susceptibility = new double[plotPoints];
	}
	
	public void run(){
		double tIncrement = (endT-startT) / (double)plotPoints;
		
		//burn in
		for(int i=0; i<burnIn; i++){
			for(int j=0;j<updatesPerSweep;j++){
				ising.update();
			}
		}
		
		for(int k=0; k<plotPoints+1; k++){
			ising.setTemperature(startT + k*tIncrement);
			ising.reset();
			
			for(int i=0; i<sweeps; i++){
				for(int j=0;j<updatesPerSweep;j++){
					ising.update();
				}
				ising.updateSums();
			}
			System.out.printf("%.2f %f %f\n",ising.getTemperature(),ising.averageAbsM(),ising.errorAbsM());
		
			
			
		}
	
	}
	
	
	//main method... remove
	public static void main(String[] args){
		double startT = 0.5;
		double endT = 5;
		
		int burnIn = 100;
		int plotPoints = 40;
		int sweeps = 500;
		int updatesPerSweep = 5000;
		
		
		IsingModelWithAverages ising = new IsingModelWithAverages(30,30,1,1,endT,0.6,true);
		
		IsingPlotController s = new IsingPlotController(ising,startT,endT,plotPoints,burnIn,sweeps,updatesPerSweep);
	
		s.run();
	}
	
}
