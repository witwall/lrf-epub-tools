package lrf.objects;

import lrf.buffer.Reader;
import lrf.conv.Renderer;
import lrf.parse.ParseException;

public class PageAtr extends BBObj {

	@Override
	public void render(Renderer pars)
			throws Exception {
		pars.push(this);
		itRendTags(pars);
	}

	public PageAtr(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_PageAtr, pb);
	}
}
