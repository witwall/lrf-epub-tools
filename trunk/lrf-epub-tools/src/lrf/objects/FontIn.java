package lrf.objects;

import lrf.buffer.Reader;
import lrf.parse.ParseException;

public class FontIn extends BBObj {

	public FontIn(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_Font, pb);
		// TODO Auto-generated constructor stub
	}
}
