package lrf.objects.tags;

import java.io.IOException;

import lrf.buffer.ByteReader;
import lrf.io.BBeBOutputStream;
import lrf.io.LRFSerial;
import lrf.objects.BBObj;

public class UnknowContent extends Tag implements LRFSerial {
	int cnt=0;
	ByteReader reader=null;
	public UnknowContent(int i, BBObj p, int c) {
		super(i, p);
		cnt=c;
	}
	public UnknowContent(int i, BBObj p, ByteReader br){
		super(i,p);
		reader=br;
	}
	@Override
	public int serial(BBeBOutputStream os, int promoteID) throws IOException {
		return os.putShort(cnt);
	}
	@Override
	public void toXML(StringBuffer sb) {
		if(reader==null)
			pad(sb,"<UnknowTag shortVal=\""+cnt+"\"/>", false);
		else{
			int sz=(int)(reader.size()-reader.getPos());
			pad(sb,"<UnknowTag streamSize=\""+(sz)+"\">", false);
			byte buf[]=reader.getSubBuf(0, sz);
			for(int i=0,j=0;i<buf.length;i++){
				if(++j==33){
					sb.append("\n");
					j=1;
				}
				int k=buf[i];
				if(k<0)
					k+=256;
				String s=Integer.toHexString(k);
				if(s.length()==1)
					s="0"+s;
				sb.append(s);
			}
			sb.append("\n");
			pad(sb,"<UnknowTag>", false);
		}
	}

}
