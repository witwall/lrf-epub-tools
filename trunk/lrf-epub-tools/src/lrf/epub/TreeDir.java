package lrf.epub;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public class TreeDir extends EPUBMetaData {
	File dir;
	String dirCanonicalPath;
	Hashtable<String, String> pars=new Hashtable<String, String>();
	
	public TreeDir(File dir, String auth, String title) throws Exception{
		super("d:\\tmp\\books.epub","en");
		this.dir=dir;
		dirCanonicalPath=dir.getCanonicalPath();
		pars.put("creator", auth);
		pars.put("identifier", createRandomIdentifier());
		pars.put("language", "en");
		pars.put("rights", "free");
		pars.put("publisher", "");
		pars.put("title", title);
		recurseDirs(dir,getNavMap());
		close();
	}

	public static void main(String args[]){
		try {
			File f=new File("d:\\tmp\\books");
			new TreeDir(f,"Eladio","A");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void recurseDirs(File d, XMLNode navPoint) throws IOException {
		File list[]=d.listFiles();
		if(list==null)
			return;
		for(int i=0;i<list.length;i++){
			File f=list[i];
			String noExtUrl=f.getCanonicalPath().substring(1+dirCanonicalPath.length());
			noExtUrl=noExtUrl.replace('\\', '/');
			if(f.isDirectory()){
				XMLNode newTOC=createNavPoint(f.getName(), noExtUrl+".xhtml", navPoint);
				recurseDirs(f,newTOC);
				continue;
			}
			processFile(f, noExtUrl);
		}
	}

	public void setParam(String name,String value){
		pars.put(name, value);
	}
	
	@Override
	public String getCreator() {
		return pars.get("creator");
	}

	@Override
	public String getIdentifier() {
		return pars.get("identifier");
	}

	@Override
	public String getPublisher() {
		return pars.get("publisher");
	}

	@Override
	public String getRights() {
		return pars.get("rights");
	}

	@Override
	public Vector<String> getSubject() {
		return new Vector<String>();
	}

	@Override
	public String getTitle() {
		return pars.get("title");
	}

}
