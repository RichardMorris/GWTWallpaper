package org.singsurf.compat;

public class Point {

	public int x,y;

	public Point(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Point(Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	
}
