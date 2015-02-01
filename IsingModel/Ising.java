import java.awt.EventQueue;
import java.io.IOException;


public class Ising {

	public static void main(String[] args){
		
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				IsingMenu menu = new IsingMenu();
				IsingController controller = new IsingController();
				new MenuAndViewController<IsingSettings>(menu,controller);
			}
		});
		
		
		/*
		try {
			Runtime r = Runtime.getRuntime();
			Process p = r.exec("python Plot.py");
			p.waitFor();
			System.out.println("hello");
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		*/
	}
}
