package lrf.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BoundedRangeModel;
import javax.swing.JScrollBar;

import org.xhtmlrenderer.simple.FSScrollPane;

public class EPUBScrollPane extends FSScrollPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6056295381538370947L;
	final EPUBPanel view;


	public EPUBScrollPane(){
		this(new EPUBPanel());
	}
	
	public EPUBScrollPane(final EPUBPanel aview) {
		super(aview);
		view = aview;
		view.getActionMap().put(PAGE_DOWN, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent evt) {
				JScrollBar sb = getVerticalScrollBar();
				BoundedRangeModel brm = sb.getModel();
				int current = brm.getValue();
				brm.setValue(current + sb.getBlockIncrement(1));
				if (brm.getValue() == current) {
					// Fin de Pagina
					aview.nextSubDoc();
				}
			}
		});
		view.getActionMap().put(PAGE_UP, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent evt) {
				JScrollBar sb = getVerticalScrollBar();
				BoundedRangeModel brm = sb.getModel();
				int current = brm.getValue();
				brm.setValue(current - sb.getBlockIncrement(-1));
				if (brm.getValue() == current) {
					// Fin de Pagina
					aview.prevSubDoc();
				}
			}
		});
		view.getActionMap().put(PAGE_END, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent evt) {
				JScrollBar sb = getVerticalScrollBar();
				sb.getModel().setValue(sb.getModel().getMaximum());
			}
		});
		view.getActionMap().put(PAGE_START, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent evt) {
				JScrollBar sb = getVerticalScrollBar();
				sb.getModel().setValue(0);
			}
		});
		view.getActionMap().put(LINE_DOWN, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent evt) {
				JScrollBar sb = getVerticalScrollBar();
				BoundedRangeModel brm = sb.getModel();
				int current = brm.getValue();
				brm.setValue(current + sb.getUnitIncrement(1));
				if (brm.getValue() == current) {
					// Fin de Pagina
					aview.nextSubDoc();
				}
			}
		});
		view.getActionMap().put(LINE_UP, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent evt) {
				JScrollBar sb = getVerticalScrollBar();
				BoundedRangeModel brm = sb.getModel();
				int current = brm.getValue();
				brm.setValue(current - sb.getUnitIncrement(-1));
				if (brm.getValue() == current) {
					// Fin de Pagina
					aview.prevSubDoc();
				}
			}
		});
	}

}
