package lrf.pdf.flow;

import java.awt.geom.Rectangle2D;
import java.util.Hashtable;

import lrf.html.HtmlDoc;

public abstract class Piece implements Comparable<Piece> {
	double horizAdj;
	boolean isEndOfParagraph = false;

	boolean isFoot = false;
	boolean isHead = false;
	boolean isStartOfParagraph = false;
	int numPage;
	public static Hashtable<Integer, Rectangle2D.Float> 
		pageBoundaries=new Hashtable<Integer, Rectangle2D.Float>();

	/**
	 * means 0 left, 1 center, 2 right ,3 justify on the x coordinate
	 */
	int position = 1;

	Rectangle2D.Float rect;

	@Override
	public int compareTo(Piece o) {
		if (numPage < o.numPage)
			return -1;
		if (numPage > o.numPage)
			return 1;
		if (Math.abs(getY() - o.getY()) > 0) {
			if (getY() < o.getY())
				return -1;
			else
				return 1;
		}
		if (rect.getX() < o.rect.getX())
			return -1;
		if (rect.getX() > o.rect.getX())
			return 1;
		return 0;
	}

	public abstract void emitHTML(HtmlDoc doc);

	public float getHeight() {
		return (float) rect.getHeight();
	}

	public float getWidth() {
		return (float) rect.getWidth();
	}

	public float getX() {
		return (float) (rect.getX());
	}

	public float getY() {
		return (float) (rect.getY());
	}

	public void hPos(double lBorder, double rBorder) {
		if (isCentered())
			position = 1;
		else if (isLeftJustified())
			position = 0;
		else if (isRightJustified())
			position = 2;
		else
			position = 3;
	}

	public void init(int np, float x, float y, Rectangle2D.Float r) {
		this.rect = (Rectangle2D.Float) r.clone();
		rect.setRect(x + rect.getX(), y + rect.getY(), rect.getWidth(), rect.getHeight());
		numPage = np;
		
		Rectangle2D.Float rp=pageBoundaries.get(np);
		if(rp==null)
			pageBoundaries.put(np, rect);
		else{
			pageBoundaries.put(np, recubre(rp,rect));
		}
	}

	public Rectangle2D.Float recubre(Rectangle2D.Float r1, Rectangle2D.Float r2){
		Rectangle2D.Float ret=new Rectangle2D.Float();
		double r1x1=r1.getX(),r1x2=r1x1+r1.getWidth(),r1y1=r1.getY(),r1y2=r1y1+r1.getHeight();
		double r2x1=r2.getX(),r2x2=r2x1+r2.getWidth(),r2y1=r2.getY(),r2y2=r2y1+r2.getHeight();
		ret.setRect(
				Math.min(r1x1, r2x1),
				Math.min(r1y1, r2y1),
				Math.max(r1x2, r2x2)-Math.min(r1x1, r2x1),
				Math.max(r1y2, r2y2)-Math.min(r1y1, r2y1)
				);
		return ret;
	}
	
	public boolean isCentered() {
		Rectangle2D.Float r=pageBoundaries.get(numPage);
		
		double pieceMin_X = getX(), w = getWidth(), pieceMax_X = pieceMin_X + w;
		double lefM = (double) (pieceMin_X - r.getX());
		double rigM = (double) (r.getX()+r.getWidth() - pieceMax_X);
		
		if(lefM<10 || rigM<10)
			return false;
		
		if(lefM/rigM<1){
			if(lefM/rigM>0.95)
				return true;
		}else{
			if(lefM/rigM<1.05)
				return true;
		}
		return false;
	}

	public boolean isFooter(double yFoot) {
		if (getY() > yFoot)
			return true;
		if (Math.abs(getY() - yFoot) < 0)
			return true;
		return false;
	}

	public boolean isHeader(double yHead) {
		if (getY() < yHead)
			return true;
		if (Math.abs(getY() - yHead) < 0)
			return true;
		return false;
	}

	public boolean isHorizAdjacent(Piece other) {
		if (Math.abs(getY() - other.getY()) > 0) {
			horizAdj = -1;
			return false;
		}
		double tx = getX(), ox = other.getX();
		if (tx < ox) {
			horizAdj = Math.abs(tx + getWidth() - ox);
			if (horizAdj < 0.01)
				return true;
		} else {
			horizAdj = Math.abs(ox + other.getWidth() - tx);
			if (horizAdj < 0.01)
				return true;
		}
		return false;
	}

	public boolean isLeftJustified() {
		Rectangle2D.Float r=pageBoundaries.get(numPage);
		double pieceMin_X = getX();
		double lefM = (double) (pieceMin_X - r.getX());
		if(lefM<10)
			return true;
		return false;
	}

	public boolean beginOnLeftBorder(double leftBorder, double rightBorder) {
		Rectangle2D.Float r=pageBoundaries.get(numPage);
		if (getX() - r.getX() < r.getWidth()/10)
			return true;
		return false;
	}

	public boolean isRightJustified() {
		Rectangle2D.Float r=pageBoundaries.get(numPage);
		double pieceMin_X = getX(), w = getWidth(), pieceMax_X = pieceMin_X + w;
		double rigM = (double) (r.getX()+r.getWidth() - pieceMax_X);
		if(rigM<10)
			return true;
		return false;
	}

	public boolean endOnRightBorder(double leftBorder, double rightBorder) {
		Rectangle2D.Float r=pageBoundaries.get(numPage);
		if ( r.getX()+r.getWidth() - getX() - getWidth() < r.getWidth()/10 )
			return true;
		return false;
	}


	public String toString() {
		if (this instanceof TextPiece)
			return ((TextPiece) this).txt + "(" + getX() + "," + getY() + ","
					+ getWidth() + "," + getHeight() + ")";
		else
			return "(" + getX() + "," + getY() + "," + getWidth() + ","
					+ getHeight() + ")imagen";
	}

	public void vPos(double yHead, double yFoot) {
		if (isHeader(yHead))
			isHead = true;
		if (isFooter(yFoot))
			isFoot = true;
	}
}
