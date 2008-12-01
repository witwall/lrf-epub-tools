package lrf.pdf.flow;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import lrf.html.HtmlDoc;

public class ImagePiece extends Piece {

	BufferedImage im;
	static int imNum=1;
	public ImagePiece(int np,float x, float y, Image img, AffineTransform at){
		init(
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
		BufferedImage bim=(BufferedImage)img;
		int w=(int)(bim.getWidth()*at.getScaleX());
		int h=(int)(bim.getHeight()*at.getScaleY());
		Image scaledImage=bim.getScaledInstance(
				w,
				h,
				Image.SCALE_SMOOTH
				);
		 im=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		 Graphics2D g2d=im.createGraphics();
		 g2d.drawImage(scaledImage, null, null);
		 g2d.dispose();
	}

	@Override
	public void emitHTML(HtmlDoc doc) {
		try {
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			ImageIO.write(im, "PNG", baos);
			doc.addImage(imNum++, im.getWidth(null), im.getHeight(null), ".png", baos.toByteArray());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
