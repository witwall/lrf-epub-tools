package lrf.pdf.flow;

import java.awt.geom.Rectangle2D;

import lrf.conv.BaseRenderer;
import lrf.html.HtmlDoc;
import lrf.html.StyleItem;

public class PageBreakPiece extends Piece {
	public PageBreakPiece(int numPage){
		this.numPage=numPage;
		this.rect=new Rectangle2D.Float(-1,-1,1,1);
	}
	@Override
	public void emitHTML(HtmlDoc doc) {
		if(numPage==1)
			return;
		if(!BaseRenderer.noPageBreakEmit)
			doc.setTemporaryStyle(new StyleItem(StyleItem.pbbef,"always"));
	}

}
