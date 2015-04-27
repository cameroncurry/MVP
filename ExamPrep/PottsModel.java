import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class PottsModel implements GridModel<Integer> {
	
	//main method to run the Potts Model
	public static void main(String[] args){
		
		int L = 50;
		double T = 0.5;
		
		JFrame f = new JFrame();
		f.setSize(500,500);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		
		
		final PottsModel potts = new PottsModel(L,T);

		PottsGrid view = new PottsGrid();
		view.set(potts.getAgents());
		
		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.setBackground(Color.white);
		JButton plotButton = new JButton("Plot");
		controlPanel.add(plotButton,BorderLayout.WEST);
		
		plotButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				potts.plot();
			}
		});
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(controlPanel,BorderLayout.NORTH);
		contentPanel.add(view);
		
		f.getContentPane().add(contentPanel);
		f.setVisible(true);
		
		for(int i=0; true; i++){
			int [] coords = potts.update();
			if(coords != null)view.repaintAgent(coords[0], coords[1]);
			
			if(i % L*L == 0){
				potts.updateFractions();
			}
		}
		
	}

	private Integer[][] agents;
	private Random r;
	private double J,kb,T;
	private int L;
	
	//fraction of cells in each state
	private Observable fraction;
	
	public PottsModel(int L,double temp){
		this.agents = new Integer[L][L];
		this.J = 1;
		this.kb = 1;
		this.T = temp;
		this.L = L;
		this.r = new Random();
		initialiseAgents();
		
		this.fraction = new Observable("Fraction-of-States", "time", "fraction", "0", "1", "2");
	}
	
	private void initialiseAgents(){
		
		for(int i=0;i<agents.length;i++){
			for(int j=0;j<agents[i].length;j++){
				//uniformly randomly of either 0,1,2
				agents[i][j] = r.nextInt(3);
			}
		}
		
	}
	
	public Integer[][] getAgents(){return agents;}
	
	//pick one site at random to update
	public int[] update(){
		int x = r.nextInt(agents.length);
		int y = r.nextInt(agents.length);
		
		int agent = agents[x][y];
		double energy = energyIJ(x,y);
		double prob = Math.exp(energy / (kb*T));
		
		if(r.nextDouble() < prob){
			agents[x][y] = agent == 2 ? 0 : agent +1;  
			return new int[] {x,y};
		}
		else{
			return null;
		}
		
	}
	
	private double energyIJ(int i, int j){
		int e = 0;
		
		//periodic boundary
		int agent = agents[i][j];
		if(agents[(i+1)%L][j] == agent) e++;
		if(agents[(i-1+L)%L][j] == agent) e++;
		if(agents[i][(j+1)%L] == agent)e++;
		if(agents[i][(j-1+L)%L] == agent)e++;
		
		return -J*e;
	}
	
	
	public void plot(){
		fraction.plot();
	}
	private int index = 0;
	public void updateFractions(){
		double tot = L*L;
		
		double sum0 = 0;
		double sum1 = 0;
		
		for(int i=0;i<L;i++){
			for(int j=0;j<L;j++){
				if(agents[i][j] == 0){
					sum0 ++;
				}
				else if(agents[i][j] == 1){
					sum1 ++;
				}
			}
		}
		
		fraction.add(0, index); index++;
		fraction.add(1, sum0 / tot);
		fraction.add(2, sum1 / tot);
		fraction.add(3,1 -  ((sum0+sum1)/tot));
	}
	
	

}

class PottsGrid extends GridView<Integer>{

	private static final long serialVersionUID = 1L;

	void drawBox(Integer agent, Graphics g, int x, int y, int width, int height) {
		if(agent == 0){
			g.setColor(Color.blue);
		}
		else if(agent == 1){
			g.setColor(Color.green);
		}
		else{
			g.setColor(Color.red);
		}
		g.fillRect(x, y, width, height);
	}
	
}
