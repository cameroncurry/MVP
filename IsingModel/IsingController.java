import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class IsingController implements Viewable<IsingSettings>, Runnable {

	
	private JButton backButton;
	private JSpinner tSpinner; // spinner to set temperatre dynamically
	
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
		
		
		JPanel tPanel = new JPanel();
		tPanel.setBackground(Color.white);
		JLabel t = new JLabel("Temperature");
		tPanel.add(t);
		
		tSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0, 99, 0.1));
		tPanel.add(tSpinner,BorderLayout.EAST);
		
		tSpinner.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				ising.setTemperature((Double)tSpinner.getValue());
			}
		});
		
		
		header.add(tPanel,BorderLayout.EAST);
		
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
	
	
	public void setSettings(IsingSettings settings){
		ising = new IsingModel(settings);
		simulationThread = new Thread(this);
		threadFlag = true;
		simulationThread.start();
		tSpinner.setValue(settings.T);
	}
	
	//clicked back button, going back to menu - stop thread
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
