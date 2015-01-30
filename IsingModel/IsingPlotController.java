
public class IsingPlotController implements Runnable {

	private IsingModelWithAverages ising;
	private double startT, endT;
	
	private int burnIn;
	private int plotPoints, averagePoints, updates;
	
	private double[] temp, magnetisation, susceptibility;
	
	
	public IsingPlotController(IsingModelWithAverages ising, double T1, double T2, int burnIn, 
			int plotPoints, int pointsToAverage, int updatesBetweenSweeps){
		
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
		this.averagePoints = pointsToAverage;
		this.updates = updatesBetweenSweeps;
		
		this.temp = new double[plotPoints];
		this.magnetisation = new double[plotPoints];
		this.susceptibility = new double[plotPoints];
	}
	
	public void run(){
		double tIncrement = (endT-startT) / (double)plotPoints;
		
		//burn in
		for(int i=0; i<burnIn; i++){
			ising.update();
		}
		
		for(int k=0; k<plotPoints+1; k++){
			ising.setTemperature(startT + k*tIncrement);
			ising.reset();
			
			for(int i=0; i<averagePoints; i++){
				for(int j=0;j<updates;j++){
					ising.update();
				}
				ising.updateSums();
			}
			System.out.printf("%.2f %f\n",ising.getTemperature(),ising.averageE());
		
			
			
		}
	
	}
	
	
	//main method... remove
	public static void main(String[] args){
		double startT = 0.5;
		double endT = 5;
		
		int burnIn = 10000;
		int plotPoints = 25;
		int averagePoints = 100;
		int updates = 20000;
		
		IsingModelWithAverages ising = new IsingModelWithAverages(30,30,1,1,endT,0.6,false);
		
		IsingPlotController s = new IsingPlotController(ising,startT,endT,burnIn,plotPoints,averagePoints,updates);
	
		s.run();
	}
	
}
