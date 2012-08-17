package dt;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleFilter implements Notes.Filter {

	final HashMap<String, AtomicLong> durationByDay = new HashMap<String, AtomicLong>();
	final TreeSet<String> allTags = new TreeSet<String>();
	private final HashSet<String> excludes;
	private final HashSet<String> includes;

	public SimpleFilter(HashSet<String> includes, HashSet<String> excludes) {
		this.includes = includes;
		this.excludes = excludes;
	}

	@Override
	public void note(Date timestamp, long duration, String[] tags, String note) {
		if (tags.length == 0) {
			return;
		}

		if (!checkTags(tags)) {
			return;
		}

		System.out.println(Util.formatDate(timestamp) + ", " + Util.formatDuration(duration) + ", " + Arrays.toString(tags) + " => " + note);

		for (String tag : tags) {
			allTags.add(tag);
		}

		// get the day in local time
		final String day = Util.formatDay(timestamp);
		AtomicLong t = durationByDay.get(day);
		if (t == null) {
			t = new AtomicLong(0);
			durationByDay.put(day, t);
		}
		t.addAndGet(duration);
	}

	private boolean checkTags(String[] tags) {
		boolean oke = true;
		for (String t : tags) {
			if (excludes.contains(t)) {
				oke = false;
				break;
			}
		}

		if (includes.isEmpty()) {
			return oke;
		}

		boolean oki = false;
		for (String t : tags) {
			if (includes.contains(t)) {
				oki = true;
			}
		}

		return oke && oki;
	}

	public static void main(String[] args) throws IOException {
		final HashSet<String> includes = new HashSet<String>();
		final HashSet<String> excludes = new HashSet<String>();

		for (String s : args) {
			if (s.startsWith("-")) {
				excludes.add(s.substring(1));
			} else if (s.startsWith("+")) {
				includes.add(s.substring(1));
			} else {
				System.out.println("Ignoring " + s);
			}
		}

		final File file = new File(System.getProperty("user.home"), "discretetime");
		final Notes notes = new Notes(file);
		final SimpleFilter filter = new SimpleFilter(includes, excludes);
		notes.trawl(filter);
		for (Map.Entry<String, AtomicLong> e : filter.durationByDay.entrySet()) {
			System.out.println(e.getKey() + ": " + Util.formatDuration(e.getValue().get()));
		}

		System.out.println("Tags: " + Arrays.toString(filter.allTags.toArray()));
	}
}
