/*
Created 2 Apr 2007 - Richard Morris
*/
package org.singsurf.wallpaper;

import org.singsurf.compat.Color;
import org.singsurf.compat.Graphics;
import org.singsurf.compat.Point;
import org.singsurf.compat.Rectangle;
import org.singsurf.wallpaper.tessrules.TessRule;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class DrawableRegion {
    static final boolean DEBUG = false;

    /** Un-zoomed rectangle */
    public Rectangle baseRect;
    /** Zoomed rectangle for source pixels */
    public Rectangle srcRect;
    /** Zoomed rectangle for destination pixels */
    public Rectangle destRect = new Rectangle(0, 0, 100, 100);
    /** Display area */
    public Rectangle dispRect;
    /** Offset for actual coordinates from image coordinates. */
    public Point offset = new Point(0, 0);
    /** area actually drawn */
    // int minX,maxX,minY,maxY;
    /** size of image */
    // int width,height;
    /** offset used creating an expanded image */
    // int offsetX=0,offsetY=0;

    int backgroundRGB = Color.black.getRGB();

    /** displayable area */
    int viewpointL, viewpointR, viewpointT, viewpointB;

    /** pixels of underlying image */
    // public int[] inpixels;
    /** pixels of work image */
    // public int[] pixels;

    public ImageData inImageData, outImageData;
    public CanvasPixelArray inCPA;

    public CanvasPixelArray outCPA;

    // MemoryImageSource source;

    /** Output image */
    // Image outImage;

    /** Status */
    public boolean img_ok = false;

    /** Wallpaper instance */
    Wallpaper wall = null;

    public DrawableRegion(Wallpaper wall) {
	this.wall = wall;
    }

    void calcDispRegion() {
	int minX = this.viewpointL;
	int minY = this.viewpointT;
	int maxX = (this.destRect.width > this.viewpointR ? this.viewpointR : this.destRect.width);
	int maxY = (this.destRect.height > this.viewpointB ? this.viewpointB : this.destRect.height);
	this.dispRect = new Rectangle(minX, minY, maxX - minX, maxY - minY);
	if (DEBUG)
	    System.out.println("CDR w " + this.destRect.width + " h " + this.destRect.height + " ");
	if (DEBUG)
	    System.out.println("vp " + viewpointL + " " + viewpointT + " " + viewpointR + " " + viewpointB);
	if (DEBUG)
	    System.out.println("" + minX + " " + minY + " " + maxX + " " + maxY);
    }

    // public void fillSource() {
    // if(!img_ok) return;
    // //System.out.println("fillSource");
    // this.source.newPixels(this.dispRect.x,this.dispRect.y,this.dispRect.width,this.dispRect.height);
    // }

    public void reset() {
	if (!img_ok)
	    return;
	// System.out.println("reload");
	for (int i = 0; i < inCPA.getLength(); ++i)
	    outCPA.set(i, inCPA.get(i));
    }

    public void resetDelayed() {
	if (!img_ok)
	    return;
	// System.out.println("reloadDelayed");
	// System.arraycopy(inpixels,0,pixels,0,inpixels.length);
	for (int i = 0; i < inCPA.getLength(); ++i)
	    outCPA.set(i, inCPA.get(i));
    }

    public boolean loadImage(Image imgin, int width, int height) {
	img_ok = false;
	int w = 0, h = 0;
	int offX = imgin.getWidth() < width ? (width - imgin.getWidth()) / 2 : 0;
	int offY = imgin.getHeight() < height ? (height - imgin.getHeight()) / 2 : 0;

	h = imgin.getHeight();
	w = imgin.getWidth();
	GWT.log("load image " + w + " " + h);
	Canvas canvasTmp = Canvas.createIfSupported();
	Context2d contextTmp = canvasTmp.getContext2d();
	canvasTmp.setCoordinateSpaceWidth(width);
	canvasTmp.setCoordinateSpaceHeight(height);
	ImageElement imageElement = ImageElement.as(imgin.getElement());
	contextTmp.setFillStyle("black");
	contextTmp.fillRect(0, 0, width, height);
	contextTmp.drawImage(imageElement, offX, offY);
	inImageData = contextTmp.getImageData(0, 0, width, height);
	outImageData = contextTmp.createImageData(width, height);
	inCPA = inImageData.getData();
	outCPA = outImageData.getData();
	copySrcDest();
	this.srcRect = new Rectangle(0, 0, width, height);
	this.destRect = new Rectangle(0, 0, width, height);
	calcDispRegion();

	if (DEBUG)
	    System.out.println("loadImage successful: Width " + w + " height " + h);
	this.img_ok = true;
	return img_ok;
    }

    public boolean loadImageData(ImageData imgin) {
	img_ok = false;
	int w = 0, h = 0;

	h = imgin.getHeight();
	w = imgin.getWidth();

	GWT.log("load image " + w + " " + h);
	inImageData = imgin;
	outImageData = wall.context.createImageData(w, h);
	inCPA = inImageData.getData();
	outCPA = outImageData.getData();
	copySrcDest();
	this.srcRect = new Rectangle(0, 0, w, h);
	this.destRect = new Rectangle(0, 0, w, h);
	calcDispRegion();

	if (DEBUG)
	    System.out.println("loadImageData successful: Width " + w + " height " + h);
	this.img_ok = true;
	return img_ok;
    }

    public boolean isGoodImage() {
	return this.img_ok;
    }

    // public Image getActiveImage() {
    // if(source!=null)
    // return outImage;
    // return null;
    // }

    public void setViewport(Rectangle r) {
	if (DEBUG)
	    System.out.println("DR.setViewport " + r);
	this.viewpointL = r.x - this.offset.x;
	this.viewpointR = r.x + r.width - this.offset.x;
	this.viewpointT = r.y - this.offset.y;
	this.viewpointB = r.y + r.height - this.offset.y;
	calcDispRegion();
    }

    protected void makeSrc(int w, int h) {
	this.srcRect = new Rectangle(0, 0, w, h);
	inImageData = wall.context.createImageData(w, h);
	inCPA = inImageData.getData();
    }

    protected void makeDest(int w, int h) {
	this.destRect = new Rectangle(0, 0, w, h);
	outImageData = wall.context.createImageData(w, h);
	outCPA = inImageData.getData();
    }

    protected void copySrcDest() {
	for (int i = 0; i < inCPA.getLength(); ++i)
	    outCPA.set(i, inCPA.get(i));
    }

    protected void copyDestSrc() {
	for (int i = 0; i < inCPA.getLength(); ++i)
	    inCPA.set(i, outCPA.get(i));
    }

    // protected void makeOutImage() {
    // if(DEBUG) System.out.println("makeOutImage");
    // this.source = new MemoryImageSource(this.destRect.width,
    // this.destRect.height, pixels, 0, this.destRect.width);
    // this.source.setAnimated(true);
    // this.outImage = Toolkit.getDefaultToolkit().createImage(source);
    // }

    public void resize(int w, int h, int xoff, int yoff) {
	int px_r, px_g, px_b, px_a;

	// try {
	System.gc();
	makeDest(w, h);
	if (TessRule.tileBackground) {
	    for (int j = 0; j < h; ++j)
		for (int i = 0; i < w; ++i) {
		    int sx = (i - xoff) % this.srcRect.width;
		    if (sx < 0)
			sx += this.srcRect.width;
		    int sy = (j - yoff) % this.srcRect.height;
		    if (sy < 0)
			sy += this.srcRect.height;

		    int outInd = 4 * (i + j * w);
		    int inInd = 4 * (sx + sy * this.srcRect.width);
		    px_r = inCPA.get(inInd);
		    px_g = inCPA.get(inInd + 1);
		    px_b = inCPA.get(inInd + 2);
		    px_a = inCPA.get(inInd + 3);

		    outCPA.set(outInd, px_r);
		    outCPA.set(outInd + 1, px_g);
		    outCPA.set(outInd + 2, px_b);
		    outCPA.set(outInd + 3, px_a);
		}
	} else {
	    for (int i = 0; i < w; ++i)
		for (int j = 0; j < h; ++j) {
		    // pixels[i+j*w]=this.backgroundRGB;
		    int outInd = 4 * (i + j * w);
		    outCPA.set(outInd, 0);
		    outCPA.set(outInd + 1, 0);
		    outCPA.set(outInd + 2, 0);
		    outCPA.set(outInd + 3, 0);
		}

	    for (int i = 0; i < this.srcRect.width; ++i)
		for (int j = 0; j < this.srcRect.height; ++j) {
		    int x = i + xoff;
		    int y = j + yoff;
		    if (x >= 0 && x < w && y >= 0 && y < h) {
			// pixels[x+y*w] = inpixels[i+j*this.destRect.width];
			int outInd = 4 * (i + j * w);
			int inInd = 4 * (i + j * this.destRect.width);
			px_r = inCPA.get(inInd);
			px_g = inCPA.get(inInd + 1);
			px_b = inCPA.get(inInd + 2);
			px_a = inCPA.get(inInd + 3);

			outCPA.set(outInd, px_r);
			outCPA.set(outInd + 1, px_g);
			outCPA.set(outInd + 2, px_b);
			outCPA.set(outInd + 3, px_a);
		    }
		}
	}
	makeSrc(w, h);
	copyDestSrc();

	calcDispRegion();
	// makeOutImage();
	this.img_ok = true;
	// }
	// catch(OutOfMemoryError e) {
	// reportMemoryError(e,w*h*2);
	// }
    }

    public void rescale(int w, int h) {
	// try {
	// MemoryImageSource mis = new
	// MemoryImageSource(this.srcRect.width,this.srcRect.height,inpixels, 0,
	// this.srcRect.width);
	// ImageFilter scale = new AreaAveragingScaleFilter(w,h);
	// ImageProducer prod = new FilteredImageSource(mis,scale);
	// Image img = Toolkit.getDefaultToolkit().createImage(prod);
	// loadImage(img);
	// }
	// catch(OutOfMemoryError e) {
	// reportMemoryError(e,w*h*2);
	// }
    }

    public void flip(String code) {
	int px_r, px_g, px_b, px_a;

	// rotated width and height
	int w = this.destRect.height, h = this.destRect.width;
	// original width and height
	int ow = this.destRect.width, oh = this.destRect.height;
	// try {
	// System.out.println("flip " + code);
	if (code.equals(Wallpaper.FLIP_X)) {
	    for (int j = 0; j < oh; ++j)
		for (int i = 0; i < ow; ++i) {
		    // pixels[i+j*ow] = inpixels[(ow-1-i)+j*ow];
		    int outInd = 4 * (i + j * ow);
		    int inInd = 4 * ((ow - 1 - i) + j * ow);
		    px_r = inCPA.get(inInd);
		    px_g = inCPA.get(inInd + 1);
		    px_b = inCPA.get(inInd + 2);
		    px_a = inCPA.get(inInd + 3);

		    outCPA.set(outInd, px_r);
		    outCPA.set(outInd + 1, px_g);
		    outCPA.set(outInd + 2, px_b);
		    outCPA.set(outInd + 3, px_a);
		}
	} else if (code.equals(Wallpaper.FLIP_Y)) {
	    for (int j = 0; j < oh; ++j)
		for (int i = 0; i < ow; ++i) {
		    // pixels[i+j*ow] = inpixels[i+(oh-1-j)*ow];
		    int outInd = 4 * (i + j * ow);
		    int inInd = 4 * (i + (oh - 1 - j) * ow);
		    px_r = inCPA.get(inInd);
		    px_g = inCPA.get(inInd + 1);
		    px_b = inCPA.get(inInd + 2);
		    px_a = inCPA.get(inInd + 3);

		    outCPA.set(outInd, px_r);
		    outCPA.set(outInd + 1, px_g);
		    outCPA.set(outInd + 2, px_b);
		    outCPA.set(outInd + 3, px_a);
		}
	} else if (code.equals(Wallpaper.FLIP_90)) {
	    for (int j = 0; j < h; ++j)
		for (int i = 0; i < w; ++i) {
		    // pixels[i+j*w] = inpixels[j+(oh-1-i)*ow];
		    int outInd = 4 * (i + j * ow);
		    int inInd = 4 * (j + (oh - 1 - i) * ow);
		    px_r = inCPA.get(inInd);
		    px_g = inCPA.get(inInd + 1);
		    px_b = inCPA.get(inInd + 2);
		    px_a = inCPA.get(inInd + 3);

		    outCPA.set(outInd, px_r);
		    outCPA.set(outInd + 1, px_g);
		    outCPA.set(outInd + 2, px_b);
		    outCPA.set(outInd + 3, px_a);
		}
	    this.destRect = new Rectangle(0, 0, w, h);
	    this.srcRect = new Rectangle(0, 0, w, h);
	} else if (code.equals(Wallpaper.FLIP_180)) {
	    for (int j = 0; j < oh; ++j)
		for (int i = 0; i < ow; ++i) {
		    // pixels[i+j*ow] = inpixels[(ow-1-i)+(oh-1-j)*ow];
		    int outInd = 4 * (i + j * ow);
		    int inInd = 4 * ((ow - 1 - i) + (oh - 1 - j) * ow);
		    px_r = inCPA.get(inInd);
		    px_g = inCPA.get(inInd + 1);
		    px_b = inCPA.get(inInd + 2);
		    px_a = inCPA.get(inInd + 3);

		    outCPA.set(outInd, px_r);
		    outCPA.set(outInd + 1, px_g);
		    outCPA.set(outInd + 2, px_b);
		    outCPA.set(outInd + 3, px_a);
		}
	} else if (code.equals(Wallpaper.FLIP_270)) {
	    for (int j = 0; j < h; ++j)
		for (int i = 0; i < w; ++i) {
		    // pixels[i+j*w] = inpixels[(ow-1-j)+i*ow];
		    int outInd = 4 * (i + j * ow);
		    int inInd = 4 * ((ow - 1 - j) + i * ow);
		    px_r = inCPA.get(inInd);
		    px_g = inCPA.get(inInd + 1);
		    px_b = inCPA.get(inInd + 2);
		    px_a = inCPA.get(inInd + 3);

		    outCPA.set(outInd, px_r);
		    outCPA.set(outInd + 1, px_g);
		    outCPA.set(outInd + 2, px_b);
		    outCPA.set(outInd + 3, px_a);
		}
	    this.destRect = new Rectangle(0, 0, w, h);
	    this.srcRect = new Rectangle(0, 0, w, h);
	} else { // flip x-y
	    for (int j = 0; j < h; ++j)
		for (int i = 0; i < w; ++i) {
		    // pixels[i+j*w] = inpixels[j+i*ow];
		    int outInd = 4 * (i + j * ow);
		    int inInd = 4 * (j + i * ow);
		    px_r = inCPA.get(inInd);
		    px_g = inCPA.get(inInd + 1);
		    px_b = inCPA.get(inInd + 2);
		    px_a = inCPA.get(inInd + 3);

		    outCPA.set(outInd, px_r);
		    outCPA.set(outInd + 1, px_g);
		    outCPA.set(outInd + 2, px_b);
		    outCPA.set(outInd + 3, px_a);
		}
	    this.destRect = new Rectangle(0, 0, w, h);
	    this.srcRect = new Rectangle(0, 0, w, h);
	}

	copyDestSrc();
	calcDispRegion();
	// makeOutImage();
	this.img_ok = true;
	// }
	// catch(OutOfMemoryError e) {
	// reportMemoryError(e,w*h*2);
	// }

    }

    public void paint(Graphics g, Wallpaper wall2) {
	if (DEBUG)
	    System.out.println("DR:paint");
	// try {
	g.drawImage(outImageData, this.offset.x, this.offset.y, wall2);
	// g.
	// g.gcontext.putImageData(outImageData, this.offset.x, this.offset.y );
	// }
	// catch(OutOfMemoryError e) {
	// reportMemoryError(e,destRect.width*destRect.height);
	// }

	return;
    }

    // protected void reportMemoryError(OutOfMemoryError e,int reqSize) {
    // if(wall!=null) wall.setText("Out of memory");
    // System.err.print("Out of memory: '"+e.getMessage());
    // System.err.println("' while resizing image");
    // //System.err.println("MaxMemory "+Runtime.getRuntime().maxMemory());
    // System.err.println("FreeMemory "+Runtime.getRuntime().freeMemory());
    // System.err.println("This image will require "+(reqSize*4)+" bytes");
    // //System.err.println("TotalMemory "+Runtime.getRuntime().totalMemory());
    // System.err.println("Rerun application with the -Xmx512m VM flag to assign
    // more memory");
    // }

}
