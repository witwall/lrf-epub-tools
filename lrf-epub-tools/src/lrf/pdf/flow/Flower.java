package lrf.pdf.flow;

import java.util.Collections;
import java.util.Vector;

import lrf.html.HtmlDoc;



public class Flower {
	static final double initRBorder=540;
	static final double initLBorder=60;
	
	double pWidth=600D;
	double pHeight=800D;
	double yHead=40D;
	double yFoot=760D;
	double rBorder=initRBorder;
	double lBorder=initLBorder;
	
	int currentPageHeight, currentPageWidth;
	
	Vector<Piece> pieces=new Vector<Piece>();
	Piece lastAdded=null;
	
	public void newPage(int pn, int width, int height){
		currentPageHeight=height;
		currentPageWidth=width;
		addPiece(new PageBreakPiece(pn));
	}
	
	public void addPiece(Piece p){
		if(lastAdded!=null && lastAdded instanceof TextPiece && p instanceof TextPiece){
			TextPiece la=(TextPiece)lastAdded;
			TextPiece pp=(TextPiece)p;
			if( 	la.getY()==pp.getY() && 
					la.getX()< pp.getX() &&
					pp.getX()-(la.getX()+la.getWidth())<10 &&
					la.isSameStyle(pp)){
					
				la.txt+=pp.txt;
				la.rect.setRect(
						la.getX(), 
						la.getY(), 
						la.getWidth()+pp.getWidth(), 
						la.getHeight());
				return;
			}
		}
		lastAdded=p;
		pieces.add(p);
	}
	
	public void managePieces(HtmlDoc doc){
		rBorder=initRBorder;
		lBorder=initLBorder;
		//Ordenamos las piezas empezando por la primera pagina,
		//dentro de cada pagina por altura (y) y luego por
		//posicion x.
		Collections.sort(pieces);
		//Inicializamos algunas variables
		TextPiece last=null;
		TextPiece lastSOP=null;
		//Ahora nos dedicamos a detectar los paragraphs.
		double resetBorderAtY=900;
		double lengthOfText=0;
		for(Piece p:pieces){
			if(last!=null){
				if(last.numPage!=p.numPage || p.getY()>=resetBorderAtY){
					resetBorderAtY=900;
					lBorder=initLBorder;
					rBorder=initRBorder;
				}
			}
			if(p instanceof ImagePiece){
				ImagePiece im=(ImagePiece)p;
				im.hPos(lBorder, rBorder);
				switch(im.position){
				case 0: lBorder=im.getX()+im.getWidth(); break;
				case 1: break;
				case 2: rBorder=im.getX(); break;
				}
				resetBorderAtY=im.getY()+im.getHeight();
			}else if(p instanceof TextPiece){
				TextPiece current=(TextPiece)p;
				if(last==null){
					current.isStartOfParagraph=true;
					current.isEndOfParagraph=true;
					lengthOfText=current.getWidth();
					lastSOP=current;
				}else{
					if( last.isHorizAdjacent(current) ||
						(last.isOnRightBorder(rBorder) && current.isOnLeftBorder(lBorder)))
					{
						last.isEndOfParagraph=false;
						current.isStartOfParagraph=false;
						current.isEndOfParagraph=true;
						lengthOfText+=current.getWidth();
					}else{
						//Fin de paragraph. Hay que comprobar si son head o foot
						last.isEndOfParagraph=true;
						if(lengthOfText<500){
							lastSOP.hPos(lBorder, rBorder);
							lastSOP.vPos(yHead, yFoot);
						}else{
							lastSOP.position=3; //justify
						}
						current.isStartOfParagraph=true;
						current.isEndOfParagraph=true;
						lengthOfText=current.getWidth();
						lastSOP=current;
						//Emitimos
						
					}
				}
				last=current;
			}
		}
		dumpPieces(pieces,lastSOP,doc);
		Vector<Piece> preserve=new Vector<Piece>();
		for(int pos=pieces.indexOf(lastSOP);pos<pieces.size();pos++){
			preserve.add( pieces.get(pos) );
		}
		pieces=preserve;
	}
	
	public void dumpPieces(Vector<Piece> vpi, TextPiece until, HtmlDoc doc){
		TextPiece.adjusted=false;
		//Primero acumulamos los textos con los mismos estilos.
		for(int i=0;i<pieces.size()-1 && !pieces.get(i+1).equals(until);i++){
			Piece p=pieces.get(i);
			Piece q=pieces.get(i+1);
			if(p instanceof TextPiece && q instanceof TextPiece){
				TextPiece tpp=(TextPiece)p;
				TextPiece tpq=(TextPiece)q;
				if(tpp.sameStyles(tpq)){
					//Agruparlos en p
					tpp.txt+=tpq.txt;
					pieces.remove(i+1);
					i--;
				}
			}
		}
		for(Piece p:vpi){
			if(p.equals(until))
				break;
			p.emitHTML(doc);
		}
	}
	
	public void dumpPieces(HtmlDoc doc){
		dumpPieces(pieces,null,doc);
	}
}
