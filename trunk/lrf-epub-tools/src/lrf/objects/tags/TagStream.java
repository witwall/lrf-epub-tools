package lrf.objects.tags;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import java.util.zip.Inflater;

import lrf.base64.Base64;
import lrf.buffer.ByteReader;
import lrf.buffer.Reader;
import lrf.conv.Renderer;
import lrf.io.BBeBOutputStream;
import lrf.objects.BBObj;
import lrf.objects.TOC;

public class TagStream extends Tag {
	final static String blks = "                                         ";
	public String imageType = null;
	Reader reader = null;
	public Vector<Tag> tags = new Vector<Tag>();

	@Override
	public int serial(BBeBOutputStream os, int promoteID) throws IOException {
		return 0;
	}

	@Override
	public void render(Renderer pars)
			throws Exception {
		for (int i = 0; i < tags.size(); i++){
			Tag t=tags.elementAt(i);
			t.render(pars);
		}
	}

	public TagStream(int id, BBObj p, Reader r)
			throws UnsupportedEncodingException {
		super(id, p);
		reader = r;
		int pt = padre.getType();
		if((padre.getType() == BBObj.ot_ImageStream) || (padre.getType() == BBObj.ot_PlaneStream))
			return;
		if (pt == BBObj.ot_TOC) {
			TOC toc = (TOC) p;
			int numTOCEntries = reader.getInt(true);
			int entriesOffset[] = new int[numTOCEntries];
			for (int i = 0; i < numTOCEntries; i++) {
				entriesOffset[i] = reader.getInt(true);
			}
			for (int i = 0; i < numTOCEntries; i++) {
				toc.new Entry(reader.getInt(true), reader.getInt(true), reader
						.getString(true));
			}
		}else{
			int xk = padre.getPadre().getXorKey();
			while (!reader.isEmpty()) {
				Tag t = null;
				try {
					t = loadTag(padre, reader, xk);
				} catch (Exception e) {
					if(!reader.canGet(1)){
						t = new UnknowContent(0x100, padre, reader.getByte(true));
					}else{
						t = new UnknowContent(0x100, padre, reader.getShort(true));
					}
				}
				if (t != null)
					tags.add(t);
			}
		}
	}

	public static Tag procStream(BBObj padre, int tagID, Reader pb, int xorKey)
			throws UnsupportedEncodingException {
		// stream size
		int streamFlags = pb.getShort(true);
		pb.getShort(true); //
		int streamSize = pb.getInt(true);
		pb.getShort(true);
		Reader main=pb;
		boolean isImageStream=
			(padre.getType() == BBObj.ot_ImageStream) || (padre.getType() == BBObj.ot_PlaneStream);
		// stream start
		if ((streamFlags & 0x200) == 0x200) {
			int size = streamSize;
			int key = (size % xorKey) + 0x0F;
			if (size > 0x400 && !((streamFlags & 0x100) == 0x100)) {
				size = 0x400;
			}
			byte unscrambled[] = new byte[streamSize];
			pb.copy(0, unscrambled, 0, streamSize);
			for (int i = 0; i < size; i++) {
				unscrambled[i] = (byte) (pb.get(i) ^ key & 0x00ff);
			}
			pb = new ByteReader(unscrambled, 0);
		}
		String imageType = null;
		TagStream ts = null;
		if (isImageStream) {
			if ((streamFlags & 0x0f) == 1) {
				imageType = ".jpg";
			} else if ((streamFlags & 0x0f) == 4) {
				imageType = ".gif";
			} else if ((streamFlags & 0x0f) == 3) {
				imageType = ".bmp";
			} else {
				imageType = ".unk";
			}
		}
		if (((streamFlags & 0x100) == 0x100)) {
			// Decompress the bytes
			try {
				int decompSize = pb.getInt(true);
				Inflater decompresser = new Inflater();
				decompresser.setInput(pb.getSubBuf(0, streamSize - 4));
				byte[] result = new byte[decompSize];
				decompresser.inflate(result);
				decompresser.end();
				Reader br = new ByteReader(result, 0);
				ts = new TagStream(tagID, padre, br);
			} catch (Exception e) {
				System.err.println("Error inflating data");
				e.printStackTrace();
			}
		} else {
			ts = new TagStream(tagID, padre, pb.getSubReader(0, streamSize));
		}
		main.skip(streamSize);
		ts.setImageType(imageType);
		return ts;
	}

	public byte[] getBytes() {
		byte ret[] = new byte[(int) reader.size()];
		reader.copy(0, ret, 0, ret.length);
		return ret;
	}

	public void setImageType(String s) {
		imageType = s;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		toXML(sb);
		return sb.toString();
	}

	public void toXML(StringBuffer sb) {
		String tName = tagNames[id];
		reader.reset();
		if(reader.isEmpty())
			return;
		int tagCode = reader.getByte();
		int pt=padre.getType();
		if (pt == BBObj.ot_Block && tagCode!=3) {
			pad(sb, " <Tag Type=\"" + tName + "\" EXPECTED LINKREF >", false);
			String b6 = Base64.encodeBytes(reader.getSubBuf(0, (int) reader
					.size()));
			pad(sb, b6.replace("\n", "    \n"), false);
			pad(sb, " </Tag>", false);
		} else if (pt == BBObj.ot_TOC) {
			// El dump se realiza en otro lugar
		} else if (pt == BBObj.ot_Text 
				|| pt == BBObj.ot_Page 
				|| pt == BBObj.ot_Block
				|| pt == BBObj.ot_Footer
				|| pt == BBObj.ot_Header) {
			pad(sb, " <Stream>", false);
			for (int i = 0; i < tags.size(); i++)
				tags.elementAt(i).toXML(sb);
			pad(sb, " </Stream>", false);
		} else if (tName.equals("*ImageRect")) {
			reader.reset();
			addValue(reader.getShort(true), 2);
			addValue(reader.getShort(true), 2);
			addValue(reader.getShort(true), 2);
			addValue(reader.getShort(true), 2);
			pad(sb, " <Tag Type=\"" + tName + "\" Coord=\"" + getValueAt(0) + ","
					+ getValueAt(1) + "," + getValueAt(2) + "," + getValueAt(3)
					+ "\"/>", false);
		} else {
			pad(sb, " <Tag Type=\"" + tName + "\" "
					+ (imageType == null ? "" : "Format=\"" + imageType+"\"") + ">", false);
			String b6 = Base64.encodeBytes(reader.getSubBuf(0, (int) reader
					.size()));
			pad(sb, "   " + b6.replace("\n", "\n   "), false);
			pad(sb, " </Tag>", false);
		}
	}

}
