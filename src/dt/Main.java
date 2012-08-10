package dt;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import dt.Popup.Listener;

public class Main {

	private static long period = 10 * 60 * 1000l;

	private static final Logger log = Util.getLogger();

	private static Popup popup = null;

	private static final Object lock = new Object();

	private static boolean userPopup = false;

	private static boolean running = true;
	private static final Object sleep = new Object();

	public static void main(String[] args) throws IOException {
		if (System.getProperty("period") != null) {
			period = Long.parseLong(System.getProperty("period"));
		}

		try {
			setup();
			timingLoop();
		} catch (Throwable e) {
			log.log(Level.SEVERE, "Error:", e);
		} finally {
			if (popup != null) {
				popup.dispose();
			}
			if (SystemTray.isSupported()) {
				Tray.close();
			}
		}
	}

	private static void setup() throws IOException {
		final File file = new File(System.getProperty("user.home"),
				"discretetime");
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

		new Thread("StartupSystemTrayAdder") {
			{
				setDaemon(true);
			}

			@Override
			public void run() {
				loopyAddToSystemTray();
			}
		}.start();
	}

	// deal with windows startup issues in the background
	private static void loopyAddToSystemTray() {
		do {
			try {
				addToSystemTray();
				break;
			} catch (Exception ex) {
				log.log(Level.FINE, "Could not get to SystemTray:", ex);
			}
			Util.sleep(5000);
		} while (true);
	}

	private static void addToSystemTray() throws IOException, AWTException {
		if (SystemTray.isSupported()) {
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
					synchronized (sleep) {
						userPopup = true;
						sleep.notify();
					}
				}
			});
		} else {
			log.warning("SystemTray not available");
		}
	}

	private static void timingLoop() {
		while (running) {
			popup.popup();
			userPopup = false;

			// wait for the user to close the window
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					log.log(Level.WARNING, "Unexpected interrupt", e);
				}
			}

			// Wait until it is time to come up again
			final long start = System.currentTimeMillis();
			while (System.currentTimeMillis() - start < period && running
					&& !userPopup) {
				synchronized (sleep) {
					try {
						if (running) {
							sleep.wait(1000);
						}
					} catch (InterruptedException e) {
						log.log(Level.WARNING, "Unexpected interrupt", e);
					}
				}
			}
		}
	}
}
