import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.*;

public class IsingController implements Viewable<IsingSettings>, Runnable {

	
	private static final long serialVersionUID = 1L;
	
	private JButton backButton;
	
	private JPanel fullPanel;
	private IsingView view;
	private IsingModel ising;
	
	private Thread simulationThread;
	
	
	public IsingController(){
		fullPanel = new JPanel();
		fullPanel.setLayout(new BorderLayout());
		
		JPanel header = new JPanel();
		header.setBackground(Color.white);
		header.setLayout(new BorderLayout());
		
		backButton = new JButton("Back");
		header.add(backButton,BorderLayout.WEST);
		
		fullPanel.add(header,BorderLayout.NORTH);
		
		view = new IsingView();
		fullPanel.add(view);
		
	}
	
	/*
	 * viewable methods
	 */
	
	public JPanel getView() {
		return fullPanel;
	}

	public JComponent backClickComponent() {
		return backButton;
	}
	
	
	public void setSettings(IsingSettings settings) {
		ising = new IsingModel(settings);
		simulationThread = new Thread(this);
		simulationThread.start();
	}
	
	public void willShowMenu(){
		simulationThread.interrupt();
	}
	
	
	public void run() {
		//run ising simulation
		
		//set initial view
		view.setAndRepaint(ising.getSpins());
		
		//loop simulation
		for(int i=0; true; i++){
			
			ising.update();
			
			view.setAndRepaint(ising.getSpins());
			
		}
		
		//return null;
	}
	
}


/*
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
	
}
*/
