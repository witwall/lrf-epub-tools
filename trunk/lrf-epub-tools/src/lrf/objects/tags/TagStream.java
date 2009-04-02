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
			int tt=padre.getType();
			if (size > 0x400 && (tt==BBObj.ot_ImageStream || 
					             tt==BBObj.ot_Font ||
					             tt==BBObj.ot_Sound)) {
				size = 0x400;
			}
			byte unscrambled[] = new byte[streamSize];
			pb.copy(0, unscrambled, 0, streamSize);
			for (int i = 0; i < size; i++) {
				unscrambled[i] = (byte) (unscrambled[i] ^ key & 0x00ff);
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
				imageType = ".png";
			}
		}
		if (((streamFlags & 0x100) == 0x100)) {
			// Decompress the bytescd tgmp
			try {
				int decompSize = pb.getInt(false);
				Inflater decompresser = new Inflater();
				decompresser.setInput(pb.getSubBuf(4, streamSize - 4));
				byte[] result = new byte[decompSize>0x800000 ? 0x800000 : decompSize];
				decompresser.inflate(result);
				decompresser.end();
				Reader br = new ByteReader(result, 0);
				ts = new TagStream(tagID, padre, br);
			} catch (Exception e) {
				System.out.print("Error inflating data id="+padre.getID()+",size="+streamSize);
				System.out.print(":Brute force descrambling...");
				byte scrambled[] = new byte[streamSize];
				main.copy(0, scrambled, 0, streamSize);
				byte unscrambled[] = new byte[streamSize];
				for(int kk=0;kk<256;kk++){
					for (int i = 0; i < streamSize; i++) {
						unscrambled[i] = (byte) (scrambled[i] ^ kk & 0x00ff);
					}
					pb=new ByteReader(unscrambled,0);
					try {
						int decompSize = pb.getInt(false);
						Inflater decompresser = new Inflater();
						decompresser.setInput(pb.getSubBuf(4, streamSize - 4));
						byte[] result = new byte[decompSize>0x800000 ? 0x800000 : decompSize];
						decompresser.inflate(result);
						System.out.println("KEY FOUND!");
						decompresser.end();
						Reader br = new ByteReader(result, 0);
						ts = new TagStream(tagID, padre, br);
						main.skip(streamSize);
						if(ts!=null)
							ts.setImageType(imageType);
						return ts;
					}catch(Exception ee){
						
					}
				}
				main.skip(streamSize);
				System.out.println("Sorry, Key not found.");
				return new UnknowContent(tagID,padre,(ByteReader)pb);
				//e.printStackTrace();
			}
		} else {
			ts = new TagStream(tagID, padre, pb.getSubReader(0, streamSize));
		}
		main.skip(streamSize);
		if(ts!=null)
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
		toXML(sb,0);
		return sb.toString();
	}

	public void toXML(StringBuffer sb, int level) {
		String tName = tagNames[id];
		reader.reset();
		if(reader.isEmpty())
			return;
		int tagCode = reader.getByte();
		int pt=padre.getType();
		if (pt == BBObj.ot_Block && tagCode!=3) {
			padele(level,sb,tName,true,false,true,"Error", "Expected LinkRef");
			String b6 = Base64.encodeBytes(reader.getSubBuf(0, (int) reader.size()));
			pad(sb,b6,false);
			padele(level,sb,tName,false,true, true);
		} else if (pt == BBObj.ot_TOC) {
			// El dump se realiza en otro lugar
		} else if (pt == BBObj.ot_Text 
				|| pt == BBObj.ot_Page 
				|| pt == BBObj.ot_Block
				|| pt == BBObj.ot_Footer
				|| pt == BBObj.ot_Header) {
			padele(level,sb,"Stream",true,false, true);
			for (int i = 0; i < tags.size(); i++){
				Tag t=tags.elementAt(i);
				t.toXML(sb,level+1);
			}
			padele(level,sb,"Stream",false,true, true);
		} else if (tName.equals("*ImageRect")) {
			reader.reset();
			addValue(reader.getShort(true), 2);
			addValue(reader.getShort(true), 2);
			addValue(reader.getShort(true), 2);
			addValue(reader.getShort(true), 2);
			padele(level,sb,tName,true,true,
					true,"c1",
					""+getValueAt(0),"c2",
					""+getValueAt(1),"c3",
					""+getValueAt(2),"c4", ""+getValueAt(3)
					);
		} else {
			padele(level,sb,tName,true,false,
					false,"Format", imageType==null?"Unknown":imageType);
			String b6 = Base64.encodeBytes(reader.getSubBuf(0, (int) reader.size()));
			pad(sb,b6,false);
			pad(sb,"\n",false);
			padele(level,sb,tName,false,true, true);
		}
	}

}
