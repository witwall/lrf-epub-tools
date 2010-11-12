package lrf.pdf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.AttributedCharacterIterator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import lrf.Utils;
import lrf.base64.Base64;
import lrf.pdf.flow.Flower;
import lrf.pdf.flow.ImagePiece;

public class GraphicsHook extends Graphics2D {
	private PrintWriter pw;
	private Flower flw;
	private int height,width;
	
	public GraphicsHook(){
		this(null);
	}
	
	public GraphicsHook(PrintWriter pw2){
		pw=pw2;
		flw=new Flower();
		init();
	}
	
	public Flower getFlower(){
		return flw;
	}
	
	private void init(){
		if(pw!=null) pw.println("<pdf>");
	}
	public void close(){
		if(pw!=null) pw.print("</page>\n</pdf>\n");
		if(pw!=null) pw.close();
	}
	public void error(String s){
		if(pw!=null) pw.println(" <error txt=\""+s+"\"/>");
	}

	public void newPage(int w, int h){
		height=h;
		width=w;
		flw.newPage(w, h);
		if(flw.pageNumber>1){
			if(pw!=null) pw.println("</page>");
			if(pw!=null) pw.println("<page number=\""+(flw.pageNumber)+"\">");
		}else{
			if(pw!=null) pw.println("<page number=\""+(flw.pageNumber)+"\">");
		}
	}
	
	public void pf(Font f,int lev){
		pl(lev);
		if(pw!=null) pw.print("<font name=\""+f.getFontName()+"\"");
		if(pw!=null) pw.print(" size=\""+f.getSize()+"\"");
		if(f.isBold())
			if(pw!=null) pw.print(" bold=\"true\"");
		if(f.isItalic())
			if(pw!=null) pw.print(" italic=\"true\"");
		if(pw!=null) pw.println("/>");
	}
	
	public String pathIter(double[] val, int sz){
		String ret="";
		for(int i=0;i<sz;i++){
			ret+="x"+(i+1)+"=\""+val[i*2]+"\" y"+(i+1)+"=\""+val[1+i*2]+"\"";
		}
		return ret;
	}
	
	public void pShape(String cmd, Shape s){
		if(pw!=null) pw.println(" <"+cmd+">");
		if(pw!=null) pw.println("  <shape>");
		if(s!=null){
		PathIterator pi=s.getPathIterator(null);
		while(!pi.isDone()){
			double ret[]=new double[6];
			switch(pi.currentSegment(ret)){
			case PathIterator.SEG_CLOSE:
				if(pw!=null) pw.println("   <close/>");
				break;
			case PathIterator.SEG_CUBICTO:
				if(pw!=null) pw.println("   <cubicto "+pathIter(ret, 3)+"/>");
				break;
			case PathIterator.SEG_LINETO:
				if(pw!=null) pw.println("   <lineto "+pathIter(ret, 1)+"/>");
				break;
			case PathIterator.SEG_MOVETO:
				if(pw!=null) pw.println("   <moveto "+pathIter(ret, 1)+"/>");
				break;
			case PathIterator.SEG_QUADTO:
				if(pw!=null) pw.println("   <quadto "+pathIter(ret, 2)+"/>");
				break;
			}
			pi.next();
		}
		}
		if(pw!=null) pw.println("  </shape>");
		if(pw!=null) pw.println(" </"+cmd+">");
	}
	
	public void p(String s, int... v){
		if(pw!=null) pw.print(" <"+s);
		for(int i=0;i<v.length;i++){
			if(pw!=null) pw.print(" v"+(i+1)+"=\""+v[i]+"\"");
		}
		if(pw!=null) pw.print("/>\n");
	}

	public void p(int lev, String s, int... v){
		pl(lev);
		p(s,v);
	}
	
	public void pStr(int lev, String s, String c, float... v){
		pl(lev);
		if(pw!=null) pw.print("<"+s);
		for(int i=0;i<v.length;i++){
			if(pw!=null) pw.print(" v"+(i+1)+"=\""+v[i]+"\"");
		}
		if(pw!=null) pw.print(">"+Utils.toXMLText(c)+"</"+s+">\n");
	}

	public void pStr(int lev, String s, String at, String c, float... v){
		pl(lev);
		if(pw!=null) pw.print("<"+s+" "+at);
		for(int i=0;i<v.length;i++){
			if(pw!=null) pw.print(" v"+(i+1)+"=\""+v[i]+"\"");
		}
		if(pw!=null) pw.print(">"+c+"</"+s+">\n");
	}
	public void pKV(String s, String k, String v, int lev){
		pl(lev);
		if(pw!=null) pw.println("<"+s+" key=\""+k+"\" value=\""+v+"\"/>");
	}

