/*
Created 9 Apr 2007 - Richard Morris
*/
package org.singsurf.wallpaper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.singsurf.compat.BasicStroke;
import org.singsurf.compat.Color;
import org.singsurf.compat.Graphics;
import org.singsurf.compat.Graphics2D;
import org.singsurf.compat.Rectangle;
import org.singsurf.compat.Stroke;
import org.singsurf.wallpaper.tessrules.TessRule;

import com.google.gwt.core.client.GWT;

public class FundamentalDomain {
    final static boolean DEBUG=false;
    final static boolean BW_SYMS=false;
    public static final int HEXAGON = 1;
    public static final int FRIEZE = 2;
    public static final int POINT = 3;
    public static final int PARALLOGRAM = 0;
    public static final int BASIC=4;

//	int[] vertexX = {397,281,281,397,0,0};
//	int[] vertexY = {218,218,102,102,0,0,0};
//	int[] oldVertexX = {397,281,281,397,0,0};
//	int[] oldVertexY = {218,218,102,102,0,0,0};
	public Vec[] cellVerts = new Vec[]{ new Vec(397,281), new Vec(281,218), new Vec(281,102),
	    		new Vec(397,102), new Vec(0,0), new Vec(0,0) };
	
	Vec[] oldCellVerts = new Vec[]{ new Vec(397,281), new Vec(281,218), new Vec(281,102),
		new Vec(397,102), new Vec(0,0), new Vec(0,0) };
	
	/** Number of points round fundamental domain **/
	public int numFund=0;
	/** number of points in tessellation region */
	public int numOuterPoints=4;
	/** number of selectable points */
	public int numSelPoints=3;

	/** X coords of points in fund domain **/
//	int[] fundX = {0,0,0,0,0,0};
	/** X coords of points in fund domain **/
public //	int[] fundY = {0,0,0,0,0,0};
	Vec[] fund = new Vec[] {new Vec(0,0),new Vec(0,0),new Vec(0,0),new Vec(0,0),new Vec(0,0),new Vec(0,0)};

	private int latticeType=0;
	Vec U=null,V=null,O=null;
	public int det=0;
	
	boolean drawGlideLines = false;
        boolean drawReflectionLines = false;
        boolean drawRotationPoints = false;
        boolean drawCells = false;
        boolean drawDomain = true;
        boolean drawSelectionPoints = true;
        boolean drawTiles = false;
	/**
	 * Gets the index of vertex closest to x,y or -1 in none close.
	 * @param x
	 * @param y
	 * @return index of vertex or -1
	 */
	public int getClosestVertex(int x,int y) {
		int mindistsq = 100;
		int index = -1;
		for(int i=0;i<this.numSelPoints;++i)
			if((cellVerts[i].x-x)*(cellVerts[i].x-x) + 
				(cellVerts[i].y-y)*(cellVerts[i].y-y) < mindistsq )
			{
				mindistsq = (cellVerts[i].x-x)*(cellVerts[i].x-x) + 
					(cellVerts[i].y-y)*(cellVerts[i].y-y);
				index = i;
			}
		return index;
	}

	public void setVertex(int index,int x,int y) {
		cellVerts[index].setLocation(x, y);
	}
	
	public void saveOldVerticies() {
	    for(int i=0;i<6;++i)
	    {
	        oldCellVerts[i].x = cellVerts[i].x;
	        oldCellVerts[i].y = cellVerts[i].y;
	    }
	}

	public void restoreOldVerticies() {
	    for(int i=0;i<6;++i)
	    {
	        cellVerts[i].x = oldCellVerts[i].x;
	        cellVerts[i].y = oldCellVerts[i].y;
	    }
	}

	/**
	 * Reset the domain to something suitable
	 * @param r
	 */
	public void resetDomain(Rectangle r) {
		int w = r.width;
		int h = r.height;
		int min = (w>h?h:w);
		cellVerts[0].x = r.x + w/2 + min/4;
		cellVerts[0].y = r.y + h/2;
		cellVerts[1].x = r.x + w/2;
		cellVerts[1].y = r.y + h/2;
		cellVerts[2].x = r.x + w/2;
		cellVerts[2].y = r.y + h/2 - min/4;
		numFund=0; numOuterPoints =3;
	}

