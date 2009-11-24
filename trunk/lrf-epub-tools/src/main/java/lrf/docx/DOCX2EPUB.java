package lrf.docx;

import java.io.File;
import java.io.IOException;

public class DOCX2EPUB {
	
	File dirDes=null;
	File dirOri=null;
	
	public DOCX2EPUB(File ori, File des){
		dirDes=des;
		dirOri=ori;
	}
	
	public void process(String docname) {
		try {
			//Creamos el contexto.
			Context ctx=new Context(docname);
			int ldo=dirOri.getCanonicalPath().length();
			String fdst=docname.substring(ldo);
			System.out.print("converting "+fdst);
			fdst=fdst.substring(0,fdst.length()-5)+".epub";
			File epubFile=new File(dirDes,fdst);
			epubFile.getParentFile().mkdirs();
			// Parse Document XML file with default handler
			ctx.parse(epubFile);
			//Final
			System.out.println(" done.");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void recurse(File dir){
		File list[]=dir.listFiles();
		for(int i=0;i<list.length;i++){
			if(list[i].isDirectory()){
				recurse(list[i]);
			}else if(list[i].getName().toLowerCase().endsWith(".docx")){
				try {
					process(list[i].getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	
}
