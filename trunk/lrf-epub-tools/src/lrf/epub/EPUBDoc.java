package lrf.epub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import lrf.Utils;

import org.apache.xpath.jaxp.XPathFactoryImpl;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class EPUBDoc {
	String opfPath,opf,tocPath,toc,opfDir;
	NSContext context=new NSContext();
	File fileName;
	public Hashtable<String, String> itemsID_MT=new Hashtable<String, String>();
	public Hashtable<String, String> itemsHR_ID=new Hashtable<String, String>();
	public Hashtable<String, String> itemsID_HR=new Hashtable<String, String>();
	public Vector<String> spines=new Vector<String>();
	
	public EPUBDoc(String s) throws Exception{
		this(new File(s));
	}
	
	public String getOPFDir(){
		return opfDir;
	}
	
	public EPUBDoc(File f) throws Exception {
		fileName=f;
		while(toc==null){
			FileInputStream fis=new FileInputStream(fileName);
			ZipInputStream zis=new ZipInputStream(fis);
			ZipEntry entry=null;
			while((entry=zis.getNextEntry())!=null){
				if(entry.getName().equals("META-INF/container.xml")){
					opfPath=getXPathVal("opf", zis, 
						"/container/rootfiles/rootfile/@full-path");
					int pos=opfPath.lastIndexOf("/");
					opfDir=opfPath.substring(0,pos+1);
				}
				if(opfPath!=null && entry.getName().equals(opfPath)){
					opf=getIS(zis);
					String ncxItem=getXPathVal("opf", opf, "/package/spine/@toc");
					tocPath=opfDir+getXPathVal("opf", opf, "//item[@id=\""+ncxItem+"\"]/@href");
				}
				if(tocPath!=null && entry.getName().equals(tocPath)){
					toc=getIS(zis);
					break;
				}
			}
			zis.close();
		}
		//Extraemos de cada item de opf su mime-type
		NodeList nl=getNodeSet("opf", opf, "/package/manifest/item");
		for(int i=0;i<nl.getLength();i++){
			Node n=nl.item(i);
			NamedNodeMap nnm=n.getAttributes();
			itemsID_MT.put(nnm.getNamedItem("id").getNodeValue(), 
					  nnm.getNamedItem("media-type").getNodeValue());
			itemsHR_ID.put(nnm.getNamedItem("href").getNodeValue(), 
					  nnm.getNamedItem("id").getNodeValue());
			itemsID_HR.put(nnm.getNamedItem("id").getNodeValue(), 
					nnm.getNamedItem("href").getNodeValue());
		}
		//Ahora el spine
		nl=getNodeSet("opf", opf, "/package/spine/itemref");
		for(int i=0;i<nl.getLength();i++){
			Node n=nl.item(i);
			NamedNodeMap nnm=n.getAttributes();
			spines.add(nnm.getNamedItem("idref").getNodeValue());
		}
	}

	private String getXPathVal(String doct, ZipInputStream zis, String xp) throws Exception {
		String xmlDocStr=getIS(zis);
		return getXPathVal(doct, xmlDocStr,xp);
	}

	private String getXPathVal(String doct, String xmlDocStr, String expres)	
	throws XPathExpressionException {
		xmlDocStr=xmlDocStr.replace("xmlns=", "xmlns:dEf=");
		context.getNSFromXMLDoc(doct, xmlDocStr);
		InputSource is=new InputSource(new StringReader(xmlDocStr));
		XPathFactory xpf=XPathFactoryImpl.newInstance();
		XPath xpath=xpf.newXPath();
		xpath.setNamespaceContext(context);
		XPathExpression xpe=xpath.compile(expres);
		return (String)xpe.evaluate(is, XPathConstants.STRING);
	}
	
	private String getIS(ZipInputStream zis) throws IOException{
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		Utils.writeTo(zis, bos);
		return new String(bos.toByteArray());
	}
	
	public String firstDoc(){
		String href;
		try {
			String item=getXPathVal("opf", opf, "/package/spine/itemref[1]/@idref");
			href = getXPathVal("opf", opf, "/package/manifest/item[@id=\""+item+"\"]/@href");
		} catch (XPathExpressionException e) {
			return null;
		}
		return href;
	}
	
	
	
	public String getAutor(){
		try {
			return getXPathVal("opf", opf, "//dc:creator/text()");
		} catch (XPathExpressionException e) {
			return null;
		}
	}
	
	public String getTitle(){
		String ret=null;
		try {
			ret=getXPathVal("opf", opf, "//dc:title/text()");
		} catch (XPathExpressionException e) {
			return null;
		}
		return ret;
	}
	
	public String getTOC(){
		return toc;
	}
	
	public String getOPF(){
		return opf;
	}

	public NodeList getNodeSet(String doct, String doc, String exp) throws XPathExpressionException{
		context.getNSFromXMLDoc(doct, doc);
		XPathFactory xpf=XPathFactoryImpl.newInstance();
		XPath xpath=xpf.newXPath();
		xpath.setNamespaceContext(context);
		InputSource is=new InputSource(new StringReader(doc.replace("xmlns=", "xmlns:dEf=")));
		XPathExpression xpe=xpath.compile(exp);
		return (NodeList)xpe.evaluate(is, XPathConstants.NODESET);
	}
	
	void appendNavPoint2(EPUBMetaData epmd, XMLNode padre, Node navpoint, String prepath) 
	throws XPathExpressionException{
		String toctext=null;
		String url=null;
		Node n=navpoint.getFirstChild();
		XMLNode este=null;
		Vector<Node> navPointChilds=new Vector<Node>();
		while(n!=null){
			if(n.getNodeType()==Node.TEXT_NODE){
				n=n.getNextSibling();
				continue;
			}
			if(n.getLocalName().equals("navLabel")){
				for(Node m=n.getFirstChild();m!=null;m=m.getNextSibling()){
					if(m.getNodeType()==Node.TEXT_NODE)
						continue;
					if(m.getLocalName().equals("text")){
						toctext=Utils.toUnhandText(m.getTextContent().trim());
						break;
					}
				}
			}
			if(n.getLocalName().equals("content")){
				url=n.getAttributes().getNamedItem("src").getNodeValue();
			}
			if(este==null && toctext!=null && url!=null){
				if(!toctext.equals("Top") && !url.endsWith("#top"))
					este=epmd.createNavPoint(toctext, prepath+url, padre);
			}
			if(n.getLocalName().equals("navPoint")){
				navPointChilds.add(n);
			}
			n=n.getNextSibling();
		}
		for(int i=0;i<navPointChilds.size();i++)
			appendNavPoint2(epmd, este, navPointChilds.elementAt(i), prepath);
	}
	
	public void setNavPoint(EPUBMetaData epmd, String prepath, XMLNode padre) 
	throws XPathExpressionException{
		NodeList nset=getNodeSet("ncx", toc,"//dEf:navPoint");
		for(int i=0;i<nset.getLength();i++){
			appendNavPoint2(epmd, padre, nset.item(i), prepath);
		}
	}

	public InputStream getInputStream(String entName) throws IOException{
		FileInputStream fis=new FileInputStream(fileName);
		ZipInputStream zis=new ZipInputStream(fis);
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		ZipEntry entry=null;
		while((entry=zis.getNextEntry())!=null){
			if(entry.getName().equals(entName)){
				Utils.writeTo(zis, baos);
				break;
			}
		}
		zis.close();
		return new ByteArrayInputStream(baos.toByteArray());
	}
	
	public int getNumOfDocs(){
		return spines.size();
	}
	
	public InputStream getInputStream(int i) throws IOException{
		if(i<0||i>=getNumOfDocs())
			return null;
		return getInputStream(itemsID_HR.get(spines.elementAt(i)));
	}
	
	public InputStream getInputStream() throws IOException{
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		for(int i=0;i<spines.size();i++){
			Utils.writeTo(getInputStream(i), baos);
		}
		return new ByteArrayInputStream(baos.toByteArray());
	}
}

