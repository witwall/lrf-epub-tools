package lrf.gui;

import javax.swing.JFrame;

public class Viewer {
	String file;
	public Viewer(String file){
		this.file=file;
	}
	
	public void run(){
		// Create a JPanel subclass to render the page
	    EPUBPanel panel;
		try {
			file=file.replace(" ", "%20");
			file=file.replace("\\", "/");
			String url="epub://"+file+"/";
			
			panel = new EPUBPanel(url);
		    // Put our panel in a scrolling pane. You can use
		    // a regular JScrollPane here, or our FSScrollPane.
		    // FSScrollPane is already set up to move the correct
		    // amount when scrolling 1 line or 1 page
		    EPUBScrollPane scroll = new EPUBScrollPane(panel);
		    JFrame frame = new JFrame("LRFTools EPUB Viewing");
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    frame.getContentPane().add(scroll);
		    frame.pack();
		    frame.setSize(600, 800);
		    frame.setVisible(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
