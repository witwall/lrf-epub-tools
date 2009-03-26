package lrf.docx.states;

import java.io.File;
import java.util.Vector;

import lrf.docx.Context;
import lrf.docx.SHRelations;
import lrf.docx.SHRelations.Relation;

import org.xml.sax.Attributes;

public class STDrawing implements State {

	private static STDrawing singleton=new STDrawing();
	
	private STDrawing(){
		
	}
	
	public static STDrawing getInstance(){
		return singleton;
	}
	@Override
	public void endDoc(Context context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endEle(Context context, String uri, String lName, String name) {
		if(lName.equals("posOffset")){
			context.avoidCharsEmits=false;
		}
	}

	@Override
	public void startDoc(Context context) {
		// TODO Auto-generated method stub

	}

	private Vector<String> imgs=new Vector<String>();
	@Override
	public void startEle(Context context, SHRelations rels, String uri, String lName, String name, Attributes attr) {
		if(lName.equals("blip")){
			String imgId=attr.getValue("r:embed");
			
			if(imgId!=null){
				Relation rel=rels.getRelation(imgId);
				File ff=new File(context.getFNOut());
				String tgt=ff.getName()+"-"+rel.target;
				tgt=tgt.replace("/", "-");
				tgt=tgt.replace("\\", "-");
				String toadd=
					"<img src=\""+tgt+"\"" +
					" align=\"Center\"" +
					" alt=\"" +rel.type+"\"/>";
				context.addData(toadd);
				if(!imgs.contains(tgt)){
					context.addBytes(tgt,context.getZipOSNamed("word/"+rel.target));
					imgs.add(tgt);
				}
			}
		}else if(lName.equals("posOffset")){
			context.avoidCharsEmits=true;
		}
	}
}
