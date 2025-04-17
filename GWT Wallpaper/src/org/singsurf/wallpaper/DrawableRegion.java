/*
Created 2 Apr 2007 - Richard Morris
*/
package org.singsurf.wallpaper;

import org.singsurf.compat.Color;
import org.singsurf.compat.Graphics;
import org.singsurf.compat.Point;
import org.singsurf.compat.Rectangle;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
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
		int minX = viewpointL;
		int minY = viewpointT;
		int maxX = (destRect.width > viewpointR ? viewpointR : destRect.width);
		int maxY = (destRect.height > viewpointB ? viewpointB : destRect.height);
		dispRect = new Rectangle(minX, minY, maxX - minX, maxY - minY);
		if (DEBUG)
			Wallpaper.consoleLog("CDR w " + destRect.width + " h " + destRect.height + " ");
		if (DEBUG)
			Wallpaper.consoleLog("vp " + viewpointL + " " + viewpointT + " " + viewpointR + " " + viewpointB);
		if (DEBUG)
			Wallpaper.consoleLog("" + minX + " " + minY + " " + maxX + " " + maxY);
	}

	// public void fillSource() {
	// if(!img_ok) return;
	// //System.out.println("fillSource");
	// source.newPixels(dispRect.x,dispRect.y,dispRect.width,dispRect.height);
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
		//Wallpaper.consoleLog("load image " + w + " " + h);
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
		srcRect = new Rectangle(0, 0, width, height);
		destRect = new Rectangle(0, 0, width, height);
		calcDispRegion();

		if (DEBUG)
			Wallpaper.consoleLog("loadImage successful: Width " + w + " height " + h);
		img_ok = true;
		return img_ok;
	}

	public boolean loadImage(Image imgin, int width, int height, double factor) {
		img_ok = false;
		int w = 0, h = 0;
		int scaledWidth = (int) (imgin.getWidth() * factor);
		int scaledHeight = (int) (imgin.getHeight() * factor);
		
		int offX = scaledWidth < width ? (width - scaledWidth) / 2 : 0;
		int offY = scaledHeight < height ? (height - scaledHeight) / 2 : 0;

		h = imgin.getHeight();
		w = imgin.getWidth();
		Wallpaper.consoleLog("load scaled image " + w + " " + h +"-> "+scaledWidth+" "+scaledHeight);
		Canvas canvasTmp = Canvas.createIfSupported();
		Context2d contextTmp = canvasTmp.getContext2d();
		canvasTmp.setCoordinateSpaceWidth(width);
		canvasTmp.setCoordinateSpaceHeight(height);
		ImageElement imageElement = ImageElement.as(imgin.getElement());
		contextTmp.setFillStyle("black");
		contextTmp.fillRect(0, 0, width, height);
		contextTmp.drawImage(imageElement, 0,0,w,h, offX, offY,scaledWidth,scaledHeight);
		//contextTmp.drawImage(imageElement, offX, offY);
		inImageData = contextTmp.getImageData(0, 0, width, height);
		outImageData = contextTmp.createImageData(width, height);
		inCPA = inImageData.getData();
		outCPA = outImageData.getData();
		copySrcDest();
		srcRect = new Rectangle(0, 0, width, height);
		destRect = new Rectangle(0, 0, width, height);
		calcDispRegion();

		if (DEBUG)
			Wallpaper.consoleLog("loadImage successful: Width " + w + " height " + h);
		img_ok = true;
		return img_ok;
	}

	public boolean loadImageData(ImageData imgin) {
		img_ok = false;
		int w = 0, h = 0;

		h = imgin.getHeight();
		w = imgin.getWidth();

//		GWT.log("load image " + w + " " + h);
		inImageData = imgin;
		outImageData = wall.context.createImageData(w, h);
		inCPA = inImageData.getData();
		outCPA = outImageData.getData();
		copySrcDest();
		srcRect = new Rectangle(0, 0, w, h);
		destRect = new Rectangle(0, 0, w, h);
		calcDispRegion();

		if (DEBUG)
			Wallpaper.consoleLog("loadImageData successful: Width " + w + " height " + h);
		img_ok = true;
		return img_ok;
	}

	public boolean isGoodImage() {
		return img_ok;
	}

	// public Image getActiveImage() {
	// if(source!=null)
	// return outImage;
	// return null;
	// }

	public void setViewport(Rectangle r) {
		if (DEBUG)
			Wallpaper.consoleLog("DR.setViewport " + r.x + " " + r.y + " " + r.width + " " + r.height);
		viewpointL = r.x - offset.x;
		viewpointR = r.x + r.width - offset.x;
		viewpointT = r.y - offset.y;
		viewpointB = r.y + r.height - offset.y;
		calcDispRegion();
	}

	protected void makeSrc(int w, int h) {
		srcRect = new Rectangle(0, 0, w, h);
		inImageData = wall.context.createImageData(w, h);
		inCPA = inImageData.getData();
	}

	protected void makeDest(int w, int h) {
		destRect = new Rectangle(0, 0, w, h);
		outImageData = wall.context.createImageData(w, h);
		outCPA = inImageData.getData();
	}

	protected void copySrcDest() {
		for (int i = 0; i < inCPA.getLength(); ++i)
			outCPA.set(i, inCPA.get(i));
	}

//	protected void copyDestSrc() {
//		for (int i = 0; i < inCPA.getLength(); ++i)
//			inCPA.set(i, outCPA.get(i));
//	}


	public void rescale(int w, int h) {
		// try {
		// MemoryImageSource mis = new
		// MemoryImageSource(srcRect.width,srcRect.height,inpixels, 0,
		// srcRect.width);
		// ImageFilter scale = new AreaAveragingScaleFilter(w,h);
		// ImageProducer prod = new FilteredImageSource(mis,scale);
		// Image img = Toolkit.getDefaultToolkit().createImage(prod);
		// loadImage(img);
		// }
		// catch(OutOfMemoryError e) {
		// reportMemoryError(e,w*h*2);
		// }
	}


	public void paint(Graphics g, Wallpaper wall2) {
//		if (DEBUG)
//			System.out.println("DR:paint");
		// try {
		g.drawImage(outImageData, offset.x, offset.y, wall2);
		// g.
		// g.gcontext.putImageData(outImageData, offset.x, offset.y );
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
