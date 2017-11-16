package org.singsurf.compat;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;

public class Stroke {

	Context2d.LineCap lc;
	Context2d.LineJoin  lj;
	public double lw, ml;
	//public FillStrokeStyle fss;
	
	public Stroke(String lineCap, String lineJoin, 
			double lineWidth, double miterLimit) {
		lc = Context2d.LineCap.valueOf(lineCap);
		lj = Context2d.LineJoin.valueOf(lineJoin);
		lw = lineWidth;
		ml = miterLimit;
		//fss = strokeStyle;
	}

	public Stroke(LineCap lc, LineJoin lj, double lw, double ml) {
		super();
		this.lj = lj;
		this.lc = lc;
		this.lw = lw;
		this.ml = ml;
		//this.fss = fss;
	}
	
}
