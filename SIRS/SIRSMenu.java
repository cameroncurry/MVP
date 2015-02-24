import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class SIRSMenu extends DualMenuPanel<SIRSSettings,SIRSPlotSettings> {

	private static final long serialVersionUID = 1L;
	
	//initial menu components
	private JSpinner rowsSpinner, columnsSpinner;
	private JSpinner p1Spinner, p2Spinner, p3Spinner;
	private JSpinner percentSSpinner, percentISpinner, percentImmuneSpinner;
	private JButton viewButton, changeMenu, plotButton;
	
	private CardLayout cardLayout;
	private JPanel cardPanel;
	
	//plot menu components
	private JSpinner pointsSpinner, burnInSpinner, sweepsSpinner, updatesSpinner;
	private JRadioButton plotTime, plotContour;

	public SIRSMenu() {
		setLayout(new BorderLayout());
		
		this.add(headerPanel(),BorderLayout.NORTH);
		this.add(footerPanel(),BorderLayout.SOUTH);
		
		cardPanel = new JPanel();
		cardLayout = new CardLayout();
		cardPanel.setLayout(cardLayout);
		
		cardPanel.add(settingsPanel());
		cardPanel.add(plotSettingsPanel());
		
		this.add(cardPanel,BorderLayout.CENTER);
		
		
		
		//change color of spinner back to black when value changed
		ChangeListener percentSpinnerChanger = new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				percentSSpinner.getEditor().getComponent(0).setForeground(Color.black);
				percentISpinner.getEditor().getComponent(0).setForeground(Color.black);
				percentImmuneSpinner.getEditor().getComponent(0).setForeground(Color.black);
			}
		};
		
		percentSSpinner.addChangeListener(percentSpinnerChanger);
		percentISpinner.addChangeListener(percentSpinnerChanger);
		percentImmuneSpinner.addChangeListener(percentSpinnerChanger);
	}
	
	

	@Override
	public boolean canShowView(){
		
		double s = (Double)percentSSpinner.getValue();
		double i = (Double)percentISpinner.getValue();
		double I = (Double)percentImmuneSpinner.getValue();
		if((s+i+I) > 1.0){
			percentSSpinner.getEditor().getComponent(0).setForeground(Color.red);
			percentISpinner.getEditor().getComponent(0).setForeground(Color.red);
			percentImmuneSpinner.getEditor().getComponent(0).setForeground(Color.red);
			return false;
		}
		else{
			return true;
		}
		
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

	@Override
	public JComponent secondClickComponent() {
		return plotButton;
	}

	@Override
	public SIRSPlotSettings getSecondSettings() {
		SIRSPlotSettings settings = new SIRSPlotSettings(getSettings());
		settings.points = (Integer)pointsSpinner.getValue();
		settings.sweeps = (Integer)sweepsSpinner.getValue();
		settings.updatesPerSweep = (Integer)updatesSpinner.getValue();
		settings.burnIn = (Integer)burnInSpinner.getValue();
		settings.plotTime = (Boolean)plotTime.isSelected();
		settings.plotContour = (Boolean)plotContour.isSelected();
		return settings;
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
	
	private JPanel settingsPanel(){
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
		
		
		JButton absorbingButton = new JButton("Absorbing");
		gbc.gridy = 6;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.ipadx = 5;
		gbc.insets.top = 10;
		panel.add(absorbingButton,gbc);
		
		absorbingButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				p1Spinner.setValue(0.);
				p2Spinner.setValue(0.2);
				p3Spinner.setValue(0.2);
				percentSSpinner.setValue(0.3);
				percentISpinner.setValue(0.3);
				percentImmuneSpinner.setValue(0.);
			}
		});
		
		JButton waveButton = new JButton("Wave");
		gbc.gridx = 0;
		gbc.gridwidth = 6;
		panel.add(waveButton,gbc);
		
		waveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				p1Spinner.setValue(1.);
				p2Spinner.setValue(0.1);
				p3Spinner.setValue(0.01);
				percentSSpinner.setValue(0.15);
				percentISpinner.setValue(0.01);
				percentImmuneSpinner.setValue(0.);
			}
		});
		
		JButton equilibButton = new JButton("Equilibrium");
		gbc.gridx = 4;
		gbc.gridwidth = 2;
		panel.add(equilibButton,gbc);
		
		equilibButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				p1Spinner.setValue(1.);
				p2Spinner.setValue(1.);
				p3Spinner.setValue(1.);
				percentSSpinner.setValue(0.3);
				percentISpinner.setValue(0.3);
				percentImmuneSpinner.setValue(0.);
			}
		});
		
		viewButton = new JButton("View");
		gbc.gridy = 7;
		gbc.gridx = 0;
		gbc.gridwidth = 4;
		gbc.ipadx = 5;
		gbc.ipady = 10;
		gbc.insets.top = 20;
		panel.add(viewButton,gbc);
		
		changeMenu = new JButton("Plot");
		gbc.gridx = 3;
		panel.add(changeMenu,gbc);
		
		changeMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				updatesSpinner.setValue((Integer)(rowsSpinner.getValue())*(Integer)columnsSpinner.getValue());
				cardLayout.last(cardPanel);
				
			}
		});
		
		return panel;
	}
	
	private JPanel plotSettingsPanel(){
		JPanel panel = new JPanel();
		panel.setBackground(Color.white);
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		
		JLabel titleLabel = new JLabel("<html><font size=+1><b>Plot Settings</b></font></html>");
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 4;
		gbc.insets.bottom = 20;
		gbc.ipadx = 3;
		panel.add(titleLabel,gbc);
		
		gbc.gridwidth = 1;
		gbc.insets.bottom = 5;
		gbc.ipadx = 0;
		
		
		JLabel pointsLabel = new JLabel("Points to Plot");
		gbc.gridy = 1;
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		panel.add(pointsLabel,gbc);
		
		
		pointsSpinner = new JSpinner(new SpinnerNumberModel(100,0,1000,5));
		gbc.gridx = 1;
		gbc.ipadx = -10;
		panel.add(pointsSpinner,gbc);
		
		gbc.ipadx = 0;
		
		JLabel updatesLabel = new JLabel("Updates per Sweep");
		gbc.gridy = 2;
		gbc.gridx = 0;
		gbc.insets.right = 15;
		panel.add(updatesLabel,gbc);
		
		gbc.insets.right = 0;
		
		updatesSpinner = new JSpinner(new SpinnerNumberModel(10000,0,100000,500));
		gbc.gridx = 1;
		gbc.insets.left = 15;
		gbc.ipadx = -20;
		panel.add(updatesSpinner,gbc);
		
		gbc.insets.left = 0;
		gbc.ipadx = 0;
		gbc.gridwidth = 1;
		
		JLabel sweepsLabel = new JLabel("Sweeps per Point");
		gbc.gridy = 3;
		gbc.gridx = 0;
		panel.add(sweepsLabel,gbc);
		
		sweepsSpinner = new JSpinner(new SpinnerNumberModel(50,0,500,10));
		gbc.gridx = 1;
		panel.add(sweepsSpinner,gbc);
		
		JLabel burnInLabel = new JLabel("Burn-in (Sweeps)");
		gbc.gridx = 2;
		gbc.insets.left = 10;
		panel.add(burnInLabel,gbc);
		
		gbc.insets.left = 0;
		
		burnInSpinner = new JSpinner(new SpinnerNumberModel(0,0,1000,50));
		gbc.gridx = 3;
		gbc.ipadx = -10;
		panel.add(burnInSpinner,gbc);
		
		gbc.ipadx = 0;
		
		plotContour = new JRadioButton("Contour Plot");
		gbc.gridy = 4;
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		panel.add(plotContour,gbc);
		
		plotTime = new JRadioButton("Time Plot",true);
		gbc.gridx = 1;
		gbc.insets.left = 10;
		panel.add(plotTime,gbc);
		
		
		JButton backButton = new JButton("Back");
		gbc.gridy = 5;
		gbc.gridx = 0;
		gbc.insets.top = 20;
		gbc.ipady = 10;
		panel.add(backButton,gbc);
		
		plotButton = new JButton("Plot");
		gbc.gridx = 1;
		panel.add(plotButton,gbc);
		
		
		backButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cardLayout.first(cardPanel);
			}
		});
		
		plotContour.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(plotContour.isSelected()){
					plotTime.setSelected(false);
				}
				else{
					plotContour.setSelected(true);
				}
			}
		});
		
		plotTime.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(plotTime.isSelected()){
					plotContour.setSelected(false);
				}
				else{
					plotTime.setSelected(true);
				}
			}
		});
		
		return panel;
	}
	

}
