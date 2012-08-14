package dt;

import java.awt.Image;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class Util {
	private static final Logger log = getLogger();

	public static Logger getLogger() {
		final Exception ex = new Exception();
		return Logger.getLogger(ex.getStackTrace()[1].getClassName());
	}

	private static ThreadLocal<SimpleDateFormat> fmt = new ThreadLocal<SimpleDateFormat>();

	private static final SimpleDateFormat dateFmt() {
		SimpleDateFormat ret = fmt.get();
		if (ret == null) {
			ret = new SimpleDateFormat("yyyyMMdd-HHmmss");
			fmt.set(ret);
		}
		return ret;
	}

	public static String formatDate(Date date) {
		return dateFmt().format(date);
	}

	public static Date parseDate(String date) throws ParseException {
		return dateFmt().parse(date);
	}

	public static void sleep(long period) {
		try {
			Thread.sleep(period);
		} catch (InterruptedException e) {
			log.log(Level.WARNING, "Unexpected interrupt", e);
		}
	}

	public static Image loadImage(String img) throws IOException {
		final InputStream in = ClassLoader.getSystemResourceAsStream(img);
		try {
			if (in != null) {
				return ImageIO.read(in);
			} else {
				throw new FileNotFoundException("Could not find " + img);
			}
		} finally {
			close(in);
		}
	}
	
	public static void close(Closeable c) {
		try {
			if (c != null) c.close();
		} catch (Exception e) {
			// ignore
		}
	}
}
