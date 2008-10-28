package lrf.gui;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.jcp.xml.dsig.internal.dom.Utils;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.resource.CSSResource;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.resource.XMLResource;
import org.xhtmlrenderer.swing.AWTFSImage;
import org.xhtmlrenderer.util.XRLog;

public class EPUBUserAgentCallback implements UserAgentCallback {

	String _baseURL;
	
	public EPUBUserAgentCallback(String ub){
		setBaseURL(ub);
	}
	
	@Override
	public String getBaseURL() {
		return _baseURL;
	}

	@Override
	public byte[] getBinaryResource(String uri) {
		InputStream is=resolveAndOpenStream(uri);
		try {
			return Utils.readBytesFromStream(is);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public CSSResource getCSSResource(String uri) {
        return new CSSResource(resolveAndOpenStream(uri));
	}

	@Override
	public ImageResource getImageResource(String uri) {
        ImageResource ir = null;
        uri = resolveURI(uri);
        //TODO: check that cached image is still valid
        InputStream is = resolveAndOpenStream(uri);
        if (is != null) {
            try {
                BufferedImage img = ImageIO.read(is);
                if (img == null) {
                    throw new IOException("ImageIO.read() returned null");
                }
                ir = createImageResource(uri, img);
            } catch (FileNotFoundException e) {
                XRLog.exception("Can't read image file; image at URI '" + uri + "' not found");
            } catch (IOException e) {
                XRLog.exception("Can't read image file; unexpected problem for URI '" + uri + "'", e);
            }
        }
        if (ir == null) {
            ir = createImageResource(uri, null);
        }
        return ir;
	}

	/**
	 * Factory method to generate ImageResources from a given Image. May be overridden in subclass.

	 * @param uri The URI for the image, resolved to an absolute URI. 
	 * @param img The image to package; may be null (for example, if image could not be loaded).
	 *
	 * @return An ImageResource containing the image.
	 */
	protected ImageResource createImageResource(String uri, Image img) {
		return new ImageResource(AWTFSImage.createImage(img));
	}


	
	@Override
	public XMLResource getXMLResource(String uri) {
		XMLResource xmlresResource;
		InputStream is=null;
		try {
			is=new URL(uri).openStream();
			xmlresResource = XMLResource.load(is);
		} catch (MalformedURLException e1) {
			return null;
		} catch (IOException e2) {
			return null;
		} finally {
			if(is!=null) try {
				is.close();
			}catch (IOException e3){
				
			}
		}
		return xmlresResource;
	}

	@Override
	public boolean isVisited(String uri) {
		return false;
	}

	@Override
	public String resolveURI(String uri) {
		if(uri==null)
			return null;
		String ret=null;
		if(_baseURL==null){
			try {
				URL result=new URL(uri);
				setBaseURL(result.toExternalForm());
			}catch(MalformedURLException e){
				return null;
			}
		}
		try {
			URL result=new URL(new URL(_baseURL),uri);
			ret=result.toString();
		} catch(MalformedURLException e){
			ret=null;
		}
		return ret;
	}

	@Override
	public void setBaseURL(String url) {
		if(_baseURL!=null && url!=null && url.length()==0)
			return;
		_baseURL=url;
	}

	/**
     * Gets a Reader for the resource identified
     *
     * @param uri PARAM
     * @return The stylesheet value
     */
    //TOdO:implement this with nio.
    protected InputStream resolveAndOpenStream(String uri) {
        java.io.InputStream is = null;
        uri = resolveURI(uri);
        try {
            is = new URL(uri).openStream();
        } catch (java.net.MalformedURLException e) {
            XRLog.exception("bad URL given: " + uri, e);
        } catch (java.io.FileNotFoundException e) {
            XRLog.exception("item at URI " + uri + " not found");
        } catch (java.io.IOException e) {
            XRLog.exception("IO problem for " + uri, e);
        }
        return is;
    }

}
