package chessApplication.start;


import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.DARKGOLDENROD;
import static javafx.scene.paint.Color.WHITE;

import java.io.IOException;

import chess.Board;
import chess.Game;
import chess.Player;
import chessApplication.BoardHandler;
import chessApplication.Controller;
import chessApplication.ModelListeners;
import chessApplication.SecondStage;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class StartStage {

	private Controller   controller;
	private ActionEvent  event;
	private SecondStage  secondStage;
	private BoardHandler boardHandler;
	
	//CONSTRUCTOR
	public StartStage(Controller controller, ActionEvent event) {
		this.controller   = controller;
		this.event        = event;
		this.secondStage  = controller.getSecondStage();
		this.boardHandler = controller.getBoardHandler();

	}
	
	//METHODS
	public void setSceneAndShow() {
		secondStage.getIcons().add(new Image(getClass().getResource("../../resources/images/icon.png").toString()));
		secondStage.setTitle("Start...");
		secondStage.setStage(event);
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("StartLayout.fxml"));
			Scene startScene = new Scene(loader.load());
			secondStage.setScene(startScene);
			//retrieving the controller of the startStage from the loader.
			StartController startController = loader.getController();
			//Listener for "settings are accepted by the user".
			startController.getAccepted().addListener((ChangeListener<Boolean>) (ObservableValue, oldValue, newValue) -> {
				if (newValue == true) {
					//disable & enable the option buttons.
					boardHandler.handleButtons(controller.offerDraw);
					//clear the board to start new game.
					boardHandler.clear();
					//remove welcome message & adjust the font.
					controller.validationMessages.setVisible(true);
					controller.validationMessages.setText("");
					controller.validationMessages.getStyleClass().add("messages-normal");
					//sets player names.
					Color colorAtBottom = startController.colorPiecesAtBottom.getSelectionModel().getSelectedItem().equals("White") ? WHITE : BLACK;
					controller.player1.setText(colorAtBottom == WHITE ? startController.getPlayerBlack() : startController.getPlayerWhite());
					controller.player2.setText(colorAtBottom == WHITE ? startController.getPlayerWhite() : startController.getPlayerBlack());
					controller.player1Symbol.setText(colorAtBottom == WHITE ? "♟" : "♙");
					controller.player2Symbol.setText(colorAtBottom == WHITE ? "♙" : "♟");
					controller.getBoardHandler().setColor.accept(colorAtBottom == WHITE ? controller.player2Labels : controller.player1Labels, DARKGOLDENROD);
					controller.getBoardHandler().setColor.accept(colorAtBottom == WHITE ? controller.player1Labels : controller.player2Labels, BLACK);
					controller.player1Labels.setVisible(true);
					controller.player2Labels.setVisible(true);
					//create the model with accepted values from the user.
					Player white = new Player(startController.getPlayerWhite(), WHITE);
					Player black = new Player(startController.getPlayerBlack(), BLACK);
					Game game = new Game(new Board(), white, black);
					game.setActivePlayer(white);
					controller.setActivePlayer(WHITE);
					//give model to controller.
					controller.setGame(game);
					//set the board and the pieces in the view.
					boardHandler.set(colorAtBottom);
					//set eventHandlers for drag & drop gesture
					boardHandler.setDragAndDrop();
					//setting the listeners on the observable values from the model & view.
					new ModelListeners(controller);
					controller.getViewListeners().set();
					//if user has selected time
					if (! (startController.minutesField.getText().equals("00") 
							&& startController.hoursField.getText().equals("00"))) {
						controller.seconds1.setText(String.format("%02d", 0));
						controller.seconds2.setText(String.format("%02d", 0));
						controller.minutes1.setText(String.format("%02d", startController.getMinutes()));
						controller.minutes2.setText(String.format("%02d", startController.getMinutes()));
						controller.hours1.setText(String.format("%02d", startController.getHours()));
						controller.hours2.setText(String.format("%02d", startController.getHours()));
						controller.timer1.setVisible(true);
						controller.timer2.setVisible(true);
						boardHandler.startTimers(colorAtBottom);
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		secondStage.centerStage();
		secondStage.show();
	}
}
