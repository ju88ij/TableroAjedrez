//Yasin
package chessApplication.endedbyuser;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EndedByUserController {
	
	private SimpleBooleanProperty endedByUser = new SimpleBooleanProperty(false);
	
	@FXML VBox   root;
	@FXML Text   message;
	@FXML Button cancelButton;
	@FXML Button acceptButton;
	
	//METHODS
	@FXML
	private void acceptButtonAction(ActionEvent event) {
		endedByUser.set(true);
		((Stage) cancelButton.getScene().getWindow()).close();
	}
	
	@FXML
	private void cancelButtonAction(ActionEvent event) {
		((Stage) cancelButton.getScene().getWindow()).close();
		
	}
	
	//GETTERS & SETTERS
	public SimpleBooleanProperty getEndedByUser() {
		return endedByUser;
	}
}
