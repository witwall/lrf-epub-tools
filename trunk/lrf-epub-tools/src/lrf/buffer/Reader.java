package lrf.buffer;

import java.io.UnsupportedEncodingException;

public interface Reader {

	public boolean canGet(int i);

	public int get(int pos);

	public int getByte();

	public int getByte(int offset);

	public int getByte(boolean advance);

	public int getInt();

	public int getInt(boolean advance);

	public int getInt(int offset);

	public int getShort();

	public int getShort(boolean advance);

	public int getShort(int offset);

	public String getString(boolean advance)
			throws UnsupportedEncodingException;

	public byte[] getSubBuf(int off, int len);

	public boolean isEmpty();

	public void skip(int skippedBytes);
	
	public int getPos();
	
	public long size();
	
	public void copy(int srcOff, byte dest[], int destOff, int length);
	
	public void reset();
	
	public Reader getSubReader(int off, int len);
}