	public void resetDomain(Rectangle r,int xsize,int ysize) {
		int w = r.width;
		int h = r.height;
		cellVerts[0].x = r.x + w/2 + xsize;
		cellVerts[0].y = r.y + h/2 + ysize;
		cellVerts[1].x = r.x + w/2 - xsize;
		cellVerts[1].y = r.y + h/2 + ysize;
		cellVerts[2].x = r.x + w/2 - xsize;
		cellVerts[2].y = r.y + h/2 - ysize;
		numFund=0; numOuterPoints =3;
	}

	public void resizeDomain(int xsize,int ysize) {
		int cenX = (cellVerts[0].x + cellVerts[1].x) / 2;
		int cenY = (cellVerts[1].y + cellVerts[2].y) / 2;

		cellVerts[0].x = cenX + xsize;
		cellVerts[0].y = cenY + ysize;
		cellVerts[1].x = cenX - xsize;
		cellVerts[1].y = cenY + ysize;
		cellVerts[2].x = cenX - xsize;
		cellVerts[2].y = cenY - ysize;
		numFund=0; numOuterPoints =3;
	}

	public void paint(Graphics g) {

	    if (this.drawDomain) {
	        g.setColor(Color.orange); // use lower case colors for compatability
 //           System.out.println("Outer "+(0)+" "+cellVerts[0]);
	        for (int i = 0; i < numOuterPoints - 1; ++i)
	        {
	            g.drawLine(cellVerts[i].x, cellVerts[i].y,
	                    cellVerts[i + 1].x, cellVerts[i + 1].y);
	//            System.out.println("Outer "+(i+1)+" "+cellVerts[i+1]);
	        	        }
	        g.drawLine(cellVerts[numOuterPoints - 1].x,
	                cellVerts[numOuterPoints - 1].y,
	                cellVerts[0].x, cellVerts[0].y);


	        g.setColor(Color.blue); // use lower case colors for compatability
	        if (numFund > 0) {
	            for (int i = 1; i < numFund; ++i) {
	                g.drawLine(fund[i - 1].x, fund[i - 1].y,
	                        fund[i].x, fund[i].y);
	            }
	            if(numFund > 2)
	                g.drawLine(fund[0].x, fund[0].y,
	                        fund[numFund - 1].x, fund[numFund - 1].y);
	        }
	    }
	    if(this.drawSelectionPoints)
	    {
	        g.setColor(Color.red); // use lower case colors for compatability
	        g.fillOval(cellVerts[0].x, cellVerts[0].y, 8, 8);
//	        g.setColor(Color.white); // use lower case colors for compatability
//	        g.drawOval(cellVerts[0].x - 3, cellVerts[0].y - 3, 8, 8);

	        g.setColor(Color.green); // use lower case colors for compatability
	        g.fillOval(cellVerts[1].x, cellVerts[1].y, 8, 8);
//	        g.setColor(Color.white); // use lower case colors for compatability
//	        g.drawOval(cellVerts[1].x - 3, cellVerts[1].y - 3, 8, 8);

	        if (numSelPoints == 3) {
	            g.setColor(Color.blue); // use lower case colors for compatability
	            g.fillOval(cellVerts[2].x, cellVerts[2].y, 8, 8);
//		        g.setColor(Color.white); // use lower case colors for compatability
//		        g.drawOval(cellVerts[2].x - 2, cellVerts[2].y - 3, 8, 8);
	        }
	        g.stroke();
	    }
	}

	public void zoom(float f) {
	    for(int i=0;i<6;++i) {
	        cellVerts[i].x = (int) (f*cellVerts[i].x);
	        cellVerts[i].y = (int) (f*cellVerts[i].y);
	    }
	    for(int i=0;i<numFund;++i){
	        fund[i].x = (int) (f*fund[i].x);
	        fund[i].y = (int) (f*fund[i].y);
	    }

	}

