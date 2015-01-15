import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.*;

public class IsingController {

	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				new IsingController();
			}
		});
	}
	
	private JFrame frame;
	private IsingView view;
	private IsingModel ising;
	
	public IsingController(){
		initGUI();
		
		view = new IsingView();
		ising = new IsingModel(20,20,0.5);
		
		view.set(ising.get());
		
		frame.getContentPane().add(view);
		frame.setVisible(true);
	}
	
	private void initGUI(){
		frame = new JFrame();
		frame.setSize(700, 500);
		frame.setMinimumSize(new Dimension(700,400));
		frame.setLocationRelativeTo(null);//frame centre of screen
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
