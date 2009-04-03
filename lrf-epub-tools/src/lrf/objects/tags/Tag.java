package lrf.objects.tags;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import lrf.Utils;
import lrf.buffer.Reader;
import lrf.conv.Renderable;
import lrf.conv.Renderer;
import lrf.io.BBeBOutputStream;
import lrf.io.LRFSerial;
import lrf.objects.BBObj;
import lrf.parse.ParseException;

public class Tag implements Renderable, LRFSerial {
	int id;
	Vector<Integer> list = new Vector<Integer>();
	String stringVal = null;
	public BBObj padre = null;
	public static int currentFontSize=-1;
	public static int currentBaseLineSkip=-1;
	public static int fontSizeSigma=0;
	boolean emitLN=true;

	@Override
	public int serial(BBeBOutputStream os, int promoteID) throws IOException {
		// Cabecera
		os.putByte(id);
		os.putByte(0xf5);
		int ret = 2;
		if (id == 0xCC) { // Es un texto
			ret += os.putString(stringVal);
		} else {
			for (int i = 0; i < getNumValues(); i++) {
				switch (getValueSizeAt(i)) {
				case 2:
					ret += os.putShort(getValueAt(i));
					break;
				case 4:
					ret += os.putInt(getValueAt(i));
					break;
				default:
					throw new IOException("Unknown tag value size");
				}
			}
		}
		return ret;
	}

	public int getType() {
		return id;
	}

	public void setType(int ty) {
		id=ty;
	}

	public Tag(int i, BBObj p) {
		id = i;
		padre = p;
	}

	public void addValue(int val, int size) {
		list.add(new Integer(val));
		list.add(new Integer(size));
		int pos=list.size()/2-1;
		setValueAt(pos, val, size);
	}

	/**
	 * valor de la lista
	 * 
	 * @param pos
	 * 		Posicion en la lista del valor
	 * @return
	 * @throws Exception
	 */
	public int getValueAt(int pos) {
		if (2 * pos < list.size())
			return list.elementAt(2 * pos);
		return 0xff;
	}

	public void setValueAt(int pos, int val, int size){
		if(2*pos>=list.size())
			return;
		String name=getName();
		if(name.equalsIgnoreCase("FontSize")){
			if(currentFontSize<0)
				currentFontSize=val;
			int incr=(val-currentFontSize)*100/currentFontSize;
			if(Math.abs(incr)<fontSizeSigma){
				val=currentFontSize;
			}
		}
		if(name.equals("BaseLineSkip")){
			if(currentBaseLineSkip<0)
				currentBaseLineSkip=val;
			int incr=fontSizeSigma;
			if(currentBaseLineSkip!=0)
				incr=(val-currentBaseLineSkip)*100/currentBaseLineSkip;
			if(Math.abs(incr)<fontSizeSigma){
				val=currentBaseLineSkip;
			}
		}
		list.set(2*pos, val);
		list.set(2*pos+1, size);
	}
	
	public int getValueSizeAt(int pos) {
		if (2 * pos + 1 < list.size())
			return list.elementAt(2 * pos + 1).intValue();
		return -1;
	}

	public int getNumValues() {
		return list.size() / 2;
	}

	public void setStringVal(String s) {
		stringVal = s;
	}

	public String getStringVal() {
		return stringVal;
	}

	public String getName() {
		if(id>=tagNames.length)
			return "Unknown";
		return tagNames[id];
	}

