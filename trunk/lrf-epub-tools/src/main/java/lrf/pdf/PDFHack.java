package lrf.pdf;

import java.io.IOException;

import lrf.pdf.flow.Flower;
import lrf.pdf.flow.TextPiece;

import org.apache.pdfbox.pdfviewer.PageDrawer;
import org.apache.pdfbox.util.TextPosition;

public class PDFHack extends PageDrawer{
	Flower flower;
	
	public PDFHack(Flower f) throws IOException {
		super();
		flower=f;
	}

	@Override
	protected void processTextPosition(TextPosition tp) {
		super.processTextPosition(tp);
		flower.addPiece(new TextPiece(flower.pageNumber,tp));
		//System.out.print("\n"+tp.getX()+","+tp.getY()+","+tp.getWidth()+","+tp.getHeight()+":"+tp.getCharacter());
	}
	
}