	public void rescale(float xfactor,float yfactor) {
		for(int i=0;i<6;++i) {
			cellVerts[i].x = (int) (xfactor*cellVerts[i].x);
			cellVerts[i].y = (int) (yfactor*cellVerts[i].y);
		}
		for(int i=0;i<numFund;++i){
			fund[i].x = (int) (xfactor*fund[i].x);
			fund[i].y = (int) (yfactor*fund[i].y);
		}
			
	}

	public void shift(int x,int y) {
		for(int i=0;i<6;++i) {
			cellVerts[i].x += x;
			cellVerts[i].y += y;
		}
		for(int i=0;i<numFund;++i){
			fund[i].x += x;
			fund[i].y += y;
		}
	}

	public void flip(String code,int w,int h, TessRule tr) {
		if(code.equals(Wallpaper.FLIP_X)) {
			for(int i=0;i<6;++i) 
				cellVerts[i].x = w - cellVerts[i].x;
		}
		else if(code.equals(Wallpaper.FLIP_Y)) {
			for(int i=0;i<6;++i) 
				cellVerts[i].y = h - cellVerts[i].y;
		}
		else if(code.equals(Wallpaper.FLIP_90)) {
			for(int i=0;i<6;++i) {
				int x = h - cellVerts[i].y;
				int y = cellVerts[i].x;
				cellVerts[i].x = x;
				cellVerts[i].y = y;
			}
		}
		else if(code.equals(Wallpaper.FLIP_180)) {
			for(int i=0;i<6;++i) {
				int x = w - cellVerts[i].x;
				int y = h - cellVerts[i].y;
				cellVerts[i].x = x;
				cellVerts[i].y = y;
			}
		}
		else if(code.equals(Wallpaper.FLIP_270)) {
			for(int i=0;i<6;++i) {
				int x = cellVerts[i].y;
				int y = w - cellVerts[i].x;
				cellVerts[i].x = x;
				cellVerts[i].y = y;
			}
		}
		tr.fixFlip(code,this);
//		tr.calcFrame(this,-1, true);
//		tr.fixVerticies(this);
//		tr.calcFund(this);
	}

	private Vec getOrigin() {
		if(this.latticeType == FundamentalDomain.PARALLOGRAM)
			return new Vec(cellVerts[1].x,cellVerts[1].y);
		else if(this.latticeType==FundamentalDomain.HEXAGON)
			return new Vec(cellVerts[0].x,cellVerts[0].y);
		else
			return new Vec(cellVerts[1].x,cellVerts[1].y);
	}
	private Vec getU() {
		if(this.latticeType == FundamentalDomain.PARALLOGRAM)
			return new Vec(cellVerts[0].x-cellVerts[1].x,cellVerts[0].y-cellVerts[1].y);
		else if(this.latticeType==FundamentalDomain.HEXAGON)
			return new Vec(cellVerts[2].x-cellVerts[0].x,cellVerts[2].y-cellVerts[0].y);
		else
			return null;
	}

	private Vec getV() {
		if(this.latticeType == FundamentalDomain.PARALLOGRAM)
			return new Vec(cellVerts[2].x-cellVerts[1].x,cellVerts[2].y-cellVerts[1].y);
		else if(this.latticeType==FundamentalDomain.HEXAGON)
			return new Vec(cellVerts[4].x-cellVerts[0].x,cellVerts[4].y-cellVerts[0].y);
		else
			return null;
	}

//	public Rectangle getOuterBoundingBox() {
//		int minX=cellVerts[0].x;
//		int maxX=cellVerts[0].x;
//		int minY=cellVerts[0].y;
//		int maxY=cellVerts[0].y;
//		for(int i=1;i<numOuterPoints;++i) {
//			if(cellVerts[i].x<minX) minX = cellVerts[i].x;
//			if(cellVerts[i].x>maxX) maxX = cellVerts[i].x;
//			if(cellVerts[i].y<minY) minY = cellVerts[i].y;
//			if(cellVerts[i].y>maxY) maxY = cellVerts[i].y;
//		}
//		return new Rectangle(minX,minY,maxX-minX,maxY-minY);
//	}