	public void q(String s, double... v){
		if(pw!=null) pw.print(" <"+s);
		if(v!=null)
			for(int i=0;i<v.length;i++)
				if(pw!=null) pw.print(" v"+(i+1)+"=\""+v[i]+"\"");
		if(pw!=null) pw.print("/>\n");
	}

	public void qf(String s, float... v){
		if(pw!=null) pw.print(" <"+s);
		if(v!=null)
			for(int i=0;i<v.length;i++)
				if(pw!=null) pw.print(" v"+(i+1)+"=\""+v[i]+"\"");
		if(pw!=null) pw.print("/>\n");
	}

	public void qf(String s, int lev, float... v){
		pl(lev);
		qf(s,v);
	}
	
	public void qf2(String s, int lev, float v){
		pl(lev);
		qf(s,v);
	}

	public void pa(AffineTransform at, int lev){
		if(at==null)
			return;
		pl(lev);
		if(pw!=null) pw.println("<affineTx>"+at.toString()+"</affineTx>");
	}
	
	public void pl(int lev){
		for(int i=0;i<lev;i++)
			if(pw!=null) pw.print(" ");
	}
	
	public void po(String s,int lev){
		pl(lev);
		if(pw!=null) pw.println("<"+s+">");
	}
	
	public void pc(String s, int lev){
		pl(lev);
		if(pw!=null) pw.println("</"+s+">");
	}
	
	public void pi(double x, double y, Image img, Integer bgColor, int lev, AffineTransform xt) 
	throws IOException {
		pl(lev);
		int h=img.getHeight(null)*800/height;
		int w=img.getWidth(null)*600/width;
		String s="Image type=\"png\" x=\""+x+"\" y=\""+y+"\" h=\""+h+"\" w=\""+w+"\"";
		if(bgColor!=null)
			po(s+" bgColor=\""+bgColor.intValue()+"\"",1);
		else
			po(s,1);
		byte bs[]=toFormat(img, "jpeg");
		if(pw!=null) pw.println(Base64.encodeBytes(bs));
		pc("Image",2);
		flw.addPiece(new ImagePiece(flw.pageNumber,(float)x,(float)y,img,xt));
	}
	
	@Override
	public void clearRect(int x, int y, int width, int height) {
		p("clearRect",x,y,width,height);
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		p("clipRect",x,y,width,height);
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		p("copyArea",x,y,width,height,dx,dy);
	}

	@Override
	public Graphics create() {
		p("create");
		return this;
	}

	@Override
	public void dispose() {
		p("dispose");
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		p("drawArc",x,y,width,height,startAngle,arcAngle);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		drawImage(img,new AffineTransform(1f,0f,0f,1f,x,y),observer);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		po("drawImage", 1);
		pa(new AffineTransform(1f,0f,0f,1f,x,y),2);
		try {
			pi(x,y,img,bgcolor.getRGB(),2,null);
		}catch(IOException e){
			p("Error");
		}
		pc("drawImage", 1);
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			ImageObserver observer) {
		p("drawImage-ImageObserver",x,y,width,height);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			Color bgcolor, ImageObserver observer) {
		p("drawImage-bgColor-ImageObserver",x,y,width,height);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		p("drawImage-ImageObserver",dx1,dy1,dx2,dy2,sx1,sy1,sx2,sy2);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgcolor,
			ImageObserver observer) {
		p("drawImage-bgColor-ImageObserver",dx1,dy1,dx2,dy2,sx1,sy1,sx2,sy2);
		return true;
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		p("drawLine",x1,y1,x2,y2);
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		p("drawOval",x,y,width,height);
	}

	@Override
	public void drawPolygon(int[] points, int[] points2, int points3) {
		p("drawPoly-x",points);
		p("drawPoly-y",points2);
		p("drawPoly-n",points3);
	}

	@Override
	public void drawPolyline(int[] points, int[] points2, int points3) {
		p("drawPolyline-x",points);
		p("drawPolyline-y",points2);
		p("drawPolyline-n",points3);
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		p("drawRoundRect",x,y,width,height,arcWidth,arcHeight);
	}

	@Override
	public void drawString(String str, int x, int y) {
		pStr(1,"drawString",str,x,y);
		//flw.addPiece(new TextPiece(flw.pageNumber,x,y,str,_font,_color,getFontRenderContext()));
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		p("drawString-iterator",x,y);
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		p("fillArc",x,y,width,height,startAngle,arcAngle);

	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		p("fillOval",x,y,width,height);
	}

