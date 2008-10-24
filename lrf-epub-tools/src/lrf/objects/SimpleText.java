package lrf.objects;

import lrf.buffer.Reader;
import lrf.parse.ParseException;

public class SimpleText extends BBObj {

	public SimpleText(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_SimpleText, pb);
	}
}
