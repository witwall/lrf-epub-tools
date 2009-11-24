package lrf.objects;

import lrf.buffer.Reader;
import lrf.parse.ParseException;

public class ParagraphAtr extends BBObj {

	public ParagraphAtr(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_ParagraphAtr, pb);
	}
}
