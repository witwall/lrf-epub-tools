package lrf.gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lrf.epub.EPUBDoc;

import org.jdesktop.application.Application;
import org.xhtmlrenderer.simple.XHTMLPanel;

import com.cloudgarden.resource.ArrayFocusTraversalPolicy;

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
public class EPUBScrollToolbarPanel extends javax.swing.JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6256909245654333194L;
	private JSlider slider;
	private JSeparator jSeparator1;
	private JToolBar infoLine;
	private JSlider autoScroll;
	private JLabel uriLabel;
	private JToolBar toolBar;
	private JScrollPane scroll;
	private XHTMLPanel xhtmlPanel;
    private JButton jButton1;
    private JButton jButton2;

    /** Constant used for mapping a key binding to "scroll down 1 page" */
    public static final String PAGE_DOWN = "page-down";

    /** Constant used for mapping a key binding to "scroll up 1 page" */
    public static final String PAGE_UP = "page-up";

    /** Constant used for mapping a key binding to "scroll down 1 line" */
    public static final String LINE_DOWN = "down";

    /** Constant used for mapping a key binding to "scroll up 1 line" */
    public static final String LINE_UP = "up";

    /** Constant used for mapping a key binding to "scroll to end of document" */
    public static final String PAGE_END = "page-end";

    /** Constant used for mapping a key binding to "scroll to top of document" */
    public static final String PAGE_START = "page-start";

    public EPUBUserAgentCallback uac=new EPUBUserAgentCallback("");
    
    class Timer extends Thread {
    	public boolean doNotScroll=false;
		public void run() {
			while(true){
				try { Thread.sleep(100); } catch (InterruptedException e) {}
				if(doNotScroll)
					continue;
				int aut=autoScroll.getValue();
				if(aut!=0){
	                JScrollBar sb = scroll.getVerticalScrollBar();
	                BoundedRangeModel brm=sb.getModel();
	                for(int i=0;i<aut;i++){
	                	brm.setValue(brm.getValue() + sb.getUnitIncrement(1));
	    				try { Thread.sleep(10); } catch (InterruptedException e) {}
	                }
				}
			}
		}
    }
    
    public Timer timer;
    
	public EPUBScrollToolbarPanel() {
		super();
		initGUI();
		initListener();
		timer=new Timer();
		timer.start();
	}
	
	private void initGUI() {
		try {
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			setPreferredSize(new Dimension(400, 300));
			{
				slider = new JSlider();
				this.add(slider, BorderLayout.EAST);
				slider.setOrientation(1);
				slider.setMajorTickSpacing(1);
				slider.setPaintTicks(true);
			}
			{
				toolBar = new JToolBar();
				this.add(toolBar, BorderLayout.NORTH);
				toolBar.setPreferredSize(new java.awt.Dimension(400, 20));
				{
					jButton1 = new JButton();
					toolBar.add(jButton1);
					jButton1.setName("jButton1");
				}
				{
					jButton2 = new JButton();
					toolBar.add(jButton2);
					jButton2.setName("jButton2");
				}
				{
					jSeparator1 = new JSeparator();
					toolBar.add(jSeparator1);
					jSeparator1.setOrientation(SwingConstants.VERTICAL);
				}
				{
					autoScroll = new JSlider();
					toolBar.add(autoScroll);
					autoScroll.setSize(100, 16);
					autoScroll.setPreferredSize(new java.awt.Dimension(100, 16));
					autoScroll.setValue(0);
					autoScroll.setMaximum(10);
					autoScroll.setMaximumSize(new java.awt.Dimension(100, 16));
				}
			}
			{
				scroll = new JScrollPane();
				this.add(scroll, BorderLayout.CENTER);
				scroll.setPreferredSize(new java.awt.Dimension(384, 296));
				{
					xhtmlPanel = new XHTMLPanel(uac);
					scroll.setViewportView(xhtmlPanel);
					xhtmlPanel.setPreferredSize(new java.awt.Dimension(368, 258));
				}
				scroll.setFocusCycleRoot(true);
				scroll.setFocusTraversalPolicy(new ArrayFocusTraversalPolicy(new java.awt.Component[] {xhtmlPanel}));
			}
			{
				infoLine = new JToolBar();
				this.add(infoLine, BorderLayout.SOUTH);
				{
					uriLabel = new JLabel();
					infoLine.add(uriLabel);
					uriLabel.setName("uriLabel");
					uriLabel.setBorder(BorderFactory.createTitledBorder(""));
					uriLabel.setSize(300, 10);
					uriLabel.setPreferredSize(new java.awt.Dimension(268, 16));
				}
			}
			this.setFocusTraversalPolicy(new ArrayFocusTraversalPolicy(new java.awt.Component[] {scroll}));
			this.setFocusCycleRoot(true);
			Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void initListener(){
        xhtmlPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
        	put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), PAGE_DOWN);
        xhtmlPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
        	put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), PAGE_UP);
        xhtmlPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
        	put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), LINE_DOWN);
        xhtmlPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
        	put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), LINE_UP);
        xhtmlPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
        	put(KeyStroke.getKeyStroke(KeyEvent.VK_END, KeyEvent.CTRL_MASK), PAGE_END);
        xhtmlPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
        	put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), PAGE_END);
        xhtmlPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
        	put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.CTRL_MASK), PAGE_START);
        xhtmlPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
        	put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), PAGE_START);
        xhtmlPanel.getActionMap().put(PAGE_DOWN,
                new AbstractAction() {
                    private static final long serialVersionUID = 1L;
                    public void actionPerformed(ActionEvent evt) {
                    	mgrVSB(1, 2, 1);
                    }
                });
        xhtmlPanel.getActionMap().put(PAGE_END,
                new AbstractAction() {
                    private static final long serialVersionUID = 1L;
                    public void actionPerformed(ActionEvent evt) {
                    	mgrVSB(2, 0, 0);
                    }
                });
        xhtmlPanel.getActionMap().put(PAGE_UP,
                new AbstractAction() {
                    private static final long serialVersionUID = 1L;
                    public void actionPerformed(ActionEvent evt) {
                    	mgrVSB(-1, 2, 1);
                    }
                });
        xhtmlPanel.getActionMap().put(PAGE_START,
                new AbstractAction() {
                    private static final long serialVersionUID = 1L;
                    public void actionPerformed(ActionEvent evt) {
                    	mgrVSB(-2, 0, 0);
                    }
                });
        xhtmlPanel.getActionMap().put(LINE_DOWN,
                new AbstractAction() {
                    private static final long serialVersionUID = 1L;
                    public void actionPerformed(ActionEvent evt) {
                    	mgrVSB(1, 1, 1);
                    }
                });
        xhtmlPanel.getActionMap().put(LINE_UP,
                new AbstractAction() {
                    private static final long serialVersionUID = 1L;
                    public void actionPerformed(ActionEvent evt) {
                    	mgrVSB(-1, 1, 1);
                    }
                });
        xhtmlPanel.addComponentListener( new ComponentAdapter() {
            /** Invoked when the component's size changes. Reset scrollable increment, because
             * page-down/up is relative to current view size.
             */
            public void componentResized(ComponentEvent e) {
                JScrollBar bar = scroll.getVerticalScrollBar();
                timer.doNotScroll=true;
                // NOTE: use the scroll pane size--the XHTMLPanel size is a virtual size of the entire
                // page
                
                // want to page down leaving the current line at the bottom be the first at the top
                // TODO: this will only work once unit increment is set correctly; multiplier is a workaround (PWW 28-01-05)
                int incr = (int)(getSize().getHeight() - (bar.getUnitIncrement(1) * 3));
                scroll.getVerticalScrollBar().setBlockIncrement(incr);
                timer.doNotScroll=false;
            }
        });        
        jButton1.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		scaleFS(true);
        	};});
        jButton2.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		scaleFS(false);
        	};});
        slider.addChangeListener(new ChangeListener(){
        	@Override
        	public void stateChanged(ChangeEvent e) {
        		if(epd==null)
        			return;
        		int sv=slider.getValue();
        		int nd=epd.getNumOfDocs();
        		if(lastInnerDoc!=nd-1-sv && sv<nd )
    				setDocument(epd.getURLForSpine(nd-1-sv));
        	};});
	}
	int lastInnerDoc=0;
	String currentUri;
	EPUBDoc epd;
	public void setDocument(String uri){
		currentUri=uri;
		xhtmlPanel.setVisible(false);
		uriLabel.setText(uri);
		uac.setBaseURL(uri);
		xhtmlPanel.setDocument(uri);
		epd=EPUBDoc.load(uri);
		slider.setMaximum(epd.getNumOfDocs());
		lastInnerDoc=epd.getLastServedSpine();
		slider.setValue(epd.getNumOfDocs()-lastInnerDoc-1);
		xhtmlPanel.setVisible(true);
	}
	/**
	 * Manages Scroll. It must change DocNumber from epub if possible
	 * @param action -1 back, 1 forward, 2 PageEnd, -2 pageStart
	 * @param object 1 unit, 2 block
	 * @param increment vertical increment measured in 'unit' or 'block'
	 */
	private void mgrVSB(int action, int object, int increment){
        JScrollBar bar = scroll.getVerticalScrollBar();
        BoundedRangeModel brm=bar.getModel();
        int cv=brm.getValue();
        boolean atBeginning=(cv==0);
        boolean atEnd=(cv==brm.getMaximum()-brm.getExtent());
        int last=epd.getLastServedSpine();
        int max=epd.getNumOfDocs();
        if(action==2){
        	brm.setValue(brm.getMaximum());
        }else if(action==-2){
        	brm.setValue(0);
        }else if(atEnd && action>0){
        	if(last<max-1){
        		setDocument(epd.getURLForSpine(last+1));
        	}
        }else if(atBeginning && action<0){
        	if(last>0){
        		setDocument(epd.getURLForSpine(last-1));
            	xhtmlPanel.setVisible(false);
            	brm.setValue(brm.getMaximum());
            	xhtmlPanel.setVisible(true);
        	}
        }else{
        	int incr=(object==1 ? bar.getUnitIncrement(action) : bar.getBlockIncrement(action));
        	brm.setValue(cv+action*incr);
        }
	}
	
	private void scaleFS(boolean increase){
		xhtmlPanel.setVisible(false);
        JScrollBar bar = scroll.getVerticalScrollBar();
        BoundedRangeModel brm=bar.getModel();
        int cv=brm.getValue();
        if(increase)
        	xhtmlPanel.incrementFontSize();
        else
        	xhtmlPanel.decrementFontSize();
        brm.setValue(cv);
		xhtmlPanel.setVisible(true);
	}
}
