
public class SIRSPlotSettings extends SIRSSettings {

	public int points;
	public int sweeps;
	public int updatesPerSweep;
	public int burnIn;
	public boolean plotTime;
	public boolean plotContour;
	
	public SIRSPlotSettings(SIRSSettings settings) {
		super.rows = settings.rows;
		super.columns = settings.columns;
		super.p1 = settings.p1;
		super.p2 = settings.p2;
		super.p3 = settings.p3;
		super.percentS = settings.percentS;
		super.percentI = settings.percentI;
		super.percentImmune = settings.percentImmune;
	}

}
