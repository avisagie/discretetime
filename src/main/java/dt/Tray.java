package dt;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;

/**
 * Manage the system tray presence
 */
public class Tray {

	public interface Listener {
		void settings();

		void pause();

		void quit();

		void popup();
	}

	public static void setup(final Listener listener) throws IOException,
			AWTException {
		final SystemTray st = SystemTray.getSystemTray();
		final Dimension d = st.getTrayIconSize();
		int sz = Math.min(d.height, d.width);
		final Image icon = Util.loadImage("images/clock" + sz + ".png");
		final TrayIcon trayIcon = new TrayIcon(icon, "discretetime");

		final PopupMenu menu = new PopupMenu();
		final MenuItem show = new MenuItem("Show...");
		show.addActionListener((evt) -> listener.popup());
		menu.add(show);

		menu.addSeparator();

		final MenuItem settings = new MenuItem("Settings...");
		settings.addActionListener((evt) -> listener.settings());
		menu.add(settings);

//		final MenuItem pause = new MenuItem("Pause...");
//		pause.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				listener.pause();
//			}
//		});
//		menu.add(pause);

		menu.addSeparator();

		final MenuItem quit = new MenuItem("Quit");
		quit.addActionListener(evt -> listener.quit());
		menu.add(quit);

		trayIcon.setPopupMenu(menu);
		
		trayIcon.addActionListener((evt) -> listener.popup());

		st.add(trayIcon);
	}

	public static void close() {
		final SystemTray st = SystemTray.getSystemTray();
		for (TrayIcon i : st.getTrayIcons()) {
			st.remove(i);
		}
	}
}
