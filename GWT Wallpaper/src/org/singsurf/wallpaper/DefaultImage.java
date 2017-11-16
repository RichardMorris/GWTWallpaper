/*
Created 18 May 2007 - Richard Morris
*/
package org.singsurf.wallpaper;

import org.singsurf.compat.Color;
import org.singsurf.compat.Graphics;

import com.google.gwt.canvas.dom.client.ImageData;



public class DefaultImage {
	private static final int NUM_CIRCLES = 100;
    /** Size of default image */
    static final int MIN_SIZE=10;
    static final int MAX_SIZE=200;
    
	   static ImageData createDefaultImage(int width,int height, Wallpaper wall) {
		   Graphics g = wall.graphics;
			g.setColor(Color.black);
	    	g.fillRect(0,0,width,height);
	    	for(int i=0;i<NUM_CIRCLES;++i) {
	    		float r = (float) Math.random();
	    		float gr = (float) Math.random();
	    		float b = (float) Math.random();
	    		g.setColor(new Color(r,gr,b));
	    		int x = (int) (Math.random() * width);
	    		int y = (int) (Math.random() * height);
	    		int w = (int) (Math.random() * MAX_SIZE + MIN_SIZE);
//	    		int min =x; 
//	   		if(y<min) min=y;
//	    		if(DEFAULT_SIZE-x<min) min = DEFAULT_SIZE-x;
//	    		if(DEFAULT_SIZE-y<min) min = DEFAULT_SIZE-y;

	    		g.fillOval(x-width, y-height, w,w);
	    		g.fillOval(x-width, y		, w,w);
	    		g.fillOval(x-width, y+height, w,w);
	    		
	    		g.fillOval(x, y-height, w,w);
	    		g.fillOval(x, y       , w,w);
	    		g.fillOval(x, y+height, w,w);

	    		g.fillOval(x+height, y-height, w,w);
	    		g.fillOval(x+height, y		 , w,w);
	    		g.fillOval(x+height, y+height, w,w);
	    	}
			ImageData res = wall.context.getImageData(0, 0, width, height);
			return res;
	    }

}
