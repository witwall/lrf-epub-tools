package lrf.objects;

import lrf.buffer.Reader;
import lrf.parse.ParseException;

public class Import extends BBObj {

	public Import(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_Import, pb);
	}
}