	public Rectangle getFDBoundingBox() {
		int minX=fund[0].x;
		int maxX=fund[0].x;
		int minY=fund[0].y;
		int maxY=fund[0].y;
		for(int i=1;i<numFund;++i) {
			if(fund[i].x<minX) minX = fund[i].x;
			if(fund[i].x>maxX) maxX = fund[i].x;
			if(fund[i].y<minY) minY = fund[i].y;
			if(fund[i].y>maxY) maxY = fund[i].y;
		}
		return new Rectangle(minX,minY,maxX-minX,maxY-minY);
	}

	public int getLatticeWidth() {
		if(this.latticeType != FundamentalDomain.PARALLOGRAM && this.latticeType != FundamentalDomain.HEXAGON)
			return -1;

		Vec UplusV = U.add(V);
		Vec UminusV = U.sub(V);
		int l1 = UplusV.x; if(l1<0) l1 = -l1;
		int l2 = UminusV.x; if(l2<0) l2 = -l2;
		return (l1>l2?l1:l2);
	}
	public int getLatticeHeight() {
		if(this.latticeType != FundamentalDomain.PARALLOGRAM && this.latticeType != FundamentalDomain.HEXAGON)
			return -1;

		Vec UplusV = U.add(V);
		Vec UminusV = U.sub(V);
		int l1 = UplusV.y; if(l1<0) l1 = -l1;
		int l2 = UminusV.y; if(l2<0) l2 = -l2;
		return (l1>l2?l1:l2);
	}
	
//	Point getDsiplacement() {
//		Point p = new Point(cellVerts[2].x-cellVerts[0].x,cellVerts[2].y-cellVerts[0].y);
//		return p;
//	}
	
	public Vec getLaticeIndicies(Vec P) {
		int alpha = V.y * P.x - V.x * P.y;
		int beta  = -U.y * P.x + U.x * P.y;
		if(DEBUG) System.out.println("gli: "+P+" "+alpha+" "+beta);
		return new Vec(alpha / det,beta / det);

	}
	/** Gets all the lattice points within a specified rectangle */
	public Vec[] getLatticePoints(Rectangle rect) {
	    
/*	        if(this.latticeType == FundamentalDomain.FRIEZE) {
	            Vec u = cellVerts[0].diff(cellVerts[1]);
	            List<Vec> points = new ArrayList<Vec>();
	            points.add(cellVerts[0]);
	            Vec last = cellVerts[0];
	            do {
	                last = last.sum(u);
	                points.add(last);
	            } while(rect.contains(last));
	            last = cellVerts[1];
                    do {
                        last = last.diff(u);
                        points.add(last);
                    } while(rect.contains(last));
                    return points.toArray(new Vec[points.size()]);
	        }
*/		if(this.latticeType != FundamentalDomain.PARALLOGRAM && this.latticeType != FundamentalDomain.HEXAGON)
			return new Vec[]{new Vec(rect.x,rect.y)};
		if(det==0)
			return new Vec[]{new Vec(rect.x,rect.y)};
		List points = new ArrayList();
		Vec corners[] = new Vec[4];
		if(DEBUG) System.out.println("glp "+rect);
		corners[0] = new Vec(rect.x,rect.y);
		corners[1] = new Vec(rect.x+rect.width,rect.y);
		corners[2] = new Vec(rect.x+rect.width,rect.y+rect.height);
		corners[3] = new Vec(rect.x,rect.y+rect.height);
		Vec indices[] = new Vec[4];
		for(int i=0;i<4;++i) {
			indices[i] = this.getLaticeIndicies(corners[i]);
			if(DEBUG) System.out.println(indices[i]);
		}
		int minIndX = indices[0].x;
		int maxIndX = indices[0].x;
		int minIndY = indices[0].y;
		int maxIndY = indices[0].y;
		for(int i=1;i<4;++i) {
			if(indices[i].x<minIndX) minIndX = indices[i].x;
			if(indices[i].x>maxIndX) maxIndX = indices[i].x;
			if(indices[i].y<minIndY) minIndY = indices[i].y;
			if(indices[i].y>maxIndY) maxIndY = indices[i].y;
		}
		if(DEBUG) System.out.println("U "+U+" V "+V);
		for(int i=minIndX;i<=maxIndX;++i) 
			for(int j=minIndY;j<=maxIndY;++j) {
				Vec p = Vec.linComb(i,U,j,V);
				if( rect.contains(p) ) {
					points.add(p);
					if(DEBUG) System.out.println("good lattice point: "+i+" "+j+" "+p);
				}
				else
				    if(DEBUG) System.out.println("bad  lattice point: "+i+" "+j+" "+p);
			}
		
		return (Vec[]) points.toArray(new Vec[points.size()]);
	}
	
