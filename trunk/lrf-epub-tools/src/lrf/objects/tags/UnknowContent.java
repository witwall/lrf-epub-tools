package lrf.objects.tags;

import java.io.IOException;

import lrf.io.BBeBOutputStream;
import lrf.io.LRFSerial;
import lrf.objects.BBObj;

public class UnknowContent extends Tag implements LRFSerial {
	int cnt=0;
	public UnknowContent(int i, BBObj p, int c) {
		super(i, p);
		cnt=c;
	}
	@Override
	public int serial(BBeBOutputStream os, int promoteID) throws IOException {
		return os.putShort(cnt);
	}
	@Override
	public void toXML(StringBuffer sb) {
		pad(sb,"<UnknowTag shortVal=\""+cnt+"\"/>", false);
	}

}
