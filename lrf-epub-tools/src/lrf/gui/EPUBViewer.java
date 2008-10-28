package lrf.gui;
import com.cloudgarden.resource.ArrayFocusTraversalPolicy;
import java.awt.BorderLayout;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import lrf.epub.EPUBDoc;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class EPUBViewer extends javax.swing.JFrame implements Observer {
	private JScrollPane jScrollPane1;
	private EPUBScrollToolbarPanel ePUBScrollToolbarPanel1;
	private JPanel jPanel1;
	private FileTree fileTree1;
	private JSplitPane jSplitPane1;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				EPUBViewer inst = new EPUBViewer();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public EPUBViewer() {
		super();
		initGUI();
	    fileTree1.getObservable().addObserver(this);
}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			this.setPreferredSize(new java.awt.Dimension(800, 300));
			this.setMinimumSize(new java.awt.Dimension(650, 50));
			{
				jSplitPane1 = new JSplitPane();
				getContentPane().add(jSplitPane1, BorderLayout.CENTER);
				{
					jScrollPane1 = new JScrollPane();
					jSplitPane1.add(jScrollPane1, JSplitPane.LEFT);
					jScrollPane1.setPreferredSize(new java.awt.Dimension(300, 258));
					jScrollPane1.setMinimumSize(new java.awt.Dimension(300, 50));
					{
						fileTree1 = new FileTree();
						jScrollPane1.setViewportView(fileTree1);
						fileTree1.setPreferredSize(new java.awt.Dimension(300, 257));
						fileTree1.setMinimumSize(new java.awt.Dimension(300, 50));
						fileTree1.setMaximumSize(new java.awt.Dimension(600, 800));
						fileTree1.setSize(300, 1116);
					}
				}
				{
					jPanel1 = new JPanel();
					BorderLayout jPanel1Layout = new BorderLayout();
					jSplitPane1.add(jPanel1, JSplitPane.RIGHT);
					jPanel1.setLayout(jPanel1Layout);
					jPanel1.setMinimumSize(new java.awt.Dimension(300, 50));
					{
						ePUBScrollToolbarPanel1 = new EPUBScrollToolbarPanel();
						jPanel1.add(ePUBScrollToolbarPanel1, BorderLayout.CENTER);
						ePUBScrollToolbarPanel1.setPreferredSize(new java.awt.Dimension(300, 300));
					}
				}
				jSplitPane1.setFocusCycleRoot(true);
				jSplitPane1.setFocusTraversalPolicy(new ArrayFocusTraversalPolicy(new java.awt.Component[] {jScrollPane1, jPanel1}));
				jSplitPane1.setMinimumSize(new java.awt.Dimension(650, 50));
			}
			pack();
			this.setSize(650, 300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void update(Observable o, Object arg) {
		try {
			File f=fileTree1.epubToShow;
			EPUBDoc doc=EPUBDoc.load(f);
			String app=fileTree1.linkInsideEpub;
			String uri;
			if(app==null || app.length()==0){
				uri=doc.getRootURL();
			}else{
				uri=EPUBDoc.toEPUBUrl(f)+app;
			}
			ePUBScrollToolbarPanel1.setDocument(uri);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