	public Rectangle getMinimalRectangle(Vec[] points) {
	    int minArea = Integer.MAX_VALUE;
	    Vec minP=null, minQ=null;
	    int minW=0,minH=0;
	    for(int i=0;i<points.length;++i) {
	        Vec p = points[i];
                if(p.x <= 0 || p.y <= 0) continue;
	        for(int j=0;j<points.length;++j) {
	            Vec q = points[j];
	            if(q.x < 0 || q.y < 0) continue;
                    if(p.cross(q)==0) continue;

	            int w = p.x > q.x ? p.x : q.x;
	            int h = p.y > q.y ? p.y : q.y;

	            int area = w*h;
//	            System.out.println(""+p+" "+q+" "+area);
	            if(area == 0) continue;
	            if(area < minArea) {
	                minArea = area;
	                minP = p;
	                minQ = q;
	                minW = w;
	                minH = h;
	            }
	        }
	    }
 //           System.out.println("final "+minP+" "+minQ+" "+minArea);
	    if(minP==null || minQ==null) return null;
            return new Rectangle(0,0,minW,minH);
	}
	Comparator diagonalComparator = new Comparator() {

	    public int compare(Vec v1, Vec v2) {
	        int d1 = v1.x + v1.y;
	        int d2 = v2.x + v2.y;
	        if(d1<d2) return -1;
	        if(d1>d2) return 1;
	        if(v1.x<v2.x) return -1;
	        if(v1.x>v2.x) return 1;
	        return 0;
	    }

	    public int compare(Object arg0, Object arg1) {
	        return compare((Vec) arg0,(Vec) arg1);
	    }

	};
	public void setLatticeType(int type) {
		this.latticeType = type;
		if(type != FundamentalDomain.PARALLOGRAM && type != FundamentalDomain.HEXAGON)
			return;
		this.U = this.getU();
		this.V = this.getV();
		this.O = this.getOrigin();
		this.det = U.x * V.y - U.y * V.x;
		//System.out.println("Frame "+U+" "+V+" det "+det);
	}

	public void removeLatticePoints(LinkedList points, Rectangle rect) {
		ListIterator it = points.listIterator();
		while(it.hasNext()) {
			Vec v = (Vec) it.next();
			if(rect.contains(v))
				it.remove();
		}
	}

	public int removeLatticePoints(Vec[] points, boolean[] done,Rectangle rect) {
		int minX = rect.x;
		int minY = rect.y;
		int maxX = rect.x + rect.width;
		int maxY = rect.y + rect.height;
		int first=-1;
		for(int i=0;i<points.length;++i) {
			if(!done[i]) {
				if(points[i].x > minX && points[i].x < maxX 
				 && points[i].y > minY && points[i].y < maxY)
					done[i]=true;
				else if(first==-1)
					first=i;
			}
		}
/*		while(it.hasNext()) {
			Vec v = it.next();
			if(rect.contains(v))
				it.remove();
		}
*/
		return first;
	}

