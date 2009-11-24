package lrf.objects;

import lrf.buffer.Reader;
import lrf.conv.Renderer;
import lrf.parse.ParseException;

public class TextAtr extends BBObj {

	public TextAtr(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_TextAtr, pb);
	}

	@Override
	public void render(Renderer pars)
			throws Exception {
		pars.push(this);
		itRendTags(pars);
	}
}
