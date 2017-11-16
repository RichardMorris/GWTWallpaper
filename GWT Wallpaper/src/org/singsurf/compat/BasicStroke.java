package org.singsurf.compat;

import com.google.gwt.canvas.dom.client.Context2d;

public class BasicStroke extends Stroke {

	public BasicStroke(int width) {
		super(Context2d.LineCap.SQUARE, 
				Context2d.LineJoin.MITER, 
				(double) width, 1.0);
		
	}

}
