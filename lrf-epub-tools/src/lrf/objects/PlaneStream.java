package lrf.objects;

import lrf.buffer.Reader;
import lrf.parse.ParseException;

public class PlaneStream extends BBObj {

	public PlaneStream(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_PlaneStream, pb);
	}
}
