package lrf.merge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import lrf.epub.EPUBDoc;
import lrf.epub.EPUBMetaData;
import lrf.epub.XMLNode;

public class MergeEPUBAndTOC extends EPUBMetaData{
	
	String title,autor,publisher,id;
	int currentBookNumber=0;
	File dest;
	Hashtable<String, XMLNode> ppaths=new Hashtable<String, XMLNode>();
	
	public void appendBook(File eBook, String ppath) throws Exception {
		currentBookNumber++;
		EPUBDoc doc=new EPUBDoc(eBook);
		ZipInputStream zis=new ZipInputStream(new FileInputStream(eBook));
		ZipEntry ze;
		//Se añaden todos los contenidos
		String opfDir=doc.getOPFDir();
		Hashtable<String, XMLNode> forSpinning=new Hashtable<String, XMLNode>();
		while((ze=zis.getNextEntry())!=null){
			String name=ze.getName();
			if(name.startsWith(opfDir))
				name=name.substring(opfDir.length());
			String id=doc.itemsHR_ID.get(name);
			if(id==null)
				continue;
			String mt=doc.itemsID_MT.get(id);
			String epubUrl=""+currentBookNumber+"/"+name;
			XMLNode xmln=mftAddNodoWithMT(epubUrl,mt);
			addIS(epubUrl, zis, 5);
			forSpinning.put(id, xmln);
		}
		//Construimos el spinning
		for(int i=0;i<doc.spines.size();i++){
			spineItems.add(forSpinning.get(doc.spines.elementAt(i)));
		}
		zis.close();
		XMLNode book;
		if(ppath!=null && ppaths.get(ppath)==null){
			ppaths.put(ppath,createNavPoint(ppath, "", getNavMap()));
		}
		//Ahora hay que rellenar los NavPoints:
		if(ppath!=null){
			book=createNavPoint(
					doc.getTitle().trim(), 
					""+currentBookNumber+"/"+doc.firstDoc(), 
					ppaths.get(ppath));
		}else{
			book=createNavPoint(
					doc.getTitle().trim(), 
					""+currentBookNumber+"/"+doc.firstDoc(), 
					getNavMap());
		}
		//Y debajo de el añadimos el propio TOC del Book
		doc.setNavPoint(this, ""+currentBookNumber+"/",book);
	}

	@Override
	public String getCreator() {
		return autor;
	}

	@Override
	public String getIdentifier() {
		return id;
	}

	@Override
	public String getLanguage() {
		return "en";
	}

	@Override
	public String getPublisher() {
		return "Unknown";
	}

	@Override
	public String getRights() {
		return "free";
	}

	@Override
	public Vector<String> getSubject() {
		return new Vector<String>();
	}

	@Override
	public String getTitle() {
		return title;
	}

	public MergeEPUBAndTOC(File destFile, String title, String auth) throws FileNotFoundException, IOException{
		this.title=title;
		this.autor=auth;
		dest=destFile;
		id=createRandomIdentifier();
		init(dest.getAbsolutePath());
	}
	
}
