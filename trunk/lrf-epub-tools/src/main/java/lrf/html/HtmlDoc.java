package lrf.html;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import lrf.Utils;
import lrf.conv.BaseRenderer;
import lrf.epub.EPUBEntity;
import lrf.epub.EPUBMetaData;
import lrf.objects.tags.Tag;


public class HtmlDoc implements EPUBEntity{
	public String title,fNam,auth,id,producer;
	public File tmp;
	public Hashtable<Integer,String> imagenes=new Hashtable<Integer,String>();
	ByteArrayOutputStream bos;
	PrintWriter pw;
	Vector<String> emits=new Vector<String>();
	
	boolean isDivOpen=false;
	
	HtmlStyle currentStyle=new HtmlStyle(new Vector<Tag>());
	HtmlStyle bookStyle,pageStyle,blockStyle;
	HtmlStyle initStyle=new HtmlStyle(new Vector<Tag>());
	
	public static final int es_book=0;
	public static final int es_page=1;
	public static final int es_block=2;
	public static final int es_text=3;
	public static final int es_curr=4;
	
	public void setEstilo(int hob, HtmlStyle estilo){
		switch(hob){
		case es_book:
			currentStyle=initStyle;
			changeStyle(estilo);
			bookStyle=estilo;
			break;
		case es_page:
			currentStyle=bookStyle;
			changeStyle(estilo);
			pageStyle=estilo;
			break;
		case es_block:
			currentStyle=pageStyle;
			changeStyle(estilo);
			blockStyle=estilo;
			break;
		case es_text:
			currentStyle=blockStyle;
			changeStyle(estilo);
			break;
		}
	}

	public void emitText(String txt){
		if(!isDivOpen)
			openDiv();
		emits.add(Emitter.spanOpen(currentStyle));
		if(anchorAntes!=null){
			emits.add(anchorAntes);
			anchorAntes=null;
		}
		emits.add(Utils.toXMLText(txt));
		if(anchorDespues!=null){
			emits.add(anchorDespues);
			anchorDespues=null;
		}
		emits.add(Emitter.spanClose());
	}
	
	public String anchorAntes=null;
	public String anchorDespues=null;
	public void setTemporaryStyle(StyleItem si){
		if(si.getLevel()==StyleItem.st_proc){
			if(si.getPropName().equals("BeginButton")){
				anchorAntes=Emitter.anchorOrig(si.value,true);
			}
			if(si.getPropName().equals("EndButton")){
				anchorDespues=Emitter.anchorOrig(null,false);
			}
		}else{
			StyleItem actual=currentStyle.getStyle(si.propName);
			if(actual==null || !actual.equals(si))
				changeStyle(new HtmlStyle(si));
		}
	}
	
	private boolean firstBody=true;
	private void changeStyle(HtmlStyle estilo) {
		HtmlStyle diff=currentStyle.newStyles(estilo, StyleItem.st_body);
		if(diff.getNumProps()>0){
			//Generar Body
			closeDiv();
			currentStyle.overrideWith(diff);
			if(firstBody)
				emits.add("</body>");
			emits.add(Emitter.body(currentStyle));
			firstBody=false;
		}
		diff=currentStyle.newStyles(estilo, StyleItem.st_div);
		if(diff.getNumProps()>0){
			closeDiv();
			currentStyle.overrideWith(diff);
			openDiv();
		}
		diff=currentStyle.newStyles(estilo, StyleItem.st_span);
		if(diff.getNumProps()>0){
			currentStyle.overrideWith(diff);
		}
	}
	
	public void openDiv(){
		emits.add(Emitter.divOpen(currentStyle));
		isDivOpen=true;
	}
	
	public void closeDiv(){
		if(!isDivOpen)
			return;
		emits.add(Emitter.divClose());
		isDivOpen=false;
	}
	
	public void newParagraph(){
		closeDiv();
		openDiv();
	}
	
	public void emitAnchorDest(String anchorName){
		emits.add(Emitter.anchorDest(anchorName));
	}
	
	public HtmlDoc(String filename, String title, String auth, String producer, String id, File tmp){
		this.fNam=filename;
		this.title=title;
		this.auth=auth;
		this.producer=producer;
		this.id=id;
		this.tmp=tmp;
		bos=new ByteArrayOutputStream();
		pw=new PrintWriter(bos);
		emits.add(Emitter.head(auth, id, title, fNam+".css")+"\n");
	}

