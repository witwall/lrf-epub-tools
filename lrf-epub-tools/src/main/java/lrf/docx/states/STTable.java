package lrf.docx.states;

import lrf.docx.Context;
import lrf.docx.SHRelations;

import org.xml.sax.Attributes;

public class STTable implements State {
	private static int vmerge;
	private static int id, id2;// temporary index
	private final int TCW = 15;// 1440/PixelsPerInch
	private static STTable singleton = new STTable();

	private STTable() {
		init();
	}

	public static STTable getInstance() {
		return singleton;
	}

	public void startDoc(Context context) {
	}

	public void startEle(Context context, SHRelations rels, String uri,
			String localName, String qName, Attributes attributes) {
		if (localName.equals("tbl")) {
			String str = "<table cellpadding=\"2\" cellspacing=\"2\" border=\"1\">"
					+ "<tbody>";
			context.addData(str);
		} else if (localName.equals("tr")) {
			context.addData("<tr>");
		} else if (localName.equals("tc")) {
			id = context.getCount();
			context.addData("<td style=\"vertical-align: top;\">");
		} else if (localName.equals("tcW")) {
			int tc_width = Integer.parseInt(attributes.getValue(uri, "w"))
					/ TCW;
			context.rewriteData(id, "<td style=\"vertical-align: top;width:"
					+ tc_width + "px;\">");
		} else if (localName.equals("gridSpan")) {
			context.rewriteData(id,
					"<td style=\"vertical-align: top;\"colspan=\""
							+ attributes.getValue(uri, "val") + "\">");
		} else if (localName.equals("vmerge")) {
			if (attributes.getValue(uri, "val") == null) {
				vmerge++;
				context.removeData(id);// delete <td>
				context.rewriteData(id2,
						"<td style=\"vertical-align: top;\" rowspan=\""
								+ vmerge + "\">");
			} else if (attributes.getValue(uri, "val").equals("restart")) {
				vmerge = 1;
				context.rewriteData(id,
						"<td style=\"vertical-align: top;\" rowspan=\""
								+ vmerge + "\">");
				id2 = id;
			}
		}
	}

	public void endEle(Context context, String uri, String localName,
			String qName) {
		if (localName.equals("tc")) {
			if (vmerge < 2) { // if vmerge >= 2, </td> is already added
				context.addData("</td>");
			}
		} else if (localName.equals("tr")) {
			context.addData("</tr>");
		} else if (localName.equals("tbl")) {
			context.addData("</tbody></table>");
		}
	}

	public void endDoc(Context context) {
	}

	public void init() {
		vmerge = 0;
		id = 0;
		id2 = 0;
	}
}