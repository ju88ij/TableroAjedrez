//Austin
package chessApplication;


import static chess.util.Action.CASTLING;
import static chess.util.Action.ENPASSANT;
import static chess.util.Action.PROMOTION;
import static chess.util.Converter.convert;
import static chess.util.Converter.viewPieceAtIndexes;
import static chessApplication.DragAndDropHandler.setOnDragDetected;
import static chessApplication.DragAndDropHandler.setOnDragDone;
import static chessApplication.DragAndDropHandler.setOnDragDropped;
import static chessApplication.DragAndDropHandler.setOnDragEntered;
import static chessApplication.DragAndDropHandler.setOnDragExited;
import static chessApplication.DragAndDropHandler.setOnDragOver;
import static javafx.scene.layout.GridPane.getColumnIndex;
import static javafx.scene.layout.GridPane.getRowIndex;
import static javafx.scene.paint.Color.ANTIQUEWHITE;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.BURLYWOOD;
import static javafx.scene.paint.Color.DARKGOLDENROD;
import static javafx.scene.paint.Color.WHITE;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import chess.Field;
import chess.Move;
import chess.util.Action;
import chessApplication.promotion.PromotionStage;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class BoardHandler {
   private static final int SMALLEST_SIZE_1      = 42;
   private static final int SIZE_2               = 48;
   private static final int SIZE_3               = 54;
   private static final int SIZE_4               = 60;
   private static final int SMALLEST_THRESHOLD_1 = 75;
   private static final int THRESHOLD_2          = 85;
   private static final int THRESHOLD_3          = 95;
   private final Background    lightTile         = new Background(new BackgroundFill(ANTIQUEWHITE, CornerRadii.EMPTY, Insets.EMPTY));
   private final Background    darkTile          = new Background(new BackgroundFill(BURLYWOOD, CornerRadii.EMPTY, Insets.EMPTY));
   private final List<String>  sideMarkers       = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8");
   private final List<String>  topMarkers        = Arrays.asList("h", "g", "f", "e", "d", "c", "b", "a");
   private Color               placement;
   private Move                translatedMove;
   private GridPane            board;
   private VBox                left;
   private HBox                top;
   private Controller          controller;
   private Label               viewField;
   private Field               modelField;
   private Timers              timers;

   //CONSTRUCTOR
   public BoardHandler(Controller controller) {
      this.controller = controller;
      this.board      = controller.board;
      this.left       = controller.left;
      this.top        = controller.top;
      setDimensions();
   }

   //METHODS
   //method that takes care of the resizing of the view.
   private void setDimensions() {
      NumberBinding minOfWidthAndHeight = Bindings.min(
            controller.root.widthProperty().subtract(controller.right.prefWidthProperty()).divide(8),
            controller.root.heightProperty().divide(8));
      //set alignment for top markers
      top.getChildren().forEach(c -> {((Region) c).prefWidthProperty().bind(minOfWidthAndHeight);}); 
      //set alignment for side markers
      left.getChildren().forEach(c ->{((Region) c).prefHeightProperty().bind(minOfWidthAndHeight);});
      //set alignment for the board
      board.getChildren().forEach(c -> {
         ((Region) c).prefWidthProperty().bind(minOfWidthAndHeight);
         ((Region) c).prefHeightProperty().bind(minOfWidthAndHeight);
      });
      //set the wrapping width for the Text node.
      controller.evaluationMessages.wrappingWidthProperty().bind(controller.right.prefWidthProperty().subtract(20.0));
      //adjust font size, so the size of the pieces gets bigger when the board gets bigger and smaller when the board gets smaller.
      minOfWidthAndHeight.addListener((ChangeListener<Number>) (ObservableValue, oldValue, newValue) -> {
         board.getChildren().forEach(c -> ((Region) c).styleProperty().set("-fx-font-size:"+adjustFontSize(newValue.intValue())+";"));
      });

   }

   public int adjustFontSize (int newValue) {
      int fontSize = SMALLEST_SIZE_1;
      if (SMALLEST_THRESHOLD_1 < newValue && newValue <= THRESHOLD_2) fontSize = SIZE_2;
      else if (THRESHOLD_2 < newValue && newValue <= THRESHOLD_3) fontSize = SIZE_3;
      else if (THRESHOLD_3 < newValue) fontSize = SIZE_4;
      return fontSize;
   }

   //help function.
   public BiFunction<Color, Integer, Integer> translate = (c, i) -> {
      return c == WHITE ? 7-i : i;
   };

   /*
    * sets all the pieces on the board and adjusts the markers around the board.
    * The parameter color determines the color for the pieces at the bottom of the board.
    * White = black pieces at top of board, white pieces at bottom. Black = vice versa.
    */
   public void set(Color piecesAtBottom) {
      this.placement = piecesAtBottom;
      //set the top and side markers for playing with white or black.
      for (int i=0; i<8; i++) {
         ((Labeled) left.getChildren()
               .get(i))
         .setText(sideMarkers.get(translate.apply(placement, i)));
         ((Labeled) top.getChildren()
               .get(i))
         .setText(topMarkers.get(translate.apply(placement, i)));
      }
      //place the pieces or nothing if an empty field.
      board.getChildren()
      .forEach(c -> {((Labeled) c).setText(viewPieceAtIndexes.
            apply(translate.apply(placement, getRowIndex(c)), translate.apply(placement, getColumnIndex(c))).orElse(""));
      });
   }

   //Function that returns a translated move, when passing the move from view to model and vice versa. This function is needed
   //because the view allows playing with white at bottom or top of board. This causes the moves to be mirrored.
   public  Function<Move, Move> translateMove = m -> {
      if (placement == BLACK) return m;
      Move translatedMove = new Move(
            new Field(translate.apply(placement, m.getStartX()), translate.apply(placement, m.getStartY())), 
            new Field(translate.apply(placement, m.getEndX()), translate.apply(placement, m.getEndY())));
      translatedMove.setPiece(m.getPiece());
      return translatedMove;


   };

   /*
    * The parameter is a button that you want to enable. This method
    * handles the enabling/disabling for the other buttons as needed.
    */
   public void handleButtons(Button b) {
      if (b == controller.start) {
         controller.start.setDisable(false);
         controller.resign.setDisable(true);
         controller.offerDraw.setDisable(true);
         controller.claimDraw.setDisable(true);
      }
      if (b == controller.resign || b == controller.offerDraw) {
         controller.start.setDisable(true);
         controller.resign.setDisable(false);
         controller.offerDraw.setDisable(false);
         controller.claimDraw.setDisable(true);
      }
      if (b == controller.claimDraw) {
         controller.start.setDisable(true);
         controller.resign.setDisable(false);
         controller.offerDraw.setDisable(true);
         controller.claimDraw.setDisable(false);
      }
   };

   /*
    * method for starting the timers for the players
    */
   public void startTimers(Color piecesAtBottom) {
      timers = new Timers(controller);
      timers.setAndStart(piecesAtBottom);
   }


   //help method to select only the children of the HBox that are labels and set their color.
   public BiConsumer<HBox, Color> setColor = (h,c) -> {
      h.getChildren().stream()
      .forEach(e -> {
         if (e instanceof Label && e != controller.player1Symbol && e!= controller.player2Symbol) ((Labeled) e).setTextFill(c);
         if (e instanceof HBox) ((Pane) e).getChildren().forEach(l -> ((Labeled) l).setTextFill(c));
      });
   };

   //method to change text color for active player and to manage the active timers.
   @FXML
   public void switchPlayer() {
      boolean player1Active = controller.player1.getTextFill() == DARKGOLDENROD;
      getTimers().ifPresent(t -> t.switchTimers());
      setColor.accept(player1Active ? controller.player1Labels : controller.player2Labels, BLACK);
      setColor.accept(player1Active ? controller.player2Labels : controller.player1Labels, DARKGOLDENROD);
      controller.setActivePlayer(controller.getActivePlayer() == WHITE ? BLACK : WHITE); 
   }

   /*
    * method that takes care of the extra action that is needed with castling, en passant and promotion.
    * For castling, this is placing the rook and for en passant, it's removing the captured pawn.
    * For promotion it's showing the promotion stage, so the user can choose a piece for promotion.
    */
   public void executeAction(Enum<Action> action, Move move) {
      //enpassant  contains a move with the same field for start, as for end. This is the field containing the pawn to be taken.
      if (action == ENPASSANT) {
         translatedMove = translateMove.apply(move);
         board.getChildren().forEach(c -> {
            if (getRowIndex(c) == translatedMove.getStartX() && getColumnIndex(c) == translatedMove.getStartY()) {
               ((Labeled) c).setText("");
            }
         });
      }
      //castling contains the move the Rook has to make. We move rook from start field to end field.
      if (action == CASTLING) {
         translatedMove = translateMove.apply(move);
         String piece = board.getChildren()
               .stream()
               .filter(c -> getRowIndex(c) == translatedMove.getStartX() && getColumnIndex(c) == translatedMove.getStartY())
               .map(c -> {
                  String rook = ((Labeled) c).getText();
                  ((Labeled) c).setText("");
                  return rook;
               })
               .findAny()
               .get();
         board.getChildren()
         .stream()
         .filter(c -> getRowIndex(c) == translatedMove.getEndX() && getColumnIndex(c) == translatedMove.getEndY())
         .forEach(c -> ((Labeled) c).setText(piece));
      }
      //promotion contains the move made by the pawn to the last row. We get the promotion fields of the model and the view.
      //We show the promotion stage to the user, who can select a piece for promotion.
      if (action == PROMOTION) {
         modelField = move.getEnd();
         translatedMove = translateMove.apply(move);
         viewField = (Label) board.getChildren()
               .stream()
               .filter(c -> getRowIndex(c) == translatedMove.getEndX() && getColumnIndex(c) == translatedMove.getEndY())
               .findAny()
               .get();
         PromotionStage promotionStage = new PromotionStage(controller, translatedMove.getColor());
         promotionStage.setSceneAndShow();
      }
   }

   //method that sets the promotion piece in the view and the model.
   public void setPromotionPiece(String piece) {
      viewField.setText(piece);
      controller.getGame().setPromotionPiece(modelField, convert(piece));
   }

   public void clear() {
      board.getChildren().forEach(c -> ((Labeled) c).setText(""));
      controller.captured.getChildren().forEach(c -> ((Labeled) c).setText(""));
      controller.captured.setVisible(false);
      controller.timer1.setVisible(false);
      controller.timer2.setVisible(false);
      getTimers().ifPresent(t -> t.stop());
      controller.validationMessages.setText("");
      controller.evaluationMessages.setText("");
   }

   /*
    * sets the view to "the game has ended" position.
    * Activates the start button.
    */
   public void endGame() {
      removeDragAndDrop();
      controller.getViewListeners().remove();
      controller.getModelListeners().remove();
      getTimers().ifPresent(t -> t.stop());
      setColor.accept(controller.player1Labels, BLACK);
      setColor.accept(controller.player2Labels, BLACK);
      controller.validationMessages.setText("");
      handleButtons(controller.start);
   }

   /*
    * method sets de drag&drop handlers for
    * the pieces. 
    */
   public void setDragAndDrop() {
      board.getChildren().forEach(c -> {
         setOnDragDetected
         .andThen(setOnDragEntered)
         .andThen(setOnDragOver)
         .andThen(setOnDragExited)
         .andThen(setOnDragDropped) 
         .andThen(setOnDragDone)
         .accept((Label) c);
      });
   }

   /*
    * removes the drag&drop handlers.
    */
   public void removeDragAndDrop() {
      board.getChildren().forEach(c ->  {
         ((Labeled) c).setOnDragDetected(null);
         ((Labeled) c).setOnDragOver(null);
         ((Labeled) c).setOnDragExited(null);
         ((Labeled) c).setOnDragEntered(null);
         ((Labeled) c).setOnDragDropped(null);
         ((Labeled) c).setOnDragDone(null);
      });
   }

   /*
    * method to provide a visual cue, when the game ends with a winner.
    */
   public void discoLights() {
      new Thread(){
         public void run(){
            for(int i=0; i<3; i++) {
               for(int j=0; j<80; j++){
                  DropShadow dropShadow = new DropShadow(5.0, 3.0, 2.0, Color.color(0.4, 0.4, 0.5));
                  board.getChildren()
                  .forEach(l -> l.setEffect(dropShadow));
                  Random r = new Random();
                  //r.nextInt(64) we randomly pick one of the 64 fields and change it background to random values
                  ((Region) board.getChildren()
                        .get(r.nextInt(64)))
                  // last value of Color is opacity, 1.0 == not transparent; 0.0 = completely transparent
                  .setBackground(new Background(new BackgroundFill(new Color(Math.random(), Math.random(), Math.random(), Math.random()),
                        CornerRadii.EMPTY, Insets.EMPTY)));
                  try {
                     Thread.sleep(1L * 15L);
                  } catch (InterruptedException e) {
                     e.printStackTrace();
                  }
               }
               board.getChildren()
               .forEach(c -> { 
                  c.setEffect(null);
                  ((Region) c).setBackground(((GridPane.getRowIndex(c) + getColumnIndex(c)) %2 == 0) ? lightTile : darkTile);
               });
            }
         }
      }.start();
   }

   //GETTERS & SETTERS
   public Optional<Timers> getTimers() {
      return Optional.ofNullable(timers);
   }

}
