package lrf.pdf;

import java.io.OutputStream;
import java.io.PrintWriter;

public class NullablePrintWriter extends PrintWriter {
	OutputStream os=null;
	public NullablePrintWriter(OutputStream out) {
		super(out==null ? System.out: out);
		os=out;
	}
	@Override
	public void print(String s) {
		if(os!=null)
			super.print(s);
	}
	@Override
	public void println(String x) {
		if(os!=null)
			super.println(x);
	}
	@Override
	public void print(char c) {
		if(os!=null)
		super.print(c);
	}
	@Override
	public void print(char[] s) {
		if(os!=null)
		super.print(s);
	}
	@Override
	public void print(double d) {
		if(os!=null)
		super.print(d);
	}
	@Override
	public void print(float f) {
		if(os!=null)
		super.print(f);
	}
	@Override
	public void print(int i) {
		if(os!=null)
		super.print(i);
	}
	@Override
	public void print(long l) {
		if(os!=null)
		super.print(l);
	}
	@Override
	public void print(Object obj) {
		if(os!=null)
		super.print(obj);
	}
	@Override
	public void println() {
		if(os!=null)
		super.println();
	}
	@Override
	public void println(boolean x) {
		if(os!=null)
		super.println(x);
	}
	@Override
	public void println(char x) {
		if(os!=null)
		super.println(x);
	}
	@Override
	public void println(char[] x) {
		if(os!=null)
		super.println(x);
	}
	@Override
	public void println(double x) {
		if(os!=null)
		super.println(x);
	}
	@Override
	public void println(float x) {
		if(os!=null)
		super.println(x);
	}
	@Override
	public void println(int x) {
		if(os!=null)
		super.println(x);
	}
	@Override
	public void println(long x) {
		if(os!=null)
		super.println(x);
	}
	@Override
	public void println(Object x) {
		if(os!=null)
		super.println(x);
	}

}
