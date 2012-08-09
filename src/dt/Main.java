package dt;

import java.awt.SystemTray;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import dt.Popup.Listener;

public class Main {

	private static final long PERIOD = 15 * 60 * 1000l;

	private static final Logger log = Util.getLogger();

	private static Popup popup;

	private static final Object lock = new Object();

	private static boolean running = true;
	private static final Object sleep = new Object();
	
	public static void main(String[] args) throws IOException {
		File file = new File(System.getProperty("user.home"), "discretetime");
		log.log(Level.INFO, "Starting in {0}", file);
		final Notes model = new Notes(file);
		popup = new Popup(model, new Listener() {
			@Override
			public void done() {
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		});

		if (SystemTray.isSupported()) {
			try {
				Tray.setup(new Tray.Listener() {

					@Override
					public void settings() {
						// TODO Auto-generated method stub
					}

					@Override
					public void quit() {
						synchronized (sleep) {
							running = false;
							sleep.notifyAll();
						}
						synchronized (lock) {
							lock.notifyAll();
						}
					}

					@Override
					public void pause() {
						// TODO Auto-generated method stub
					}
					
					@Override
					public void popup() {
						popup.popup();
					}
				});
			} catch (Exception e) {
				log.log(Level.WARNING, "Tray icon not set", e);
			}
		} else {
			log.warning("SystemTray not available");
		}

		while (running) {
			popup.popup();
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					log.log(Level.WARNING, "Unexpected interrupt", e);
				}
			}
			synchronized (sleep) {
				try {
					if (running) sleep.wait(PERIOD);
				} catch (InterruptedException e) {
					log.log(Level.WARNING, "Unexpected interrupt", e);
				}
			}
		}
		
		popup.dispose();
		System.exit(0);
	}
}
