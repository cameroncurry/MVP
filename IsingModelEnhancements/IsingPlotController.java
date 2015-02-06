import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;



public class IsingPlotController implements Runnable, Viewable<IsingSettingsWithAverages> {

	private JPanel fullPanel;
	private IsingImageView view;
	private IsingModelWithAverages ising;
	private JButton backButton;
	
	private Thread simulationThread;
	
	private double startT, endT;
	
	private int burnIn;
	private int plotPoints, sweeps, updatesPerSweep;
	
	
	//2d arrays: {{value,error} , {value,error}, ...}
	private double[] temp;
	private double[] absM, E;
	private double[][] susceptibility, heatCapacity;
	private boolean plotE; // plot energy and heat capacity
	private boolean plotM; // plot abs M and susceptibility
	
	
	public IsingPlotController(){
		fullPanel = new JPanel();
		fullPanel.setLayout(new BorderLayout());
		
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BorderLayout());
		headerPanel.setBackground(Color.white);
		backButton = new JButton("Back");
		headerPanel.add(backButton,BorderLayout.WEST);
		
		this.view = new IsingImageView();
		
		fullPanel.add(headerPanel,BorderLayout.NORTH);
		fullPanel.add(view);
	}
	
	
	//burn in is the number of sweeps to discard at start of simulation
	//sweeps is the number of sweeps that will occur for every plot point
	//1 sweep wil update the model width*height times
	public void setSettings(IsingSettingsWithAverages settings){
		
		this.ising = new IsingModelWithAverages(settings);
		
		//start with highest temperature and work down
		if(settings.T1 > settings.T2){
			startT = settings.T1;
			endT = settings.T2;
		}
		else{
			startT = settings.T2;
			endT = settings.T1;
		}
		
		this.burnIn = settings.burnIn;
		this.plotPoints = settings.plotPoints;
		this.sweeps = settings.sweeps;
		this.updatesPerSweep = settings.updatesPerSweep;
		
		this.temp = new double[settings.plotPoints+1];
		
		
		//if both check boxes happen to be unticked, plot all four functions
		if(!settings.plotEandC && !settings.plotMandX){
			this.plotE = true;
			this.plotM = true;
		}
		else{
			this.plotE = settings.plotEandC;
			this.plotM = settings.plotMandX;
		}
		
		if(this.plotE){	
			this.E = new double[settings.plotPoints+1];
			this.heatCapacity = new double[settings.plotPoints+1][2];
		}
		
		if(this.plotM){
			this.absM = new double[settings.plotPoints+1];
			this.susceptibility = new double[settings.plotPoints+1][2];
		}
		
		
		view.startAnimation();
		simulationThread = new Thread(this);
		simulationThread.start();
	}
	
	public void run(){
		fillArrays();
		try {
			if(!simulationThread.isInterrupted()){
				printArraysToFile();
				Runtime r = Runtime.getRuntime();
				Process p = r.exec(String.format("python IsingPlot.py %s",this.plotM));
				p.waitFor();
			
				BufferedImage image = ImageIO.read(new File("img.png"));
				this.view.setImage(image);
			}
		}
		catch(InterruptedException e){
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			this.temp = null;
			this.absM = null;
			this.susceptibility = null;
			this.E = null;
			this.heatCapacity = null;
		}
	}
	
	//run the simulation and fill arrays
	private void fillArrays(){
		double tIncrement = (endT-startT) / (double)plotPoints;
		
		//burn in
		for(int i=0; i<burnIn; i++){
			for(int j=0;j<updatesPerSweep;j++){
				ising.update();
			}
		}
		
		for(int k=0; k<plotPoints+1 && !simulationThread.isInterrupted(); k++){
			ising.reset();
			ising.setTemperature(startT + k*tIncrement);
			
			
			for(int i=0; i<sweeps; i++){
				for(int j=0;j<updatesPerSweep;j++){
					ising.update();
				}
				ising.updateSums();
			}
			
			//add values to arrays
			temp[k] = ising.getTemperature();
			
			
			if(this.plotM){
				absM[k] = ising.averageAbsM();
				susceptibility[k][0] = ising.susceptibility();
				susceptibility[k][1] = ising.errorSusceptibility();
			}
			if(this.plotE){
				E[k] = ising.averageE();
				heatCapacity[k][0] = ising.heatCapacity();
				heatCapacity[k][1] = ising.errorHearCapacity();
			}
			
		}	
	}
	
	private void printArraysToFile() throws FileNotFoundException {
		PrintWriter w = new PrintWriter("data.txt");
		for(int i=0; i<plotPoints;i++){
			
			if(this.plotE && !this.plotM){ // print just energy
				w.printf("%.3f %f %f %f\n", temp[i],E[i],heatCapacity[i][0],heatCapacity[i][1]);
			}
			else if(!this.plotE && this.plotM){
				w.printf("%.3f %f %f %f\n", temp[i],absM[i],susceptibility[i][0],susceptibility[i][1]);
			}
			else {
				//T, absM, sus, errodSus, E, heat, errorHeat
				w.printf("%.3f %f %f %f %f %f %f\n", temp[i],absM[i],susceptibility[i][0],
						susceptibility[i][1],E[i],heatCapacity[i][0],heatCapacity[i][1]);
			}
			
		}
		w.close();
	}
	
	
	public JPanel getView() {
		return fullPanel;
	}

	public JComponent backClickComponent() {
		return backButton;
	}

	public void willShowMenu() {
		view.stopAnimation();
		view.setImage(null);
		
		simulationThread.interrupt();
	}
	
}
