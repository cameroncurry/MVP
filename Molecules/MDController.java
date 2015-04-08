import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class MDController implements Viewable<MDSettings>, Runnable {
	
	private JPanel cardPanel;
	private final CardLayout cardLayout;
	private JPanel viewablePanel;
	private JPanel plotPanel;
	private ImagePanel imagePanel;
	private JButton backButton;
	
	private JPanel viewCardPanel;
	private CardLayout viewCardLayout;
	private MD2DView view2d;
	private MD3DView view3d;
	
	private MDModel model;
	
	private boolean threadFlag;
	
	private int speed;
	
	
	public MDController(){
		cardPanel = new JPanel();
		cardLayout  = new CardLayout();
		cardPanel.setLayout(cardLayout);
		viewablePanel = new JPanel(new BorderLayout());
		plotPanel = new JPanel(new BorderLayout());
		
		threadFlag = false;
		
		speed = 3;
		
		/*
		 * Viewpanel
		 */
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(Color.black);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setBackground(Color.black);
		backButton = new JButton("Back");
		leftPanel.add(backButton);
		
		JLabel speedLabel = new JLabel("<html><font color='white'>Speed:</font></html>");
		leftPanel.add(speedLabel);
		
		final JSlider speedSlider = new JSlider(JSlider.HORIZONTAL,0,10,3);
		leftPanel.add(speedSlider);
		
		headerPanel.add(leftPanel,BorderLayout.WEST);
		
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.black);
		JButton eButton = new JButton("E");
		JButton tButton = new JButton("T");
		JButton rButton = new JButton("<r^2>");
		buttonPanel.add(new JLabel("<html><font color=white><b>Plot:</b></font></html>"));
		buttonPanel.add(rButton);
		buttonPanel.add(tButton);
		buttonPanel.add(eButton);
		
		headerPanel.add(buttonPanel,BorderLayout.EAST);
		
		viewablePanel.add(headerPanel,BorderLayout.NORTH);
		
		viewCardPanel = new JPanel();
		viewCardLayout = new CardLayout();
		viewCardPanel.setLayout(viewCardLayout);
		
		view2d = new MD2DView();
		viewCardPanel.add(view2d);
		
		
		view3d = new MD3DView();
		viewCardPanel.add(view3d);
		
		viewablePanel.add(viewCardPanel);
		cardPanel.add(viewablePanel);
		
		/*
		 * Plot panel
		 */
		JPanel imageHeader = new JPanel(new BorderLayout());
		imageHeader.setBackground(Color.white);
		JButton imageBackButton = new JButton("Back");
		imageHeader.add(imageBackButton,BorderLayout.WEST);
		plotPanel.add(imageHeader,BorderLayout.NORTH);
		
		imagePanel = new ImagePanel();
		plotPanel.add(imagePanel);
		cardPanel.add(plotPanel);
		
		
		
		//add actions to buttons
		speedSlider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				speed = speedSlider.getValue();
			}
		});
		
		view2d.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				model.setSoft(false);
				view2d.setSoft(false);
			}
		});
		view3d.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				model.setSoft(false);
			}
		});
		
		imageBackButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cardLayout.first(cardPanel);
				restartThread();
			}
		});
		
		eButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				plotEnergy();
			}
		});
		
		tButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				plotTemperature();
			}
		});
		
		rButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				plotRSquared();
			}
		});
	}
	

	public void setSettings(MDSettings settings) {
		model = new MDModel(settings);
		
		if(settings.twoD){
			view2d.set(model.getParticles(), model.getBoxDimension());
			view3d.set(null,1);
			viewCardLayout.first(viewCardPanel);
		}
		else{
			view3d.set(model.getParticles(),model.getBoxDimension());
			view2d.set(null, 1);
			viewCardLayout.last(viewCardPanel);
		}
		
		threadFlag = false;
		
		if(settings.random)view2d.setSoft(true);
		else view2d.setSoft(false);
		
		new Thread(this).start();
	}
	
	
	public void run() {
		for(int i=0;!threadFlag;i++){
			//update model
			model.velocityVerlet();
			if(i%300 == 0){
				if(!model.isSoft()){
					model.updateArrays();
				}
			}
			
			//slow down animation
			if(speed == 0 || i%(50*speed) == 0){
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {}
			}
			
			//update view
			if(model.isTwoD()){
				view2d.repaint();
			}
			else {
				view3d.repaint();
			}
			
		}
	}
	
	private void restartThread(){
		this.threadFlag = false;
		new Thread(this).start();
	}

	public JPanel getView() {
		return cardPanel;
	}

	public JComponent backClickComponent() {
		return backButton;
	}


	@Override
	public void willShowMenu() {
		threadFlag = true;
		view2d.set(null, 0);	
	}
	
	
	private void plotEnergy(){
		threadFlag = true; // stop verlet algorithm
		cardLayout.last(cardPanel);
		
		double[] ke = model.kineticEnergy();
		double[] pe = model.potentialEnergy();
		try {
			PrintWriter p = new PrintWriter("MD.txt");
			for(int i=0;i<ke.length;i++){
				p.printf("%.4f %.4f\n",ke[i],pe[i]);
			}
			p.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		
		Runtime r = Runtime.getRuntime();
		Process pr;
		try {
			pr = r.exec("python MDPlot.py");
			pr.waitFor();
			
			BufferedImage image = ImageIO.read(new File("MDfig.png"));
			this.imagePanel.setImage(image);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void plotTemperature(){
		threadFlag = true;
		cardLayout.last(cardPanel);
		
		double[] t = model.temperature();
		try{
			PrintWriter p = new PrintWriter("MD.txt");
			for(int i=0;i<t.length;i++){
				p.printf("%.4f\n",t[i]);
			}
			p.close();
		}catch(Exception e){}
		
		
		Runtime r = Runtime.getRuntime();
		Process pr;
		try {
			pr = r.exec("python MDPlot.py true");
			pr.waitFor();
			
			BufferedImage image = ImageIO.read(new File("MDfig.png"));
			this.imagePanel.setImage(image);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void plotRSquared(){
		threadFlag = true;
		cardLayout.last(cardPanel);
		
		double[] rSq = model.rSqaured();
		try{
			PrintWriter p = new PrintWriter("MD.txt");
			for(int i=0; i<rSq.length;i++){
				p.printf("%.4f\n", rSq[i]);
			}
			p.close();
			
			Runtime r = Runtime.getRuntime();
			Process pr = r.exec("python MDPlot.py false");
			pr.waitFor();
			
			BufferedImage image = ImageIO.read(new File("MDfig.png"));
			this.imagePanel.setImage(image);
		}catch(Exception e){}
	}
	
	

}
