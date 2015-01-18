import java.awt.Dimension;

import javax.swing.*;

public class IsingController extends SwingWorker<Void,Void>{

	public static void main(String[] args){
		
		
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run(){
					IsingController c = new IsingController();
					c.start();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		try {
			Runtime.getRuntime().exec("echo hello");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	private JFrame frame;
	private IsingView view;
	private IsingModel ising;
	
	
	public IsingController(){
		initGUI();
		
		view = new IsingView();
		//IsingModel(width,height,J,kb,T, initial spin up prob.)
		ising = new IsingModel(100,100,1,1,0.01,0.50,false);
		
	
		frame.getContentPane().add(view);
		frame.setVisible(true);
	}
	
	private void initGUI(){
		frame = new JFrame();
		frame.setSize(700,500);
		frame.setMinimumSize(new Dimension(600,400));
		frame.setLocationRelativeTo(null);//frame centre of screen
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void start(){
		this.execute();
	}

	/*
	 * Swing worker methods
	 */
	protected Void doInBackground() {
		//run ising simulation
		
		//set initial view
		view.set(ising.getSpins());
		
		//loop simulation
		for(int i=0; true; i++){
			
			ising.update();
			view.set(ising.getSpins());
		}
		
	}
	
	
}
