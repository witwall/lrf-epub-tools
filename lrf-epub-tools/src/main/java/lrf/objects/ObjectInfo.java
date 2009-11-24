package lrf.objects;

import lrf.buffer.Reader;
import lrf.parse.ParseException;

public class ObjectInfo extends BBObj {

	public ObjectInfo(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_ObjectInfo, pb);
	}
}
