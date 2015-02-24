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
		this.infected = new double[settings.points];
		
		new Thread(this).start();
	}
	
	public void run(){
		
		try {
			runSimulation();
			printToFile();
			
			Runtime r = Runtime.getRuntime();
			Process p = r.exec("python SirsPlot.py");
			p.waitFor();
			
			BufferedImage image = ImageIO.read(new File("sirsfig.png"));
			this.imagePanel.setImage(image);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void runSimulation(){
		//run to equilibrium
		for(int i=0; i<settings.burnIn && !threadFlag;i++){
			for(int j=0;j<settings.updatesPerSweep;j++){
				model.update();
			}
		}
		
		//collect data
		for(int i=0;i<settings.points;i++){
			for(int j=0;j<settings.updatesPerSweep;j++){
				model.update();
			}
			this.infected[i] = model.normedTotalInfected();
		}
			
	}
	
	private void printToFile() throws FileNotFoundException{
		PrintWriter p = new PrintWriter("sirs.txt");
		
		for(int i=0;i<infected.length;i++){
			p.printf("%d %.4f\n",i,infected[i]);
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
