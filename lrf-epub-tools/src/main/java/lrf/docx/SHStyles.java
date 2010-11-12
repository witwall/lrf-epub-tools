package lrf.docx;

import java.util.Enumeration;
import java.util.Hashtable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SHStyles extends DefaultHandler {
	boolean allow=false;
	String currentStyleName=null;
	String defaultStyleName=null;
	String justify=null;
	int fontSize=0;
	boolean bold=false;
	boolean italic=false;
	String color=null;
	String heading=null;
	Hashtable<String,String> estilos=new Hashtable<String, String>();
	Hashtable<String,String> estilosHeadings=new Hashtable<String, String>(); 
	@Override
	public void startElement(String uri, String localName, String name, Attributes at) 
	throws SAXException {
		super.startElement(uri, localName, name, at);
		if(localName.equals("style")){
			String type=at.getValue("w:type");
			if(type!=null && (type.equals("paragraph") || type.equals("character"))){
				allow=true;
				currentStyleName=type+at.getValue("w:styleId");
				String dv=at.getValue(uri, "default");
				if(dv!=null && dv.equals("1")){
					defaultStyleName=currentStyleName;
				}
			}
		}else if(allow){
			if(localName.equals("name")){
				heading=at.getValue(uri, "val");
			}else if(localName.equals("jc")){
				justify=at.getValue("w:val");
			}else if(localName.equals("sz")){
				fontSize=Integer.parseInt(at.getValue("w:val"));
			}else if(localName.equals("b")){
				String boldness=at.getValue(uri,"val");
				if(boldness==null)
					bold=true;
				else if(Integer.parseInt(boldness)>0)
					bold=true;
			}else if(localName.equals("i")){
				italic=true;
			}else if(localName.equals("color")){
				color=at.getValue(uri,"val");
			}
		}
	}
	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, name);
		if(localName.equals("style") & currentStyleName!=null){
			allow=false;
			estilos.put(
					currentStyleName,
					 (justify==null?" text-align:justify;":" text-align:"+justify+";")
					+(fontSize==0?  "":" font-size:"+(getFontSize(fontSize))+";")
					+(!bold?        "":" font-weight:bold;")
					+(!italic?      "":" font-style:italic;")
					+(color==null?  "":" font-color=#"+color+";")
					);
			estilosHeadings.put(currentStyleName,heading);
			justify=null;
			fontSize=0;
			currentStyleName=null;
			bold=false;
			italic=false;
			color=null;
			heading=null;
		}
	}

	public String getCSS(){
		if(estilos.size()==0)
			return null;
		String ret="";
		for(Enumeration<String> sne=estilos.keys();sne.hasMoreElements();){
			String sname=sne.nextElement();
			ret+="."+sname+" {"+estilos.get(sname)+"}\n";
		}
		if(defaultStyleName!=null){
			ret+="DIV {"+estilos.get(defaultStyleName)+"}\n";
		}
		ret+="p { text-indent:2.0em; padding-top:0.2em; padding-bottom:0.2em; text-align:justify; }";
		return ret;
	}
	
	public String getDefaultStyleName(){
		return defaultStyleName;
	}
	
	public static String getFontSize(int px){
		float fs=(float)px/24f;
		fs=Math.round(fs*100);
		fs/=100;
		return ""+fs+"em";
	}
	
	public String getHeading(String name){
		return estilosHeadings.get(name);
	}
}
