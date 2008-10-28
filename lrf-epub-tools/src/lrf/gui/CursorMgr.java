package lrf.gui;

import java.awt.Cursor;
import java.awt.Window;

public class CursorMgr {
	public static Window container;
	
	private static int waitCursorCounter=0;
	
	public static void setWaitCursor(){
		waitCursorCounter++;
		if(container==null)
			return;
		Cursor c=new Cursor(Cursor.WAIT_CURSOR);
		container.setCursor(c);
	}
	public static void releaseWaitCursor(){
		if(waitCursorCounter>0)
			waitCursorCounter--;
		if(container==null)
			return;
		if(waitCursorCounter==0){
			Cursor c=new Cursor(Cursor.DEFAULT_CURSOR);
			container.setCursor(c);
		}
	}
}
