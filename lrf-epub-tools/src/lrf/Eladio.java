package lrf;

import java.io.File;

import lrf.merge.MergeEPUBAndTOC;

public class Eladio {
	static String dirOrg="d:\\_Eladio\\_Libros\\epubBooks\\singles";
	static String dirOut="d:\\_Eladio\\_Libros\\epubBooks\\grouped";

	public static void main(String args[]){
		File fOrg=new File(dirOrg);
		File list[]=fOrg.listFiles();
		for(int i=0;i<list.length;i++){
			try {
				processLetra(list[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void processLetra(File f) throws Exception{
		File list[]=f.listFiles();
		for(int i=0;i<list.length;i++){
			File fout=new File(dirOut,list[i].getName()+".epub");
			if(fout.exists()){
				System.out.println("Skipping "+fout.getName());
			}else{
				try {
					System.out.println("Creando "+fout.getName());
					MergeEPUBAndTOC met=new MergeEPUBAndTOC(
							fout,
							list[i].getName(),
							list[i].getName());
					processAuthor(met,list[i],null);
					met.close();
					System.out.println("OK "+fout.getName());
				} catch (Exception e) {
					System.out.println("Error "+fout.getName());
					throw e;
				}
			}
		}
	}
	
	public static void processAuthor(MergeEPUBAndTOC met, File f, String pp) throws Exception{
		File list[]=f.listFiles();
		for(int i=0;i<list.length;i++){
			String name=list[i].getName();
			if(list[i].isDirectory()){
				System.out.println(" Entering "+list[i].getName());
				processAuthor(met, list[i], list[i].getName());
			}else if(name.endsWith(".epub")){
				try {
					System.out.print("  Adding "+list[i].getName());
					met.appendBook(list[i], pp);
					System.out.println("  OK.");
				}catch(Exception excep){
					System.out.println("  Error.");
					throw excep;
				}
			}
		}
	}
}