	@Override
	public void fillPolygon(int[] points, int[] points2, int points3) {
		p("fillPolygon-x",points);
		p("fillPolygon-y",points2);
		p("fillPolygon-n",points3);

	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		p("fillRect",x,y,width,height);
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		p("fillRoundRect",x,y,width,height,arcWidth,arcHeight);
	}

	@Override
	public Shape getClip() {
		p("getClip");
		return _clip;
	}

	@Override
	public Rectangle getClipBounds() {
		p("getClipBounds");
		return null;
	}

	@Override
	public Color getColor() {
		p("getColor");
		return _color;
	}

	@Override
	public Font getFont() {
		p("getFont");
		return _font;
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		p("getFontMetrics-"+f.getFamily()+"-"+f.getFontName());
		return null;
	}

	Shape _clip=null;
	@Override
	public void setClip(Shape clip) {
		_clip=clip;
		pShape("setClip", clip);
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		p("setClip",x,y,width,height);
	}

	Color _color=null;
	@Override
	public void setColor(Color c) {
		if(_color==null || _color!=c)
			p("setColor",c.getRGB());
		_color=c;
	}

	Font _font=null;
	@Override
	public void setFont(Font f) {
		if(_font==null || 
			!f.getName().equals(_font.getName()) || 
			f.getSize()!=_font.getSize() ||
			f.getStyle()!=_font.getStyle()){
			
			pf(f,1);
		}
		_font=f;
	}

	@Override
	public void setPaintMode() {
		p("setPaintMode");
	}

	@Override
	public void setXORMode(Color c1) {
		p("setXorMode",c1.getRGB());
	}

