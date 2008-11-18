package lrf.pdf.flow;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class ImagePiece extends Piece {
	public ImagePiece(int np,float x, float y, Image img, AffineTransform at){
		super(
			np,
			0,
			0,
			new Rectangle2D.Float(
				(float)(at==null? x :at.getScaleX()*x+at.getShearX()*y+at.getTranslateX()),
				(float)(at==null? y :at.getShearY()*x+at.getScaleY()*y+at.getTranslateY()),
				(float)(at==null? img.getWidth(null) :at.getScaleX()*img.getWidth(null)),
				(float)(at==null? img.getHeight(null):at.getScaleY()*img.getHeight(null))
			)
		);
	}
	
}