	public Graphics graphics;

	public void paintSymetries(Graphics g, TessRule tr) {
		GWT.log("paintSym "+drawReflectionLines);
		this.graphics=g;
		
		Rectangle rect = g.getClipBounds();
	
		if(rect==null) return;
		Vec[] points = this.getLatticePoints(
				new Rectangle(
					rect.x-this.getLatticeWidth(),
					rect.y-this.getLatticeHeight(),
					rect.width+this.getLatticeWidth()*2,
					rect.height+this.getLatticeHeight()*2));
		if(points == null || points.length == 0) return;
		int minLen = points[0].sub(this.O).lenSq();
		Vec minVec=this.O;
		for(int i=0;i<points.length;++i) {
		        Vec p = points[i];
			int len = p.sub(this.O).lenSq();
			if(len<minLen) {
				minLen = len;
				minVec = p;
			}
		}

                Vec diff=this.O.sub(minVec);

               if(this.drawCells)
                {
                   for(int i=0;i<points.length;++i) {
                       Vec p = points[i];
                        if(det>0)
                            tr.paintDomainEdges(U, V, p.add(diff), this);
                        else
                            tr.paintDomainEdges(V, U, p.add(diff), this);
                    }
                }
                if(this.drawTiles)
                {
                    if(this.latticeType == FundamentalDomain.PARALLOGRAM)
                        for(int i=0;i<points.length;++i) {
                            Vec p = points[i];
                            Vec p2 = p.add(diff);
                            this.drawLatticeLine(p2,p2.add(U));
                            this.drawLatticeLine(p2,p2.add(V));
                            this.drawLatticeLine(p2,p2.add(U.negate()));
                            this.drawLatticeLine(p2,p2.add(V.negate()));
                        }

                    if(this.latticeType == FundamentalDomain.HEXAGON)
                        for(int i=0;i<points.length;++i) { Vec p = points[i];
                            Vec p2 = p.add(diff);
                            this.drawLatticeLine(p2,p2.add(Vec.linComb(-1, U, -1, V, 3)));
                            this.drawLatticeLine(p2,p2.add(Vec.linComb(2, U, -1, V, 3)));
                            this.drawLatticeLine(p2,p2.add(Vec.linComb(-1, U, 2, V, 3)));
                        }
                }
                if(this.drawReflectionLines) {
                    boolean oldGlide = this.drawGlideLines;
                    boolean oldRot = this.drawRotationPoints;
                    this.drawGlideLines = false;
                    this.drawRotationPoints = false;
                    GWT.log("DrawRef "+U.x+" "+U.y+" "+V.x+" "+V.y);
                    for(int i=0;i<points.length;++i) { 
                    	Vec p = points[i];
                        if(det>0)
                            tr.paintSymetries(this.U,this.V,p.add(diff),this);
                        else
                            tr.paintSymetries(this.V,this.U,p.add(diff),this);
                        GWT.log("Ref "+p.x+" "+p.y);
                    }
                    this.drawGlideLines = oldGlide;
                    this.drawRotationPoints = oldRot;
                }
                 if(this.drawGlideLines) {
                    boolean oldRef = this.drawReflectionLines;
                    boolean oldRot = this.drawRotationPoints;
                    this.drawReflectionLines = false;
                    this.drawRotationPoints = false;
                    for(int i=0;i<points.length;++i) { Vec p = points[i];
                        if(det>0)
                            tr.paintSymetries(this.U,this.V,p.add(diff),this);
                        else
                            tr.paintSymetries(this.V,this.U,p.add(diff),this);
                    }
                    this.drawReflectionLines = oldRef;
                    this.drawRotationPoints = oldRot;
                }

                if(this.drawRotationPoints) {
                    boolean oldRef = this.drawReflectionLines;
                    boolean oldGlide = this.drawGlideLines;
                    this.drawReflectionLines = false;
                    this.drawGlideLines = false;
                    for(int i=0;i<points.length;++i) { Vec p = points[i];
                        if(det>0)
                            tr.paintSymetries(this.U,this.V,p.add(diff),this);
                        else
                            tr.paintSymetries(this.V,this.U,p.add(diff),this);
                    }
                    this.drawReflectionLines = oldRef;
                    this.drawGlideLines = oldGlide;
                }
                
	}
	
