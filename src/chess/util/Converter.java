package chess.util;

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Piece;
import chess.pieces.Queen;
import chess.pieces.Rook;
import javafx.util.Pair;

public final class Converter {

   private static final double                   SIZE_THRESHOLD = 78.0;
   private static final int                      SMALL_IMAGE    = 2;
   private static final int                      BIG_IMAGE      = 3;
   private static final Map<Piece, List<String>> FEN_MAP        = new HashMap<Piece, List<String>>();
   private static final Map<List<Pair<Integer, Integer>>,Pair<String, Piece>> PIECES = new HashMap<>();

   private Converter() {}

   static {
      //white
      FEN_MAP.put(new King  (WHITE), Arrays.asList("K","♔", Converter.class.getClassLoader().getResource("resources/images/normal/whiteKing.png").toString(),
            Converter.class.getClassLoader().getResource("resources/images/big/whiteKing2.png").toString()));
      FEN_MAP.put(new Queen (WHITE), Arrays.asList("Q","♕", Converter.class.getClassLoader().getResource("resources/images/normal/whiteQueen.png").toString(),
            Converter.class.getClassLoader().getResource("resources/images/big/whiteQueen2.png").toString()));
      FEN_MAP.put(new Rook  (WHITE), Arrays.asList("R","♖", Converter.class.getClassLoader().getResource("resources/images/normal/whiteRook.png").toString(),
            Converter.class.getClassLoader().getResource("resources/images/big/whiteRook2.png").toString()));
      FEN_MAP.put(new Bishop(WHITE), Arrays.asList("B","♗", Converter.class.getClassLoader().getResource("resources/images/normal/whiteBishop.png").toString(),
            Converter.class.getClassLoader().getResource("resources/images/big/whiteBishop2.png").toString()));
      FEN_MAP.put(new Knight(WHITE), Arrays.asList("N","♘", Converter.class.getClassLoader().getResource("resources/images/normal/whiteKnight.png").toString(),
            Converter.class.getClassLoader().getResource("resources/images/big/whiteKnight2.png").toString()));
      FEN_MAP.put(new Pawn  (WHITE), Arrays.asList("P","♙", Converter.class.getClassLoader().getResource("resources/images/normal/whitePawn.png").toString(),
            Converter.class.getClassLoader().getResource("resources/images/big/whitePawn2.png").toString()));
      //black
      FEN_MAP.put(new King  (BLACK), Arrays.asList("k","♚", Converter.class.getClassLoader().getResource("resources/images/normal/blackKing.png").toString(), 
            Converter.class.getClassLoader().getResource("resources/images/big/blackKing2.png").toString()));
      FEN_MAP.put(new Queen (BLACK), Arrays.asList("q","♛", Converter.class.getClassLoader().getResource("resources/images/normal/blackQueen.png").toString(),
            Converter.class.getClassLoader().getResource("resources/images/big/blackQueen2.png").toString()));
      FEN_MAP.put(new Rook  (BLACK), Arrays.asList("r","♜", Converter.class.getClassLoader().getResource("resources/images/normal/blackRook.png").toString(),
            Converter.class.getClassLoader().getResource("resources/images/big/blackRook2.png").toString()));
      FEN_MAP.put(new Bishop(BLACK), Arrays.asList("b","♝", Converter.class.getClassLoader().getResource("resources/images/normal/blackBishop.png").toString(),
            Converter.class.getClassLoader().getResource("resources/images/big/blackBishop2.png").toString()));
      FEN_MAP.put(new Knight(BLACK), Arrays.asList("n","♞", Converter.class.getClassLoader().getResource("resources/images/normal/blackKnight.png").toString(),
            Converter.class.getClassLoader().getResource("resources/images/big/blackKnight2.png").toString()));
      FEN_MAP.put(new Pawn  (BLACK), Arrays.asList("p","♟", Converter.class.getClassLoader().getResource("resources/images/normal/blackPawn.png").toString(),
            Converter.class.getClassLoader().getResource("resources/images/big/blackPawn2.png").toString()));
   }