	public static String tagNames[] = {
			"*ObjectStart",
			"*ObjectEnd",
			"*ObjectInfoLink",
			"*Link",
			"*StreamSize",
			"*StreamStart",
			"*StreamEnd",
			"OddHeaderID",
			"EvenHeaderId",
			"OddFooterId",
			"EvenFooterId",
			"*ContainedObjectsList",
			"Unknown_0C",
			"Unknown_0D",
			"Unknown_0E",
			"Unknown_0F",
			"Unknown_10",
			"FontSize",
			"FontWidth",
			"FontEscapement",
			"FontOrientation",
			"FontWeight",
			"FontFacename",
			"TextColor",
			"TextBgColor",
			"WordSpace",
			"LetterSpace",
			"BaseLineSkip",
			"LineSpace",
			"ParIndent",
			"ParSkip",
			"Unknown_1F",
			"Unknown_20",
			"TopMargin",
			"HeadHeight",
			"HeadSep",
			"OddSideMargin",
			"*PageHeight",
			"*PageWidth",
			"FootSpace",
			"FootHeight",
			"BGImageName",
			"SetEmptyView", /* 1 show, 2 empty */
			"PagePosition", // 0 - any, 1 - upper, 2 0 lower
			"EvenSideMargin", "Unknown_2D", "BlockAttrUnknown0", "Unknown_2F",
			"Unknown_30", "BlockWidth", "BlockHeight", "BlockRule",
			"Unknown_34", "Layout", "Unknown_36", "Unknown_37", "Unknown_38",
			"Unknown_39", "Unknown_3A", "Unknown_3B", "BlockAlignment", /*
																		 * 1=left
																		 * ,
																		 * 4=center
																		 */
			"Unknown_3D", "Unknown_3E", "Unknown_3F", "Unknown_40",
			"*MiniPageHeight", "*MiniPageWidth", "Unknown_43", "Unknown_44",
			"Unknown_45", "*LocationY", "*LocationX", "Unknown_48", "PutSound",
			"*ImageRect", "*ImageSize", "*ImageStream", "Unknown_4D",
			"Unknown_4E", "Unknown_4F", "Unknown_50", "*CanvasWidth",
			"*CanvasHeight", "Unknown_53", "*StreamFlags", "ImageNamePerhaps",
			"Unknown_56", "Unknown_57", "Unknown_58", "*FontFileName",
			"Unknown_5A", "ViewPoint", "*PageList", "*FontFaceName",
			"Unknown_5E", "Unknown_5F", "Unknown_60", "ButtonFlags",
			"BaseButtonStart", "BaseButtonEnd", "FocusinButtonStart",
			"FocusinButtonEnd", "PushButtonStart", "PushButtonEnd",
			"UpButtonStart", "UpButtonEnd", "StartActionsStart",
			"StartActionsEnd", "*JumpTo", "SendMessage", "CloseWindow",
			"Unknown_6F", "Unknown_70", "Unknown_71", "Unknown_72",
			"RuledLine", "Unknown_74", "RubyAlign", "RubyOverhang",
			"EmpDotsPosition", "EmpDotsCode", "EmpLinePosition", "EmpLineMode",
			"*ChildPageTree", "*ParentPageTree", "Unknown_7D", "Unknown_7E",
			"Unknown_7F", "Unknown_80", "ItalicBegin", "ItalicEnd",
			"Unknown_83", "Unknown_84", "Unknown_85", "Unknown_86",
			"Unknown_87", "Unknown_88", "Unknown_89", "Unknown_8A",
			"Unknown_8B", "Unknown_8C", "Unknown_8D", "Unknown_8E",
			"Unknown_8F", "Unknown_90", "Unknown_91", "Unknown_92",
			"Unknown_93", "Unknown_94", "Unknown_95", "Unknown_96",
			"Unknown_97", "Unknown_98", "Unknown_99", "Unknown_9A",
			"Unknown_9B", "Unknown_9C", "Unknown_9D", "Unknown_9E",
			"Unknown_9F", "Unknown_A0", "BeginP", "EndP", "Unknown_A3",
			"Unknown_A4", "KomaGaiji", "KomaEmpDotChar?", "BeginButton",
			"EndButton", "BeginRuby", "EndRuby", "*BeginRubyBase",
			"*EndRubyBase", "*BeginRubyText", "*EndRubyText", "Unknown_AF",
			"Unknown_B0", "KomaYokomoji ", "Unknown_B2", "TateBegin",
			"TateEnd", "NekaseBegin", "NekaseEnd", "BeginSup", "EndSup",
			"BeginSub", "EndSub", "Unknown_BB", "Unknown_BC", "Unknown_BD",
			"Unknown_BE", "Unknown_BF", "Unknown_C0", "BeginEmptyLine",
			"EndEmptyLine", "BeginDrawChar", "EndDrawChar", "Unknown_C5",
			"Unknown_C6", "Unknown_C7", "KomaAutoSpacing", "Unknown_C9",
			"Space", "Unknown_CB", "Unknown_CC", "Unknown_CD", "Unknown_CE",
			"Unknown_CF", "Unknown_D0", "KomaPlot", "EOL", "Unknown_D3",
			"Wait", "Unknown_D5", "SoundStop", "MoveObj", "*BookFont",
			"KomaPlotText", "SetWaitProp", /* 1=replay,2=noreplay */
			"Unknown_DB", "Unknown_DC", "CharSpace", "Unknown_DE",
			"Unknown_DF", "Unknown_E0", "Unknown_E1", "Unknown_E2",
			"Unknown_E3", "Unknown_E4", "Unknown_E5", "Unknown_E6",
			"Unknown_E7", "Unknown_E8", "Unknown_E9", "Unknown_EA",
			"Unknown_EB", "Unknown_EC", "Unknown_ED", "Unknown_EE",
			"Unknown_EF", "Unknown_F0", "LineWidth", "LineColor", "FillColor",
			"LineMode", "MoveTo", "LineTo", "DrawBox", "DrawEllipse",
			"Unknown_F9", "Unknown_FA", "Unknown_FB", "Unknown_FC",
			"Unknown_FD", "Unknown_FE", "Unknown_FF" };

