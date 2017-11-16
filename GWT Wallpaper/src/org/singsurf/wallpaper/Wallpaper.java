package org.singsurf.wallpaper;

import org.singsurf.compat.Color;
import org.singsurf.compat.Font;
import org.singsurf.compat.FontMetrics;
import org.singsurf.compat.Graphics;
import org.singsurf.compat.Graphics2D;
import org.singsurf.compat.Point;
import org.singsurf.compat.Rectangle;
import org.singsurf.wallpaper.tessrules.DiamondRule;
import org.singsurf.wallpaper.tessrules.TessRule;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;

/**
 * An applet which calculates wallpaper patterns using a portion of an image.
 **/

public class Wallpaper
	implements MouseDownHandler, MouseUpHandler, MouseMoveHandler, MouseOverHandler, MouseOutHandler {
    /** Labels and action commands for flip/rotate */
    public static final String FLIP_X = "Flip X";
    public static final String FLIP_Y = "Flip Y";
    protected static final String FLIP_90 = "Rotate clockwise";
    protected static final String FLIP_180 = "Rotate 180";
    protected static final String FLIP_270 = "Rotate anti-clockwise";
    private static final boolean DEBUG = false;

    public DrawableRegion dr;
    public FundamentalDomain fd;
    public TessRule tr = DiamondRule.rhombCM;
    public Controller controller;
    public GraphicalTesselationPanel gtp;
    public Panel topBar;
    public TextArea description;

    public Canvas myCanvas;
    public Graphics graphics;
    public Context2d context;
    /** Offset of image in canvas */
    public Point offset = new Point(0, 0);
    private boolean paintDone = false;
    public int clickCount = 0;
    private boolean mousePressed;

    int canvasWidth = 800, canvasHeight = 600;

    /** Mouse mode */
    static final int MOUSE_NORMAL = 0;
    static final int MOUSE_PIPET = 1;
    int mouseMode = MOUSE_NORMAL;

    /** Whether we are in interactive mode */
    boolean interactiveMode = true;

    /** the current vertex for moving the triangle */
    int curvertex = 0;

    /** Last time mouse was clicked **/
    private long lasttime = 0;

    Button origTileButton;
    /** Current path of animations */
    // AnimationPath path;
    /** Button to stop animations */
    // protected Button stopBut;
    // private TimerTask animateTask = null;
    // public Thread animate = null;
    // private final Timer timer = new Timer();
    // boolean animRunning = false;
    // protected JComboBox animateChoice;
    private CheckBox showFund;

    public Wallpaper(int w, int h) {
	myCanvas = Canvas.createIfSupported();
	myCanvas.setCoordinateSpaceWidth(w);
	myCanvas.setCoordinateSpaceHeight(h);

	context = myCanvas.getContext2d();
	graphics = new Graphics2D(context);
	dr = new DrawableRegion(this);

	fd = new FundamentalDomain();
	controller = new Controller(this, dr, fd);
	gtp = new GraphicalTesselationPanel(controller);
	controller.setTesselation(gtp.currentTr);
	topBar = buildButtonBar();
	description = new TextArea();
	description.setVisibleLines(5);

	GWT.log("Wallpaper construct");

	myCanvas.addMouseDownHandler(this);
	myCanvas.addMouseUpHandler(this);
	myCanvas.addMouseMoveHandler(this);
	myCanvas.addMouseOverHandler(this);
	myCanvas.addMouseOutHandler(this);
    }

    public void loadImage(Image img) {
	// dr.setViewport(new Rectangle(0,0,img.getWidth(),img.getHeight()));
	// graphics.setClipBounds(new
	// Rectangle(0,0,img.getWidth(),img.getHeight()));
	dr.setViewport(new Rectangle(0, 0, canvasWidth, canvasHeight));
	graphics.setClipBounds(new Rectangle(0, 0, canvasWidth, canvasHeight));
	dr.loadImage(img, canvasWidth, canvasHeight);
	dr.calcDispRegion();
	fd.resetDomain(dr.dispRect);
	controller.tr.firstCall = true;
	controller.calcGeom();
	controller.redraw();
    }

    public void imageFailed(String filename) {
	GWT.log("Image " + filename + " failed to load.");
	setText("Image " + filename + " failed to load.");
	dr.setViewport(new Rectangle(0, 0, 800, 600));
	graphics.setClipBounds(new Rectangle(0, 0, 800, 600));
	ImageData id = DefaultImage.createDefaultImage(800, 600, this);
	dr.loadImageData(id);
	dr.calcDispRegion();
	fd.resetDomain(dr.dispRect);
	controller.tr.firstCall = true;
	controller.calcGeom();
	controller.redraw();

    }

    public void paintCanvas() {
	paintCanvas(graphics);
    }

    public void paintCanvas(Graphics g) {
	if (DEBUG)
	    log("paintCanvas" + dr.dispRect + " clicks " + clickCount);
	long t0 = System.currentTimeMillis();

	// System.out.printf("cp %d %d %d %d %d
	// %d\n",fd.verticies[0].x,fd.verticies[0].y,fd.verticies[1].x,fd.verticies[1].y,fd.verticies[2].x,fd.verticies[2].y);
	// System.out.printf("%d %d%n", this.offset.x,this.offset.y);
	g.translate(this.offset.x, this.offset.y);
	Rectangle bounds = g.getClipBounds();
	if (bounds != null && (bounds.x + bounds.width > dr.dispRect.x + dr.dispRect.width)) {
	    g.clearRect(dr.dispRect.x + dr.dispRect.width, bounds.y,
		    bounds.x + bounds.width - (dr.dispRect.x + dr.dispRect.width), bounds.height);
	}
	if (bounds != null && (bounds.y + bounds.height > dr.dispRect.y + dr.dispRect.height)) {
	    g.clearRect(bounds.x, dr.dispRect.y + dr.dispRect.height, bounds.width,
		    bounds.y + bounds.height - (dr.dispRect.y + dr.dispRect.height));
	}
	// g.clipRect(dr.dispRect.x,dr.dispRect.y,dr.dispRect.width,dr.dispRect.height);
	dr.paint(g, this);
	g.setPaintMode();

	fd.paintSymetries(g, controller.tr);
	fd.paint(g);

	if (clickCount == 0)
	    paintIntro(g);
	if (clickCount == 1)
	    paintIntro2(g);

	if (this.controller.constrainVertices)
	    fd.paintRegularTile(g);

	g.translate(-this.offset.x, -this.offset.y);
	paintDone = true;
	long t1 = System.currentTimeMillis();
	// g.setColor(Color.white);
	// g.drawRect(0, 0, 150, 25);
	g.setColor(Color.black);
//	g.drawString("time " + (t1 - t0) + "ms", 5, 20);
    }

    private void paintIntro(Graphics g) {
	Vec base = this.controller.tr.frameO;
	String s1 = "Click and drag the red, green or blue dots";
	String s2 = "to change the pattern.";
	Font f = new Font("SansSerif", Font.BOLD, 16);
	g.setFont(f);
	FontMetrics fm = g.getFontMetrics();
	int len1 = fm.stringWidth(s1);
	int height = fm.getHeight();
	int ascent = fm.getMaxAscent();
	g.setColor(Color.white);
	g.fillRoundRect(210, base.y + 20, len1 + 20, height * 2 + 20, 20, 20);
	g.setColor(Color.black);

	g.drawString(s1, 220, base.y + 30 + ascent);
	g.drawString(s2, 220, base.y + 30 + ascent + height);
    }

    private void paintIntro2(Graphics g) {
	String s1 = "Click a button on the left";
	String s2 = "to change the type of pattern.";
	Font f = new Font("SansSerif", Font.BOLD, 16);
	g.setFont(f);
	FontMetrics fm = g.getFontMetrics();
	int len2 = fm.stringWidth(s2);
	int height = fm.getHeight();
	int accent = fm.getMaxAscent();
	g.setColor(Color.white);
	g.fillRoundRect(10, 10, len2 + 20, height * 2 + 20, 20, 20);
	g.setColor(Color.black);

	g.drawString(s1, 20, 20 + accent);
	g.drawString(s2, 20, 20 + accent + height);
    }

    protected Panel buildButtonBar() {
	Panel p2 = new HorizontalPanel();
	origTileButton = new Button("Original Image");
	origTileButton.addClickHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		if (DEBUG)
		    System.out.println("OrigBut " + controller.showingOriginal);
		if (controller.showingOriginal) {
		    controller.applyTessellation();
		    origTileButton.setText("Original Image");
		} else {
		    controller.showOriginal();
		    origTileButton.setText("Tile Image");
		}
	    }
	});
	p2.add(origTileButton);

	Button b4 = new Button("Reset");
	b4.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent e) {
		fd.resetDomain(dr.dispRect);
		controller.tr.firstCall = true;
		controller.calcGeom();
		controller.redraw();
	    }
	});
	p2.add(b4);

	showFund = new CheckBox("Show Domain", true);
	p2.add(showFund);
	showFund.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		boolean checked = ((CheckBox) event.getSource()).getValue();
		fd.drawDomain = checked;
		controller.redraw();
	    }
	});

	// interactiveCB = new JCheckBox("Interactive mode",interactiveMode);
	// interactiveCB.addItemListener(new ItemListener() {
	// public void itemStateChanged(ItemEvent e) {
	// if(e.getStateChange() == ItemEvent.SELECTED)
	// {
	// interactiveMode = true;
	// myCanvas.repaint();
	// ta.setText("Interactive mode.");
	// }
	// else
	// {
	// interactiveMode = false;
	// ta.setText("Double click to redraw.");
	// myCanvas.repaint();
	// }
	// }});
	// p2.add(interactiveCB);

	CheckBox symmetryCB = new CheckBox("Draw symmetry");
	symmetryCB.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		boolean checked = ((CheckBox) event.getSource()).getValue();
		GWT.log("Draw symmetry " + checked);
		if (checked) {
		    fd.drawGlideLines = true;
		    fd.drawCells = true;
		    fd.drawReflectionLines = true;
		    fd.drawRotationPoints = true;
		    controller.redraw();
		} else {
		    fd.drawGlideLines = false;
		    fd.drawCells = false;
		    fd.drawReflectionLines = false;
		    fd.drawRotationPoints = false;
		    controller.redraw();
		}
	    }
	});
	p2.add(symmetryCB);

	// p2.add(new JLabel("Anim"));
	// stopBut = new JButton("Start");
	// stopBut.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// stopStartAnim();
	// }
	//
	// });
	// stopBut.setVisible(true);
	// stopBut.setEnabled(true);
	// p2.add(stopBut);

	// JComboBox animateMenu = buildAnimationChoice();
	// p2.add(animateMenu);

	return p2;
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
	if (DEBUG)
	    log("Mouse pressed");
	++clickCount;
	mousePressed = true;
	// log(
	// " rel " + event.getRelativeY(myCanvas.getElement()) +
	// " client " + event.getClientY() +
	// " screen " + event.getScreenY() +
	// " Y " + event.getY() +
	// " canvas " + myCanvas.getAbsoluteTop() +
	// " ele " + myCanvas.getCanvasElement().getAbsoluteTop() +
	// " scroll " + myCanvas.getCanvasElement().getScrollTop()
	// );
	// int x0 = event.getClientX() - myCanvas.getAbsoluteLeft();
	// int y0 = event.getClientY() - myCanvas.getAbsoluteTop();
	// log("diff " + y0 );
	int x0 = event.getX();
	int y0 = event.getY();
	int x = x0 - offset.x;
	int y = y0 - offset.y;
	if (x > dr.destRect.width)
	    x = dr.destRect.width;
	if (x < 0)
	    x = 0;
	if (y > dr.destRect.height)
	    y = dr.destRect.height;
	if (y < 0)
	    y = 0;
	curvertex = fd.getClosestVertex(x, y);
	if (curvertex != -1) {
	    fd.saveOldVerticies();
	    fd.setVertex(curvertex, x, y);
	}
	// TODO work out double click
	// if(event..getClickCount()>1)
	// {
	// if(DEBUG) log("clicks "+e.getClickCount());
	// // Only recalculate when paint has been completed or 1 sec passed.
	// // and 0.1s has passed.
	// long curtime = System.currentTimeMillis();
	// //log("t "+(curtime- lasttime)+" pd "+paintDone);
	// if(curtime- lasttime <100) return;
	// if(!paintDone && curtime- lasttime <1000 ) return;
	// lasttime = curtime;
	// paintDone = false;
	// if(controller.showingOriginal /* || !this.interactiveMode */ ) {
	// controller.applyTessellation();
	// }
	// else {
	// controller.showOriginal();
	// }
	// }
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
	if (DEBUG)
	    log("Mouse released");
	mousePressed = false;
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
	if (DEBUG)
	    log("Mouse out");
	mousePressed = false;
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
	// TODO Auto-generated method stub

    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
	if (mousePressed)
	    mouseDragged(event);
	else
	    mouseMoved(event);
    }

    public void mouseMoved(MouseMoveEvent event) {
	// if(DEBUG) log("Mouse moved");

	// TODO if(mouseMode==MOUSE_PIPET) {
	// int x = event.getX()-offset.x;
	// int y = event.getY()-offset.y;
	// if(x>=dr.destRect.width || y>=dr.destRect.height) {
	// myCanvas.setCursor(Cursor.getDefaultCursor());
	// return;
	// }
	// myCanvas.setCursor(pipet);
	// int outInd = x+y*dr.destRect.width;
	// Color col = new Color(dr.pixels[outInd]);
	// setText(col.toString());
	// return;
	// }
	// int x = event.getX()-offset.x;
	// int y = event.getY()-offset.y;

	int x0 = event.getX();
	int y0 = event.getY();
	int x = x0 - offset.x;
	int y = y0 - offset.y;

	int index = fd.getClosestVertex(x, y);
	// if(DEBUG) log("Mouse moved "+index);
	if (index != -1)
	    myCanvas.getElement().getStyle().setCursor(Cursor.POINTER);
	else
	    myCanvas.getElement().getStyle().setCursor(Cursor.DEFAULT);
    }

    public void mouseDragged(MouseMoveEvent event) {

	if (DEBUG)
	    log("mouseDragged " + event);
	if (!mousePressed) {
	    log("FAKE Event " + event);
	    return;
	}
	// TODO if((e.getModifiers() & InputEvent.BUTTON3_MASK )!= 0) return;
	if (curvertex == -1)
	    return;
	fd.saveOldVerticies();

	// int x = e.getX()-offset.x;
	// int y = e.getY()-offset.y;

	int x0 = event.getX();
	int y0 = event.getY();
	int x = x0 - offset.x;
	int y = y0 - offset.y;

	if (x > dr.destRect.width)
	    x = dr.destRect.width;
	if (x < 0)
	    x = 0;
	if (y > dr.destRect.height)
	    y = dr.destRect.height;
	if (y < 0)
	    y = 0;
	fd.setVertex(curvertex, x, y);

	controller.calcGeom();
	// log(fd.toString(dr));
	// log(Arrays.toString(fd.getLatticePoints(dr.dispRect)));

	// if(fd.tileableRegion(dr.dispRect)==null) {
	// log("Not tileable");
	// fd.restoreOldVerticies();
	// }
	// else
	// log("Not tileable");

	// System.out.printf("md %d\n",fd.verticies[1].y-fd.verticies[0].y);
	if (!interactiveMode) {
	    // repaintLines(myCanvas.getGraphics());
	    paintCanvas(); // redraw(false);
	    return;
	}

	// Only recalculate when paint has been completed or 1 sec passed.
	// and 0.1s has pased.
	// long curtime = System.currentTimeMillis();
	// log("t "+(curtime- lasttime)+" pd "+paintDone);
	// if(curtime- lasttime <100) return;
	if (!paintDone /* && curtime- lasttime <1000 */)
	    return;
	// lasttime = curtime;
	paintDone = false;
	controller.applyTessellation();
    }

    private void log(String message) {
	consoleLog(message);
    }

    public void setText(String message) {
	description.setText(message);
    }

    public static native void consoleLog(String msg) /*-{
		console.log(msg);
    }-*/;

}
