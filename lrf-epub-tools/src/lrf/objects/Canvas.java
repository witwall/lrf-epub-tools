package lrf.objects;

import lrf.buffer.Reader;
import lrf.conv.Renderer;
import lrf.parse.ParseException;

public class Canvas extends BBObj {

	@Override
	public void render(Renderer pars)
			throws Exception {
		pars.push(this);
		itRendTags(pars);
		pars.pop();
	}

	public Canvas(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_Canvas, pb);
		// TODO Auto-generated constructor stub
	}
}