	public static final int tagSize[] = { 6, // 00
			0, // 01
			4, // 02
			4, // 03
			4, // 04
			0, // 05
			0, // 06
			4, // 07
			4, // 08
			4, // 09
			4, // 0A
			0, // 0B
			-1, // 0C
			0, // 0D
			2, // E
			-1, // 0F
			-1, // 10
			2, // 11
			2, // 12
			2, // 13
			2, // 14
			2, // 15
			0, // 16
			4, // 17
			4, // 18
			2, // 19
			2, // 1A
			2, // 1B
			2, // 1C
			2, // 1D
			2, // E
			-1, // 1F
			-1, // 20
			2, // 21
			2, // 22
			2, // 23
			2, // 24
			2, // 25
			2, // 26
			2, // 27
			2, // 28
			6, // 29
			2, // 2A
			2, // 2B
			2, // 2C
			4, // 2D
			2, // E
			-1, // 2F
			-1, // 30
			2, // 31
			2, // 32
			2, // 33
			4, // 34
			2, // 35
			2, // 36
			4, // 37
			2, // 38
			2, // 39
			2, // 3A
			-1, // 3B
			2, // 3C
			2, // 3D
			2, // E
			-1, // 3F
			-1, // 40
			2, // 41
			2, // 42
			-1, // 43
			4, // 44
			4, // 45
			2, // 46
			2, // 47
			2, // 48
			8, // 49
			8, // 4A
			4, // 4B
			4, // 4C
			0, // 4D
			12, // E
			-1, // 4F
			-1, // 50
			2, // 51
			2, // 52
			4, // 53
			2, // 54
			0, // 55
			0, // 56
			2, // 57
			2, // 58
			0, // 59
			0, // 5A
			4, // 5B
			0, // 5C
			0, // 5D
			2, // E
			-1, // 5F
			-1, // 60
			2, // 61
			0, // 62
			0, // 63
			0, // 64
			0, // 65
			0, // 66
			0, // 67
			0, // 68
			0, // 69
			0, // 6A
			0, // 6B
			0, // 6C
			0, // 6D
			0, // E
			-1, // 6F
			-1, // 70
			0, // 71
			0, // 72
			10, // 73
			-1, // 74
			2, // 75
			2, // 76
			2, // 77
			0, // 78
			2, // 79
			2, // 7A
			4, // 7B
			4, // 7C
			-1, // 7D
			-1, // E
			-1, // 7F
			-1, // 80
			0, // 81
			0, // 82
			-1, // 83
			-1, // 84
			-1, // 85
			-1, // 86
			-1, // 87
			-1, // 88
			-1, // 89
			-1, // 8A
			-1, // 8B
			-1, // 8C
			-1, // 8D
			-1, // E
			-1, // 8F
			-1, // 90
			-1, // 91
			-1, // 92
			-1, // 93
			-1, // 94
			-1, // 95
			-1, // 96
			-1, // 97
			-1, // 98
			-1, // 99
			-1, // 9A
			-1, // 9B
			-1, // 9C
			-1, // 9D
			-1, // E
			-1, // 9F
			-1, // A0
			4, // A1
			0, // A2
			-1, // A3
			-1, // A4
			0, // A5
			0, // A6
			4, // A7
			0, // A8
			0, // A9
			0, // AA
			0, // AB
			0, // AC
			0, // AD
			0, // AE
			-1, // AF
			-1, // B0
			0, // B1
			0, // B2
			0, // B3
			0, // B4
			0, // B5
			0, // B6
			0, // B7
			0, // B8
			0, // B9
			0, // BA
			0, // BB
			0, // BC
			0, // BD
			0, // BE
			-1, // BF
			-1, // C0
			0, // C1
			0, // C2
			2, // C3
			0, // C4
			2, // C5
			2, // C6
			0, // C7
			2, // C8
			0, // C9
			2, // CA
			0, // CB
			2, // CC
			-1, // CD
			-1, // CE
			-1, // CF
			-1, // D0
			0, // D1
			0, // D2
			-1, // D3
			2, // D4
			-1, // D5
			0, // D6
			14, // D7
			4, // D8
			8, // D9
			2, // DA
			2, // DB
			2, // DC
			2, // DD
			-1, // DE
			-1, // DF
			-1, // E0
			-1, // E1
			-1, // E2
			-1, // E3
			-1, // E4
			-1, // E5
			-1, // E6
			-1, // E7
			-1, // E8
			-1, // E9
			-1, // EA
			-1, // EB
			-1, // EC
			-1, // ED
			-1, // EE
			-1, // EF
			-1, // F0
			2, // F1
			4, // F2
			4, // F3
			2, // F4
			4, // F5
			4, // F6
			4, // F7
			4, // F8
			6, // F9
			-1, // FA
			-1, // FB
			-1, // FC
			-1, // FD
			-1, // FE
			-1, // FF
	};

