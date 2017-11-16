package org.singsurf.wallpaper.client;


import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * Modified from https://code.google.com/p/gwt-examples/wiki/gwt_hmtl5
 */
public class GWT_SimpleWallpaper implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void OldOnModuleLoad() {
		final Button sendButton = new Button("Send");
		final TextBox nameField = new TextBox();
		nameField.setText("GWT User");
		final Label errorLabel = new Label();

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);

		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);
		nameField.selectAll();

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = nameField.getText();
//				if (!FieldVerifier.isValidName(textToServer)) {
//					errorLabel.setText("Please enter at least four characters");
//					return;
//				}

				// Then, we send the input to the server.
				sendButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);
	}
	
	private Canvas canvasScreen;
	private Context2d contextScreen;
	private Label errorLabel;

	public void onModuleLoad() {
	    canvasScreen = Canvas.createIfSupported();
	     
	    if (canvasScreen == null) {
	      RootPanel.get().add(new Label("Sorry, your browser doesn't support the HTML5 Canvas element"));
	      return;
	    }
	    // for example
	    canvasScreen.setCoordinateSpaceHeight(514);
	    canvasScreen.setCoordinateSpaceWidth(687);
	    
	    contextScreen = canvasScreen.getContext2d();
		RootPanel.get("canvasContainer").add(canvasScreen);

		errorLabel = new Label();
		RootPanel.get("errorLabelContainer").add(errorLabel);

	   loadImage();
	}

	// This is important to use a handler!
	private void loadImage() {
	    final Image img = new Image("tile.jpg");
	    RootPanel.get().add(img);
	    img.setVisible(false);
	    
	    img.addLoadHandler(new LoadHandler() {
	    	public void onLoad(LoadEvent event) {
		        drawImage(img);
		      }
	    } );
	}

	private void drawImage(Image img) {
	    ImageData imageData = getImageData(img);
	    int w = imageData.getWidth();
		int h = imageData.getHeight();
		System.out.println(w);
		System.out.println(h);
		ImageData ref1 = contextScreen.createImageData(w, h);
	    ImageData ref2 = contextScreen.createImageData(w, h);
	    ImageData ref3 = contextScreen.createImageData(w, h);
	    CanvasPixelArray c0 = imageData.getData();
	    CanvasPixelArray c1 = ref1.getData();
	    CanvasPixelArray c2 = ref2.getData();
	    CanvasPixelArray c3 = ref3.getData();
    	for(int j=0;j<h;++j) {
 	      for(int i=0;i<w;++i) {
	    	for(int k=0;k<4;++k) {
	    		int pix = c0.get(4*(i+j*w)+k);
	    		c1.set( 4*((w-i-1) +       j*w)+k, pix);
	    		c2.set( 4*( i      + (h-j-1)*w)+k, pix);
	    		c3.set( 4*((w-i-1) + (h-j-1)*w)+k, pix);
	    	} } }
	    
	    drawToScreen(imageData,0,0);
	    drawToScreen(ref1,w,0);
	    drawToScreen(ref2,0,h);
	    drawToScreen(ref3,w,h);
	    errorLabel.setText("wid "+w+" hig "+h);
	}

	private ImageData getImageData(Image image) {
	    
	    Canvas canvasTmp = Canvas.createIfSupported();
	    Context2d context = canvasTmp.getContext2d();

	    int ch = image.getHeight();
	    int cw = image.getWidth();

	    canvasTmp.setCoordinateSpaceHeight(ch);
	    canvasTmp.setCoordinateSpaceWidth(cw);
	    
	    ImageElement imageElement = ImageElement.as(image.getElement());
	    
	    // tell it to scale image
	    
	    // draw image to canvas
	    context.drawImage(imageElement, 0,0,cw/2,ch/2);
	    
	    // get image data
	    ImageData imageData = context.getImageData(0, 0, cw/2, ch/2);

	    return imageData;
	}

	private void drawToScreen(ImageData imageData,int x,int y) {
	    contextScreen.putImageData(imageData, x, y);
	}

}
