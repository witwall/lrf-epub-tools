package lrf.pdf;

import java.awt.Image;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.Document;

public class SVGHook extends SVGGraphics2D {

	//final static int yoff=44; 
	final static int yoff=20; 
	
	@Override
	public void drawPolygon(Polygon p) {
		// TODO Auto-generated method stub
		super.drawPolygon(p);
	}

	@Override
	public void draw(Shape s) {
		if(s instanceof GeneralPath){
			GeneralPath gp=(GeneralPath)s;
			gp.transform(AffineTransform.getTranslateInstance(0, -yoff));
		}
		super.draw(s);
	}

	@Override
	public void fill(Shape s) {
		if(s instanceof GeneralPath){
			GeneralPath gp=(GeneralPath)s;
			gp.transform(AffineTransform.getTranslateInstance(0, -yoff));
		}
		super.fill(s);
	}

	@Override
	public void fillPolygon(Polygon p) {
		// TODO Auto-generated method stub
		super.fillPolygon(p);
	}

	public SVGHook(Document domFactory) {
		super(domFactory);
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		// TODO Auto-generated method stub
		y-=yoff;
		super.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		y-=yoff;
		super.drawGlyphVector(g, x, y);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		// TODO Auto-generated method stub
		y1-=yoff;
		y2-=yoff;
		super.drawLine(x1, y1, x2, y2);
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		y-=yoff;
		super.drawOval(x, y, width, height);
	}

	@Override
	public void drawPolygon(int[] points, int[] points2, int points3) {
		// TODO Auto-generated method stub
		super.drawPolygon(points, points2, points3);
	}

	@Override
	public void drawPolyline(int[] points, int[] points2, int points3) {
		// TODO Auto-generated method stub
		for(int i=0;i<points2.length;i++)
			points2[i]-=yoff;
		super.drawPolyline(points, points2, points3);
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		y-=yoff;
		super.drawRect(x, y, width, height);
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		// TODO Auto-generated method stub
		y-=yoff;
		super.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		// TODO Auto-generated method stub
		y-=yoff;
		super.drawString(iterator, x, y);
	}

	@Override
	public void drawString(String str, int x, int y) {
		// TODO Auto-generated method stub
		y-=yoff;
		super.drawString(str, x, y);
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		// TODO Auto-generated method stub
		y-=yoff;
		super.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		y-=yoff;
		super.fillOval(x, y, width, height);
	}

	@Override
	public void fillPolygon(int[] points, int[] points2, int points3) {
		// TODO Auto-generated method stub
		for(int i=0;i<points2.length;i++)
			points2[i]-=yoff;
		super.fillPolygon(points, points2, points3);
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		y-=yoff;
		super.fillRect(x, y, width, height);
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		// TODO Auto-generated method stub
		y-=yoff;
		super.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public void drawString(AttributedCharacterIterator ati, float x, float y) {
		y-=yoff;
		super.drawString(ati, x, y);
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		// TODO Auto-generated method stub
		xform.translate(0, yoff*21/14);
		return super.drawImage(img, xform, obs);
	}

}
