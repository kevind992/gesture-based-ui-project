package com.leap;

import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.leap.Vector;
import com.lego.ConnectionManager;

public class LeapListener extends Listener {

	private ConnectionManager cm;

	public void onInit(Controller controller) {
		cm = new ConnectionManager();
		System.out.println("Initialized");
	}

	public void onConnect(Controller controller) {
		System.out.println("Connected");
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
		controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
		controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
	}

	public void onDisconnect(Controller controller) {
		// Note: not dispatched when running in a debugger.
		System.out.println("Disconnected");
	}

	public void onExit(Controller controller) {
		System.out.println("Exited");
	}

	public void onFrame(Controller controller) {
		Frame frame = controller.frame();

		Hand furthestLeft = frame.hands().leftmost();
		Hand furthestRight = frame.hands().rightmost();

		// Detection for Right hand code 
		if (furthestRight.isRight()) {
			float roll = furthestRight.palmNormal().roll();
			roll *= -1;
			
			// Closed fist gesture
			if (furthestRight.grabStrength() > 0.90) {
				cm.sendCommand(11);
				System.out.println("Close Claw!");
			} else { // If left hand is doing nothing
				cm.sendCommand(10);
			}
			if (roll > 1) { // Open claw gesture
				System.out.println("Open Claw!");
				cm.sendCommand(4);
			}
		}
		// Detection code for Left hand
		if (furthestLeft.isLeft()) {
			
			float roll = furthestLeft.palmNormal().roll();
			roll *= -1;

			if (furthestLeft.grabStrength() > 0.90) {
				cm.sendCommand(12);
				System.out.println("Stop!");
			}
			
			GestureList gestures = frame.gestures();
			for (Gesture gesture : gestures) { 
				//cm.sendCommand(12);
				switch (gesture.type()) {
//	        		
				case TYPE_SWIPE: // Switch statement for detecting up and down swipe
					SwipeGesture swipe = new SwipeGesture(gesture);

					Vector swipeVector = swipe.direction();
					float swipeDirectionY = swipeVector.getY();

					if (swipeDirectionY > 0) {
						System.out.println("Move Up");
						cm.sendCommand(2);
					} else if (swipeDirectionY < 0) {
						System.out.println("Move Down");
						cm.sendCommand(3);
					} else {
						cm.sendCommand(14);
					}

//        			System.out.println("Swipe Direction X: " + swipeDirectionX);
					break;

				case TYPE_CIRCLE: // Switch statement for detecting a circle gesture
					CircleGesture circle = new CircleGesture(gesture);

					String clockwiseness;
					
					if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI / 2) {
						clockwiseness = "clockwise";
						// move right
						System.out.println("Move right");
						cm.sendCommand(1);
					} else {
						clockwiseness = "counterclockwise";
						// move left
						System.out.println("Move left");
						cm.sendCommand(5);
					}

					// send command
					break;
				/*case TYPE_SCREEN_TAP:
					ScreenTapGesture tap = new ScreenTapGesture(gesture);

					Pointable tapPointable = tap.pointable();

					Finger tapFinger = new Finger(tapPointable);

					Finger.Type fingerType = tapFinger.type();

					if (fingerType.equals(Finger.Type.TYPE_INDEX)) {
						System.out.println("Move up");
						cm.sendCommand(12);
					}
					break;*/
				default:
					System.out.println("Unrecognised gesture!");
				}
			}
		}
	}
}