	@Override
	public void translate(int x, int y) {
		p("translate",x,y);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		if(pw!=null) pw.println(" <addRenderingHints>");
		Set<Object> keys=(Set<Object>)hints.keySet();
		for(Object k : keys){
			pKV("renderingHint",k.toString(),hints.get(k).toString(),2);
		}
		if(pw!=null) pw.println(" </addRenderingHints>");
	}
	@Override
	public void clip(Shape s) {
		pShape("clipShape",s);
	}
	@Override
	public void draw(Shape s) {
		pShape("draw",s);
	}
	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		q("drawGlypthVector",x,y);
	}
	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		if(img.getHeight(null)<3)
			return false;
		po("drawImage", 1);
		pa(xform,2);
		try {
			pi(0,0,img,null,2,xform);
		}catch(IOException e){
			p("Error");
		}
		pc("drawImage", 1);
		return false;
	}
	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		Image img1=op.filter(img, null);
		drawImage(img1,new AffineTransform(1f,0f,0f,1f,x,y),null);
	}
	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		drawImage((Image)img, xform, null);
	}
	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		drawImage((Image)img, xform, null);
	}
	@Override
	public void drawString(String str, float x, float y) {
		if(str.trim().length()==0)
			return;
		pStr(1,"drawString",str,x,y);
		//flw.addPiece(new TextPiece(flw.pageNumber,x,y,str,_font,_color,getFontRenderContext()));
	}
	@Override
	public void drawString(AttributedCharacterIterator iterator, float x,
			float y) {
		q("drawString-attributed",x,y);
	}
	@Override
	public void fill(Shape s) {
		pShape("fill",s);
	}
	@Override
	public Color getBackground() {
		q("getBackground");
		return _background;
	}
	@Override
	public Composite getComposite() {
		p("getComposite");
		return null;
	}
	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		p("getDeviceConfiguration");
		return null;
	}
	FontRenderContext _frc=null;
	@Override
	public FontRenderContext getFontRenderContext() {
		//p("getFontRendererContext");
		if(_frc==null)
			_frc=new FontRenderContext(null,true,true);
		return _frc;
	}
	@Override
	public Paint getPaint() {
		p("getPaint");
		return _paint;
	}
	@Override
	public Object getRenderingHint(Key hintKey) {
		p("GetRenderingHint-"+hintKey.toString());
		return null;
	}
	@Override
	public RenderingHints getRenderingHints() {
		p("getRenderingHints");
		return null;
	}
	@Override
	public Stroke getStroke() {
		p("getStroke");
		return null;
	}
	@Override
	public AffineTransform getTransform() {
		p("getAffineTRansform");
		return _affineTransform;
	}
	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		p("hit");
		return false;
	}
	@Override
	public void rotate(double theta) {
		q("rotate",theta);
	}
	@Override
	public void rotate(double theta, double x, double y) {
		q("rotate",theta,x,y);
	}
	@Override
	public void scale(double sx, double sy) {
		q("scale",sx,sy);
	}
	Color _background;
	@Override
	public void setBackground(Color color) {
		p("setBackground",color.getRGB());
		_background=color;
	}
	Composite _composite=null;
	@Override
	public void setComposite(Composite comp) {
		p("setComposite");
		_composite=comp;
	}
	Paint _paint;
	@Override
	public void setPaint(Paint paint) {
		p("setPaint");
		_paint=paint;
	}
	
	Hashtable<String, String> rhs=new Hashtable<String, String>();
	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		String currentHintValue=rhs.get(hintKey.toString());
		if(currentHintValue==null || !currentHintValue.equals(hintValue.toString())){
			pKV("setRenderingHint",hintKey.toString(),hintValue.toString(),1);
			rhs.put(hintKey.toString(), hintValue.toString());
		}
	}
	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		po("setRenderingHints-MAP",2);
		Set<?> keys=hints.keySet();
		for(Iterator<?> it=keys.iterator();it.hasNext();){
			Object key=it.next();
			pKV("setRenderingHint",key.toString(),hints.get(key).toString(),3);
		}
		po("setRenderingHints-MAP",2);
	}
	Stroke _stroke=null;
	@Override
	public void setStroke(Stroke s) {
		_stroke=s;
		if(s instanceof BasicStroke){
			BasicStroke bs=(BasicStroke)s;
			po("setStroke",2);
			qf("dashArray",3,bs.getDashArray());
			qf2("dashPhase",3,bs.getDashPhase());
			qf2("lineWidth",3,bs.getLineWidth());
			qf2("miterLimit",3,bs.getMiterLimit());
			switch(bs.getEndCap()){
			case BasicStroke.CAP_BUTT: p(3,"capButt"); break;
			case BasicStroke.CAP_ROUND: p(3,"capRound"); break;
			case BasicStroke.CAP_SQUARE: p(3,"capSquare"); break;
			}
			switch(bs.getLineJoin()){
			case BasicStroke.JOIN_BEVEL: p(3,"joinBevel"); break;
			case BasicStroke.JOIN_MITER: p(3,"joinMiter"); break;
			case BasicStroke.JOIN_ROUND: p(3,"joinRound"); break;
			}
			pc("setStroke",2);
		}else{
			pStr(1,"setStroke", s.toString());
		}
	}
	AffineTransform _affineTransform=AffineTransform.getScaleInstance(1, 1);
	@Override
	public void setTransform(AffineTransform Tx) {
		_affineTransform=Tx;
	}
	@Override
	public void shear(double shx, double shy) {
		q("shear",shx,shy);
	}
	@Override
	public void transform(AffineTransform Tx) {
		po("transform-Tx",2);
		pa(Tx, 3);
		pc("transform-Tx",2);
	}
	@Override
	public void translate(double tx, double ty) {
		q("translate",tx,ty);
	}

	/**
	 * Converts the specified image to a <code>java.awt.image.BuffededImage</code>.  
	 * If the image is already a buffered image, it is cast and returned.  
	 * Otherwise, the image is drawn onto a new buffered image.  
	 * 
	 * @param  img  the image
	 * @return  a buffered image
	 */
	public static BufferedImage imageToBufferedImage(Image img) {
		// if it's already a buffered image, return it (assume it's fully loaded already)
		if(img instanceof BufferedImage) {
			return (BufferedImage)img;
		}
		// create a new buffered image and draw the specified image on it
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), 
			BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		g2d.dispose();
		return bi;
	}
	/**
	 * Converts the specified image to a byte array which is an image file 
	 * of the specified format.  The formats that can be used are whatever 
	 * formats are supported by the Java Image I/O package.  
	 * 
	 * @param  img     the image
	 * @param  format  the image format (jpeg, png, etc)
	 * @return  the bytes of the image file
	 * @throws  IOException  on I/O errors
	 */
	@SuppressWarnings("unchecked")
	public static byte[] toFormat(Image img, String format) throws IOException {
		BufferedImage bi = imageToBufferedImage(img);
		Iterator writers = ImageIO.getImageWritersByFormatName(format.toLowerCase());
		if(writers == null || !writers.hasNext()) {
			throw new IllegalArgumentException("Unsupported format (" + format + ")");
		}
		ImageWriter writer = (ImageWriter)writers.next();
		IIOImage iioImg = new IIOImage(bi, null, null);
		ImageWriteParam iwparam = writer.getDefaultWriteParam();
		// if JPEG, set image quality parameters
		if("jpeg".equalsIgnoreCase(format)) {
			iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwparam.setCompressionQuality(1.0f);
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writer.setOutput(ImageIO.createImageOutputStream(out));
		writer.write(null, iioImg, iwparam);
		return out.toByteArray();
	}
 

}
