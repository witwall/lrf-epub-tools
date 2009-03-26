package lrf.pdf.flow;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import lrf.html.HtmlDoc;

public class ImagePiece extends Piece {

	BufferedImage im;
	static int imNum=1;
	
	
	public ImagePiece(int np,float x, float y, Image img, AffineTransform at){
		int imgHe=img.getHeight(null);
		int imgWi=img.getWidth(null);
		float sx=1,sy=1;
		if(imgHe>800)
			sy=800f/imgHe;
		if(imgWi>600)
			sx=600f/imgWi;
		float mins=Math.min(sx,sy);
		at=new AffineTransform(mins,0,0,mins,0,0);
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
		AffineTransformOp atop=new AffineTransformOp(at,null);
		im=atop.filter(bim, null);
		double xx=at.getTranslateX();
		double yy=at.getTranslateY();
		int w=(int)(bim.getWidth()*Math.abs(at.getScaleX()));
		int h=(int)(bim.getHeight()*Math.abs(at.getScaleY()));
		if(xx+w>im.getWidth())
			xx-=w;
		if(yy+h>im.getHeight())
			yy-=h;
		im=im.getSubimage((int)xx, (int)yy, w, h);
		/*
		Image scaledImage=aux.getScaledInstance(w,h,Image.SCALE_SMOOTH);
		im=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d=im.createGraphics();
		g2d.drawImage(scaledImage, null, null);
		g2d.dispose();
		*/
	}

	@Override
	public void emitHTML(HtmlDoc doc) {
		try {
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			ImageIO.write(im, "jpeg", baos);
			doc.addImage(imNum++, im.getWidth(null), im.getHeight(null), ".jpg", baos.toByteArray());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
