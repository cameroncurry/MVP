import javax.swing.JFrame;


public class MDController {

	public static void main(String[] args) throws InterruptedException{
		
		JFrame f = new JFrame();
		f.setSize(700, 600);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		
		MD2DView view = new MD2DView();
		f.getContentPane().add(view);
		
		
		
		int N = 32; 
		double timeStep = 0.0001;
		double sigma = 1;
		double epsilon = 1; 
		double density = 0.3;
		boolean LJ = true;
		boolean random = false;
		boolean twoD = true;
		boolean periodic = true;
		
		MDModel model = new MDModel(N,timeStep,sigma,epsilon,density,LJ,random,twoD,periodic);
		view.set(model.getParticles(), model.getBoxDimension());
		
		
		f.setVisible(true);
		
		
		
		for(int i=0; true; i++){
			if(random && i == 500){
				model.setSoft(false);
				System.out.println("soft false");
			}
			model.velocityVerlet();
			//if(i%1000 == 0){
				//Thread.sleep(1);
			//}
			
			view.repaint();
		}
		
		
	}

}
