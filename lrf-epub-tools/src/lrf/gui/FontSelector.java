package lrf.gui;

import java.awt.GraphicsEnvironment;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComboBox;
import javax.swing.JLabel;


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
public class FontSelector extends javax.swing.JPanel implements  ItemListener  {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4950410185516025989L;
	JComboBox fonts;
    String fontChoice = "Dialog";
    int styleChoice = 0;
    int sizeChoice = 12;
    Oable oable;
    
	public FontSelector(Observer oer) {
		super();
		initGUI();
		oable=new Oable(oer);
	}
	
	class Oable extends Observable {
		public Oable(Observer e){
			addObserver(e);
		}
		public void changed(String choice){
			setChanged();
			notifyObservers(choice);
		}
	}
	
	private void initGUI() {
		try {
			this.setPreferredSize(new java.awt.Dimension(278, 34));
	        add(new JLabel("Font family:"));

	        GraphicsEnvironment gEnv =GraphicsEnvironment.getLocalGraphicsEnvironment();
	        fonts = new JComboBox(gEnv.getAvailableFontFamilyNames());
	        fonts.setSelectedItem(fontChoice);
	        fonts.setMaximumRowCount(5);
	        fonts.addItemListener(this);
	        add(fonts);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /*
     * Detect a state change in any of the settings and create a new
     * Font with the corresponding settings. Set it on the test component.
     */
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) {
            return;
        }
        if (e.getSource() == fonts) {
            fontChoice = (String)fonts.getSelectedItem();
            oable.changed(fontChoice);
        }
    }
    
}
