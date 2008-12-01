package lrf.pdf.flow;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.util.Hashtable;

import lrf.html.HtmlDoc;
import lrf.html.HtmlStyle;
import lrf.html.StyleItem;


/**
 * Representa un texto en una posicion con una metrica determinada
 * Debe guardar informacion del estado de todos los estilos involucrados
 * Mantiene informacion de con qué otros textos está unido por los 
 * puntos cardinales.
 * En general se desecharan los textos que no vayan unidos al menos a otro
 * texto, para evitar mostrar headers y footers.
 * @author elinares
 *
 */
public class TextPiece extends Piece {
	String txt;
	Font font;
	Color color;
	int maxUsedFS=-1;
	int maxUsedKey=-1;
	boolean adjusted=false;
	public static Hashtable<Integer,Integer> fszs=new Hashtable<Integer,Integer>();
	
	public boolean isSameStyle(TextPiece tp){
		if(color.getRGB()!=tp.color.getRGB())
			return false;
		if(font.getSize()!=tp.font.getSize())
			return false;
		if(!font.getFontName().equals(tp.font.getFontName()))
			return false;
		return true;
	}
	
	public void adjustFS(){
		for(int p:fszs.keySet()){
			int used=fszs.get(p);
			if(maxUsedFS<used){
				maxUsedFS=used;
				maxUsedKey=p;
			}
		}
		adjusted=true;
	}
	
	public TextPiece(
			int np,float x, float y, String txt, Font _font, Color _color, FontRenderContext frc){
		init(np,x,y,_font.getStringBounds(txt, frc));
		font=_font;
		color=_color;
		this.txt=txt;
		if(fszs.get(font.getSize())==null)
			fszs.put(font.getSize(), 1);
		else
			fszs.put(font.getSize(), 1+fszs.get(font.getSize()));
	}

	public HtmlStyle getHtmlStyles(){
		HtmlStyle ret=new HtmlStyle(new StyleItem(StyleItem.color,color.getRGB()));
		ret.add(new StyleItem(StyleItem.fsize,font.getSize()));
		if(font.isItalic())
			ret.add(new StyleItem(StyleItem.fstyl,"italic"));
		if(font.isBold())
			ret.add(new StyleItem(StyleItem.fweig,"bold"));
		return ret;
	}

	@Override
	public void emitHTML(HtmlDoc doc) {
		if(!adjusted)
			adjustFS();
		if( (isHead && numPage>1) || (isFoot && numPage>1) )
			return;
		if(isStartOfParagraph){
			doc.setTemporaryStyle(new StyleItem(StyleItem.tinde,"1em",false));
			switch(position){
			case 0: doc.setTemporaryStyle(new StyleItem(StyleItem.talig,"left",false)); break;
			case 1: doc.setTemporaryStyle(new StyleItem(StyleItem.talig,"center",false)); break;
			case 2: doc.setTemporaryStyle(new StyleItem(StyleItem.talig,"right",false)); break;
			case 3: doc.setTemporaryStyle(new StyleItem(StyleItem.talig,"justify",false)); break;
			}
		}
		doc.setTemporaryStyle(new StyleItem(StyleItem.color,"#"+color.getRGB(),false));
		float fs=Math.round((float)font.getSize()/maxUsedKey*100)/100;
		doc.setTemporaryStyle(new StyleItem(StyleItem.fsize,""+fs+"em",false));
		if(font.isItalic())
			doc.setTemporaryStyle(new StyleItem(StyleItem.fstyl,"italic",false));
		if(font.isBold())
			doc.setTemporaryStyle(new StyleItem(StyleItem.fweig,"bold",false));
		doc.emitText(txt);
		if(isEndOfParagraph){
			doc.closeDiv();
		}
	}
}

