package lrf.buffer;

import java.io.UnsupportedEncodingException;

public class ByteReader implements Reader {
    public byte buffer[];
    public int ndx;

    public ByteReader(byte buf[], int initpos)
    {
        buffer = buf;
        ndx = initpos;
    }

    /* (non-Javadoc)
	 * @see lrf.buffer.Reader#canGet(int)
	 */
    public boolean canGet(int i)
    {
        return ndx + i < buffer.length;
    }

    /* (non-Javadoc)
	 * @see lrf.buffer.Reader#get(int)
	 */
    public int get(int pos)
    {
        if(ndx + pos < buffer.length)
            return buffer[ndx + pos];
        else
            throw new IndexOutOfBoundsException();
    }

    /* (non-Javadoc)
	 * @see lrf.buffer.Reader#getByte()
	 */
    public int getByte()
    {
        return getByte(0);
    }

    /* (non-Javadoc)
	 * @see lrf.buffer.Reader#getByte(int)
	 */
    public int getByte(int offset)
    {
        return buffer[ndx + offset] & 0xff;
    }

    /* (non-Javadoc)
	 * @see lrf.buffer.Reader#getInt()
	 */
    public int getInt()
    {
        return getInt(0);
    }

    /* (non-Javadoc)
	 * @see lrf.buffer.Reader#getInt(boolean)
	 */
    public int getInt(boolean advance)
    {
        if(advance)
        {
            ndx += 4;
            return getInt(-4);
        } else
        {
            return getInt(0);
        }
    }

    /* (non-Javadoc)
	 * @see lrf.buffer.Reader#getInt(int)
	 */
    public int getInt(int offset)
    {
        return (buffer[ndx + offset] & 0xff) + ((buffer[ndx + offset + 1] & 0xff) << 8) + ((buffer[ndx + offset + 2] & 0xff) << 16) + ((buffer[ndx + offset + 3] & 0xff) << 24);
    }

    /* (non-Javadoc)
	 * @see lrf.buffer.Reader#getShort()
	 */
    public int getShort()
    {
        return getShort(0);
    }

    /* (non-Javadoc)
	 * @see lrf.buffer.Reader#getShort(boolean)
	 */
    public int getShort(boolean advance)
    {
        if(advance)
        {
            ndx += 2;
            return getShort(-2);
        } else
        {
            return getShort(0);
        }
    }

    /* (non-Javadoc)
	 * @see lrf.buffer.Reader#getShort(int)
	 */
    public int getShort(int offset)
    {
        return (buffer[ndx + offset] & 0xff) + ((buffer[ndx + offset + 1] & 0xff) << 8);
    }

    /* (non-Javadoc)
	 * @see lrf.buffer.Reader#getString(boolean)
	 */
    public String getString(boolean advance)
        throws UnsupportedEncodingException
    {
        int sz = getShort();
        String ret = new String(buffer, ndx + 2, sz, "UTF-16LE");
        if(advance)
            ndx += 2 + sz;
        ret = ret.replace("\r\n", "\\n");
        ret = ret.replace("\r", "\\n");
        ret = ret.replace("\n", "\\n");
        ret = ret.replace("\t", "\\t");
        return ret;
    }

    /* (non-Javadoc)
	 * @see lrf.buffer.Reader#getSubBuf(int, int)
	 */
    public byte[] getSubBuf(int off, int len)
    {
        byte ret[] = new byte[len];
        System.arraycopy(buffer, ndx + off, ret, 0, len);
        return ret;
    }

    /* (non-Javadoc)
	 * @see lrf.buffer.Reader#isEmpty()
	 */
    public boolean isEmpty()
    {
        return ndx >= buffer.length;
    }

    /* (non-Javadoc)
	 * @see lrf.buffer.Reader#set(int, int)
	 */
    public void set(int pos, int b)
    {
        if(ndx + pos < buffer.length)
            buffer[ndx + pos] = (byte)(b & 0xff);
        else
            throw new IndexOutOfBoundsException();
    }

	@Override
	public void copy(int srcOff, byte[] dest, int destOff, int length) {
		try {
			System.arraycopy(buffer, ndx+srcOff, dest, destOff, length);
		}catch(ArrayIndexOutOfBoundsException aioobe){
			aioobe=null;
		}
	}

	@Override
	public int getPos() {
		return ndx;
	}

	@Override
	public Reader getSubReader(int off, int len) {
		byte ret[]=new byte[len];
		copy(off,ret,0,len);
		return new ByteReader(ret,0);
	}

	@Override
	public void reset() {
		ndx=0;
	}

	@Override
	public long size() {
		return buffer.length;
	}

	@Override
	public void skip(int skippedBytes) {
		ndx+=skippedBytes;
	}

}
