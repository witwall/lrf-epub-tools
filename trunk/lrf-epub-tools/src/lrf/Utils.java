package lrf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

	public static String toTOCText(String app) {
		app=toUnhandText(app);
		app=app.replace("’", "");
		app=app.replace("'", "");
		app=app.replace("‘", "");
		app=app.replace("\"", "");
		return app;
	}

	public static String toXMLText(String app) {
		app=app.replace('\u000C', ' ');
		app=app.replace("&", "&amp;");
		app=app.replace("©", "&copy;");
		app=app.replace("º", "&ordm;");
		app=app.replace("ª", "&ordf;");
		app=app.replace("'", "&apos;");
		app=app.replace("‘", "&lsquo;");
		app=app.replace("’", "&rsquo;");
		app=app.replace("\"","&quot;");
		app=app.replace("“", "&ldquo;");
		app=app.replace("”", "&rdquo;");
		app=app.replace("<", "&lt;");
		app=app.replace(">", "&gt;");
		app=app.replace("’", "&apos;");
		app=app.replace("—", "-");
		app=app.replace("…", "...");
		app=app.replace("¿", "&iquest;");
		app=app.replace("¡", "&iexcl;");
		app=app.replace("«", "&laquo;");
		app=app.replace("»", "&raquo;");
		app=app.replace("’", "&apos;");
		app=app.replace("Æ", "&AElig;");
		app=app.replace("á", "&aacute;");
		app=app.replace("é", "&eacute;");
		app=app.replace("í", "&iacute;");
		app=app.replace("ó", "&oacute;");
		app=app.replace("ú", "&uacute;");
		app=app.replace("Á", "&Aacute;");
		app=app.replace("É", "&Eacute;");
		app=app.replace("Í", "&Iacute;");
		app=app.replace("Ó", "&Oacute;");
		app=app.replace("Ú", "&Uacute;");
		app=app.replace("ñ", "&ntilde;");
		app=app.replace("Ñ", "&Ntilde;");
		app=app.replace("£", "&pound;");
		app=app.replace("§", "&sect;");
		app=app.replace("®", "&reg;");
		app=app.replace("†", "&dagger;");
		app=app.replace("€", "&euro;");
		//app=app.replace("", "&;");
		//app=app.replace("", "&;");
		//app=app.replace("", "&;");
		//app=app.replace("", "&;");
		return app;
	}

	public static String toUnhandText(String app) {
		app=app.replace('\u000C', ' ');
		app=app.replace("’", "'");
		app=app.replace("‘", "'");
		app=app.replace("\"", "'");
		app=app.replace("“", "'");
		app=app.replace("”", "'");
		app=app.replace("—", "-");
		app=app.replace("…", "...");
		app=app.replace("Æ", "AE");
		app=app.replace("á", "a");
		app=app.replace("é", "e");
		app=app.replace("í", "i");
		app=app.replace("ó", "o");
		app=app.replace("ú", "u");
		app=app.replace("Á", "A");
		app=app.replace("É", "E");
		app=app.replace("Í", "I");
		app=app.replace("Ó", "O");
		app=app.replace("Ú", "U");
		app=app.replace("ñ", "n");
		app=app.replace("Ñ", "N");
		app=app.replace("\t"," ");
		app=app.replace("\n"," ");
		byte n[]=app.getBytes();
		for(int i=0;i<n.length;i++){
			if(n[i]<0)
				n[i]=32;
		}
		app=new String(n);
		return app;
	}

	public static void writeTo(InputStream is, OutputStream os) throws IOException{
		int readed;
		byte buf[] = new byte[1024 * 1024];
		while ((readed = is.read(buf, 0, buf.length)) > 0) {
			os.write(buf, 0, readed);
		}
	}

	public static byte[] getResourceAsByteArray(String rsName) throws IOException{
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		writeTo("".getClass().getResourceAsStream(rsName),baos);
		return baos.toByteArray();
	}
	
	public static String surroundWithQuotes(String style, String tgt) {
		int pos=style.indexOf(tgt);
		if(pos>=0){
			pos+=tgt.length();
			while(style.charAt(pos)!=':')
				pos++;
			pos++;
			while( Character.isWhitespace(style.charAt(pos)))
				pos++;
			int pos2=pos;
			while(style.charAt(pos2)!=';')
				pos2++;
			style=style.substring(0,pos)+
			      "\""+style.substring(pos,pos2)+"\""+style.substring(pos2);
		}
		return style;
	}

	public static String getValueFromStyle(String style, String tgt){
		int pos=style.indexOf(tgt);
		if(pos>=0){
			pos+=tgt.length();
			while(style.charAt(pos)!=':')
				pos++;
			pos++;
			while( Character.isWhitespace(style.charAt(pos)))
				pos++;
			int pos2=pos;
			while(style.charAt(pos2)!=';')
				pos2++;
			return style.substring(pos,pos2);
		}
		return "";
	}
	
	public static String setValueFromStyle(String style, String tgt,String val){
		int pos=style.indexOf(tgt);
		if(pos>=0){
			pos+=tgt.length();
			while(style.charAt(pos)!=':')
				pos++;
			pos++;
			while( Character.isWhitespace(style.charAt(pos)))
				pos++;
			int pos2=pos;
			while(style.charAt(pos2)!=';')
				pos2++;
			style=style.substring(0,pos)+
			      "\""+val+"\""+style.substring(pos2);
		}
		return style;
	}
}
