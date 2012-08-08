package dt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

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
			ret = new SimpleDateFormat("yyyyMMdd-hhmmss");
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
}
