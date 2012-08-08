package dt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Date;

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
			out.print(Util.formatDate(timestamp));
			out.print(',');
			out.println(note.replace('\n', ';').replace("\r", ""));
		} finally {
			out.flush();
			st.getChannel().force(true);
			out.close();
		}
	}
}
