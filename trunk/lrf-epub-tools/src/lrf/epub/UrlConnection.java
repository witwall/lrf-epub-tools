package lrf.epub;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

public class UrlConnection extends URLConnection {
	public static Hashtable<String, EPUBDoc> books=new Hashtable<String, EPUBDoc>();
	String inside;
	String name;
	protected UrlConnection(URL url) throws MalformedURLException {
		super(url);
		String aux=url.getPath();
		int pos=aux.toLowerCase().indexOf(".epub/");
		if(pos<0)
			throw new MalformedURLException(url.toString());
		name=aux.substring(0,pos+5);
		EPUBDoc epd=books.get(name);
		if(epd==null){
			try {
				epd=new EPUBDoc(new File(name));
				books.put(name, epd);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(pos+6>=aux.length()){
			//Suponemos el spine
			inside=epd.itemsID_HR.get(epd.spines.elementAt(0));
		}else{
			inside=aux.substring(pos+6);
		}
		inside=epd.getOPFDir()+inside;
	}

	@Override
	public void connect() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public InputStream getInputStream() throws IOException {
		return books.get(name).getInputStream(inside);
	}

}
