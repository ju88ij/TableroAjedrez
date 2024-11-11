//Yasin
package chessApplication.endedbyuser;

import static chess.util.GameEvaluator.EvaluationResult.RESIGN;
import static javafx.scene.paint.Color.WHITE;

import java.io.IOException;

import chess.util.GameEvaluator.EvaluationResult;
import chessApplication.Controller;
import chessApplication.SecondStage;
import chessApplication.Sounds;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class EndedByUserStage {

	private Controller            controller;
	private SecondStage           secondStage;
	private EvaluationResult      result;
	private EndedByUserController endedByUserController;
	private String                activePlayer;
	private String                otherPlayer;
	
	//CONSTRUCTOR
	public EndedByUserStage(Controller controller, EvaluationResult result) {
		this.controller   = controller;
		this.secondStage  = controller.getSecondStage();
		//result can be resign or offer draw.
		this.result       = result;
		this.activePlayer = controller.getActivePlayer() == WHITE ? "Player White" : "Player Black";
		this.otherPlayer  = controller.getActivePlayer() == WHITE ? "Player Black" : "Player White"; 
	}
	
	
	//METHODS
	public void setSceneAndShow() {
		secondStage.setTitle("");
		String endMessage       = result == RESIGN ? activePlayer+" resigns."+"\n"+otherPlayer+" wins." : "Both players agreed on a draw.";
		String acceptButtonText = result == RESIGN ? "Resign" : "Accept";
		String cancelButtonText = result == RESIGN ? "Play On" : "Decline";
		String confirmationText = result == RESIGN ? activePlayer+"\n"+" are you sure you want to resign?" : activePlayer+" offers a draw."+"\n"+otherPlayer+" do you accept?";
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EndedByUserLayout.fxml"));
			Scene endedByUserScene = new Scene(loader.load());
			secondStage.setScene(endedByUserScene);
			endedByUserController = loader.getController();
			//Listener for "user accepts draw or is sure he wants to resign".
			endedByUserController.getEndedByUser().addListener((ChangeListener<Boolean>) (ObservableValue, oldValue, newValue) -> {
				if (newValue == true) {
					if (result == RESIGN) {
						controller.getBoardHandler().discoLights();
						Sounds.resign();
					}
					else Sounds.draw();
				}
				controller.evaluationMessages.setText(endMessage);
				controller.getBoardHandler().endGame();
			});
			endedByUserController.acceptButton.setText(acceptButtonText);
			endedByUserController.cancelButton.setText(cancelButtonText);
			endedByUserController.message.setText(confirmationText);
		} catch (IOException e) {
			e.printStackTrace();
		}
		secondStage.centerStage();
		secondStage.show();
	}
}
