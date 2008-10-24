package lrf.objects;

import lrf.buffer.Reader;
import lrf.conv.Renderer;
import lrf.objects.tags.Tag;
import lrf.parse.ParseException;

public class PageTree extends BBObj {

	@Override
	public void render(Renderer pars) throws Exception {
		Tag refList = getTagAt(0);
		itRendListRef(pars, refList);
	}

	public PageTree(Book b, int objectID, Reader pb)
			throws ParseException {
		super(b, objectID, ot_PageTree, pb);
	}
}
