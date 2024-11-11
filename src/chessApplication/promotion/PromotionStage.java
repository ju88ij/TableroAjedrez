//Yasin
package chessApplication.promotion;

import static chess.util.Converter.getViewPieceFromFEN;
import static javafx.scene.paint.Color.WHITE;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import chessApplication.Controller;
import chessApplication.SecondStage;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.paint.Color;

public class PromotionStage {

	private Controller  controller;
	private SecondStage secondStage;
	private Color       color;

	//CONSTRUCTOR
	public PromotionStage(Controller controller, Color color) {
		this.controller  = controller;
		this.secondStage = controller.getSecondStage();
		this.color       = color;
	}
	

	//METHODS
	public void setSceneAndShow() {
		secondStage.setTitle("Promotion...");
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("PromotionLayout.fxml"));
			Scene promotionScene = new Scene(loader.load());
			secondStage.setScene(promotionScene);
			//retrieving the controller of the promotionStage from the loader.
			PromotionController promotionController = loader.getController();
			List<String> promotionPieces = color == WHITE ? 
					Arrays.asList(getViewPieceFromFEN("N"),	getViewPieceFromFEN("B"), getViewPieceFromFEN("R"), getViewPieceFromFEN("Q")) 
					: Arrays.asList(getViewPieceFromFEN("n"), getViewPieceFromFEN("b"), getViewPieceFromFEN("r"), getViewPieceFromFEN("q"));
					for (int i=0; i<4 ; i++) ((Labeled) promotionController.pieces.getChildren().get(i)).setText(promotionPieces.get(i));
					//Listener for "settings are accepted by the user".
					promotionController.piece.addListener((ChangeListener<String>) (ObservableValue, oldValue, newValue) -> {
						if (newValue != "") {
							//this places the piece chosen for promotion on it's field, in both the view and the model.
							controller.getBoardHandler().setPromotionPiece(newValue);
							//When the player has chosen his promotion piece, his timer is stopped and the other player's timer is started.
							controller.getBoardHandler().switchPlayer();
						}
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
		secondStage.centerStage();
		//so the user can't close the window without selecting a piece.
		secondStage.setPromotionPieceSelected(false);
		secondStage.show();
	}
}
