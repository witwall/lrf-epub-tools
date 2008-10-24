package lrf.objects;

import lrf.buffer.Reader;
import lrf.parse.ParseException;

public class PopUpWindow extends BBObj {

	public PopUpWindow(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_PopUpWin, pb);
	}
}
