package org.singsurf.wallpaper.animation;

import org.singsurf.wallpaper.Controller;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;

public class AnimationController {
	Controller controller;
	public AnimationController(Controller controller) {
		this.controller = controller;
		path = new BounceAnimation(1);
	}
	
    AnimationPath path = null;
    boolean first = true;
    boolean stop = false;
    AnimationScheduler animationScheduler = AnimationScheduler.get();
    
	public void setAnimationPath(AnimationPath path) {
			this.path = path;
			first = true;
	}
	
	public void startAnim() {
		if(path == null) return;
//		if(first) {
			path.firstItteration(controller.fd,controller.dr.destRect);
			first = false;
//		}
		stop = false;
		animationScheduler.requestAnimationFrame(new AnimationCallback() {

			@Override
			public void execute(double timestamp) {
				if(stop) return;
				path.nextItteration(controller.fd);
	            controller.applyTessellation();
				animationScheduler.requestAnimationFrame(this);				
			}
		});
	}

	public void stopAnim() {
		stop = true;
	}


}
