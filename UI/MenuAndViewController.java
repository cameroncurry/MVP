import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;


/*
 * Class that provides UI to go between a menu panel and a view panel
 * With class T that is passed from the menu to the view
 */

abstract class MenuPanel<MenuSettings> extends JPanel {
	
	private static final long serialVersionUID = 1L;
	//component that provides the action to move to view panel
	public abstract JComponent showViewClickComponent();
	//generic class passed from menu to view ideally with settings
	public abstract MenuSettings getSettings();
}

interface Viewable<MenuSettings> {

	//JPanel to put in card layout
	public JPanel getView();
	
	//component that provides action to go back to menu
	public JComponent backClickComponent();
	
	//generic class passed from menu panel for settings
	public void setSettings(MenuSettings settings);
	
	public void willShowMenu();
}

public class MenuAndViewController<MenuSettings> {

	private JFrame frame;
	private JPanel cardPanel; //panel to hold cards
	private MenuPanel<MenuSettings> menu;
	private Viewable<MenuSettings> viewable;
	private CardLayout cardLayout;
	
	
	
	public MenuAndViewController(MenuPanel<MenuSettings> menu, Viewable<MenuSettings> viewable){
		setupFrame();
		
		cardPanel = new JPanel();
		cardLayout = new CardLayout();
		cardPanel.setLayout(cardLayout);
		
		this.menu = menu;
		this.viewable = viewable;
		cardPanel.add(menu);
		cardPanel.add(viewable.getView());
		
		menu.showViewClickComponent().addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				showView();
			}
		});
		
		viewable.backClickComponent().addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				showMenu();
			}
		});
		
		
		frame.getContentPane().add(cardPanel);
		frame.setVisible(true);
	}
	
	
	private void setupFrame(){
		frame = new JFrame();
		frame.setSize(700,500);
		frame.setMinimumSize(new Dimension(600,400));
		frame.setLocationRelativeTo(null);//frame centre of screen
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void showView(){
		viewable.setSettings(menu.getSettings());
		cardLayout.next(cardPanel);
	}
	
	private void showMenu(){
		viewable.willShowMenu();
		cardLayout.first(cardPanel);
	}
	
}
