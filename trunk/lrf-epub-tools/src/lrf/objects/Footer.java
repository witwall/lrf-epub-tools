package lrf.objects;

import lrf.buffer.Reader;
import lrf.conv.Renderer;
import lrf.parse.ParseException;

public class Footer extends BBObj {

	@Override
	public void render(Renderer pars)
			throws Exception {
		pars.push(this);
		pars.setFooter(true);
		itRendTags(pars);
		pars.setFooter(false);
	}

	public Footer(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_Footer, pb);
	}
}
