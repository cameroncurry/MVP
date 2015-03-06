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


public class SIRSPlotController implements Runnable, Viewable<SIRSPlotSettings> {

	//panel with image view and back button at the top
	JPanel fullPanel;
	JButton backButton;
	
	ImagePanel imagePanel;
	SIRSPlotSettings settings;
	SIRSPlotModel model;
	
	private boolean threadFlag;
	private double[] infected; //normalised infected as a function of sweeps (time)
	private double[][] infectedContour; //surface of average infected as a function of p1, p3
	private double[][] varInfectedContour;
	
	
	public SIRSPlotController() {
		fullPanel = new JPanel();
		fullPanel.setLayout(new BorderLayout());
		
		JPanel header = new JPanel();
		header.setBackground(Color.white);
		header.setLayout(new BorderLayout());
		
		backButton = new JButton("Back");
		header.add(backButton,BorderLayout.WEST);
		
		fullPanel.add(header,BorderLayout.NORTH);
		
		imagePanel = new ImagePanel();
		fullPanel.add(imagePanel,BorderLayout.CENTER);
	}
	
	@Override
	public void setSettings(SIRSPlotSettings settings) {
		this.threadFlag = false;
		model = new SIRSPlotModel(settings);
		imagePanel.startAnimation();
		this.settings = settings;
		if(settings.plotTime){
			this.infected = new double[settings.points];
		}
		else {
			this.infectedContour = new double[settings.points][settings.points];
			this.varInfectedContour = new double[settings.points][settings.points];
		}
		
		new Thread(this).start();
	}
	
	public void run(){
		
		try {
			if(settings.plotTime){
				runTime();
			}
			else {
				runContour();
			}
			if(!threadFlag){
				printToFile();
			
				Runtime r = Runtime.getRuntime();
				Process p = r.exec(String.format("python SirsPlot.py %s", settings.plotContour));
				p.waitFor();
			
				BufferedImage image = ImageIO.read(new File("sirsfig.png"));
				this.imagePanel.setImage(image);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void runTime(){
		//run to equilibrium
		for(int i=0; i<settings.burnIn;i++){
			for(int j=0;j<settings.updatesPerSweep && !threadFlag;j++){
				model.update();
			}
		}
		
		//collect data
		for(int i=0;i<settings.points;i++){
			for(int j=0;j<settings.updatesPerSweep && !threadFlag;j++){
				model.update();
			}
			this.infected[i] = model.normedTotalInfected();
		}
			
	}
	
	private void runContour(){
		double probincrement = 1. / (double)settings.points;
		
		for(int i=0; i<settings.points && !threadFlag; i++){ //points along x
			for(int j=0; j<settings.points && !threadFlag; j++){ // points along y
				//set probabilities
				model.setProbabilities(i*probincrement, settings.p2, j*probincrement);
				
				//burn in for every simulation
				for(int k=0; k<settings.burnIn; k++){
					for(int l=0;l<settings.updatesPerSweep && !threadFlag; l++){
						model.update();
					}
				}
				
				
				//run 1 simulation for this points then reset for next point
				for(int k=0; k<settings.sweeps; k++){
					for(int l=0; l<settings.updatesPerSweep && !threadFlag; l++){
						model.update();
					}
					model.updateSums();
				}
				infectedContour[i][j] = model.averageInfected();
				varInfectedContour[i][j] = model.varianceInfected();
				
				model = new SIRSPlotModel(settings); // Initialize new model for every point
				//set probabilities
			}
		}
		
	}
	
	private void printToFile() throws FileNotFoundException {
		PrintWriter p = new PrintWriter("sirs.txt");
		if(settings.plotTime){
			for(int i=0;i<infected.length;i++){
				p.printf("%d %.4f\n",i,infected[i]);
			}
		}
		else {
			PrintWriter p2 = new PrintWriter("sirs2.txt");
			for(int i=0; i<settings.points; i++){
				for(int j=0; j<settings.points; j++){
					p.printf("%.4f ", infectedContour[i][j]);
					p2.printf("%.4f ", varInfectedContour[i][j]);
				}
				p.println();
				p2.println();
			}
			p2.close();
		}
		p.close();
	}
	

	@Override
	public JPanel getView() {
		return fullPanel;
	}

	@Override
	public JComponent backClickComponent() {
		return backButton;
	}

	

	@Override
	public void willShowMenu() {
		imagePanel.stopAnimation();
		imagePanel.setImage(null);
		threadFlag = true;
	}

}
