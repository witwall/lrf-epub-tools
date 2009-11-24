package lrf.epub;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class UrlStreamHandlerFactory implements URLStreamHandlerFactory {

	@Override
	public URLStreamHandler createURLStreamHandler(String protocol) {
		if(protocol.equals("epub")){
			return new UrlStreamHandler();
		}
		return null;
	}

}
