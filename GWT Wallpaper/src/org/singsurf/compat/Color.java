package org.singsurf.compat;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;


public class Color {

	public static final Color white = new Color("white");
	public static final Color gray = new Color("gray");
	public static final Color darkGray = new Color("darkGray");
	public static final Color lightGray = new Color("lightGray");
	public static final Color black=new Color("black");
	public static final Color red = new Color("red");
	public static final Color green = new Color("green");
	public static final Color blue = new Color("blue");
	public static final Color cyan = new Color("cyan");
	public static final Color magenta = new Color("magenta");
	public static final Color yellow = new Color("yellow");
	public static final Color orange = new Color("orange");
	
	CssColor css;
	
	Color(String text) {
		css=CssColor.make(text);
	}
	public Color(int r, int g, int b) {
		css = CssColor.make(r, g, b);
	}
	public Color(float r, float g, float b) {
		css = CssColor.make((int)(r*255), (int) (g*255), (int) (b*255));
		GWT.log(css.toString());
	}
	public String getGWTColor() {
		return css.value();
	}
	public int getRGB() {
		// TODO Auto-generated method stub
		return 0;
	}

}
