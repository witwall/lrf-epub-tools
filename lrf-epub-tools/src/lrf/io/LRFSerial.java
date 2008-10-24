package lrf.io;

import java.io.IOException;

public interface LRFSerial {
	public int serial(BBeBOutputStream os, int promoteID) throws IOException;
}
