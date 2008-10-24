package lrf.objects;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import lrf.buffer.Reader;
import lrf.conv.Renderable;
import lrf.conv.Renderer;
import lrf.io.BBeBOutputStream;
import lrf.io.LRFSerial;
import lrf.objects.tags.Tag;
import lrf.parse.ParseException;

public class BBObj implements LRFSerial,Renderable  { 
	Book padre = null;
	int id;
	int objType;
	public int printPosition=0;
	boolean isRoot = false;
	Vector<Tag> tags = new Vector<Tag>();

	public static final int ot_INVALID_00 = 0;

	public static final int ot_PageTree = 1;
	public static final int ot_Page = 2;
	public static final int ot_Header = 3;
	public static final int ot_Footer = 4;
	public static final int ot_PageAtr = 5;
	public static final int ot_Block = 6;
	public static final int ot_BlockAtr = 7;
	public static final int ot_MiniPage = 8;
	public static final int ot_BlockList = 9;
	public static final int ot_Text = 10;
	public static final int ot_TextAtr = 11;
	public static final int ot_Image = 12;
	public static final int ot_Canvas = 13;
	public static final int ot_ParagraphAtr = 14;
	public static final int ot_INVALID_0f = 15;
	public static final int ot_INVALID_10 = 16;
	public static final int ot_ImageStream = 17;
	public static final int ot_Import = 18;
	public static final int ot_Buttom = 19;
	public static final int ot_Window = 20;
	public static final int ot_PopUpWin = 21;
	public static final int ot_Sound = 22;
	public static final int ot_PlaneStream = 23;
	public static final int ot_Invalid_18 = 24;
	public static final int ot_Font = 25;
	public static final int ot_ObjectInfo = 26;
	public static final int ot_INVALID_1b = 27;
	public static final int ot_BookAtr = 28;
	public static final int ot_SimpleText = 29;
	public static final int ot_TOC = 30;
	
	
	public static String getObjectTypeName(int objectType) {
		String s;
		switch (objectType) {
		case 0x00:
			s = "OBJECT_TYPE_INVALID_00";
			break;
		case 0x01:
			s = "OBJECT_TYPE_PageTree";
			break;
		case 0x02:
			s = "OBJECT_TYPE_Page";
			break;
		case 0x03:
			s = "OBJECT_TYPE_Header";
			break;
		case 0x04:
			s = "OBJECT_TYPE_Footer";
			break;
		case 0x05:
			s = "OBJECT_TYPE_PageAtr";
			break;
		case 0x06:
			s = "OBJECT_TYPE_Block";
			break;
		case 0x07:
			s = "OBJECT_TYPE_BlockAtr";
			break;
		case 0x08:
			s = "OBJECT_TYPE_MiniPage";
			break;
		case 0x09:
			s = "OBJECT_TYPE_BlockList";
			break;
		case 0x0a:
			s = "OBJECT_TYPE_Text";
			break;
		case 0x0b:
			s = "OBJECT_TYPE_TextAtr";
			break;
		case 0x0c:
			s = "OBJECT_TYPE_Image";
			break;
		case 0x0d:
			s = "OBJECT_TYPE_Canvas";
			break;
		case 0x0e:
			s = "OBJECT_TYPE_ParagraphAtr";
			break;
		case 0x0f:
			s = "OBJECT_TYPE_Invalid_0f";
			break;
		case 0x10:
			s = "OBJECT_TYPE_Invalid_10";
			break;
		case 0x11:
			s = "OBJECT_TYPE_ImageStream";
			break;
		case 0x12:
			s = "OBJECT_TYPE_Import";
			break;
		case 0x13:
			s = "OBJECT_TYPE_Button";
			break;
		case 0x14:
			s = "OBJECT_TYPE_Window";
			break;
		case 0x15:
			s = "OBJECT_TYPE_PopUpWin";
			break;
		case 0x16:
			s = "OBJECT_TYPE_Sound";
			break;
		case 0x17:
			s = "OBJECT_TYPE_PlaneStream";
			break;
		case 0x18:
			s = "OBJECT_TYPE_Invalid_18";
			break;
		case 0x19:
			s = "OBJECT_TYPE_Font";
			break;
		case 0x1a:
			s = "OBJECT_TYPE_ObjectInfo";
			break;
		case 0x1b:
			s = "OBJECT_TYPE_Invalid_1b";
			break;
		case 0x1c:
			s = "OBJECT_TYPE_BookAtr";
			break;
		case 0x1d:
			s = "OBJECT_TYPE_SimpleText";
			break;
		case 0x1e:
			s = "OBJECT_TYPE_TOC";
			break;
		case 0x1f:
			s = "OBJECT_TYPE_Invalid_1f";
			break;
		default:
			s = "OBJECT_TYPE_Invalid_" + objectType;
		}
		return s;
	}

	public BBObj(Book book, int id2, int tipo, Reader pb)
			throws ParseException {
		padre = book;
		id = id2;
		objType = tipo;
		loadTags(pb);
	}

	private void loadTags(Reader pb)
			throws ParseException {
		int xk = padre.getXorKey();
		while (!pb.isEmpty()) {
			Tag t;
			try {
				t = Tag.loadTag(this,pb, xk);
			} catch (UnsupportedEncodingException e) {
				continue;
			}
			if (t != null)
				addTag(t);
		}
	}

	public void addTag(Tag tag) {
		this.tags.add(tag);
	}

	public int getID() {
		return id;
	}

	public int getNumtag() {
		return tags.size();
	}

	public Book getPadre() {
		return padre;
	}

	public Tag getTagAt(int ndx) {
		return tags.elementAt(ndx);
	}
	
	public Tag getTag(String tagname){
		for(int i=0;i<getNumtag();i++){
			if(getTagAt(i).getName().equalsIgnoreCase(tagname))
				return getTagAt(i);
		}
		return null;
	}

	public Vector<Tag> getTags() {
		return tags;
	}

	public int getType() {
		return objType;
	}

	public String getTypeName() {
		return getObjectTypeName(objType).substring(12);
	}

	public String toString() {
		StringBuffer xObjs = new StringBuffer();
		toXML(xObjs);
		return xObjs.toString();
	}

	public void toXML(StringBuffer sb) {
		Tag.pad(sb, "<Obj ID=\"" + id + "\" objType=\""
				+ getObjectTypeName(objType).substring(12) + "\">", false);
		for (int i = 0; i < tags.size(); i++) {
			tags.elementAt(i).toXML(sb);
		}
		Tag.pad(sb, "</Obj>", false);
	}

	@Override
	public int serial(BBeBOutputStream os, int promoteID) throws IOException {
		os.putShort(0xf500);
		os.putShort(getType());
		int ret=4;
		for(int i=0;i<tags.size();i++)
			ret+=tags.elementAt(i).serial(os, promoteID);
		return ret;
	}

	@Override
	public void render(Renderer pars) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void itRendListRef(Renderer pars, Tag lr) throws Exception {
		for (int i = 0; i < lr.getNumValues(); i++) {
			padre.getObject(lr.getValueAt(i)).render(pars);
		}
	}

	public void itRendTags(Renderer pars) throws Exception {
		for(int i=0;i<tags.size();i++){
			Tag t=tags.elementAt(i);
			if(t!=null)
				t.render(pars);
		}
	}

	public void itRendRef(Renderer rend, String tagList, int pos) throws Exception {
		for (int i = 0; i < getTags().size(); i++) {
			Tag tag = getTags().elementAt(i);
			if (tag.getName().equals(tagList)) {
				padre.getObject(tag.getValueAt(pos)).render(rend);
			}
		}
	}


}
