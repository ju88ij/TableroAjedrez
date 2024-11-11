//Austin
package chessApplication;

import static chessApplication.TimeListener.parseInt;
import static javafx.animation.Animation.Status.RUNNING;
import static javafx.scene.paint.Color.WHITE;
import static javafx.util.Duration.seconds;

import java.util.function.BiConsumer;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.scene.paint.Color;

public class Timers {

	private Controller   controller;
	private Timeline     timeline1; 
	private Timeline     timeline2;	
	private TimeListener listener1;
	private TimeListener listener2;
	
	//CONSTRUCTOR
	public Timers (Controller controller) {
		this.controller = controller;
	}

	//METHODS
	public void setAndStart(Color piecesAtBottom) {
		timeline1 = new Timeline();
		timeline2 = new Timeline();
		//resetting the seconds back to 60 when new game starts.
		controller.setSecondsProperty1(60);
		controller.setSecondsProperty2(60);
		//TimeListener takes care of displaying the time for the players.
		listener1 = new TimeListener(controller, controller.getSecondsProperty1(), controller.getTimeLabels1());
		listener2 = new TimeListener(controller, controller.getSecondsProperty2(), controller.getTimeLabels2());
		//set time line for both players. label seconds1 is bound with secondsProperty1.
		//label seconds2 is bound with secondsProperty2.
		setTimeline.accept(piecesAtBottom == WHITE ? timeline2 : timeline1, controller.getSecondsProperty1());
		setTimeline.accept(piecesAtBottom == WHITE ? timeline1 : timeline2, controller.getSecondsProperty2());
		timeline1.play();
	}
	
	//help method to set the time line for the players clocks.
	BiConsumer<Timeline, IntegerProperty> setTimeline = (timeline, property) -> {
      KeyValue secondsKey = new KeyValue(property, 0);
      //one cycle is 60 seconds or one minute. So one hour is 60 cycles. 2 players -> *2
      timeline.setCycleCount(parseInt(controller.hours1) *60 + parseInt(controller.minutes1) *2);
      timeline.getKeyFrames().add(new KeyFrame(seconds(60), secondsKey));
   };
	
	public void stop() {
		timeline1.stop();
		timeline2.stop();
		controller.seconds1.textProperty().unbind();
		controller.seconds2.textProperty().unbind();
		listener1.remove();
		listener2.remove();
	}

	public void switchTimers() {
		if (timeline1.getStatus() == RUNNING) {
			timeline1.pause();
			timeline2.play();
		}
		else if (timeline2.getStatus() == RUNNING) {
			timeline2.pause();
			timeline1.play();
		}
	}
}
