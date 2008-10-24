package lrf.objects;

import lrf.buffer.Reader;
import lrf.parse.ParseException;

public class ImageStream extends BBObj {

	public ImageStream(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_ImageStream, pb);
	}
}
