import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;


public class IsingPlotMenu extends MenuPanel<IsingSettingsWithAverages> implements Viewable<IsingSettings>{

	
	private static final long serialVersionUID = 1L;

	MenuAndViewController<IsingSettingsWithAverages> controlPanel;
	
	IsingSettings previousSettings;
	
	JSpinner t1Spinner;
	JSpinner t2Spinner;
	JSpinner pointsSpinner;
	JSpinner updatesPerSweepSpinner;
	JSpinner burnInSpinner;
	JSpinner sweepsSpinner;
	JCheckBox eAndCBox;
	JCheckBox mAndXBox;
	JButton backButton;
	JButton plotButton;
	
	public IsingPlotMenu() {
		setLayout(new BorderLayout());
		
		/*
		 * Header 
		 */
		
		JPanel headerPanel = new JPanel();
		headerPanel.setBackground(Color.WHITE);
		headerPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbcH = new GridBagConstraints();
		
		//label for header title
		JLabel headerLabel = new JLabel("<html><font size=+5><i>Ising Model</i></font></html>");
		gbcH.gridx = 0;
		gbcH.gridy = 0;
		gbcH.ipadx = 10;
		gbcH.insets = new Insets(30,0,0,0);
		headerPanel.add(headerLabel,gbcH);
		
		this.add(createSettingsPanel());
		
		this.add(headerPanel,BorderLayout.NORTH);
		
		controlPanel = new MenuAndViewController<IsingSettingsWithAverages>(this,new IsingPlotController());
		
	}
	private JPanel createSettingsPanel(){	
		JPanel settings = new JPanel();
		settings.setBackground(Color.white);
		settings.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		//title label
		JLabel settingsLabel = new JLabel("Plot Settings");
		settingsLabel.setFont(new Font(Font.SANS_SERIF,Font.BOLD,20));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 5;
		//gbc.ipadx = 10;
		gbc.insets = new Insets(0,0,20,0);
		settings.add(settingsLabel,gbc);
		
		gbc.gridwidth = 1;
		gbc.insets.bottom = 0;
		//gbc.insets.right = -35;
		
		
		JLabel tLabel = new JLabel("Temperature Range");
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 4;
		gbc.insets.right = 50;
		settings.add(tLabel,gbc);
		
		gbc.insets.right = 0;
		
		t1Spinner = new JSpinner(new SpinnerNumberModel(5,0,10,0.5));
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		//gbc.insets.left = -50;
		settings.add(t1Spinner,gbc);
		
		//gbc.insets.left = -25;
		
		t2Spinner = new JSpinner(new SpinnerNumberModel(1,0,10,0.5));
		gbc.gridx = 3;
		//gbc.anchor = GridBagConstraints.LAST_LINE_END;
		gbc.insets.left = 125;
		//gbc.gridy = 1;
		settings.add(t2Spinner,gbc);
		
		gbc.gridwidth = 1;
		gbc.insets.left = 0;
		
		JLabel pointsLabel = new JLabel("Points to Plot");
		gbc.gridx = 0;
		gbc.gridy = 2;
		settings.add(pointsLabel,gbc);
		
		pointsSpinner = new JSpinner(new SpinnerNumberModel(20,1,50,2));
		gbc.gridx = 2;
		//gbc.gridwidth = 1;
		settings.add(pointsSpinner,gbc);
		
		gbc.insets.left = 20;
		
		JLabel updatesPerSweepLabel = new JLabel("Updates per Sweep");
		//gbc.gridy = 3;
		gbc.gridx = 3;
		//gbc.gridwidth = 1;
		settings.add(updatesPerSweepLabel,gbc);
		
		gbc.insets.left = 0;
		
		updatesPerSweepSpinner = new JSpinner(new SpinnerNumberModel(5000,1,100000,100));
		gbc.gridx = 4;
		//gbc.gridwidth = 2;
		settings.add(updatesPerSweepSpinner,gbc);
		
		JLabel sweepsLabel = new JLabel("Sweeps per Point");
		gbc.gridy = 3;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		settings.add(sweepsLabel,gbc);
		
		sweepsSpinner = new JSpinner(new SpinnerNumberModel(500,0,5000,100));
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		settings.add(sweepsSpinner,gbc);
		
		
		JLabel burnInLabel = new JLabel("Burn-In (sweeps)");
		//gbc.gridy = 4;
		gbc.gridx = 3;
		gbc.gridwidth = 1;
		settings.add(burnInLabel,gbc);
		
		burnInSpinner = new JSpinner(new SpinnerNumberModel(100,0,1000,50));
		gbc.gridx = 4;
		//gbc.gridwidth = 2;
		settings.add(burnInSpinner,gbc);
		
		
		
		eAndCBox = new JCheckBox("Plot Energy and Heat Capacity",true);
		gbc.gridy = 4;
		gbc.gridx = 0;
		gbc.gridwidth = 5;
		settings.add(eAndCBox,gbc);
		
		mAndXBox = new JCheckBox("Plot Magnetisation and Susceptibility",true);
		gbc.gridy = 5;
		settings.add(mAndXBox,gbc);
		
		
		backButton = new JButton("Back");
		gbc.insets.top = 10;
		gbc.insets.bottom = 50;
		gbc.ipadx = 10;
		gbc.ipady = 15;
		gbc.gridy = 6;
		gbc.gridx = 0;
		gbc.gridwidth = 4;
		settings.add(backButton,gbc);
		
		gbc.insets.right = 0;
		
		plotButton = new JButton("Plot");
		gbc.gridx = 1;
		gbc.insets.left = 50;
		settings.add(plotButton,gbc);
		
		
		
		return settings;
	}

	
	public JPanel getView() {
		return controlPanel;
	}

	
	public JComponent backClickComponent() {
		return backButton;
	}

	
	public void setSettings(IsingSettings settings) {
		this.previousSettings = settings;
	}

	public void willShowMenu() {
		//nothing to do here
	}

	
	public JComponent clickComponent() {
		return plotButton;
	}

	
	public IsingSettingsWithAverages getSettings() {
		IsingSettingsWithAverages settings = new IsingSettingsWithAverages(previousSettings);
		
		settings.T1 = (double)t1Spinner.getValue();
		settings.T2 = (double)t2Spinner.getValue();
		settings.plotPoints = (int)pointsSpinner.getValue();
		settings.updatesPerSweep = (int)updatesPerSweepSpinner.getValue();
		settings.burnIn = (int)burnInSpinner.getValue();
		settings.sweeps = (int)sweepsSpinner.getValue();
		settings.plotEandC = eAndCBox.isSelected();
		settings.plotMandX = mAndXBox.isSelected();
		
		return settings;
	}

}
