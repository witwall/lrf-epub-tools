package lrf.pdf;

import java.awt.Dimension;
import java.awt.print.PageFormat;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;

import org.apache.batik.dom.GenericDOMImplementation;
import org.pdfbox.pdfviewer.PageDrawer;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentCatalog;
import org.pdfbox.pdmodel.PDPage;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.lowagie.text.pdf.PdfReader;

public class PDF2SVG {

	public static File dirOrig=new File("D:\\tmp\\booksPDF");
	public static File dirDest=new File("D:\\tmp\\booksPDF");
	public static boolean noo=false;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		recurse(dirOrig);
	}

	public static void recurse(File dir){
		File list[]=dir.listFiles();
		for(int i=0;i<list.length;i++){
			if(list[i].isDirectory()){
				recurse(list[i]);
			}else if(list[i].getName().toLowerCase().endsWith(".pdf")){
				procPDF(list[i]);
			}
		}
	}
	

	@SuppressWarnings("unchecked")
	public static void procPDF(File pdfFile){

        // Ask the test to render into the SVG Graphics2D implementation.
        String x=null,y=null;
        try {
			x=pdfFile.getCanonicalPath();
			y=x.substring(dirOrig.getCanonicalPath().length());
			//Creamos epub
			File dest=new File(dirDest,y.substring(0,y.length()-4)+".epub");
			dest.getParentFile().mkdirs();
			if(noo && dest.exists())
				return;
			PDDocument doc=PDDocument.load(pdfFile);
			PDDocumentCatalog catalog=doc.getDocumentCatalog();
			List<PDPage> pages=catalog.getAllPages();
			PageDrawer pdr=new PageDrawer();
			//Creamos e inicializamos el epub
			PdfReader pdfReader=new PdfReader(pdfFile.getCanonicalPath());
			HashMap<String, String> info=pdfReader.getInfo();
			String title=info.get("Title");
			String author=info.get("Author");
			if(title==null)
				title="Unknown Title";
			if(author==null)
				author="Unknown Author";
			pdfReader.close();
			PDF2EPUB epub=new PDF2EPUB(title,author);
			epub.init(dest.getCanonicalPath());
			System.out.print("Processing "+pdfFile.getName());
			for(int i=0;i<pages.size();i++){
	            // Get a DOMImplementation.
	            DOMImplementation domImpl = 
	            	GenericDOMImplementation.getDOMImplementation();
	            // Create an instance of org.w3c.dom.Document.
	            String svgNS = "http://www.w3.org/2000/svg";
	            Document document = domImpl.createDocument(svgNS, "svg", null);
	            // Create an instance of the SVG Generator.
	            SVGHook svgGenerator = new SVGHook(document);
	            // Tamaño de la pagina
	            PageFormat pf=doc.getPageFormat(i);
	            int pageWidth=(int)pf.getWidth();
	            int pageHeight=(int)pf.getHeight();
				Dimension dim=new Dimension(pageWidth,pageHeight);
				//Dibujamos en SVG
				pdr.drawPage(svgGenerator, pages.get(i), dim);
		        // Finally, stream out SVG to the standard output using
		        // UTF-8 encoding.
		        boolean useCSS = true; // we want to use CSS style attributes
		        ByteArrayOutputStream baos=new ByteArrayOutputStream();
		        Writer out = new OutputStreamWriter(baos, "UTF-8");
		        svgGenerator.stream(out, useCSS);
		        out.close();
		        //Añadimos al epub la imagen y la pagina que la sostiene
		        epub.addBA("images/"+i+".svg", baos.toByteArray(), 5);
		        epub.addPage(i,pageWidth,pageHeight);
		        if(i%30==0)
		        	System.out.print(".");
			}
			System.out.print("endPages:");
			doc.close();
			epub.close();
			System.out.println("OK "+pdfFile.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
