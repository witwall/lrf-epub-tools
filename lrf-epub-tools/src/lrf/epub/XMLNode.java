package lrf.epub;

import java.util.Vector;

import lrf.Utils;

public class XMLNode {
	Vector<String> atk=new Vector<String>();
	Vector<String> atv=new Vector<String>();
	Vector<XMLNode> childs=new Vector<XMLNode>();
	public XMLNode padre=null;
	String tag=null;
	String content=null;
	boolean uText=false;
	
	public XMLNode(String tag, String content, XMLNode parent, boolean unhandText){
		this.tag=tag;
		this.content=content;
		if(parent!=null)
			parent.addChild(this);
		uText=unhandText;
	}
	public void addAtr(String name, String value){
		int pos=atk.indexOf(name);
		if(pos==-1){
			atk.add(name);
			atv.add(value);
		}else{
			atv.setElementAt(value, pos);
		}
	}
	public void addChild(XMLNode c){
		childs.add(c);
	}
	public String getAtr(String name){
		int pos=atk.indexOf(name);
		if(pos==-1){
			return null;
		}
		return atv.get(pos);
	}
	public String dump(){
		StringBuffer sb=new StringBuffer();
		dump(sb,0);
		return sb.toString();
	}
	private void dump(StringBuffer sb,int lev) {
		sb.append("\n");
		for(int kk=0;kk<lev;kk++)
			sb.append(" ");
		sb.append("<").append(tag);
		if(atk.size()>0){
			for(int i=0;i<atk.size();i++){
				String k=atk.get(i);
				String v=atv.get(i);
				sb.append(" ")
				  .append(k)
				  .append("=\"")
				  .append(v)
				  .append("\"");
			}
		}
		if(childs.size()==0 && content==null){
			sb.append("/>");
		}else{
			sb.append(">");
			if(content!=null){
				sb.append(
				uText ?	Utils.toUnhandText(content) : content 
				);
			}
			if(childs.size()>0){
				for(int i=0;i<childs.size();i++){
					childs.get(i).dump(sb,lev+1);
				}
				sb.append("\n");
				for(int kk=0;kk<lev;kk++)
					sb.append(" ");
			}
			sb.append("</").append(tag).append(">");
		}
	}
}
