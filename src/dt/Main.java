package dt;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import dt.Popup.Listener;

public class Main {
	
	private static final long PERIOD = 10 * 1000l;

	private static final Logger log = Util.getLogger();
	
	private static Popup popup;
	
	private static final Object lock = new Object();
	
	public static void main(String[] args) throws IOException {
		File file = new File(System.getProperty("user.home"), "discretetime");
		log.log(Level.INFO, "Starting in {0}", file);
		final Notes model = new Notes(file);
		popup = new Popup(model, new Listener() {
			@Override public void done() {
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		});
		
		while (true) {
			popup.popup();
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					log.log(Level.WARNING, "Unexpected interrupt", e);
				}
			}
			Util.sleep(PERIOD);
		}
	}
}