	public void addImage(int id, int w, int h, String ext, byte[] b)
			throws Exception {
		String imgfn=imagenes.get(id);
		if(imgfn==null){
			imgfn=Utils.toUnhandText(fNam+id+ext);
			FileOutputStream fosi=new FileOutputStream(new File(tmp,""+id));
			fosi.write(b);
			fosi.close();
			imagenes.put(id,imgfn);
		}
		closeDiv();
		emits.add(
				Emitter.img(
						imgfn,
						imgfn.substring(0,imgfn.length()-ext.length()),
						""+w,""+h)+"\n");
	}
	
	public Vector<String> getImagenes(){
		Vector<String>ret=new Vector<String>();
		for(Enumeration<String>enu=imagenes.elements();enu.hasMoreElements();){
			ret.add(enu.nextElement());
		}
		return ret;
	}
	
	public void createEPUB(EPUBMetaData e, String catpar) throws Exception {
		String divAnterior="1",divActual="2";
		String spanAnterior="",spanActual="";
		boolean spanAnteriorEOP=false;
		int spanAnteriorNdx=-1;
		//Eliminamos los div/div y span/span vacios
		{
			int pdo=-1;
			int pso=-1;
			boolean hasContent=false;
			for(int i=0;i<emits.size();i++){
				String em=emits.get(i);
				if(em.startsWith("<div")){
					pdo=i;
					hasContent=false;
					continue;
				}else if(em.startsWith("<span")){
					pso=i;
					hasContent=false;
					continue;
				}else if(em.startsWith("</div")){
					if(!hasContent){
						for(int j=pdo;j<=i;j++)
							emits.set(j, "");
					}
				}else if(em.startsWith("</span")){
					if(!hasContent){
						for(int j=pso;j<=i;j++)
							emits.set(j,"");
					}
				}else {
					hasContent=true;
				}
				
			}
		}
		for(int i=0;i<emits.size();i++)
			if(emits.get(i).length()==0)
				emits.remove(i--);
		for(int i=0;i<emits.size();i++){
			String base=emits.elementAt(i);
			boolean isText=!base.startsWith("<");
			//Concatenar parrafos
			if(catpar!=null && isText){
				if(divAnterior.equals(divActual) && 
				   spanAnterior.equals(spanActual) &&
				   !spanActual.contains("center") &&
				   !spanAnteriorEOP &&
				   !BaseRenderer.isBeginOfParagraph(base)){
					//No hay cambio de formato y no parece que se terminase el párrafo
					String oldText=emits.elementAt(spanAnteriorNdx);
					emits.set(spanAnteriorNdx, oldText+catpar+base);
					emits.set(i, "");
					spanAnteriorEOP=BaseRenderer.isEndOfParagraph(base);
					continue; //No comprobar div, span o text
				}
			}
			if(base.startsWith("<div")){
				divAnterior=divActual;
				divActual=base;
			}
			if(base.startsWith("<span")){
				spanAnterior=spanActual;
				spanActual=base;
			}
			if(!base.startsWith("<")){
				spanAnteriorEOP=BaseRenderer.isEndOfParagraph(base);
				spanAnteriorNdx=i;
			}
		}
		for(int i=0;i<emits.size();i++){
			pw.print(emits.elementAt(i));
		}
		pw.flush();
		try {
			//Volcamos primero el xhtml
			File fnh=new File(tmp,fNam+".html");
			FileOutputStream fos=new FileOutputStream(fnh);
			bos.writeTo(fos);
			fos.close();
			//Esto es innecesario
			//e.addFile(fileName+".html", fnh, 5);
			//Las imagenes
			Enumeration<Integer> kyss=imagenes.keys();
			for(int i=0;i<imagenes.size();i++){
				int id=kyss.nextElement();
				String imgfn=imagenes.get(id);
				FileInputStream fis=new FileInputStream(new File(tmp,""+id));
				e.processFile(fis,"images/"+imgfn);
				fis.close();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public File getHTMLFile(){
		return new File(tmp,fNam+".html");
	}
}