   static {
      PIECES.put(new ArrayList<>(Arrays.asList(new Pair<>(0,3))),                  new Pair<>("♔", new King(WHITE)));
      PIECES.put(new ArrayList<>(Arrays.asList(new Pair<>(7,3))),                  new Pair<>("♚", new King(BLACK)));
      PIECES.put(new ArrayList<>(Arrays.asList(new Pair<>(0,4))),                  new Pair<>("♕", new Queen(WHITE)));
      PIECES.put(new ArrayList<>(Arrays.asList(new Pair<>(7,4))),                  new Pair<>("♛", new Queen(BLACK)));
      PIECES.put(new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,7))), new Pair<>("♖", new Rook(WHITE)));
      PIECES.put(new ArrayList<>(Arrays.asList(new Pair<>(7,0), new Pair<>(7,7))), new Pair<>("♜", new Rook(BLACK)));
      PIECES.put(new ArrayList<>(Arrays.asList(new Pair<>(0,2), new Pair<>(0,5))), new Pair<>("♗", new Bishop(WHITE)));
      PIECES.put(new ArrayList<>(Arrays.asList(new Pair<>(7,2), new Pair<>(7,5))), new Pair<>("♝", new Bishop(BLACK)));
      PIECES.put(new ArrayList<>(Arrays.asList(new Pair<>(0,1), new Pair<>(0,6))), new Pair<>("♘", new Knight(WHITE)));
      PIECES.put(new ArrayList<>(Arrays.asList(new Pair<>(7,1), new Pair<>(7,6))), new Pair<>("♞", new Knight(BLACK)));
   }

   /*
    * Helpmethod. Given a row - and columnindex, returns a Stream containing a pair, that has the corresponding view piece as key and the
    * corresponding model piece as value.
    */
   private static Stream<Pair<String, Piece>> getViewModelPair(Integer rowIndex, Integer columnIndex) {
      return PIECES.entrySet()
            .stream()
            .filter(entryset -> entryset.getKey().contains(new Pair<>(rowIndex, columnIndex)))
            .map(entryset -> entryset.getValue());
   }

   /*
    * returns an Optional containing the symbol for the piece to be used in the view or no value, given a row - and column index.
    */
   public static BiFunction<Integer, Integer, Optional<String>> viewPieceAtIndexes = (rowIndex, columnIndex) -> {
      if (rowIndex == 1) return Optional.of("♙");
      if (rowIndex == 6) return Optional.of("♟");
      return getViewModelPair(rowIndex, columnIndex)
            .map(e -> e.getKey())
            .findFirst();
   };

   /*
    * returns an Optional containing the piece to be used in the model or no value, given a row - and column index.
    */
   public static BiFunction<Integer, Integer, Optional<Piece>> modelPieceAtIndexes = (rowIndex, columnIndex) -> {
      if (rowIndex == 1)    return Optional.of(new Pawn(WHITE));
      if (rowIndex == 6)    return Optional.of(new Pawn(BLACK));
      return getViewModelPair(rowIndex, columnIndex)
            .map(e -> e.getValue())
            .findFirst();
   };

   /*
    * help method to get what we need from FenMap. The predicate filters the values. Values are: 0 (FEN notation)
    * 1 (symbol for the piece in the view), 2 (path to image used in the DragView when moving pieces). The key
    * of FenMap is a piece for the model.
    */
   private static List<String> getValues(Predicate<Entry<Piece, List<String>>> predicate, int value){
      return FEN_MAP
            .entrySet()
            .stream()
            .filter(predicate)
            .map(e -> e.getValue().get(value))
            .sorted()
            .collect(Collectors.toList());
   }

   //help method.
   private static Piece getKey(Predicate<Entry<Piece, List<String>>> predicate){
      return FEN_MAP
            .entrySet()
            .stream()
            .filter(predicate)
            .map(e -> e.getKey())
            .findFirst()
            .get();
   }

   //given the view piece, this method returns a corresponding model piece.
   public static Piece convert(String viewPiece) {
      return getKey(e -> e.getValue().get(1) == viewPiece);
   }

   //given a model piece, returns the corresponding view piece.
   public static String convert(Piece piece) {
      return getValues(e -> e.getKey().getClass() == piece.getClass()
            && e.getKey().getColor() == piece.getColor(), 1).get(0);
   }

   //given a FEN notation for a piece, returns a piece for the view.
   public static String getViewPieceFromFEN(String fen) {
      return getValues(e -> e.getValue().get(0) == fen, 1).get(0);
   }

   //given a model piece, return it's FEN-notation
   public static String getFENFromModelPiece(Piece piece) {
      return getValues(e -> e.getKey().getClass() == piece.getClass()
            && e.getKey().getColor() == piece.getColor(), 0).get(0);
   }

   //returns all white pieces used in the view.
   public static List<String> getAllWhiteViewPieces() {
      return getValues(e -> e.getKey().getColor() == WHITE, 1);
   }

   //returns all black pieces used in the view.
   public static List<String> getAllBlackViewPieces() {
      return getValues(e -> e.getKey().getColor() == BLACK, 1);
   }

   //return the path to the image,needed to handle dragging of the pieces.
   public static String getPathToImage(String viewPiece, Double labelWidth) {
      return getValues(e -> (e.getValue().get(1)) == viewPiece,
            labelWidth < SIZE_THRESHOLD ? SMALL_IMAGE : BIG_IMAGE).get(0);
   }

}
