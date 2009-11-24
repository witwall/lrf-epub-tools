package lrf.objects;

import lrf.buffer.Reader;
import lrf.conv.Renderer;
import lrf.parse.ParseException;

public class Block extends BBObj {

	@Override
	public void render(Renderer pars)
			throws Exception {
		pars.push(this);
		if(pars.isFooter()){
			pars.newParagraph();
			itRendTags(pars);
			pars.setFooter();
			pars.newParagraph();
		}else if( pars.isHeader()){
			pars.newParagraph();
			itRendTags(pars);
			pars.setHeader();
			pars.newParagraph();
		}else{
			itRendTags(pars);
			pars.newParagraph();
		}
		pars.pop();
	}

	public Block(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_Block, pb);
	}
}