	public String toString() {
		StringBuffer sb = new StringBuffer();
		toXML(sb,0);
		return sb.toString();
	}

	public Vector<Integer> forwardRefs() {
		Vector<Integer> ret = new Vector<Integer>();
		String tName = tagNames[id];
		if (tName.equals("*ChildPageTree")) {
			ret.add(list.elementAt(0));
		} else if (tName.equals("*PageList")) {
			for (int i = 0; i < list.size() / 2; i++)
				ret.add(list.elementAt(i * 2));
		} else if (tName.equals("*ContainedObjectsList")) {
			for (int i = 0; i < list.size() / 2; i++)
				ret.add(list.elementAt(i * 2));
		} else if (tName.equals("*Link")) {
			ret.add(list.elementAt(0));
		}
		return ret;
	}

	public void toXML(StringBuffer sb, int level) {
		String tName = tagNames[id];
		if (tName.equals("*ChildPageTree")) {
			monoLinkRef(sb, tName, level+1);
		} else if (tName.equals("*PageList")) {
			//pad(sb, "<Tag Type=\"" + tName + "\">", false);
			multipleLinkRefs(sb,level+1);
			//pad(sb, "</Tag>", false);
		} else if (tName.equals("*ContainedObjectsList")) {
			//pad(sb, "<Tag Type=\"" + tName + "\">", false);
			multipleLinkRefs(sb,level+1);
			//pad(sb, "</Tag>", false);
		} else if (tName.equals("*Link")) {
			monoLinkRef(sb, tName, level+1);
		} else if (tName.equals("KomaPlot")) {
			try {
				padele(level+1,sb,"KomaPlot",true,false,
						true,"h",
						""+getValueAt(0),"w",
						""+getValueAt(1),"u", ""+getValueAt(3));
				padre.getPadre().getObject(getValueAt(2)).toXML(sb, level+1);
				padele(level+1,sb,"KomaPLot",false,true, true);
			} catch (Exception e) {
			}
		} else if (tName.equals("*ParentPageTree")) {
		} else if (tName.equals("Unknown_CC")) {
			pad(sb,stringVal+"\n",true);
		} else if (tName.equals("*ImageStream")) {
			padele(level,sb,"ImageStream",true,false, true);
			padre.getPadre().getObject(list.elementAt(0)).toXML(sb, level);
			padele(level,sb,"ImageStream",false,true, true);
		} else if (tName.equals("*JumpTo")) {
			padele(level,sb,"JumpTo",true,true,
					true,"Page",
					""+list.elementAt(0),"Block", ""+list.elementAt(2));
		} else if (tName.equals("PutSound")) {
			padele(level,sb,"PutSound",true,true,
					true,"Ref1",
					""+getValueAt(0),"Block", ""+getValueAt(1));
		} else {
			if (list.size() == 2) {
				padele(level,sb,tName,true,true,true,"Val", ""+list.elementAt(0));
			} else if (list.size() == 0 && stringVal != null) {
				padele(level,sb,"StringVal",true,true,true,"Val", stringVal);
			} else if (list.size() == 0) {
				padele(level,sb,tName,true,true, true);
			} else {
				padele(level,sb,tName,true,false, true);
				defaultDump(level+1,sb);
				padele(level,sb,tName,false,true, true);
			}
		}
	}

