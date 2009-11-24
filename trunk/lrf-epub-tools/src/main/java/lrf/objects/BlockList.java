package lrf.objects;

import lrf.buffer.Reader;
import lrf.parse.ParseException;

public class BlockList extends BBObj {

	public BlockList(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_BlockList, pb);
		// TODO Auto-generated constructor stub
	}
}
