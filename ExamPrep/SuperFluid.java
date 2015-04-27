import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import javax.swing.JFrame;


public class SuperFluid implements GridModel<Integer>{
	
	public static void main(String[] args){
		JFrame f = initFrame();
		
		final GridModel<Integer> model = new SuperFluid(50,.5, 1);
		final GridView<Integer> view = new SuperFluidView(model.getAgents());
		
		f.getContentPane().add(view);
		f.setVisible(true);
		
		
		new Thread(new Runnable(){
			public void run(){
				while(true){
					int[] coords = model.update();
					if(coords != null){
						view.repaintAgent(coords[0], coords[1]);
						if(coords.length == 4){
							view.repaintAgent(coords[2], coords[3]);
						}
					}
					
				}
			}
		}).start();
	}
	
	static JFrame initFrame(){
		JFrame f = new JFrame();
		f.setSize(500,500);
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		return f;
	}

	
	private Integer[][] agents;
	private Random r;
	
	private double T;
	private double J = 1, kb = 1;
	
	public SuperFluid(int L, double c, double T) {
		if(c < 0 || c >1){
			throw new IllegalArgumentException("probability c must be between 0-1");
		}
		else {
			this.agents = new Integer[L][L];
			this.r = new Random();
			for(int i=0;i<L;i++){
				for(int j=0;j<L;j++){
					//He4
					if(c < r.nextDouble()){
						agents[i][j] = r.nextBoolean() ? 1 : -1;
					}
					else {
						agents[i][j] = 0;
					}
				}
			}
		}
		
	}

	@Override
	public Integer[][] getAgents() {
		return agents;
	}

	@Override
	public int[] update() {
		return r.nextBoolean() ? randomUpdate() : swapUpdate();
	}
	
	private int[] swapUpdate(){
		int x1 = r.nextInt(agents.length);
		int y1 = r.nextInt(agents.length);
		
		int x2;
		int y2;
		
		do{
			x2 = r.nextInt(agents.length);
			y2 = r.nextInt(agents.length);
			
		}while(x2 == x1-1 || x2 == x1+1 || y2 == y1+1 || y2 == y1-1);
		
		
		double E = energy(x1,y1) + energy(x2,y2);
		
		if(r.nextDouble() < Math.exp(-1*E / (kb*T))){
			//swap spins
			int temp = agents[x1][y1];
			agents[x1][y1] = agents[x2][y2];
			agents[x2][y2] = temp;
			return new int[] {x1,y1,x2,y2};
		}
		else {
			//System.out.println("no update");
			return null;
		}
	}
	
	private int[] randomUpdate(){
		int x = r.nextInt(agents.length);
		int y = r.nextInt(agents.length);
		
		if(r.nextDouble() < Math.exp(-1*energy(x,y)/(kb*T))){
			agents[x][y] *= -1; //flip sign
			return new int[] {x,y};
		}
		else{
			//System.out.println("no update");
			return null;
		}
		
	}
	
	private double energy(int i, int j){
		int L = agents.length;
		
		int sum = 0;
		sum += agents[(i+1)%L][j];
		sum += agents[(i-1+L)%L][j];
		sum += agents[i][(j+1)%L];
		sum += agents[i][(j-1+L)%L];
		
		return -J*agents[i][j]*sum;
	}

}

class SuperFluidView extends GridView<Integer> {

	
	private static final long serialVersionUID = 1L;
	
	public SuperFluidView(Integer[][] agents){
		super(agents);
	}

	
	void drawBox(Integer agent, Graphics g, int x, int y, int width, int height) {
		if(agent == 0){
			g.setColor(Color.white);
		}
		else{
			g.setColor(Color.blue);
		}
		g.fillRect(x, y, width, height);
	}
	
}
