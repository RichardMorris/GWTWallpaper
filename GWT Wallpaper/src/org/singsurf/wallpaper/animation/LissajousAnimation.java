/*
Created 9 Sep 2006 - Richard Morris
*/
package org.singsurf.wallpaper.animation;

import org.singsurf.compat.Rectangle;
import org.singsurf.wallpaper.FundamentalDomain;

public class LissajousAnimation extends AnimationPath {

	double speed;
	private Rectangle rect;
	double m = 2.8;
	double n = 1.9;
	double t = 0;
	double dt = 0.5;

	public LissajousAnimation(int speed) {
		this.speed = speed;
		//Wallpaper.consoleLog("LAN constructor:");
	}

	int deltax[] = {0,0,0,0,0,0};
	int deltay[] = {0,0,0,0,0,0};
	int fdwidth = 0;
	int fdheight = 0;
	
	@Override
	public void firstItteration(FundamentalDomain fd,Rectangle rect) {
		this.rect = rect;
		Rectangle fdBB = fd.getFDBoundingBox();
		int cenx = (int) (fdBB.x + fdBB.width/2.0);
		int ceny = (int) (fdBB.y + fdBB.height/2.0);
		for(int i=0;i<6;++i)
		{
			deltax[i] = fd.cellVerts[i].x -cenx;
			deltay[i] = fd.cellVerts[i].y -ceny;
		}
		fdwidth = fdBB.width;
		fdheight = fdBB.height;
		//Wallpaper.consoleLog("LAN firstItteration: fdBB.x="+fdBB.x+" fdBB.y="+fdBB.y+" fdBB.width="+fdBB.width+" fdBB.height="+fdBB.height);
		//Wallpaper.consoleLog("                   : rect.x="+rect.x+" rect.y="+rect.y+" rect.width="+rect.width+" rect.height="+rect.height);
	}

	public void nextItteration(FundamentalDomain fd) {
		
		double x0 = rect.x + rect.width/2.0 + Math.sin(m*t) * (rect.width-fdwidth)/2.0;
		double y0 = rect.y + rect.height/2.0 + Math.sin(n*t) * (rect.height-fdheight)/2.0;
		// want to get approximately unit speed
		double dxdt = m * Math.cos(m*t) * rect.width/2;
		double dydt = n * Math.cos(n*t) * rect.height/2;
		double d = Math.sqrt(dxdt*dxdt + dydt*dydt);
		//System.out.printf("x0=%f y0=%f\n",x0,y0);
		t += 3* dt*speed/d;
		
		for(int i=0;i<6;++i)
		{
			fd.cellVerts[i].x = (int) (x0 + deltax[i]);
			fd.cellVerts[i].y = (int) (y0 + deltay[i]);
		}
//		Wallpaper.consoleLog("LAN nextItteration: fd.cellVerts[0].x="+fd.cellVerts[0].x+" fd.cellVerts[0].y="+fd.cellVerts[0].y);
	}

}
