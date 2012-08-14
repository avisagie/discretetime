package dt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Notes {

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
		if (!zero) sb.setLength(sb.length()-1); // whack the last space
		return sb;
	}

	public static String extractTags(String note) {
		final StringBuilder sb = new StringBuilder();
		return extractTags(sb, note).toString();
	}
}
