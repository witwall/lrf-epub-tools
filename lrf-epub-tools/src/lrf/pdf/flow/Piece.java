package lrf.pdf.flow;

import java.awt.geom.Rectangle2D;

import lrf.html.HtmlDoc;


public abstract class Piece implements Comparable<Piece>{
	static final double htol=100;
	static final double vtol=10;
	Rectangle2D rect;
	int numPage;
	
	boolean isHead=false;
	boolean isFoot=false;
	boolean isEndOfParagraph=false;
	boolean isStartOfParagraph=false;
	
	double horizAdj;

	/**
	 * means 0 left, 1 center, 2 right ,3 justify on the x coordinate
	 */
	int position=1;

	public void init(int np, float x, float y, Rectangle2D r) {
		this.rect=r;
		rect.setRect(x+rect.getX(), y+rect.getY(), rect.getWidth(), rect.getHeight());
		numPage=np;
	}
	public float getX(){
		return (float)(rect.getX());
	}
	public float getY(){
		return (float)(rect.getY());
	}
	public float getWidth(){
		return (float)rect.getWidth();
	}
	public float getHeight(){
		return (float)rect.getHeight();
	}
	@Override
	public int compareTo(Piece o) {
		if(numPage<o.numPage)
			return -1;
		if(numPage>o.numPage)
			return 1;
		if(Math.abs(getY()-o.getY())>vtol){
			if(getY()<o.getY())
				return -1;
			else 
				return 1;
		}
		if(rect.getX()<o.rect.getX())
			return -1;
		if(rect.getX()>o.rect.getX())
			return 1;
		return 0;
	}
	public boolean isHorizAdjacent(Piece other){
		if(Math.abs(getY()-other.getY())>vtol){
			horizAdj=-1;
			return false;
		}
		double tx=getX(),ox=other.getX();
		if(tx<ox){
			horizAdj=Math.abs(tx+getWidth()-ox);
			if(horizAdj<htol)
				return true;
		}else{
			horizAdj=Math.abs(ox+other.getWidth()-tx);
			if(horizAdj<htol)
				return true;
		}
		return false;
	}
	public boolean isCentered(double lb, double rb){
		double x=getX(),w=getWidth(),lx=x+w;
		double lefM=(double)(x-lb);
		double rigM=(double)(rb-lx);
		if( x>lb &&
				lx<rb &&
				Math.abs(lefM-rigM)<70D)
			return true;
		return false;
	}
	public boolean isOnRightBorder(double rightBorder){
		double lx=getX()+getWidth();
		if(lx>rightBorder)
			return true;
		if(Math.abs(lx-rightBorder)<htol)
			return true;
		return false;
	}
	public boolean isOnLeftBorder(double leftBorder){
		if(getX()<leftBorder)
			return true;
		if(Math.abs(getX()-leftBorder)<htol)
			return true;
		return false;
	}
	public boolean isHeader(double yHead){
		if(getY()<yHead)
			return true;
		if(Math.abs(getY()-yHead)<vtol)
			return true;
		return false;
	}
	public boolean isFooter(double yFoot){
		if(getY()>yFoot)
			return true;
		if(Math.abs(getY()-yFoot)<vtol)
			return true;
		return false;
	}

	public String toString(){
		if(this instanceof TextPiece)
			return "("+getX()+","+getY()+","+getWidth()+","+getHeight()+")"+((TextPiece)this).txt;
		else
			return "("+getX()+","+getY()+","+getWidth()+","+getHeight()+")imagen";
	}
	
	public void hPos(double lBorder, double rBorder){
		if(isCentered(lBorder,rBorder))
			position=1;
		else if(isOnLeftBorder(lBorder))
			position=0;
		else if(isOnRightBorder(rBorder))
			position=2;
		else
			position=3;
	}
	
	public void vPos(double yHead, double yFoot){
		if(isHeader(yHead))
			isHead=true;
		if(isFooter(yFoot))
			isFoot=true;
	}
	
	public abstract void emitHTML(HtmlDoc doc);
}

