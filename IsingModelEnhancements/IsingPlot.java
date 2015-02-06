import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;


public class IsingPlot {

	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				IsingMenu menu = new IsingMenu();
				IsingPlotMenu nextMenu = new IsingPlotMenu();
			
				JFrame f = Ising.initFrame();
				f.getContentPane().add(new MenuAndViewController<IsingSettings>(menu,nextMenu));
				f.setVisible(true);
			}
		});
		
		try {
			EventQueue.invokeAndWait(new Runnable(){
				public void run(){}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
