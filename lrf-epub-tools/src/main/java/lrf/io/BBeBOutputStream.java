package lrf.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class BBeBOutputStream  {
	
	FileChannel fcha=null;
	MappedByteBuffer mbb=null;
	
	/*
	 * 	00	public char[] signature = new char[8];
		08	public ushort wVersion = 999;
		0A	public ushort wPseudoEncByte;
		0C	public uint dwRootObjectId;
		10	private ulong ddwNumOfObjects;
		18	private ulong ddwObjIndexOffset;
		20	public uint dwUnknown1;
		24	public byte byBindingDir = BBeBHeader.LRF_DIRECTION_FORWARDS;
		25	public byte byPadding1;
		26	public ushort wDPI = 170;
		28	public ushort wPadding2;
		2A	public ushort wScreenWidth = BBeB.ReaderPageWidth;
		2C	public ushort wScreenHeight = BBeB.ReaderPageHeight;
		2E	public byte byColorDepth = 24;
		2F	public byte byPadding3;
		30	public byte[] byUnkonwn2 = new byte[20];
		44	public uint dwTocObjectId;
		48	public uint dwTocObjectOffset;
		4C	public ushort wDocInfoCompSize;
		4E	public ushort wThumbnailFlags;	// MSB = ThumbnailFormat, LSB = ThumbnailType
		50	public uint dwThumbSize;
		54  public uint dwUncompDocInfoSize
		58  public String CompressedBookInfoBlockData
	 */
	
	
	public BBeBOutputStream(File fn, int xor, int root, int num) throws IOException {
		fcha=new FileInputStream(fn).getChannel();
		mbb=fcha.map(MapMode.READ_WRITE, 0, 0);
		mbb.order(ByteOrder.LITTLE_ENDIAN);
		mbb.put((byte) 'L'); mbb.put((byte) 0);
		mbb.put((byte) 'R'); mbb.put((byte) 0);
		mbb.put((byte) 'F'); mbb.put((byte) 0);
		mbb.put((byte) 0  ); mbb.put((byte) 0);
		mbb.putShort((short)999);
		mbb.putShort((short)xor);
		mbb.putInt(root);
		mbb.putLong(num);
		mbb.putLong(0); //Aqui va ObjIndexOffset
		mbb.putInt(0); //Unknown
		mbb.put((byte)1); //LRF_DIRECTION_FORWARD
		mbb.put((byte)0); 
		mbb.putShort((short)170); //DPI
		mbb.putShort((short)0);
		mbb.putShort((short)600);
		mbb.putShort((short)800);
		mbb.put((byte)24);
		mbb.put((byte)0);
		for(int i=0;i<20;i++) mbb.put((byte)0);
		mbb.putInt(0); //TOC Obj ID
		mbb.putInt(0); //TOC Obj Offset
		mbb.putShort((short)0); //Document Info compressed Size
		mbb.putShort((short)0); // Thumbnail flags
		mbb.putInt(0); //Thumbnail gif image size
		mbb.putInt(0); //Uncomp Documento Info Size
		//Ahora viene el bloque de Compressed BookInfo block (Offset 0x58==88).
		
		
	}

	public int putString(String s) throws IOException {
		mbb.putShort((short)s.length());
		byte str[]=s.getBytes("UTF-16LE");
		mbb.put(str);
		return 2+str.length;
	}
	
	public int putInt(int b) throws IOException {
		mbb.putInt(b);
		return 4;
	}

	public int putShort(int b) throws IOException {
		mbb.putShort((short)b);
		return 2;
	}
	
	public int putByte(int b) throws IOException {
		mbb.put((byte)b);
		return 1;
	}
}
