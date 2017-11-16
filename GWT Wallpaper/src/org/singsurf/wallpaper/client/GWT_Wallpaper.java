package org.singsurf.wallpaper.client;

import org.singsurf.wallpaper.Wallpaper;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * Modified from https://code.google.com/p/gwt-examples/wiki/gwt_hmtl5
 */
public class GWT_Wallpaper implements EntryPoint {
	
	public Wallpaper wall;
	String filename=null;
	
	public void onModuleLoad() {
		wall = new Wallpaper(800,600);
	    
		String url = Document.get().getURL();
		int indexQ = url.indexOf('?');
		if(indexQ>0) {
			String query = url.substring(indexQ+1);
			int indexHash = query.indexOf('#');
			if(indexHash>=0)
				query = query.substring(0,indexHash);
			
			query = URL.decodeQueryString(query);
			if(query.startsWith("FILENAME=")) {
				filename = "uploads/" + query.substring(9);
				GWT.log("File "+filename);
			}
		}
		if(filename==null) 
			filename = "tile.jpg"; 
		
	    if (wall.myCanvas == null) {
	      RootPanel.get().add(new Label("Sorry, your browser doesn't support the HTML5 Canvas element"));
	      return;
	    }
	    // for example
	    
	    RootPanel.get("canvasContainer").add(wall.myCanvas);
	    RootPanel.get("controlContainer").add(wall.gtp);
	    RootPanel.get("menuContainer").add(wall.topBar);
		RootPanel.get("descriptionContainer").add(wall.description);

	// This is important to use a handler!
	    final Image img = new Image(filename);
	    img.addErrorHandler(new ErrorHandler() {
			@Override
	        public void onError(ErrorEvent event) {
				wall.imageFailed(filename);
	          }
	        });
	    
	    img.addLoadHandler(new LoadHandler() {
	    	public void onLoad(LoadEvent event) {
	    		wall.loadImage(img);
		      }
	    } );
	    RootPanel.get().add(img);
	    img.setVisible(false);
	}
}
