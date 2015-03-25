import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;



public class MDMenu extends MenuPanel<MDSettings> {

	private static final long serialVersionUID = 1L;
	
	JSpinner NSpinner, timeSpinner, densitySpinner, sigSpinner, epSpinner;
	JComboBox<String> potentialBox, boundaryBox, initialBox, dimensionBox,dynamicsBox;
	JButton startButton;

	public MDMenu() {
		this.setLayout(new BorderLayout());
		
		this.add(headerPanel(),BorderLayout.NORTH);
		this.add(footerPanel(),BorderLayout.SOUTH);
		this.add(settingsPanel());
	}
	
	private JPanel headerPanel(){
		JPanel headerPanel = new JPanel();
		headerPanel.setBackground(Color.white);
		headerPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		JLabel header = new JLabel("<html><font size=+5><i>Molecular Dynamics</i></font></html>");
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
		
		JLabel titleLabel = new JLabel("<html><font size=+1><b>Simulation Settings</b></font></html>");
		gbc.insets.bottom = 30;
		gbc.ipadx = 5;
		gbc.gridwidth = 4;
		panel.add(titleLabel, gbc);
		
		gbc.ipadx = 0;
		
		/*
		 * number of particles
		 */
		NSpinner = new JSpinner(new SpinnerNumberModel(36,1,999,10));
		gbc.gridy = 1;
		gbc.insets.bottom = 10;
		panel.add(singleCompPanel("Number of Particles:",NSpinner),gbc);
		
		gbc.insets.bottom = 0;
		gbc.gridwidth = 1;
		
		
		JPanel leftPanel = new JPanel(new GridBagLayout());
		leftPanel.setBackground(Color.white);
		
		/*
		 * time step & density
		 */
		
		timeSpinner = new JSpinner(new SpinnerNumberModel(0.001,0,100,0.01));
		densitySpinner = new JSpinner(new SpinnerNumberModel(0.3,0,1,0.1));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets.bottom = 8;
		leftPanel.add(twoCompPanel("Time Step:",timeSpinner,
								"Density:",densitySpinner),gbc);
		
		gbc.insets.bottom = 0;
		/*
		 * potential & boundary conditions
		 */
		potentialBox = new JComboBox<String>(new String[] {"Lennard-Jones","WCA"});
		boundaryBox = new JComboBox<String>(new String[] {"Periodic"});
		gbc.gridy = 1;
		leftPanel.add(twoCompPanel("Potential:",potentialBox,"Boundary:",boundaryBox),gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets.right = 6;
		panel.add(leftPanel,gbc);
		
		gbc.insets.right = 0;
		//gbc.insets.bottom = 4;
		JPanel rightPanel = new JPanel(new GridBagLayout());
		rightPanel.setBackground(Color.white);
		
		/*
		 * sigma & epsilon
		 */
		
		sigSpinner = new JSpinner(new SpinnerNumberModel(1,0,10,0.1));
		epSpinner = new JSpinner(new SpinnerNumberModel(1,0,10,0.1));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets.bottom = 8;
		gbc.anchor = GridBagConstraints.LINE_START;
		rightPanel.add(twoCompPanel("Sigma:",sigSpinner,"Epsilon",epSpinner),gbc);
		
		gbc.insets.bottom = 0;
		//gbc.anchor = GridBagConstraints.CENTER;
		
		
		/*
		 * Initial condition & dimensions
		 */
		initialBox = new JComboBox<String>(new String[] {"Grid","Random"});
		dimensionBox = new JComboBox<String>(new String[] {"2D","3D"});
		gbc.gridy = 1;
		rightPanel.add(twoCompPanel("Initialisation:",initialBox,"Dimensions:",dimensionBox),gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.insets.left = 6;
		gbc.insets.bottom = 4;
		panel.add(rightPanel,gbc);
		
		/*
		 * type of dynamics
		 */
		dynamicsBox = new JComboBox<String>(new String[] {"NVE","NVT"});
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 4;
		gbc.anchor = GridBagConstraints.CENTER;
		panel.add(singleCompPanel("Dynamics",dynamicsBox),gbc);
		
		/*
		 * start button
		 */
		startButton = new JButton("Start");
		gbc.gridy = 4;
		gbc.ipadx = 4;
		gbc.ipady = 10;
		gbc.insets.top = 20;
		panel.add(startButton,gbc);
		
		return panel;
	}
	
	static JPanel singleCompPanel(String label, JComponent comp){
		JPanel panel = new JPanel();
		panel.setBackground(Color.white);
		panel.add(new JLabel(label));
		panel.add(comp);
		return panel;
	}
	
	static JPanel twoCompPanel(String label1, JComponent comp1, String label2, JComponent comp2){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.white);
		GridBagConstraints gbc = new GridBagConstraints();
		
		
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets.top = 4;
		panel.add(new JLabel(label1),gbc);
		
		gbc.gridy = 1;
		panel.add(new JLabel(label2),gbc);
		
		gbc.insets.top = 0;
		
		JPanel compPanel = new JPanel(new GridLayout(2,1));
		compPanel.setBackground(Color.white);
		compPanel.add(comp1);
		compPanel.add(comp2);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 2;
		panel.add(compPanel,gbc);
			
		return panel;
	}

	@Override
	public JComponent clickComponent() {
		return startButton;
	}

	@Override
	public MDSettings getSettings() {
		MDSettings settings = new MDSettings();
		settings.N = (Integer)NSpinner.getValue();
		settings.timeStep = (Double)timeSpinner.getValue();
		settings.sigma = (Double)sigSpinner.getValue();
		settings.epsilon = (Double)epSpinner.getValue();
		settings.density = (Double)densitySpinner.getValue();
		
		if(potentialBox.getSelectedIndex() == 0){
			settings.LJ = true;
		}
		else {
			settings.LJ = false;
		}
		
		if(boundaryBox.getSelectedIndex() == 0){
			settings.periodic = true;
		}
		else{
			settings.periodic = false;
		}
		
		if(initialBox.getSelectedIndex() == 0){
			settings.random = false;
		}
		else{
			settings.random = true;
		}
		
		if(dimensionBox.getSelectedIndex() == 0){
			settings.twoD = true;
		}
		else{
			settings.twoD = false;
		}
		
		if(dynamicsBox.getSelectedIndex() == 0){
			settings.NVE = true;
		}
		else{
			settings.NVE = false;
		}
		
		return settings;
	}

}
