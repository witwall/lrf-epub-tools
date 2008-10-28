package lrf.epub;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UrlConnection extends URLConnection {
	String inside;
	File epubFile;
	protected UrlConnection(URL url) throws IOException {
		super(url);
		String aux=url.getPath().replace("%20"," ");
		int pos=aux.toLowerCase().indexOf(".epub/");
		if(pos<0)
			throw new MalformedURLException(url.toString());
		epubFile=new File(aux.substring(0,pos+5));
		EPUBDoc epd=EPUBDoc.load(epubFile);
		if(pos+6>=aux.length()){
			String ref=url.getRef();
			if(ref!=null){
				//Number expected, from 0 to spines.size()-1
				int spinNumber=Integer.parseInt(ref);
				inside=epd.itemsID_HR.get(epd.spines.elementAt(spinNumber));
			}else{
				//Suponemos el spine
				inside=epd.itemsID_HR.get(epd.spines.elementAt(0));
			}
		}else{
			inside=aux.substring(pos+6);
		}
	}

	@Override
	public void connect() throws IOException {
		throw new UnsupportedOperationException();

	}

	@Override
	public InputStream getInputStream() throws IOException {
		return EPUBDoc.load(epubFile).getInputStream(inside);
	}

	@Override
	public int getContentLength() {
		return EPUBDoc.load(epubFile).getNumOfDocs();
	}

}
