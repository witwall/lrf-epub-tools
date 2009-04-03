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
		
		int i;
		for(i=0;i<tags.size();i++)
			if(tags.elementAt(i).getType()==123)//ChildPageTree
				break;
		Tag cptTag=tags.elementAt(i);
		
		int cpt=cptTag.getValueAt(0); //ChildPageTree
		
		padre.getObject(cpt).render(pars);
		pars.pop();
		pars.forceNewParagraph();
	}
}
