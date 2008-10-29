package lrf.pdf;

import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.JPanel;

import org.jdesktop.application.SingleFrameApplication;
import org.pdfbox.pdfviewer.PageDrawer;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDPage;

/**
 * 
 */
public class Show extends SingleFrameApplication {
    private JPanel topPanel;
    PDDocument pd;
    @Override
    protected void startup() {
        topPanel = new JPanel();
        topPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        show(topPanel);
        List<PDPage> pages=pd.getDocumentCatalog().getAllPages();
        PDPage page=pages.get(6);
        try {
			PageDrawer pd=new PageDrawer();
			Dimension dim=topPanel.getSize();
			GraphicsHook gh=new GraphicsHook();
			pd.drawPage(gh, page, dim);
			pd.drawPage(topPanel.getGraphics(), page, dim);
			PrintWriter pw=new PrintWriter(new FileOutputStream("D:\\tmp\\p.txt"));
			pw.print(gh.toString());
			pw.close();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public Show(){
    	try {
			pd=PDDocument.load(new File("d:\\tmp\\p.pdf"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args) {
        launch(Show.class, args);
    }

}