	public void paintRegularTile(org.singsurf.compat.Graphics g) {
	    Rectangle rect = this.tileableRegion(g.getClipBounds());
	    g.setColor(Color.green);
	    if(rect!=null)
		g.drawRect(rect.x, rect.y, rect.width, rect.height);
	}
	
	static final Color reflectColour = BW_SYMS ? Color.black : Color.green;
	static final Color glideColour = BW_SYMS ? Color.black : Color.red;
	static final Color rotateColour = BW_SYMS ? Color.black : Color.yellow;
        static final Color laticeColour = Color.black;
	static final int dashLen = 8;
	static final int shapeSize = 4;
	static final double root32 = Math.sqrt(3)/2;
	
	static final int[] cospts = 
	{
		(int) Math.round(2*shapeSize*Math.cos(0)),
		(int) Math.round(2*shapeSize*Math.cos(Math.PI/3)),
		(int) Math.round(2*shapeSize*Math.cos(2*Math.PI/3)),
		(int) Math.round(2*shapeSize*Math.cos(Math.PI)),
		(int) Math.round(2*shapeSize*Math.cos(4*Math.PI/3)),
		(int) Math.round(2*shapeSize*Math.cos(5*Math.PI/3))
	};
	static final int[] sinpts = 
	{
		(int) Math.round(2*shapeSize*Math.sin(0)),
		(int) Math.round(2*shapeSize*Math.sin(Math.PI/3)),
		(int) Math.round(2*shapeSize*Math.sin(2*Math.PI/3)),
		(int) Math.round(2*shapeSize*Math.sin(Math.PI)),
		(int) Math.round(2*shapeSize*Math.sin(4*Math.PI/3)),
		(int) Math.round(2*shapeSize*Math.sin(5*Math.PI/3))
	};

	public final void drawGlideLine(Vec P1,Vec P2) {
	    if(!drawGlideLines) return;
		graphics.setColor(glideColour);
		Vec diff = P2.sub(P1);
		int len = (int) Math.sqrt(diff.lenSq());
		for(int i=0;i<len;i+=dashLen*2) {
			Vec P3 = diff.mul(i).div(len).add(P1);
			Vec P4 = diff.mul(i+dashLen).div(len).add(P1);
			graphics.drawLine(P3.x,P3.y,P4.x,P4.y);
		}
	}

