
public class Ising {

	public static void main(String[] args){
		
		IsingMenu menu = new IsingMenu();
		IsingController controller = new IsingController();
		
		new MenuAndViewController<IsingSettings>(menu,controller);
	}
}
