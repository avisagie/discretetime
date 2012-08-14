package dt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {
	private final File file;

	public Settings(File file) throws IOException {
		this.file = file;
		load();
	}

	private static final String DELAY_MINUTES = "delayMinutes";
	private int delayMinutes = 10;

	public int getDelayMinutes() {
		return delayMinutes;
	}

	public void setDelayMinutes(int delayMinutes) {
		this.delayMinutes = delayMinutes;
	}

	/**
	 * Saves to the file where it was last loaded from.
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException {
		final Properties props = new Properties();
		props.put(DELAY_MINUTES, Integer.toString(delayMinutes));

		final FileOutputStream out = new FileOutputStream(file);
		try {
			props.storeToXML(out, "");
			out.getChannel().force(true);
		} finally {
			Util.close(out);
		}
	}

	public void load() throws IOException {
		if (!file.isFile())
			return;

		final Properties props = new Properties();
		final FileInputStream in = new FileInputStream(file);
		try {
			props.loadFromXML(in);
		} finally {
			Util.close(in);
		}

		String tmp = (String) props.getProperty(DELAY_MINUTES);
		delayMinutes = tmp == null ? delayMinutes : Integer.parseInt(tmp);
	}

	public File getFile() {
		return file;
	}
}
