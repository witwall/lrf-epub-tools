package lrf.epub;

import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class NSContext implements NamespaceContext {

	String xns[][]={
	{"container","dEf","urn:oasis:names:tc:opendocument:xmlns:container"},
	{"opf","dEf","http://www.idpf.org/2007/opf"},
	{"opf","dc" ,"http://purl.org/dc/elements/1.1/"},
	{"ncx","dEf","http://www.daisy.org/z3986/2005/ncx/"}
	};
	
	public String doct="";
	
	Hashtable<String, Hashtable<String, String>> nss=new Hashtable<String, Hashtable<String, String>>(); 
	
	public NSContext(){
		for(int i=0;i<xns.length;i++){
			Hashtable<String, String> au=nss.get(xns[i][0]);
			if(au==null){
				au=new Hashtable<String, String>();
				nss.put(xns[i][0], au);
			}
			au.put(xns[i][1], xns[i][2]);
		}
	}
	
	/**
	 * Cargamos los namespaces para devolverlos correctamente despues.
	 * @param content
	 */
	public void getNSFromXMLDoc(String doct, String content){
		int pos=0;
		this.doct=doct;
		while((pos=content.indexOf("xmlns:",pos))>=0){
			String xmlnsn="";
			pos+="xmlns:".length();
			while(content.charAt(pos)!='=')
				xmlnsn+=content.charAt(pos++);
			pos+=2; //Saltamos = y "
			String url="";
			while(content.charAt(pos)!='\"')
				url+=content.charAt(pos++);
			Hashtable<String, String> au=nss.get(doct);
			if(au==null){
				au=new Hashtable<String, String>();
				nss.put(doct, au);
			}
			au.put(xmlnsn, url);
		}
	}
	
	@Override
	public String getNamespaceURI(String prefix) {
        if (prefix == null) 
        	throw new NullPointerException("Null prefix");
        else if ("xml".equals(prefix)) 
        	return XMLConstants.XML_NS_URI;
        for(int i=0;i<nss.size();i++){
        	Hashtable<String, String> dct=nss.get(doct);
        	if(dct!=null){
        		String ret=dct.get(prefix);
        		if(ret!=null)
        			return ret;
        	}
        }
        return XMLConstants.NULL_NS_URI;
	}

    // This method isn't necessary for XPath processing.
    public String getPrefix(String uri) {
        throw new UnsupportedOperationException();
    }

    // This method isn't necessary for XPath processing either.
    @SuppressWarnings("unchecked")
	public Iterator getPrefixes(String uri) {
        throw new UnsupportedOperationException();
    }


}
