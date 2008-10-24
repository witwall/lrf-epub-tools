package lrf.objects;

import lrf.buffer.Reader;
import lrf.parse.ParseException;

public class MiniPage extends BBObj {

	public MiniPage(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_MiniPage, pb);
	}
}
