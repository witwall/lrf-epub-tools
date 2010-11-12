package lrf.pdf.flow;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;

import lrf.html.HtmlDoc;
import lrf.html.HtmlStyle;
import lrf.html.StyleItem;

import org.apache.pdfbox.util.TextPosition;

/**
 * Representa un texto en una posicion con una metrica determinada Debe guardar
 * informacion del estado de todos los estilos involucrados Mantiene informacion
 * de con qué otros textos está unido por los puntos cardinales. En general se
 * desecharan los textos que no vayan unidos al menos a otro texto, para evitar
 * mostrar headers y footers.
 * 
 * @author elinares
 * 
 */
public class TextPiece extends Piece {
	String txt;
	Color color = Color.black;
	static int maxUsedFS = -1;
	static float maxUsedKey = -1;
	public static boolean adjusted = false;
	public static Hashtable<Float, Integer> fszs = new Hashtable<Float, Integer>();

	float fontSize;
	String fontName;
	boolean bold = false, italic = false, plain = false;

	public TextPiece(int np, TextPosition tp) {
		Rectangle2D.Float r = new Rectangle2D.Float();
		r.setRect(0, 0, tp.getWidth(), tp.getHeight());
		init(np, tp.getX(), tp.getY(), r);
		txt = tp.getCharacter();
		fontSize = tp.getFontSize();
		fontName = tp.getFont().getBaseFont();
		if (fontName.contains("Bold"))
			bold = true;
		if (fontName.contains("Italic"))
			italic = true;
		if (bold || italic)
			plain = false;
		if (fszs.get(fontSize) == null)
			fszs.put(fontSize, 1);
		else
			fszs.put(fontSize, 1 + fszs.get(fontSize));

	}

	public void append(TextPiece add){
		txt+=add.txt;
		rect=recubre(rect,add.rect);
	}
	
	public boolean isSameStyle(TextPiece tp) {
		if (color.getRGB() != tp.color.getRGB())
			return false;
		if (fontSize != tp.fontSize)
			return false;
		if (!fontName.equals(tp.fontName))
			return false;
		return true;
	}

	public void adjustFS() {
		for (float p : fszs.keySet()) {
			int used = fszs.get(p);
			if (maxUsedFS < used) {
				maxUsedFS = used;
				maxUsedKey = p;
			}
		}
		adjusted = true;
	}

	public HtmlStyle getHtmlStyles() {
		HtmlStyle ret = new HtmlStyle(new StyleItem(StyleItem.color,
				color.getRGB()));
		ret.add(new StyleItem(StyleItem.fsize, fontSize));
		if (italic)
			ret.add(new StyleItem(StyleItem.fstyl, "italic"));
		if (bold)
			ret.add(new StyleItem(StyleItem.fweig, "bold"));
		return ret;
	}

	@Override
	public void emitHTML(HtmlDoc doc) {
		if (!adjusted)
			adjustFS();
		if ((isHead && numPage > 1) || (isFoot && numPage > 1))
			return;
		if (isStartOfParagraph) {
			doc.setTemporaryStyle(new StyleItem(StyleItem.tinde, "1em", false));
			switch (position) {
			case 0:
				doc.setTemporaryStyle(new StyleItem(StyleItem.talig, "left",
						false));
				break;
			case 1:
				doc.setTemporaryStyle(new StyleItem(StyleItem.talig, "center",
						false));
				break;
			case 2:
				doc.setTemporaryStyle(new StyleItem(StyleItem.talig, "right",
						false));
				break;
			case 3:
				doc.setTemporaryStyle(new StyleItem(StyleItem.talig, "justify",
						false));
				break;
			}
			doc.newParagraph();
		}
		doc.setTemporaryStyle(new StyleItem(StyleItem.color, "#"
				+ Integer.toHexString(color.getRGB()).substring(2), false));
		float fs = Math.round((float) fontSize / maxUsedKey * 80) / 100F;
		doc.setTemporaryStyle(new StyleItem(StyleItem.fsize, "" + fs + "em",
				false));
		if (plain)
			doc.setTemporaryStyle(new StyleItem(StyleItem.fstyl, "normal",
					false));
		else if (italic)
			doc.setTemporaryStyle(new StyleItem(StyleItem.fstyl, "italic",
					false));
		if (bold)
			doc.setTemporaryStyle(new StyleItem(StyleItem.fweig, "bold", false));
		else
			doc.setTemporaryStyle(new StyleItem(StyleItem.fweig, "normal",
					false));
		doc.emitText(txt);
		if (isEndOfParagraph) {
			doc.closeDiv();
		}
		doc.setTemporaryStyle(new StyleItem(StyleItem.pbbef, "avoid"));
	}

	public boolean sameStyles(TextPiece other) {
		if (fontSize != other.fontSize)
			return false;
		if (italic != other.italic)
			return false;
		if (other.isStartOfParagraph)
			return false;
		if (color.getRGB() != other.color.getRGB())
			return false;
		if (bold != other.bold)
			return false;
		if (plain != other.plain)
			return false;
		return true;
	}
}
