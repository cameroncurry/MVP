import java.awt.EventQueue;


public class Ising {

	public static void main(String[] args){
		
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				IsingMenu menu = new IsingMenu();
				IsingController controller = new IsingController();
				new MenuAndViewController<IsingSettings>(menu,controller);
			}
		});
		
	}
}
