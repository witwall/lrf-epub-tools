package lrf.epub;

import java.io.InputStream;
import java.net.URL;

public class TestURL {

	public static void main(String[] args) {
		try {
			URL u=new URL(
					null,
					"epub:///EPUBBestPractices-1_0.epub/images/OPS.svg",
					new UrlStreamHandler());
			UrlConnection con=new UrlConnection(u);
			InputStream is=con.getInputStream();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
