import java.awt.*;

import javax.swing.*;


public class IsingMenu extends MenuPanel<IsingSettings> {

	
	private static final long serialVersionUID = 1L;
	
	private JSpinner rowsSpinner;
	private JSpinner columnsSpinner;
	private JSpinner J;
	private JSpinner Kb;
	private JSpinner T;
	private JSpinner prob;
	private JComboBox<String> dynamicsBox;
	
	private JButton startButton;

	public IsingMenu(){
		this.setLayout(new BorderLayout());
		
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
		
		this.add(headerPanel,BorderLayout.NORTH);
		
		
		
		/*
		 * Settings panel 
		 */
		
		JPanel settingsPanel = new JPanel();
		settingsPanel.setBackground(Color.WHITE);
		settingsPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		//title label
		JLabel settingsLabel = new JLabel("Simulation Settings");
		settingsLabel.setFont(new Font(Font.SANS_SERIF,Font.BOLD,20));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 5;
		gbc.ipadx = 10;
		gbc.insets = new Insets(0,0,20,0);
		settingsPanel.add(settingsLabel,gbc);
		
		//reset insets
		gbc.insets = new Insets(0,0,0,0);
		
		
		//size label
		JLabel sizeLabel = new JLabel("Size of Grid");
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		settingsPanel.add(sizeLabel,gbc);
		
		//rows spinner
		rowsSpinner = new JSpinner(new SpinnerNumberModel(100,1,300,10));
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		//gbc.insets.left = -50;
		settingsPanel.add(rowsSpinner,gbc);
		
		gbc.insets.left = 0;
		
		//columns spinner
		columnsSpinner = new JSpinner(new SpinnerNumberModel(100,1,300,10));
		gbc.gridx = 4;
		gbc.gridy = 1;
		settingsPanel.add(columnsSpinner,gbc);
		
		
		//temperature
		JLabel TLabel = new JLabel("Temperature");
		gbc.gridx = 0;
		gbc.gridy = 2;
		//gbc.gridwidth = 1;
		settingsPanel.add(TLabel, gbc);

		T = new JSpinner(new SpinnerNumberModel(1.0, 0, 99, 0.1));
		gbc.gridx = 1;
		gbc.gridy = 2;
		//gbc.ipadx = -10;
		//gbc.gridwidth = 2;
		settingsPanel.add(T, gbc);

		//J Label
		JLabel jLabel = new JLabel("J");
		gbc.gridx = 3;
		gbc.gridy = 2;
		//gbc.ipadx = 0;
		settingsPanel.add(jLabel, gbc);

		// J spinner
		J = new JSpinner(new SpinnerNumberModel(1.0, 0, 10, 0.1));
		gbc.gridx = 4;
		//gbc.gridy = 2;
		//gbc.gridwidth = 2;
		settingsPanel.add(J, gbc);	
		
		
		// initial up probability
		JLabel probLabel = new JLabel("Initial Spin-up Probability");
		gbc.gridx = 0;
		gbc.gridy = 3;
		//gbc.gridwidth = 1;
		settingsPanel.add(probLabel, gbc);

		prob = new JSpinner(new SpinnerNumberModel(0.5, 0, 1, 0.1));
		gbc.gridx = 1;
		//gbc.ipadx = 15;
		//gbc.gridwidth = 2;
		settingsPanel.add(prob, gbc);
		
		
		
		//Kb label
		JLabel kLabel = new JLabel("<html>k<sub>b</sub></html>");
		gbc.gridx = 3;
		gbc.gridy = 3;
		//gbc.ipadx = 0;
		//gbc.gridwidth = 1;
		settingsPanel.add(kLabel,gbc);
		
		Kb = new JSpinner(new SpinnerNumberModel(1.0,0,10,0.1));
		gbc.gridx = 4;
		gbc.gridy = 3;
		//gbc.gridwidth = 2;
		settingsPanel.add(Kb, gbc);
		
		
		
		//type of dynamics
		JLabel dynamicsLabel = new JLabel("Type of Dynamics");
		gbc.gridx = 0;
		gbc.gridy = 4;
		//gbc.gridwidth = 2;
		settingsPanel.add(dynamicsLabel,gbc);
		
		String[] dynamics = {"Glauber", "Kawasaki"};
		dynamicsBox = new JComboBox<String>(dynamics);
		gbc.gridx = 3;
		gbc.gridwidth = 2;
		settingsPanel.add(dynamicsBox,gbc);
		
		//start button
		startButton = new JButton("Start");
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 5;
		gbc.ipady = 15;
		gbc.insets = new Insets(25,0,0,0);
		settingsPanel.add(startButton,gbc);
		
		
		JLabel copyrightLabel = new JLabel("<html>&#169 Cameron Curry 2015</html>");
		gbc.gridy = 8;
		gbc.ipady = 20;
		settingsPanel.add(copyrightLabel,gbc);
		
		this.add(settingsPanel);
	}
	
	/*
	 * MenuPanel methods
	 */

	public JComponent showViewClickComponent() {
		return startButton;
	}

	
	public IsingSettings getSettings() {
		IsingSettings settings = new IsingSettings();
		settings.rows = (Integer)rowsSpinner.getValue();
		settings.columns = (Integer)columnsSpinner.getValue();
		settings.J = (Double)J.getValue();
		settings.kb = (Double)Kb.getValue();
		settings.T = (Double)T.getValue();
		settings.spinUp = (Double)prob.getValue();
		
		String s = (String)dynamicsBox.getSelectedItem();
		if(s.startsWith("G")){
			settings.glauber = true;
		}
		else {
			settings.glauber = false;
		}
		
		return settings;
	}
	
	
}
