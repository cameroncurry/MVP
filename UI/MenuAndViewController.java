import java.awt.CardLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JComponent;
import javax.swing.JPanel;


/*
 * Class that provides UI to go between a menu panel and a view panel
 * With class T that is passed from the menu to the view
 */

abstract class MenuPanel<Settings> extends JPanel {
	
	private static final long serialVersionUID = 1L;
	//component that provides the action to move to view panel
	public abstract JComponent clickComponent();
	//generic class passed from menu to view ideally with settings
	public abstract Settings getSettings();
}

interface Viewable<Settings> {

	//JPanel to put in card layout
	public JPanel getView();
	
	//component that provides action to go back to menu
	public JComponent backClickComponent();
	
	//generic class passed from menu panel for settings
	public void setSettings(Settings settings);
	
	public void willShowMenu();
}

public class MenuAndViewController<Settings> extends JPanel {

	
	private static final long serialVersionUID = 1L;
	//private JFrame frame;
	//private JPanel cardPanel; //panel to hold cards
	private MenuPanel<Settings> menu;
	private Viewable<Settings> viewable;
	private CardLayout cardLayout;
	
	
	
	public MenuAndViewController(MenuPanel<Settings> menu, Viewable<Settings> viewable){
		
		cardLayout = new CardLayout();
		this.setLayout(cardLayout);
		
		this.menu = menu;
		this.viewable = viewable;
		this.add(menu);
		this.add(viewable.getView());
		
		
		menu.clickComponent().addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				showView();
			}
		});
		
		
		viewable.backClickComponent().addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				showMenu();
			}
		});
		
	}
	
	
	private void showView(){
		viewable.setSettings(menu.getSettings());
		cardLayout.next(this);
	}
	
	private void showMenu(){
		viewable.willShowMenu();
		cardLayout.first(this);
	}
	
}
