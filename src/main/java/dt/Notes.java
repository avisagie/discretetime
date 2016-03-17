package dt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Notes {

	public interface Filter {
		void note(Date timestamp, long duration, String[] tags, String note);
	}

	private static final String[] EMPTY_ARRAY = new String[0];
	private static final Charset UTF8 = Charset.forName("utf8");
	private final File notesFile;

	public Notes(File file) throws IOException {
		if (!file.isDirectory()) {
			if (!file.mkdir()) {
				throw new IOException("Could not create " + file);
			}
		}

		notesFile = new File(file, "notes.utf8.txt");
	}

	public void addNote(Date timestamp, String note) throws IOException {
		final FileOutputStream st = new FileOutputStream(notesFile, true);
		final PrintWriter out = new PrintWriter(new OutputStreamWriter(st, UTF8));
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(Util.formatDate(timestamp)).append(',');
			extractTags(sb, note).append(',');
			sb.append(note.replace("\n", "\\n").replace("\r", "").replace(",", "\\c"));
			out.println(sb.toString());
		} finally {
			out.flush();
			st.getChannel().force(true);
			out.close();
		}
	}

	private static final Pattern tags = Pattern.compile(":\\S+");

	public static StringBuilder extractTags(StringBuilder sb, String note) {
		final Matcher m = tags.matcher(note);
		boolean zero = true;
		while (m.find()) {
			zero = false;
			sb.append(m.group()).append(' ');
		}
		if (!zero)
			sb.setLength(sb.length() - 1); // whack the last space
		return sb;
	}

	public static String extractTags(String note) {
		final StringBuilder sb = new StringBuilder();
		return extractTags(sb, note).toString();
	}

	public void trawl(Filter filter) throws IOException {
		final BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream(notesFile)));
		String line;
		Date prevDate = null;
		try {
			while ((line = rdr.readLine()) != null) {
				int start = 0;
				int pos = line.indexOf(',', start);
				if (pos < 0)
					continue;
				final Date timestamp;
				try {
					timestamp = Util.parseDate(line.substring(start, pos));
				} catch (ParseException e) {
					continue;
				}
				if (prevDate == null)
					prevDate = timestamp;

				start = pos + 1;
				pos = line.indexOf(',', start);
				if (pos < 0)
					continue;
				final String trim = line.substring(start, pos).trim();
				final String[] tags = trim.isEmpty() ? EMPTY_ARRAY : trim.split("\\s+");

				start = pos + 1;
				final String note = line.substring(start);

				long duration = timestamp.getTime() - prevDate.getTime();

				filter.note(timestamp, duration, tags, note);

				prevDate = timestamp;
			}
		} finally {
			Util.close(rdr);
		}
	}
}