	public final void drawReflectionLine(Vec P1,Vec P2) {
            if(!drawReflectionLines) return;
            if(BW_SYMS) {
                Graphics2D g2 = (Graphics2D) this.graphics; 
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(5));
                graphics.setColor(Color.black); // reflectColour
                graphics.setColor(reflectColour);
		graphics.drawLine(P1.x,P1.y,P2.x,P2.y);
		graphics.setColor(Color.white);
                g2.setStroke(new BasicStroke(3));
                graphics.drawLine(P1.x,P1.y,P2.x,P2.y);
                g2.setStroke(oldStroke);
            }
            else {
                graphics.setColor(reflectColour);
                graphics.drawLine(P1.x,P1.y,P2.x,P2.y);
            }
	}

	final protected void drawLatticeLine(Vec P1,Vec P2) {
            if(!this.drawTiles) return;
	    graphics.setColor(laticeColour);
	    graphics.drawLine(P1.x,P1.y,P2.x,P2.y);
	}

	public void drawSimpleEdge(Vec p, Vec q) {
            if(!drawCells) return;
            graphics.setColor(laticeColour);
            graphics.drawLine(p.x,p.y,q.x,q.y);
	}

	public final void drawRotationPoint(Vec P,int angle) {
	    if(!drawRotationPoints) return;

	    graphics.setColor(rotateColour);
	    switch(angle) {
	    case 0:
	    case 1:
	        break;
	    case 2:
	        //graphics.fillOval(P.x-shapeSize*3/4, P.y-shapeSize*3/4,shapeSize*3/2+1,shapeSize*3/2+1);
	        graphics.fillOval(P.x, P.y,shapeSize*3/2+1,shapeSize*3/2+1);
	        break;
	    case 3:
	        int[] triX = {cospts[0]+P.x,cospts[2]+P.x,cospts[4]+P.x};
	        int[] triY = {sinpts[0]+P.y,sinpts[2]+P.y,sinpts[4]+P.y};
	        graphics.fillPolygon(triX,triY,3);
	        break;
	    case 4:
	        graphics.fillRect(P.x-shapeSize, P.y-shapeSize,shapeSize*2+1,shapeSize*2+1);
	        break;
	    case 6:
	        int[] hexX = {cospts[0]+P.x,cospts[1]+P.x,cospts[2]+P.x,cospts[3]+P.x,cospts[4]+P.x,cospts[5]+P.x};
	        int[] hexY = {sinpts[0]+P.y,sinpts[1]+P.y,sinpts[2]+P.y,sinpts[3]+P.y,sinpts[4]+P.y,sinpts[5]+P.y};
	        graphics.fillPolygon(hexX,hexY,6);
	        break;
	    default:
	        //graphics.fillOval(P.x-shapeSize, P.y-shapeSize,shapeSize*2+1,shapeSize*2+1);
	        graphics.fillOval(P.x, P.y,shapeSize*2+1,shapeSize*2+1);
	    break;
	    }
	}

	//@Override
	@Override
    public String toString() {
	    StringBuffer sb = new StringBuffer();
	    for(int i=0;i<this.numSelPoints;++i) {
		sb.append('(');
		sb.append(cellVerts[i].x);
		sb.append(',');
		sb.append(cellVerts[i].y);
		sb.append(") ");
	    }
	    return sb.toString();
	}
	
	/**
	 * Whether the fundamental domain can be tiled.
	 * @return the rectangle which makes an axis-aligned tile, or null if not possible
	 */
	public Rectangle tileableRegion(Rectangle rect)
	{
	    if(DEBUG) System.out.println("tr: "+rect);
	    Vec[] points = this.getLatticePoints(rect);
	    if(DEBUG) {
		    System.out.println("Lattice points");
		    for(int i=0;i<points.length;++i)
			System.out.println(points[i]);
		    }
	    if(points.length<4) return null;
	    Vec pointA = points[0];
	    int lenA = pointA.lenSq(), lenAB = Integer.MAX_VALUE, lenAC = Integer.MAX_VALUE;
	    int indexA = 0,indexB=-1,indexC=-1;
	    for(int i=1;i<points.length;++i)
		if(points[i].lenSq()<lenA) {
		    pointA = points[i];
		    lenA = pointA.lenSq();
		    indexA = i;
		}
	    
	    for(int i=0;i<points.length;++i) {
		if(i==indexA) continue;
		if(points[i].y == pointA.y && points[i].sub(pointA).lenSq() < lenAB) {
		    indexB = i;
		    lenAB = points[i].sub(pointA).lenSq();
		}
		if(points[i].x == pointA.x && points[i].sub(pointA).lenSq() < lenAC) {
		    indexC = i;
		    lenAC = points[i].sub(pointA).lenSq();
		}
	    }
	    if(indexB==-1 || indexC==-1)
		return null;
	    return new Rectangle(pointA.x,pointA.y,points[indexB].x-pointA.x,points[indexC].y-pointA.y);
	}

	public String toString(DrawableRegion dr) {
	    String s = this.toString();
	    Rectangle rect = tileableRegion(dr.dispRect);
	    if(rect==null) 
		s += " cannot be tiled";
	    else 
		s += " tileable region: (" + rect.x + "," + rect.y +") ("+(rect.x+rect.width)+","+(rect.y+rect.height)+")";
	    return s;
	}


	
}