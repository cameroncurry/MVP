import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;


public class SIRSMenu extends MenuPanel<SIRSSettings> {

	private static final long serialVersionUID = 1L;
	
	
	private JSpinner rowsSpinner, columnsSpinner;
	private JSpinner p1Spinner, p2Spinner, p3Spinner;
	private JSpinner percentSSpinner, percentISpinner, percentImmuneSpinner;
	private JButton viewButton;
	
	

	public SIRSMenu() {
		setLayout(new BorderLayout());
		
		this.add(headerPanel(),BorderLayout.NORTH);
		this.add(plotSettingsPanel(),BorderLayout.CENTER);
		
		this.add(footerPanel(),BorderLayout.SOUTH);
	}
	
	private JPanel headerPanel(){
		JPanel headerPanel = new JPanel();
		headerPanel.setBackground(Color.white);
		headerPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		JLabel header = new JLabel("<html><font size=+5><i>SIRS Model</i></font></html>");
		gbc.ipadx = 5;
		gbc.insets.top = 50;
		headerPanel.add(header,gbc);
		return headerPanel;
	}
	
	private JPanel footerPanel(){
		JPanel footerPanel = new JPanel();
		footerPanel.setBackground(Color.white);
		footerPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		JLabel copyrightLabel = new JLabel("<html>&#169 Cameron Curry 2015</html>");
		gbc.insets.bottom = 50;
		footerPanel.add(copyrightLabel,gbc);
		return footerPanel;
	}
	
	private JPanel plotSettingsPanel(){
		JPanel panel = new JPanel();
		panel.setBackground(Color.white);
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		JLabel label = new JLabel("<html><font size=+1><b>Simulation Settings</b></font></html>");
		gbc.insets.bottom = 20;
		gbc.ipadx = 3;
		gbc.gridwidth = 6;
		panel.add(label,gbc);
		
		gbc.insets.bottom = 0;
		gbc.gridwidth = 1;
		
		
		JLabel sizeLabel = new JLabel("Size of Grid");
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		gbc.insets.bottom = 8;
		panel.add(sizeLabel,gbc);
		
		gbc.gridwidth = 1;
		gbc.ipadx = 0;
		
		rowsSpinner = new JSpinner(new SpinnerNumberModel(100,1,500,10));
		gbc.gridx = 3;
		gbc.gridwidth = 1;
		panel.add(rowsSpinner,gbc);
		
		columnsSpinner = new JSpinner(new SpinnerNumberModel(100,1,500,10));
		gbc.gridx = 4;
		panel.add(columnsSpinner,gbc);
		
		
		JLabel dynamicsLabel = new JLabel("Simulation Dynamics");
		gbc.gridy = 2;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		panel.add(dynamicsLabel,gbc);
		
		JLabel initialisationLabel = new JLabel("Initialisation");
		gbc.gridx = 4;
		panel.add(initialisationLabel,gbc);
		
		gbc.gridwidth = 1;
		gbc.insets.bottom = 0;
		
		JLabel p1Label = new JLabel("P(S->I)");
		gbc.gridy = 3;
		gbc.gridx = 0;
		panel.add(p1Label,gbc);
		
		p1Spinner = new JSpinner(new SpinnerNumberModel(0.5,0,1,0.1));
		gbc.gridx = 1;
		gbc.ipadx = 20;
		panel.add(p1Spinner,gbc);
		
		gbc.ipadx = 0;
		
		JLabel p2Label = new JLabel("P(I->R)");
		gbc.gridy = 4;
		gbc.gridx = 0;
		panel.add(p2Label,gbc);
		
		p2Spinner = new JSpinner(new SpinnerNumberModel(0.5,0,1,0.1));
		gbc.gridx = 1;
		gbc.ipadx = 20;
		panel.add(p2Spinner,gbc);
		
		gbc.ipadx = 0;
		
		JLabel p3Label = new JLabel("P(R->S)");
		gbc.gridy = 5;
		gbc.gridx = 0;
		panel.add(p3Label,gbc);
		
		p3Spinner = new JSpinner(new SpinnerNumberModel(0.5,0,1,0.1));
		gbc.gridx = 1;
		gbc.ipadx = 20;
		panel.add(p3Spinner,gbc);
		
		
		JLabel susceptibleLabel = new JLabel("% Susceptible");
		gbc.gridy = 3;
		gbc.gridx = 2;
		gbc.gridwidth = 4;
		panel.add(susceptibleLabel,gbc);
		
		
		percentSSpinner = new JSpinner(new SpinnerNumberModel(0.25,0,1,0.1));
		gbc.gridx = 5;
		gbc.gridwidth = 1;
		panel.add(percentSSpinner,gbc);
		
		JLabel infectedLabel = new JLabel("% Infected");
		gbc.gridy = 4;
		gbc.gridx = 2;
		gbc.gridwidth = 4;
		panel.add(infectedLabel,gbc);
		
		percentISpinner = new JSpinner(new SpinnerNumberModel(0.25,0,1,0.1));
		gbc.gridx = 5;
		gbc.gridwidth = 1;
		panel.add(percentISpinner,gbc);
		
		JLabel immuneLabel = new JLabel("% Immune");
		gbc.gridy = 5;
		gbc.gridx = 2;
		gbc.gridwidth = 4;
		panel.add(immuneLabel,gbc);
		
		percentImmuneSpinner = new JSpinner(new SpinnerNumberModel(0,0,1,0.1));
		gbc.gridx = 5;
		gbc.gridwidth = 1;
		panel.add(percentImmuneSpinner,gbc);
		
		viewButton = new JButton("View");
		gbc.gridy = 6;
		gbc.gridx = 0;
		gbc.ipadx = 5;
		gbc.ipady = 10;
		gbc.insets.top = 20;
		gbc.gridwidth = 6;
		panel.add(viewButton,gbc);
		
		return panel;
	}

	@Override
	public JComponent clickComponent() {
		return viewButton;
	}

	@Override
	public SIRSSettings getSettings() {
		SIRSSettings settings = new SIRSSettings();
		settings.rows = (Integer)rowsSpinner.getValue();
		settings.columns = (Integer)columnsSpinner.getValue();
		settings.p1 = (Double)p1Spinner.getValue();
		settings.p2 = (Double)p2Spinner.getValue();
		settings.p3 = (Double)p3Spinner.getValue();
		settings.percentS = (Double)percentSSpinner.getValue();
		settings.percentI = (Double)percentISpinner.getValue();
		settings.percentImmune = (Double)percentImmuneSpinner.getValue();
		
		return settings;
	}

}
