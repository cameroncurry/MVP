import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class Observable {
	
	//remove
	public static void main(String[] args){
		Observable o = new Observable("Energy", "x", "y", "slope", "error", "flat", "error");
		
		for(int i=0;i<10;i++){
			o.add(0,i);
			//slope
			o.add(1,i);
			o.add(2,1);
			
			//flat
			o.add(3,1);
			o.add(4,0.5);
		}
		
		o.plotWithErrors();
	}
	
	//list of lists for related observables
	//eg. kinetic & potential energies
	private ArrayList<ArrayList<Double>> observables;
	
	//labels for plot
	private String title, xlabel, ylabel;
	
	//names of observables for plot legend
	private String[] names;
	
	
	public Observable(int N, String title, String xlabel, String ylabel) {
		observables = new ArrayList<ArrayList<Double>>(N);
		
		this.title = title;
		this.xlabel = xlabel;
		this.ylabel = ylabel;
		
		for(int i=0;i<N; i++){
			observables.add(new ArrayList<Double>());
		}
	}
	
	public Observable(String title, String xlabel, String ylabel, String... names){
		this(names.length+1, title, xlabel, ylabel);
		this.names = names;
	}
	
	
	public void add(int index, double value){
		observables.get(index).add(value);
	}
	
	//standard deviation of terms in a given array of observables
	//stdev is calculated in the form <O>^2 - <O^2>
	
	public double stdev(){
		return stdev(1,1);
	}
	public double stdev(double normalisation){
		return stdev(1,normalisation);
	}
	
	public double stdev(int index, double normalisation){
		double sum = 0;
		double sum2 = 0;
		
		ArrayList<Double> values = observables.get(index);
		int length = values.size(); // get size once for efficiency
		for(int i=0;i<length;i++){
			double x = values.get(i);
			sum += x;
			sum2 += x*x;
		}
		
		double sumAv = sum / (double)length;
		double sum2Av = sum2 / (double)length;
		
		return (sum2Av - sumAv*sumAv) / normalisation;
	}
	
	
	//bootstrap error, normalisation is for the intermediary observable
	//eg. error in magnetic susceptibility requires random samples of magnetisation
	//the normalisation will be applied to the magnetisation average, not susceptibility average
	
	public double boostrapError(){
		return bootstrapError(1,1);
	}
	public double bootstrapError(double normalisation){
		return bootstrapError(1,normalisation);
	}
	public double bootstrapError(int index, double normalisation){
		Random r = new Random();
		int repetitions = 200;
		
		double resultSum = 0;
		double resultSum2 = 0;
		
		ArrayList<Double> values = observables.get(index);
		int length = values.size();
		
		for(int i=0;i<repetitions;i++){
			
			double sum = 0;
			double sum2 = 0;
			for(int j=0;j<length;j++){
				double randomValue = values.get(r.nextInt(length));
				sum += randomValue;
				sum2 += randomValue*randomValue;
			}
			
			double sumAv = sum / (double)length;
			double sum2Av = sum2 / (double)length;
			double result = (sum2Av - sumAv*sumAv) / normalisation;
			
			resultSum += result;
			resultSum2 += result*result;
		}
		
		double resultAv = resultSum / (double)repetitions;
		double result2Av = resultSum2 / (double)repetitions;
		
		return result2Av - resultAv*resultAv;
	}
	
	public void plot(){
		plot(false);
	}
	
	public void plotWithErrors(){
		plot(true);
	}
	
	public void plot(boolean errors){
		try {
			String filename = writeFile();
			
			Runtime r = Runtime.getRuntime();
			String cmd = String.format("python Observe.py %s %s %s %s", filename, title, xlabel, ylabel);
			
			cmd += names != null ? " true" : " false";
			cmd += errors? " true" : " false";
			
			Process p = r.exec(cmd);
			p.waitFor();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	private String writeFile() throws FileNotFoundException{
		String filename = "observed.txt";
		PrintWriter p = new PrintWriter(filename);
		
		//write legend names above columns
		if(names != null){
			p.printf("%.4f\t", 0.0);
			for(int i=0;i<names.length;i++){
				p.printf("%s\t", names[i]);
			}
			p.println();
		}
		
		
		int length = observables.get(0).size();
		
		for(int i=0;i<length;i++){
			for(int j=0;j<observables.size();j++){
				p.printf("%.4f\t", observables.get(j).get(i));
			}
			p.println();
		}
		
		p.close();
		return filename;
	}

}
