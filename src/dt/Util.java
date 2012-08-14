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

	private static ThreadLocal<SimpleDateFormat> dateFmt = new ThreadLocal<SimpleDateFormat>();

	private static final SimpleDateFormat dateFmt() {
		SimpleDateFormat ret = dateFmt.get();
		if (ret == null) {
			ret = new SimpleDateFormat("yyyyMMdd-HHmmss");
			dateFmt.set(ret);
		}
		return ret;
	}

	public static String formatDate(Date date) {
		return dateFmt().format(date);
	}

	public static Date parseDate(String date) throws ParseException {
		return dateFmt().parse(date);
	}

	
	private static ThreadLocal<SimpleDateFormat> dayFmt = new ThreadLocal<SimpleDateFormat>();

	private static final SimpleDateFormat dayFmt() {
		SimpleDateFormat ret = dayFmt.get();
		if (ret == null) {
			ret = new SimpleDateFormat("yyyyMMdd");
			dayFmt.set(ret);
		}
		return ret;
	}

	public static String formatDay(Date date) {
		return dayFmt().format(date);
	}

	public static Date parseDay(String date) throws ParseException {
		return dayFmt().parse(date);
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

	public static String formatDuration(long duration) {
//		final long millis = duration % 1000; 
		duration /= 1000;
		final long seconds = duration % 60;
		duration /= 60;
		final long minutes = duration % 60; duration /= 60;
		final long hours = duration % 60; duration /= 60;
		final StringBuilder sb = new StringBuilder();
		if (hours > 0) sb.append(hours).append('h'); 
		if (minutes > 0) sb.append(minutes > 9 ? "" : '0').append(minutes).append('m');
		if (seconds > 0) sb.append(seconds > 9 ? "" : '0').append(seconds).append('s');
		return sb.toString();
	}
}
