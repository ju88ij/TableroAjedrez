//Austin
package chessApplication;

import static chess.util.GameEvaluator.EvaluationResult.OUT_OF_TIME;

import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;

public class TimeListener implements ChangeListener<Number>{

	private Controller      controller;
	private IntegerProperty secondsProperty;
	private Label           seconds;
	private Label           minutes;
	private Label           hours;
	
	//CONSTRUCTOR
	public TimeListener(Controller controller, IntegerProperty secondsProperty, List<Label> timeLabels) {
		this.controller      = controller;
		this.secondsProperty = secondsProperty;
		this.seconds         = timeLabels.get(0);
		this.minutes         = timeLabels.get(1);
		this.hours           = timeLabels.get(2);
		secondsProperty.addListener(this);
	}
	
	//METHODS
	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		if (newValue.intValue() == 60) {
			seconds.textProperty().unbind();
			seconds.setText(String.format("%02d", 0));
		}
		if (oldValue.intValue() == 60) {
			seconds.textProperty().bind(secondsProperty.asString("%02d"));
		}
		if (newValue.intValue() == 59 && parseInt(hours) > 0 && parseInt(minutes) == 0) {
			hours.setText(minusOne(hours));
			minutes.setText("60");
		}
		if (newValue.intValue() == 59 && parseInt(minutes) > 0) {
			minutes.setText(minusOne(minutes));
		}
		if (newValue.intValue() == 0 && parseInt(hours) == 0 && parseInt(minutes) == 0 ) {
			controller.handleEvaluationResult.accept(OUT_OF_TIME);
		}
	}

	public void remove() {
		secondsProperty.removeListener(this);
	}

	public static int parseInt(Label label) {
		return Integer.parseInt(label.getText());
	}

	public String minusOne(Label label) {
		return String.format("%02d", parseInt(label) -1);
	}
}



