//Austin
package chessApplication;


import static javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SecondStage extends Stage {

	private final double width        = 400.0;
	private final double height       = 217.0;
	private boolean promotionSelected = true;	
	
	
	//CONSTRUCTOR
	public SecondStage() {
		this.addEventFilter(WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
	}
	
	
	//METHODS
	public void setStage(ActionEvent event) {
		this.setMinWidth(width);
		this.setMinHeight(height); 
		this.setMaxWidth(width);
		this.setMaxHeight(height);
		if (this.getModality() == Modality.NONE) this.initModality(Modality.APPLICATION_MODAL);
		if (this.getOwner() == null) this.initOwner(((Node) event.getSource()).getScene().getWindow());
	}
	
	/*
	 * promotion is not an ActionEvent. So the user can't click a button to open
	 * the promotion window. If the user closes the window without selecting a piece,
	 * the game will wait for a selection from the user that will never come. By
	 * consuming the window event, the window won't close.
	 */
	private void closeWindowEvent(WindowEvent event) {
		if (!promotionSelected) event.consume();
	}
	
	/*
	 * center secondStage in center of it's owner.
	 */
	public void centerStage() {
		this.setX(this.getOwner().getX() + this.getOwner().getWidth()  / 2 - (width/2));
		this.setY(this.getOwner().getY() + this.getOwner().getHeight() / 2 - (height/2));  
	}
	
	//GETTERS & SETTERS
	public void setPromotionPieceSelected(boolean selected) {
		promotionSelected = selected;
	}
	
}
