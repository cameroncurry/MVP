import java.awt.CardLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JComponent;
import javax.swing.JPanel;


abstract class DualMenuPanel<Settings1,Settings2> extends MenuPanel<Settings1> {
	
	private static final long serialVersionUID = 1L;
	public abstract JComponent secondClickComponent();
	public abstract Settings2 getSecondSettings();
}


public class MenuAndDualViewController<Settings1,Settings2> extends JPanel {

	private static final long serialVersionUID = 1L;

	private DualMenuPanel<Settings1,Settings2> menu;
	private Viewable<Settings1> view1;
	private Viewable<Settings2> view2;
	
	private CardLayout cardLayout;
	
	
	public MenuAndDualViewController(DualMenuPanel<Settings1,Settings2> menu, 
			final Viewable<Settings1> view1, final Viewable<Settings2> view2) {
		
		cardLayout = new CardLayout();
		this.setLayout(cardLayout);
		
		this.menu = menu;
		this.view1 = view1;
		this.view2 = view2;
		
		this.add(menu);
		this.add(view1.getView(),"1");
		this.add(view2.getView(),"2");
		
		menu.clickComponent().addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				showFirstView();
			}
		});
		
		menu.secondClickComponent().addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				showSecondView();
			}
		});
		
		view1.backClickComponent().addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				willShowMenu(view1);
			}
		});
		
		view2.backClickComponent().addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				willShowMenu(view2);
			}
		});
	}
	
	private void showFirstView(){
		if(menu.canShowView()){
			view1.setSettings(menu.getSettings());
			cardLayout.show(this, "1");
		}
	}
	
	private void showSecondView(){
		if(menu.canShowView()){
			view2.setSettings(menu.getSecondSettings());
			cardLayout.show(this, "2");
		}
	}

	
	@SuppressWarnings("rawtypes") // I know its okay to not parameterize the sender
	private void willShowMenu(Viewable sender){
		sender.willShowMenu();
		cardLayout.first(this);
	}
}