	private void monoLinkRef(StringBuffer sb, String tName, int level) {
		if (list.size() > 0) {
			BBObj xRef = padre.getPadre().getObject(list.elementAt(0));
			if(xRef!=null) {
				xRef.toXML(sb, level);
			}else{
				String xRefType = (xRef != null ? xRef.getTypeName() : "NOTFOUND");
				pad(sb, "\n<Tag Type=\"" + tName + "\" ObjRef=\""
						+ list.elementAt(0) + "\" RefType=\"" + xRefType + "\"/>", false);
			}
		} else {
			pad(sb, "\n<Tag Type=\"" + tName
					+ "\" ObjRef=\"NOTEXIST\" RefType=\"NOTFOUND\"/>", false);
		}
	}

	private void multipleLinkRefs(StringBuffer sb, int level) {
		int numRefs = list.size() / 2;
		for (int i = 0; i < numRefs; i++) {
			int xRefID = list.elementAt(i * 2);
			BBObj xRef = padre.getPadre().getObject(xRefID);
			//String xRefType = xRef.getTypeName();
			xRef.toXML(sb, level);
		}
	}

	private void defaultDump(int level, StringBuffer sb) {
		if (stringVal != null) {
			padele(level,sb,"StringVal",true,true,true,"Val", stringVal);
		}
		if (list.size() > 0) {
			padele(level,sb,"List",true,false, true);
			int vpl = 16;
			int cvpl = 0;
			String linea = "   ";
			for (int i = 0; i < list.size(); i += 2) {
				if (++cvpl > vpl) {
					pad(sb, linea, false);
					cvpl = 0;
					linea = "   ";
				}
				int siz = list.elementAt(i + 1).intValue();
				if (siz == 2)
					linea += ("s");
				else if (siz == 4)
					linea += ("i");
				else
					linea += ("sz" + siz + ":");
				linea += list.elementAt(i) + ",";
			}
			pad(sb, linea, false);
			padele(level,sb,"List",false,true, true);
		}
	}
	
	public static void padele(
			int level,StringBuffer sb, String en, 
			boolean opening, boolean closing, 
			boolean newline, String ...va ){

		for(int i=0;i<level;i++) 
			sb.append(" ");
		if(en.startsWith("*"))
			en=en.substring(1);
		if(!opening && closing)
			sb.append("</").append(en).append(">");
		else if(opening){
			sb.append("<").append(en).append(" ");
			for(int i=0;i<va.length;i+=2)
				sb.append(" ").append(va[i]).append("=\"").append(va[i+1]).append("\"");
		}
		if(opening && closing)
			sb.append("/>");
		else if(opening)
			sb.append(">");
		if(newline)
			sb.append("\n");
	}

	public static void pad(StringBuffer sb, String app, boolean scape) {
		//Hacemos 'escape de los caracteres no-xml
		if(scape){
			app = Utils.toXMLText(app);
		}
		sb.append(app);
	}

