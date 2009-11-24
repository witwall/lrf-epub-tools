package lrf.objects;

import lrf.buffer.Reader;
import lrf.parse.ParseException;

public class Sound extends BBObj {

	public Sound(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_Sound, pb);
	}
}
