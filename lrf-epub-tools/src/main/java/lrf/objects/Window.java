package lrf.objects;

import lrf.buffer.Reader;
import lrf.parse.ParseException;

public class Window extends BBObj {

	public Window(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_Window, pb);
	}
}
