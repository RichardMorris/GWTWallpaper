package org.singsurf.compat;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;

public class FontMetrics {
	Context2d c;
	public FontMetrics(Context2d context) {
		c =context;
	}

	public int getHeight() {
		int x = (int) c.measureText("x").getWidth()*3;
		GWT.log("Height "+x);
		return x;
	}

	public int stringWidth(String out) {
		return (int) c.measureText(out).getWidth();
	}

	public int getAscent() {
		int x = (int) c.measureText("x").getWidth()*2;
		GWT.log("Ascent "+x);
		return x;
	}

	public int getMaxAscent() {
		int x = (int) c.measureText("x").getWidth()*2;
		GWT.log("Max Ascent "+x);
		return x;
	}

}
