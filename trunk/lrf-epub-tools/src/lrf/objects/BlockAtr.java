package lrf.objects;

import lrf.buffer.Reader;
import lrf.conv.Renderer;
import lrf.parse.ParseException;

public class BlockAtr extends BBObj {

	public BlockAtr(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_BlockAtr, pb);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(Renderer pars)
			throws Exception {
		pars.push(this);
		itRendTags(pars);
	}

}
