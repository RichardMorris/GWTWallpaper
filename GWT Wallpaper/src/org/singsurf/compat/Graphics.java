package org.singsurf.compat;


import org.singsurf.wallpaper.Wallpaper;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;


public class Graphics {

	Context2d context;
	
	public Graphics(Context2d context) {
		super();
		this.context = context;
		//context.setTransform(1, 0, 0, 1, 0, 0);
	}

	public void setColor(Color c) {
		context.stroke();
		context.beginPath();
		context.setStrokeStyle(c.getGWTColor());
		context.setFillStyle(c.getGWTColor());
	}

	public void fillRect(int i, int j, int wid, int high) {
		context.fillRect(i, j, wid, high);
	}

	public void drawLine(int x0, int y0, int x1, int y1) {
		context.moveTo(x0, y0);
		context.lineTo(x1, y1);
	}

	public FontMetrics getFontMetrics() {
		return new FontMetrics(context);
	}

	public void drawString(String out, int xpos, int ypos) {
		context.fillText(out, xpos, ypos);
	}

	Font currentFont;
	public void setFont(Font labelFont) {
		currentFont = labelFont;
		context.setFont(labelFont.GWTString());
	}

	public void stroke() {
		context.closePath();
		context.stroke();
		context.beginPath();
	}


	public void translate(int x, int y) {
		context.translate(x, y);
	}

	public void drawRect(int x, int y, int width, int height) {
		context.fillRect(x, y, width, height);
		
	}

	public void fillPolygon(int[] triX, int[] triY, int i) {
		context.beginPath();
		context.moveTo(triX[0], triY[0]);
		context.lineTo(triX[1], triY[1]);
		context.lineTo(triX[1], triY[1]);
		context.lineTo(triX[1], triY[1]);
		context.fill();
	}
	
	public Stroke getStroke() {
		return new Stroke(context.getLineCap(),
		context.getLineJoin(),
		context.getLineWidth(),
		context.getMiterLimit()
		);

	}
	
	public void setStroke(Stroke oldStroke) {
		context.setLineCap(oldStroke.lc);
		context.setLineJoin(oldStroke.lj);
		context.setLineWidth(oldStroke.lw);
		context.setMiterLimit(oldStroke.ml);
		//context.setStrokeStyle(oldStroke.fss);
		
	}

	public void drawImage(ImageData outImageData, int x, int y, Wallpaper wall) {
		context.putImageData(outImageData,x,y);
	}

	public void clearRect(int x, int y, int w, int h) {
		context.clearRect(x, y, w, h);
		
	}

	public void setPaintMode() {
		// TODO Auto-generated method stub
		
	}

	public void fillRoundRect(int x, int y, int w, int h, int r1, int r2) {
		context.fillRect(x, y, w, h);
		
	}

	Rectangle clipBounds;
	public void setClipBounds(Rectangle rectangle) {
		clipBounds = rectangle;
	}

	public org.singsurf.compat.Rectangle getClipBounds() {
		return clipBounds;
	}

	public void fillOval(float x, float y, float w, float h) {
		context.beginPath();
		context.arc(x, y, w/2, 0, Math.PI*2,false);
		context.fill();		
	}

	public void drawOval(int x, int y, int w, int h) {
		context.beginPath();
		context.arc(x, y, w/2, 0, Math.PI*2,false);
		context.stroke();		
	}


}
