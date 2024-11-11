//Austin
package chessApplication;


import static chess.util.Converter.getAllWhiteViewPieces;
import static chess.util.Converter.getPathToImage;
import static javafx.scene.layout.GridPane.getColumnIndex;
import static javafx.scene.layout.GridPane.getRowIndex;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.DARKGOLDENROD;
import static javafx.scene.paint.Color.WHITE;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import chess.Field;
import chess.Move;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;


public final class DragAndDropHandler {

   private static final double                 SIZE_THRESHOLD   = 78.0;
   private static final int                    OFFSET_SMALL_IMG = 14;
   private static final int                    OFFSET_BIG_IMG   = 22;
   private static final Label                  TEMP_BG          = new Label();
   private static final Background             DARK_GOLD        = new Background(new BackgroundFill(DARKGOLDENROD, CornerRadii.EMPTY, Insets.EMPTY));
   private static final ObjectProperty<Move>   MOVE             = new SimpleObjectProperty<Move>();
   private static final ObservableBooleanValue MOVE_LEGAL       = new SimpleBooleanProperty(false);

   //CONSTRUCTOR
   private DragAndDropHandler() {}

   //METHODS
   /*
    * Drag detected, start drag&drop gesture. Allow any transfer mode.
    * Put piece on dragBoard.
    * Set the dragView image.
    */
   static Consumer<Label> setOnDragDetected = l -> {
      EventHandler<MouseEvent> onDragDetected = e -> {
         Dragboard db = l.startDragAndDrop(TransferMode.ANY);
         if (l.getText() != "") {
            ClipboardContent content = new ClipboardContent();
            content.putString(l.getText());
            int offset = l.getWidth() < SIZE_THRESHOLD ? OFFSET_SMALL_IMG : OFFSET_BIG_IMG;
            db.setDragView(new Image(getPathToImage(l.getText(), l.getWidth())), offset, offset);
            l.setText("");
            db.setContent(content);
         }
         e.consume();
      };
      l.setOnDragDetected(onDragDetected);
   };

   /*
    * returns true if the label we enter is empty or contains a piece from the opponent.
    */
   private static BiPredicate<Label, DragEvent> emptyOrOpponent = (l, e) -> {
      if (l.getText() == "") return true;
      Color  attack  = getAllWhiteViewPieces().contains(e.getDragboard().getString()) ? WHITE : BLACK;
      Color  defense = getAllWhiteViewPieces().contains(l.getText()) ? WHITE : BLACK;
      return attack != defense;
   };

   /*
    *  Data is dragged over the target, accept only if not dragged from the same node
    *  and if it has string data and if the field is empty, or contains enemy. 
    */
   static Consumer<Label> setOnDragOver = l -> {
      EventHandler<DragEvent> onDragOver = e -> {
         if (e.getGestureSource() != l 
               && e.getDragboard().hasString() 
               && emptyOrOpponent.test(l, e)) e.acceptTransferModes(TransferMode.MOVE);
         e.consume();
      };
      l.setOnDragOver(onDragOver);
   };

   /*
    * Mouse moved away, remove the graphical cues 
    */
   static Consumer<Label> setOnDragExited = l -> {
      EventHandler<DragEvent> onDragExited = e -> {
         if (emptyOrOpponent.test(l, e)) l.setBackground(TEMP_BG.getBackground());
         e.consume();
      };
      l.setOnDragExited(onDragExited);
   };

   /*
    * The drag-and-drop gesture entered the target.
    * Show to the user that it is an actual gesture target. 
    */
   static Consumer<Label> setOnDragEntered = l -> {
      EventHandler<DragEvent> onDragEntered = e -> {
         if (e.getDragboard().hasString() && emptyOrOpponent.test(l, e)) { 
            TEMP_BG.setBackground(l.getBackground());
            if (e.getGestureSource() != l) l.setBackground(DARK_GOLD);
         }
         e.consume();
      };
      l.setOnDragEntered(onDragEntered);
   };

   /*
    * Method that creates a move for the model, from the move made by the user
    * in the view.
    */
   private static BiFunction<Label, DragEvent, Move> convertViewToModel = (l, e) -> {
      Field start = new Field(getRowIndex(((Label) e.getGestureSource())), getColumnIndex(((Label) e.getGestureSource())));
      Field end   = new Field(getRowIndex(l), getColumnIndex(l));
      return new Move(start, end);
   };

   /*
    * If there is a string data on dragBoard, read it and use it. 
    * Move will be played in the model and moveLegal will be set.
    */
   static Consumer<Label> setOnDragDropped = l -> {
      EventHandler<DragEvent> onDragDropped = e -> {
         ((SimpleBooleanProperty) MOVE_LEGAL).set(false);
         if (e.getDragboard().hasString()) {
            MOVE.set(convertViewToModel.apply(l, e));
            if (MOVE_LEGAL.get() == true) {
               Sounds.move();
               l.setBackground(TEMP_BG.getBackground());
               l.setText(e.getDragboard().getString());
            }
         }
         //let source know whether the string was successfully 
         //transferred and used 
         e.setDropCompleted(MOVE_LEGAL.get());
         e.consume();
      };
      l.setOnDragDropped(onDragDropped);
   };

   /*
    * The drag and drop gesture ended unsuccessful,
    * place piece back on it's start field.
    */
   static Consumer<Label> setOnDragDone = l -> {
      EventHandler<DragEvent> onDragDone = e -> {
         if (e.getTransferMode() != TransferMode.MOVE) {
            ((Labeled) e.getGestureSource()).setText(e.getDragboard().getString());
         }
         e.consume();
      };
      l.setOnDragDone(onDragDone);
   };


   //GETTERS & SETTERS
   public static ObjectProperty<Move> getMove() {
      return MOVE;
   }

   public static ObservableBooleanValue getMoveLegal() {
      return MOVE_LEGAL;
   }

   public static void  setMoveLegal(boolean value) {
      ((BooleanPropertyBase) MOVE_LEGAL).set(value);
   }
}

