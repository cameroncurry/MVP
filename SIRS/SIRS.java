import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;


public class SIRS {
	
	
	public static void main(String[] args){
		
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				SIRSMenu menu = new SIRSMenu();
				SIRSController controller = new SIRSController();
				
				JFrame f = initFrame();
				f.getContentPane().add(new MenuAndViewController<SIRSSettings>(menu,controller));
				f.setVisible(true);
			}
		});
		
	}
	
	static JFrame initFrame(){
		JFrame frame = new JFrame();
		frame.setSize(800,600);
		frame.setMinimumSize(new Dimension(600,400));
		frame.setLocationRelativeTo(null);//frame centre of screen
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;
	}	

	/*
	public static void main(String[] args){
		
		//new SIRSController();
		
		EventQueue
		
		
		JFrame f = new JFrame();
		f.setSize(500, 500);
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//SIRSView view = new SIRSView();
		//SIRSModel model = new SIRSModel(100,100,)
		
		f.getContentPane().add(new SIRSMenu());
		f.setVisible(true);
		
	}
	*/
}
