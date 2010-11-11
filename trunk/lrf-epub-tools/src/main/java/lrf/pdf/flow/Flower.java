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
		pieces.add(p);
	}
	
	public void managePieces(HtmlDoc doc){
		rBorder=initRBorder;
		lBorder=initLBorder;
		//Ordenamos las piezas empezando por la primera pagina,
		//dentro de cada pagina por altura (y) y luego por
		//posicion x.
		Collections.sort(pieces);
		//Retiramos el efecto BOLD simulado (dos veces en 1 pixel)
		TextPiece p1=null,p2=null;
		for(int i=0;i<pieces.size();i++){
			Piece p=pieces.get(i);
			if(!(p instanceof TextPiece))
				continue;
			p2=(TextPiece)p;
			if(p1==null){
				p1=p2;
				continue;
			}
			if(    p1.getY()+p1.getHeight()>p2.getY()
				&& p1.getX()+p1.getWidth()>p2.getX()
				&& p1.txt.equals(p2.txt)	){
				//p1.font=p1.font.deriveFont(Font.BOLD);
				//Borramos p2
				pieces.remove(i);
				i--;
				continue;
			}
			p1=p2;
		}
		//y unimos el texto adyacente
		for(int i=0;i<pieces.size();i++){
			Piece p=pieces.get(i);
			if(!(p instanceof TextPiece))
				continue;
			p2=(TextPiece)p;
			if(p1==null){
				p1=p2;
				continue;
			}
			if( 	Math.abs(p1.getY()-p2.getY())<Piece.vertiTolerance 
					&& p1.getX()< p2.getX() 
					//&& p2.getX()-(p1.getX()+p1.getWidth())<10
					//&& p1.isSameStyle(p2)
			){
				p1.txt+=p2.txt;
				p1.rect.setRect(p1.getX(),p1.getY(),p1.getWidth()+p2.getWidth(),p1.getHeight());
				pieces.remove(i);
				i--;
				continue;
			}
			p1=p2;
		}
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
						current.txt=" "+current.txt;
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
		if(lastSOP!=null)
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
			//System.out.println(p.toString());
		}
	}
	
	public void dumpPieces(HtmlDoc doc){
		dumpPieces(pieces,null,doc);
	}
}
