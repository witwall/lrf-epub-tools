package lrf.pdf.flow;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;

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
	
	public TextPiece(
			int np,float x, float y, String txt, Font _font, Color _color, FontRenderContext frc){
		super(np,x,y,_font.getStringBounds(txt, frc));
		font=_font;
		color=_color;
		this.txt=txt;
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
}

