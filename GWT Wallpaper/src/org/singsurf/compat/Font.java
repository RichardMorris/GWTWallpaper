package org.singsurf.compat;

public class Font {
	
	public static final int PLAIN = 1;
	public static final int BOLD = 2;
	public static final int ITALIC = 3;

	String fontName;
	int style;
	int pointSize;


	public Font(String fontName, int style, int pointSize) {
		super();
		this.fontName = fontName;
		this.style = style;
		this.pointSize = pointSize;
	}


	public String GWTString() {
		String res = "";
		if(style==BOLD)
			res = "bold ";
		if(style==ITALIC)
			res = "italic ";
		res += "" + pointSize + "px ";
		res += fontName;
		return res;
	}

}
