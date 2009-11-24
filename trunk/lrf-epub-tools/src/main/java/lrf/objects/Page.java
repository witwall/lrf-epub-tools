package lrf.objects;

import lrf.buffer.Reader;
import lrf.conv.BaseRenderer;
import lrf.conv.Renderer;
import lrf.parse.ParseException;

public class Page extends BBObj {
	@Override
	public void render(Renderer pars)
			throws Exception {
		pars.setLocalDestination("P"+getID());			
		pars.resetFooters();
		pars.resetHeaders();
		itRendTags(pars);
		if(!BaseRenderer.noPageBreakEmit)
			pars.newPage(getPadre().LRFPageSize);
		pars.pop();
	}

	public Page(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_Page, pb);
	}
}
