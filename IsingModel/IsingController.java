import java.awt.Dimension;

import javax.swing.*;

public class IsingController extends SwingWorker<Void,Void>{

	public static void main(String[] args){
		IsingController c = new IsingController();
		c.start();
		/*
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
		ising = new IsingModel(25,25,1,1,0.1,0.75);
		
	
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
		view.set(ising.get());
		
		//loop simulation
		for(int i=0; true; i++){
			
			//wait for a bit
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ising.updateGlauber();
			view.set(ising.get());
		}
	}

	/*
	 * Swing worker methods
	 */
	protected Void doInBackground() {
		//run ising simulation
		
		//set initial view
		view.set(ising.get());
		
		//loop simulation
		for(int i=0; true; i++){
			System.out.print(i);
			//wait for a bit
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ising.updateGlauber();
			view.set(ising.get());
		}
		
		
		
		//return null;
	}
	
	public void done(){
		//plot graphs here
	}

	
}
