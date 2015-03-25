import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;



public class MolecularDynamics {

	public static void main(String[] args){
		
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				JFrame f = initFrame();
				
				f.getContentPane().add(new MDMenu());
				
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
}
