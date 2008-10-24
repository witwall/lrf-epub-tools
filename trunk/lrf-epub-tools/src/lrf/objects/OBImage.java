package lrf.objects;

import lrf.buffer.Reader;
import lrf.conv.Renderer;
import lrf.objects.tags.Tag;
import lrf.objects.tags.TagStream;
import lrf.parse.ParseException;

import com.lowagie.text.Image;
import com.lowagie.text.Jpeg;
import com.lowagie.text.pdf.codec.BmpImage;
import com.lowagie.text.pdf.codec.GifImage;
import com.lowagie.text.pdf.codec.PngImage;

public class OBImage extends BBObj {
	public OBImage(Book b, int id, Reader pb) throws ParseException {
		super(b, id, ot_Image, pb);
	}

	@Override
	public void render(Renderer pars) throws Exception {
		Image img = null;

		float wi = 0, he = 0;
		String extension = null;
		byte b[] = null;
		for (int i = 0; i < getTags().size(); i++) {
			Tag tag = getTags().elementAt(i);
			if (tag.getName().equals("*ImageStream")) {
				BBObj isObject = padre.getObject(tag.getValueAt(0));
				TagStream isTag = (TagStream) isObject.getTagAt(0);
				b = isTag.getBytes();
				if (isTag.imageType.equals(".jpg")) {
					img = new Jpeg(b);
					extension = ".jpg";
				} else if (isTag.imageType.equals(".gif")) {
					GifImage gif = new GifImage(b);
					img = gif.getImage(1);
					extension = ".gif";
				} else if (isTag.imageType.equals(".bmp")) {
					img = BmpImage.getImage(b);
					extension = ".bmp";
				} else { // Hay que probar PNG
					img = PngImage.getImage(b);
					extension = ".png";
				}
			} else if (tag.getName().equals("*ImageRect")) {
				wi = tag.getValueAt(2);
				he = tag.getValueAt(3);
			} else if (tag.getName().equals("*CanvasHeight")) {
				he = tag.getValueAt(0);
			} else if (tag.getName().equals("*CanvasWidth")) {
				wi = tag.getValueAt(0);
			}
		}
		if (img != null) {
			img.scaleToFit(wi / 2.5F, he / 2.5F);
			img.setAlignment(Image.MIDDLE + Image.TEXTWRAP);
			pars.addImage(img, extension, b);
		}
	}

}
