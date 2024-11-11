//Austin
package chessApplication;

import static chess.util.GameEvaluator.EvaluationResult.CHECK;
import static chess.util.GameEvaluator.EvaluationResult.CLAIM_DRAW;
import static chess.util.GameEvaluator.EvaluationResult.FIFTY_MOVES_RULE;
import static chess.util.GameEvaluator.EvaluationResult.OFFER_DRAW;
import static chess.util.GameEvaluator.EvaluationResult.RESIGN;
import static chess.util.GameEvaluator.EvaluationResult.THREE_FOLD_REPETION_RULE;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import chess.Game;
import chess.util.GameEvaluator.EvaluationResult;
import chessApplication.endedbyuser.EndedByUserStage;
import chessApplication.start.StartStage;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Controller extends Application{

	private IntegerProperty secondsProperty1 = new SimpleIntegerProperty(null, "secondsProperty1", 60);
	private IntegerProperty secondsProperty2 = new SimpleIntegerProperty(null, "secondsProperty2", 60);
	private SecondStage     secondStage      = new SecondStage();
	private Color           activePlayer     = WHITE;
	private List<Label>     timeLabels1;
	private List<Label>     timeLabels2;
	private BoardHandler    boardHandler;
	private Game            game;
	private ViewListeners   viewListeners;
	private ModelListeners  modelListeners;

	@FXML BorderPane      root;
	@FXML HBox            top;
	@FXML VBox            left;
	@FXML VBox            right;
	@FXML TilePane        captured;
	@FXML public GridPane board;
	@FXML public Label    seconds1;
	@FXML public Label    minutes1;
	@FXML public Label    hours1;
	@FXML public Label    seconds2;
	@FXML public Label    minutes2;
	@FXML public Label    hours2;
	@FXML public Label    player1;
	@FXML public Label    player2;
	@FXML public HBox     player1Labels;
	@FXML public HBox     player2Labels;
	@FXML public HBox     timer1;
	@FXML public HBox     timer2;
	@FXML public Label    validationMessages;
	@FXML public Text     evaluationMessages;
	@FXML public Label    player1Symbol;
	@FXML public Label    player2Symbol;
	@FXML public Button   start;
	@FXML public Button   resign;
	@FXML public Button   offerDraw;
	@FXML public Button   claimDraw;

	//CONSTRUCTOR
	public Controller() {}
	
	
	//METHODS
	@FXML
	private void initialize() {
		boardHandler  = new BoardHandler(this);
		boardHandler.set(BLACK);
		timeLabels1   = Arrays.asList(seconds1, minutes1, hours1);
		timeLabels2   = Arrays.asList(seconds2, minutes2, hours2);
		viewListeners = new ViewListeners(this);
	}

	public Consumer<EvaluationResult> handleEvaluationResult = result -> {
		String color         = activePlayer == WHITE ? "Black" :"White";
		String draw          = "\n"+" Game ends in a draw.";
		String rule          = "\n"+"Both players can claim a draw because of the "+"\n";
		String reason        = game.threeFoldRepetition.get() == true ? THREE_FOLD_REPETION_RULE.toString() : FIFTY_MOVES_RULE.toString();
		String winner        = getActivePlayer() == WHITE ? "Black" : "White";
		String loser         = getActivePlayer() == WHITE ? "White" : "Black";
		String outOfTimeDraw = loser+" is out of time."+"\n"+winner+" has insufficient material."+"\n"+" Game ends in a draw.";
		String outOfTimeWin  = loser+" is out of time."+"\n"+winner+" wins.";
		String checkmate     = "\n"+ color +" wins.";
		String claim         = reason+" Draw has been claimed.";
		EndedByUserStage endedByUserStage;
		switch (result) {
		case CHECK:
			evaluationMessages.setText(result.toString());
			boardHandler.handleButtons(offerDraw);
			break;
		case CHECKMATE:
			Sounds.checkmate();
			evaluationMessages.setText(result.toString()+ checkmate);
			boardHandler.endGame();
			boardHandler.discoLights();
			break;
		case STALEMATE:
		   Sounds.stalemate();
			evaluationMessages.setText(result.toString()+ draw);
			boardHandler.endGame();
			break;
		case INSUFFICIENT_MATERIAL:
		   Sounds.insufficientMaterial();
			evaluationMessages.setText(result.toString()+ draw);
			boardHandler.endGame();
			break;
		case CHECK_AND_FIFTY_MOVES_RULE:
			evaluationMessages.setText(CHECK.toString()+ rule+ FIFTY_MOVES_RULE.toString());
			boardHandler.handleButtons(claimDraw);
			break;
		case CHECK_AND_THREE_FOLD_REPETION_RULE:
			evaluationMessages.setText(CHECK.toString()+ rule+ THREE_FOLD_REPETION_RULE.toString());
			boardHandler.handleButtons(claimDraw);
			break;
		case FIFTY_MOVES_RULE:
			evaluationMessages.setText(rule+ result.toString());
			boardHandler.handleButtons(claimDraw);
			break;
		case THREE_FOLD_REPETION_RULE:
			evaluationMessages.setText(rule+ result.toString());
			boardHandler.handleButtons(claimDraw);
			break;
		case OUT_OF_TIME:
			if (secondStage.isShowing()) secondStage.close(); 
			if (game.insufficientMaterialColor.test(getActivePlayer() == WHITE ? BLACK : WHITE)) {
			   Sounds.draw();
				evaluationMessages.setText(outOfTimeDraw);
				boardHandler.endGame();
			}
			else {
			   Sounds.outOfTime();
				evaluationMessages.setText(outOfTimeWin);
				boardHandler.endGame();
				boardHandler.discoLights();
			}
			break;
		case RESIGN:
			endedByUserStage = new EndedByUserStage(this, result);
			endedByUserStage.setSceneAndShow();
			break;
		case OFFER_DRAW:
			endedByUserStage = new EndedByUserStage(this, result);
			endedByUserStage.setSceneAndShow();
			break;
		case CLAIM_DRAW:
		   Sounds.draw();
			evaluationMessages.setText(claim);
			boardHandler.endGame();
			break;
		case NORMAL_GAME_SITUATION: 
			evaluationMessages.setText("");
			boardHandler.handleButtons(offerDraw);
			break;
		}
	};

	//method to show the start options window.
	@FXML
	private void startButtonAction(ActionEvent event) {
		//only show startWindow when there isn't one already showing.
		if (!secondStage.isShowing()) {
			StartStage startStage = new StartStage(this, event);
			startStage.setSceneAndShow();
		}
	}

	@FXML
	private void resignButtonAction(ActionEvent event) {
		handleEvaluationResult.accept(RESIGN);
	}

	@FXML
	private void offerDrawButtonAction(ActionEvent event) {
		handleEvaluationResult.accept(OFFER_DRAW);

	}

	@FXML
	private void claimDrawButtonAction(ActionEvent event) {
		handleEvaluationResult.accept(CLAIM_DRAW);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.getIcons().add(new Image(getClass().getResource("../resources/images/icon.png").toString()));
		stage.setTitle("Chess...");
		root = FXMLLoader.load(getClass().getResource("Layout.fxml"));
		Scene scene = new Scene(root, 860, 490);
		stage.setMinWidth(860);
		stage.setMinHeight(525);
		stage.setScene(scene);
		stage.show();
		//On first play the AudioClips are lagging. This bug is avoided by playing a clip without sound here.
		Sounds.lagWorkAround();
	}


	public static void main(String[] args) {
		launch(args);
	}

	//GETTERS & SETTERS
	public SecondStage getSecondStage() {
		return secondStage;
	}

	public IntegerProperty getSecondsProperty1() {
		return secondsProperty1;
	}

	public IntegerProperty getSecondsProperty2() {
		return secondsProperty2;
	}

	public void setSecondsProperty1(int value) {
		secondsProperty1.set(value);
	}

	public void setSecondsProperty2(int value) {
		secondsProperty2.set(value);
	}

	public List<Label> getTimeLabels1(){
		return timeLabels1;
	}

	public List<Label> getTimeLabels2(){
		return timeLabels2;
	}

	public BoardHandler getBoardHandler() {
		return boardHandler;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Game getGame() {
		return game;
	}

	public Color getActivePlayer() {
		return activePlayer;
	}

	public void setActivePlayer(Color color) {
		this.activePlayer = color;
	}
	
	public ViewListeners getViewListeners() {
		return viewListeners;
	}
	
	public ModelListeners getModelListeners() {
		return modelListeners;
	}

	public void setModelListeners(ModelListeners modelListeners) {
		this.modelListeners = modelListeners;
	}
}
