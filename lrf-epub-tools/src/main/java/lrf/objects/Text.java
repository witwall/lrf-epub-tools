package lrf.objects;

import lrf.buffer.Reader;
import lrf.conv.Renderer;
import lrf.objects.tags.Tag;
import lrf.parse.ParseException;

public class Text extends BBObj {

	public Text(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_Text, pb);
	}

	@Override
	public void render(Renderer pars)
			throws Exception {
		for (int i = 0; i < getTags().size(); i++) {
			Tag t=getTags().elementAt(i);
			t.render(pars);
		}
		//pars.forceNewParagraph();
		pars.pop();
	}
}
