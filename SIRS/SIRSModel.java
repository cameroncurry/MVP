import java.util.Random;

public class SIRSModel {

	//-1 for susceptible
	//0 for infected
	//1 for recovered
	//2 for permanently immune
	private int[][] agents;
	
	private int width,height; //keep dimensions for quick access
	
	private double p1; //prob S->I
	private double p2; //prob I->R
	private double p3; //prob R->S
	
	private Random random;
	
	public SIRSModel(SIRSSettings settings){
		this(settings.rows,settings.columns,settings.p1,settings.p2,settings.p3,
				settings.percentS,settings.percentI,settings.percentImmune);
	}
	
	public SIRSModel(int width,int height,double p1,double p2,double p3, 
			double percentS, double percentI, double percentImmune) {
		if(p1<0 || p1>1 || p2<0 || p2>1 || p3<0 || p3>1
				|| percentS<0 || percentS>1 || percentI<0 || percentI>1 || percentImmune<0 || percentImmune>1){
			throw new IllegalArgumentException("probabilities must be between 0 and 1");
		}
		agents = new int[width][height];
		this.width = width;
		this.height = height;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.random = new Random();
		initializeArray(percentS,percentI,percentImmune);
	}
	
	//args: susceptible, infected, immune
	private void initializeArray(double s, double i, double I){
		for(int j=0; j<width;j++){
			for(int k=0;k<height;k++){
				double r = random.nextDouble();
				
				if(r<I) agents[j][k] = 2;
				else if(r<(s+I) && r>I) agents[j][k] = -1;
				else if(r<(i+s+I) && r>(s+I)) agents[j][k] = 0;
				else if(r>(I+s+i)) agents[j][k] = 1;
			}
		}
	}
	
	public int[][] getAgents(){
		return agents;
	}
	
	
	//update a randomly selected site
	public int[] update(){
		int randX = random.nextInt(width);
		int randY = random.nextInt(height);
		
		int site = agents[randX][randY];
		
		//susceptible to infected
		if(site < 0 && neighboursInfected(randX,randY)){
			if(random.nextDouble() < p1){
				agents[randX][randY] = 0;
				return new int[] {randX,randY};
			}
		}
		//infected to recovered
		else if(site == 0){
			if(random.nextDouble() < p2){
				agents[randX][randY] = 1;
				return new int[] {randX,randY};
			}
		}
		//recovered to susceptible
		else if(site == 1){ 
			if(random.nextDouble() < p3){
				agents[randX][randY] = -1;
				return new int[] {randX,randY};
			}
		}
		
		return null;
		
	}
	
	//true if at least 1 neighbour is infected
	//with periodic boundary conditions
	private boolean neighboursInfected(int x, int y){
		
		if(agents[(x+1)%width][y] == 0){
			return true;
		}
		if(agents[x][(y+1)%height] == 0){
			return true;
		}
		if(agents[(x-1+width)%width][y] == 0){
			return true;
		}
		if(agents[x][(y-1+height)%height] == 0){
			return true;
		}
		return false;
	}

}
