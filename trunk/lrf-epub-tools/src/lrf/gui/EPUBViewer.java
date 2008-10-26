package lrf.gui;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import lrf.epub.EPUBDoc;

import org.jdesktop.application.Application;


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
public class EPUBViewer extends javax.swing.JFrame {
	private JToolBar jToolBar1;
	private JScrollPane jScrollPane1;
	private JLabel jLabel4;
	private JLabel jLabel5;
	private JLabel jLabel8;
	private JLabel jLabel7;
	private JLabel jLabel6;
	private JLabel jLabel3;
	private JLabel jLabel2;
	private JLabel jLabel1;
	private JPanel jPanel1;
	private JTabbedPane jTabbedPane1;
	private FileTree fileTree1;
	private EPUBScrollPane ePUBScrollPane1;
	private JSplitPane jSplitPane1;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		EPUBDoc.initHandler();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				EPUBViewer inst = new EPUBViewer();
				inst.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public EPUBViewer() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			{
				jToolBar1 = new JToolBar();
				getContentPane().add(jToolBar1, BorderLayout.NORTH);
			}
			{
				jSplitPane1 = new JSplitPane();
				getContentPane().add(jSplitPane1, BorderLayout.CENTER);
				{
					jTabbedPane1 = new JTabbedPane();
					jSplitPane1.add(jTabbedPane1, JSplitPane.RIGHT);
					jTabbedPane1.setPreferredSize(new java.awt.Dimension(393, 434));
					{
						ePUBScrollPane1 = new EPUBScrollPane();
						jTabbedPane1.addTab("Viewer", null, ePUBScrollPane1, null);
						ePUBScrollPane1.setPreferredSize(new java.awt.Dimension(0, 0));
						ePUBScrollPane1.setName("ePUBScrollPane1");
					}
					{
						jPanel1 = new JPanel();
						GridBagLayout jPanel1Layout = new GridBagLayout();
						jTabbedPane1.addTab("Properties", null, jPanel1, null);
						jPanel1Layout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1};
						jPanel1Layout.rowHeights = new int[] {7, 7, 7, 7};
						jPanel1Layout.columnWeights = new double[] {0.1, 0.1, 0.1, 0.1};
						jPanel1Layout.columnWidths = new int[] {7, 7, 7, 7};
						jPanel1.setLayout(jPanel1Layout);
						{
							jLabel1 = new JLabel();
							jPanel1.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							jLabel1.setName("jLabel1");
						}
						{
							jLabel2 = new JLabel();
							jPanel1.add(jLabel2, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							jLabel2.setName("jLabel2");
						}
						{
							jLabel3 = new JLabel();
							jPanel1.add(jLabel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							jLabel3.setName("jLabel3");
						}
						{
							jLabel4 = new JLabel();
							jPanel1.add(jLabel4, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							jLabel4.setName("jLabel4");
						}
						{
							jLabel5 = new JLabel();
							jPanel1.add(jLabel5, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							jLabel5.setName("jLabel5");
						}
						{
							jLabel6 = new JLabel();
							jPanel1.add(jLabel6, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							jLabel6.setName("jLabel6");
						}
						{
							jLabel7 = new JLabel();
							jPanel1.add(jLabel7, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							jLabel7.setName("jLabel7");
						}
						{
							jLabel8 = new JLabel();
							jPanel1.add(jLabel8, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							jLabel8.setName("jLabel8");
						}
					}
				}
				{
					jScrollPane1 = new JScrollPane();
					jSplitPane1.add(jScrollPane1, JSplitPane.LEFT);
					jScrollPane1.setPreferredSize(new java.awt.Dimension(287, 434));
					{
						fileTree1 = new FileTree();
						jScrollPane1.setViewportView(fileTree1);
					}
				}
			}
			this.setSize(705, 476);
			Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(getContentPane());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
