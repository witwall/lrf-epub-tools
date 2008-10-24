package lrf.objects;

import lrf.buffer.Reader;
import lrf.conv.Renderer;
import lrf.parse.ParseException;

public class Header extends BBObj {

	@Override
	public void render(Renderer pars)
			throws Exception {
		pars.push(this);
		pars.setHeader(true);
		itRendTags(pars);
		pars.setHeader(false);
	}
	public Header(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_Header, pb);
	}
}