	public static Tag loadTag(BBObj padre, Reader pb, int xorKey)
			throws ParseException, UnsupportedEncodingException {
		if (pb.getByte(1) != 0xf5) { // Intentamos que sea texto hasta encontrar
			// un 0xF5
			int strlen = 0;
			while (pb.canGet(strlen + 1) && pb.getByte(strlen + 1) != 0xF5) {
				strlen += 2;
			}
			if (pb.canGet(strlen + 1)) {
				Tag t = new Tag(0xCC, padre); // Texto
				t.setStringVal(new String(pb.getSubBuf(0, strlen), "UTF-16LE"));
				pb.skip(strlen);
				return t;
			} else {
				throw new ParseException("Lost 0xf5 sync with tags at : "
						+ pb.getPos());
			}
		}
		int tagID = pb.getShort(true) & 0x00ff;
		int tagLength = Tag.tagSize[tagID];
		if (tagLength == -1) {
			throw new ParseException("Found bad 0xf5 tag at : " + pb.getPos());
		}
		switch (tagID) {
		case 0x4A: //*ImageRect
			Tag tag2=new Tag(tagID,padre);
			tag2.addValue(pb.getShort(true), 2);
			tag2.addValue(pb.getShort(true), 2);
			tag2.addValue(pb.getShort(true), 2);
			tag2.addValue(pb.getShort(true), 2);
			return tag2;
		case 73: // PutSound
			Tag sound = new Tag(tagID, padre);
			sound.addValue(pb.getInt(true), 4);
			sound.addValue(pb.getInt(true), 4);
			return sound;
		case 0xD1: // KomaPlot
			Tag komaTag = new Tag(tagID, padre);
			komaTag.addValue(pb.getShort(true), 2); // Height
			komaTag.addValue(pb.getShort(true), 2); // Width
			komaTag.addValue(pb.getInt(true), 4); // Ref
			komaTag.addValue(pb.getInt(true), 4); // Unknown
			return komaTag;
		case 0xD2: // EOL
			return null;
		case 0xCC: // Texto
			Tag t = new Tag(tagID, padre);
			// Por lo visto hay que leer tantos bytes como se especifican en'
			// len'
			int len = pb.getShort(true);
			t.setStringVal(new String(pb.getSubBuf(0, len), "UTF-16LE"));
			pb.skip(len);
			return t;
		case 161: // BeginP (Hay que esperar hasta EndP)
			pb.skip(4);
		case 162:
			return new Tag(tagID, padre);
		case 0x00:
			pb.skip(6);
			return null;
		case 0x01:
			// Object end
			return null;
		case 0x54:
			return TagStream.procStream(padre, tagID, pb, xorKey);
		case 0x06:
			// stream end
			return null;
		}

		Tag tag = new Tag(tagID, padre);
		if (tagLength == 0) {
			switch (tagID) {
			case 0x0b:
			case 0x5c:
				int count = pb.getShort(true);
				for (int i = 0; i < count; i++) {
					int data = pb.getInt(true);
					tag.addValue(data, 4);
				}
				break;
			case 0x6c:
				int i1 = pb.getInt(true);
				int i2 = pb.getInt(true);
				tag.addValue(i1, 4);
				tag.addValue(i2, 4);
				break;
			case 0x78:
				// <dword>, then <FF16> <w:len> <len string> <w2>
				int i11 = pb.getInt();
				// next tag should be F516
				int w1 = pb.getShort(4);
				int strlen1 = pb.getShort(6);
				tag.addValue(i11, 4);
				tag.addValue(w1, 2);
				tag.addValue(strlen1, 2);
				String fontName = null;
				try {
					fontName = new String(pb.getSubBuf(8, strlen1), 0, strlen1,
							"UTF-16LE");
					tag.setStringVal(fontName);
				} catch (Exception bs) {
					throw new ParseException(
							"No puedo leer el nombre de la fuente");
				}
				pb.skip(8 + strlen1);
				int w3 = pb.getShort(true);
				tag.addValue(w3, 2);
				break;
			case 0x16:
			case 0x55:
			case 0x59:
			case 0x5a:
			case 0x5d:
				// <word> size, then string of size bytes
				int strlen2 = pb.getShort();
				String string = null;
				try {
					string = new String(pb.getSubBuf(2, strlen2), 0, strlen2,
							"UTF-16LE");
				} catch (Exception bs) {
					throw new ParseException("No puedo leer string");
				}
				pb.skip(2 + strlen2);
				tag.setStringVal(string);
				break;
			}
		} else if (tagLength == 4) {
			int i = pb.getInt(true);
			tag.addValue(i, 4);
		} else if (tagLength == 2) {
			int i = pb.getShort(true);
			tag.addValue(i, 2);
		} else {
			tag = new TagStream(tagID, padre, pb.getSubReader(0, tagLength));
			pb.skip(tagLength);
		}
		return tag;
	}

