package lrf.pdf.flow;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Vector;



public class Flower {
	static final double initRBorder=540;
	static final double initLBorder=60;
	
	double pWidth=600D;
	double pHeight=800D;
	double yHead=40D;
	double yFoot=760D;
	double rBorder=initRBorder;
	double lBorder=initLBorder;
	
	Vector<Piece> pieces=new Vector<Piece>();
	
	public void addPiece(Piece p){
		pieces.add(p);
	}
	
	public void managePieces(){
		//Ordenamos las piezas empezando por la primera pagina,
		//dentro de cada pagina por altura (y) y luego por
		//posicion x.
		Collections.sort(pieces);
		//Inicializamos algunas variables
		TextPiece last=null;
		//Ahora nos dedicamos a detectar los paragraphs.
		double resetBorderAtY=900;
		double lengthOfText=0;
		for(Piece p:pieces){
			if(last!=null){
				if(last.numPage!=p.numPage ||
				   p.getY()>=resetBorderAtY){
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
							last.hPos(lBorder, rBorder);
							last.vPos(yHead, yFoot);
						}else{
							last.position=3; //justify
						}
						current.isStartOfParagraph=true;
						current.isEndOfParagraph=true;
						lengthOfText=current.getWidth();
					}
					
				}
				last=current;
			}
		}
	}
	
	public void dumpPieces(OutputStream os){
		PrintWriter pw=new PrintWriter(os);
		for(Piece p:pieces){
			if(p.isStartOfParagraph)
				pw.print("\nSP ");
			if(p.isHead)
				pw.print("H ");
			if(p.isFoot)
				pw.print("F ");
			if(p instanceof TextPiece){
				pw.print(((TextPiece) p).txt);
			}else{
				pw.print("[[IMAGE]]");
			}
			if(p.isEndOfParagraph)
				pw.print("<"+p.position+","+p.horizAdj+">");
		}
	}
}
