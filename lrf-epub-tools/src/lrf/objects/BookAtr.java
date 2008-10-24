package lrf.objects;

import lrf.buffer.Reader;
import lrf.conv.Renderer;
import lrf.objects.tags.Tag;
import lrf.parse.ParseException;

public class BookAtr extends BBObj {

	public BookAtr(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_BookAtr, pb);

	}

	@Override
	public void render(Renderer pars) throws Exception {
		//Inicializamos valores estaticos de los tags
		Tag.currentBaseLineSkip=-1;
		Tag.currentFontSize=-1;
		pars.push(this);
		int cpt=getTagAt(0).getValueAt(0); //ChildPageTree
		padre.getObject(cpt).render(pars);
		pars.pop();
		pars.forceNewParagraph();
	}
}
