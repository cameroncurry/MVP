import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;



public class SIRSController implements Runnable, Viewable<SIRSSettings>  {
	
	private JPanel fullPanel;
	private JButton backButton;
	
	private SIRSView view;
	private SIRSModel model;
	
	private Thread t;
	private boolean threadFlag;
	
	public SIRSController(){
		fullPanel = new JPanel();
		fullPanel.setBackground(Color.white);
		fullPanel.setLayout(new BorderLayout());
		
		JPanel header = new JPanel();
		header.setLayout(new BorderLayout());
		header.setBackground(Color.white);
		
		backButton = new JButton("Back");
		header.add(backButton,BorderLayout.WEST);
		
		fullPanel.add(header,BorderLayout.NORTH);
		
		this.view = new SIRSView();
		fullPanel.add(view);
		
		this.threadFlag = false;
	}
	
	
	
	@Override
	public void setSettings(SIRSSettings settings) {
		this.model = new SIRSModel(settings);
		this.view.set(model.getAgents());
		
		threadFlag = false;
		t = new Thread(this);
		t.start();
	}
	
	public void run(){
		int counter = 0;
		while(!threadFlag){
			
			int[] coords = model.update();
			
			if(counter%1000 == 0){
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					//do nothing, I want an interrupt
				}
			}
			if(coords != null){
				view.paintCoords(coords);
			}
			counter ++;
			
		}
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
		threadFlag = true;
	}

}