	@Override
	public void render(Renderer pars)
			throws Exception {
		BBObj ref=null;
		switch (getType()) {
		case 0x03: //*Link
			BBObj re=padre.getPadre().getObject(getValueAt(0));
			if(re!=null)
				re.render(pars);
			break;
		case 0xA7: //BeginButton
			int objId=getValueAt(0);
			BBObj bob=padre.getPadre().getObject(objId);
			pars.setTagValAsString("BeginButton", "P"+bob.getTag("*JumpTo").getValueAt(0));
			pars.setTagVal("buttonRef",getValueAt(0));
			break;
		case 0xA8: //EndButton
			pars.setTagVal("EndButton", 0);
			pars.setTagVal("buttonRef",-1);
			break;
		case 0xCC: // Texto
			if(pars.isFooter())
				pars.emitText(this);
			else if(pars.isHeader())
				pars.emitText(this);
			else{
				pars.emitText(this);
				if(emitLN)
					pars.forceNewParagraph();
			}
			break;
		case 0x19: // WordSpace
			pars.setTagVal("wordSpace",getValueAt(0));
			break;
		case 0x1C:
			pars.setTagVal("lineSpace",getValueAt(0));
			break;
		case 0x1B:
			pars.setTagVal("BaseLineSkip",getValueAt(0));
			break;
		case 0xD1: // KomaPLot
			padre.getPadre().getObject(getValueAt(2)).render(pars);
			break;
		case 0xA1: // BeginP
			break;
		case 0xC1: //BeginEmptyLine
			pars.newParagraph();
			break;
		case 0xC2: //EndEmptyLine
			break;
		case 0xA2: // EndP
			pars.newParagraph();
			break;
		case 0x81: // ItalicBegin
			pars.setTagVal("Italic", 1);
			break;
		case 0x82: // ItalicEnd
			pars.setTagVal("Italic", 0);
			break;
		case 0x11: // FontSize
			pars.setTagVal("FontSize",getValueAt(0));
			break;
		case 0x15: // FontWeight 400 normal, 700 Bold
			if (getValueAt(0) > 400) {
				pars.setTagVal("Bold", 1);
			} else {
				pars.setTagVal("Bold", 0);
			}
			break;
		case 0x16: // FontFaceName
			pars.setTagValAsString("fontFaceName", getStringVal());
			break;
		case 0x17: // TextColor
			pars.setTagVal("rgbColor",getValueAt(0));
			break;
		case 0x3C: //BlockAlignment
			pars.setTagVal("BlockAlignment", getValueAt(0));
			break;
		case 0x26://*PageWidth
			pars.setTagVal("*PageWidth",getValueAt(0));
			break;
		case 0x25: //*PageHeight
			pars.setTagVal("*PageHeight",getValueAt(0));
			break;
		case 0x21: //TopMargin
			pars.setTagVal("TopMargin",getValueAt(0));
			break;
		case 0x2C: //EvenSideMargin
			pars.setTagVal("EvenSideMargin",getValueAt(0));
			break;
		case 0x24: //OddSideMargin
			pars.setTagVal("OddSideMargin",getValueAt(0));
			break;
		case 73: //PutSound
			ref=padre.getPadre().getObject(getValueAt(1));
			ref.render(pars);
			break;
		case 0x09://OddFooterId
			if(pars.getPageNumber()%2==1){
				ref=padre.getPadre().getObject(getValueAt(0));
				ref.render(pars);
			}
			break;
		case 0x0A://EvenFooterId
			if(pars.getPageNumber()%2==0){
				ref=padre.getPadre().getObject(getValueAt(0));
				ref.render(pars);
			}
			break;
		}

	}

}
