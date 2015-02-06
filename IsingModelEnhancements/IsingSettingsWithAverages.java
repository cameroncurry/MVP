
public class IsingSettingsWithAverages extends IsingSettings {

	public double T1;
	public double T2;
	public int plotPoints;
	public int updatesPerSweep;
	public int burnIn;
	public int sweeps;
	public boolean plotEandC;
	public boolean plotMandX;
	
	//plain constructor
	public IsingSettingsWithAverages(){}
	
	public IsingSettingsWithAverages(IsingSettings settings){
		super.rows = settings.rows;
		super.columns = settings.columns;
		super.J = settings.J;
		super.kb = settings.kb;
		super.T = settings.T;
		super.spinUp = settings.spinUp;
		super.glauber = settings.glauber;
	}

}
