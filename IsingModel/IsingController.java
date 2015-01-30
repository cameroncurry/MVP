import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;

public class IsingController implements Viewable<IsingSettings>, Runnable {

	
	private JButton backButton;
	
	private JPanel fullPanel;
	private IsingView view;
	private IsingModel ising;
	
	private Thread simulationThread;
	private boolean threadFlag; //if flag is true then stop thread
	
	
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
		threadFlag = true;
		simulationThread.start();
	}
	
	public void willShowMenu(){
		threadFlag = false;
	}
	
	
	//run ising simulation
	public void run() {
		
		//set initial view
		view.setAndRepaint(ising.getSpins());
		
		//loop simulation
		while(threadFlag){
			
			
			int[][] coords = ising.update();
			
			//only if the update method has returned update cells, repaint view
			if(coords != null){
				view.setAndRepaint(ising.getSpins());
			}
			
		}
		
		
	}
	
}


/*
public static void main(String[] args){
	/*
	try {
		Runtime.getRuntime().exec("echo hello");
	} catch (IOException e) {
		e.printStackTrace();
	}
	
}
*/
