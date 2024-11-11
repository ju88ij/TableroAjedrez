//Jesse
package chess;

import static chess.util.Action.CASTLING;
import static chess.util.Action.ENPASSANT;
import static chess.util.Action.PROMOTION;
import static chess.util.Converter.convert;
import static chess.util.Converter.convert;
import static chess.util.FENBuilder.gameToFEN;
import static chess.util.FENBuilder.gameToFEN3FoldRule;
import static chess.util.GameEvaluator.check;
import static chess.util.GameEvaluator.checkAndFiftyMovesRule;
import static chess.util.GameEvaluator.checkAndThreeFoldRepetitionRule;
import static chess.util.GameEvaluator.fiftyMovesRule;
import static chess.util.GameEvaluator.insufficientMaterial;
import static chess.util.GameEvaluator.stalemate;
import static chess.util.GameEvaluator.threeFoldRepetitionRule;
import static chess.util.MoveValidator.kingSave;
import static chess.util.MoveValidator.legalForMove;
import static chess.util.MoveValidator.legalForPiece;
import static chess.util.MoveValidator.pathUnobstructed;
import static chess.util.MoveValidator.ValidationResult.LEGAL_MOVE;
import static chess.util.Movement.left;
import static chess.util.Movement.right;
import static chess.util.Movement.twoColumns;
import static chess.util.Movement.twoRows;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Piece;
import chess.pieces.Rook;
import chess.util.Action;
import chess.util.Converter;
import chess.util.GameEvaluator;
import chess.util.GameEvaluator.EvaluationResult;
import chess.util.MoveValidator;
import chess.util.MoveValidator.ValidationResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.paint.Color;

public class Game {

   //FIELDS
   private Board                            board;
   private Player                           white;
   private Player                           black;
   private Move                             lastMove;
   private Player                           activePlayer; 
   private List<GameState>                  gameStates            = new ArrayList<>();
   private List<String>                     threeFoldRepetionList = new ArrayList<>();
   private ObservableList<String>           takenPieces           = FXCollections.observableList(new ArrayList<>());
   private ObservableMap<Enum<Action>, Move>action                = FXCollections.observableMap(new HashMap<>());
   private ObservableList<ValidationResult> validationResult      = FXCollections.observableList(new ArrayList<>()); 
   private ObservableList<EvaluationResult> evaluationResult      = FXCollections.observableList(new ArrayList<>());
   private int                              halfMove              = 0;
   //full move starts at 1.
   private int                              fullMove              = 1;
   private String                           enpassantField        = " -";
   private final int                        ENPASSANT_ROW_WHITE   = 3;
   private final int                        ENPASSANT_ROW_BLACK   = 6;

   //CONSTRUCTOR
   public Game (Board board, Player player1, Player player2) {
      this.board   = board;
      white        = player1.getColor() == WHITE ? player1 : player2;
      black        = player1.getColor() == BLACK ? player1 : player2;
      activePlayer = white; 

   }

   //METHODS
   public void play(Move move) {
      validateMove.accept(move);
      if (validationResult.get(validationResult.size() -1) == LEGAL_MOVE) {
         executeMove(move);
         threeFoldRepetionList.add(gameToFEN3FoldRule.apply(this));
         evaluateGame.accept(move);
         lastMove = move;
         gameStates.add(collectGameState());
      }
   }

   private void executeMove(Move move) {
      count.accept(move);
      setEnpassantField.accept(move);
      execute(move);
      castling
      .andThen(enPassant)
      .andThen(hasMoved)
      .andThen(promotion)
      .accept(move);
      setActivePlayer(move.getColor() == WHITE ? black : white);
   }

   //Evaluate game and add this evaluation to observable list.
   private Consumer<Move> evaluateGame = m -> {
      evaluationResult.add(GameEvaluator
            .checkmate()
            .and(stalemate())
            .and(insufficientMaterial())
            .and(check())
            .and(checkAndThreeFoldRepetitionRule())
            .and(checkAndFiftyMovesRule())
            .and(threeFoldRepetitionRule())
            .and(fiftyMovesRule())
            .apply(this, m));
   };

   //check the validity of the move and add the result to observable list.
   private Consumer<Move> validateMove = m -> {
      validationResult.add(MoveValidator
            .legalForPlayer()
            .and(legalForMove())
            .and(legalForPiece())
            .and(pathUnobstructed())
            .and(kingSave())
            .apply(this, m));
   };

   /*
    * returns true if the move is not obstructed by a piece.
    */
   public static BiPredicate<Game, Move> pathUnobstructed = (g, m) -> {
      Piece piece = m.getPiece();
      //knights can jump & pawns have no path because they can move only one field.
      //If their path is blocked, the pawn is making a move that is illegal for a pawn.
      if (piece instanceof Knight ||  piece instanceof Pawn) return true;
      //determine direction of movement
      int oneRow = m.getEndX() - m.getStartX();
      int oneColumn = m.getEndY() - m.getStartY();
      if (oneRow != 0) oneRow /= Math.abs(oneRow); //oneRow,oneColumn = 1 or 0 or -1
      if (oneColumn != 0) oneColumn /= Math.abs(oneColumn); 
      //move one field at a time in direction of movement
      Field path = g.getField(m.getStartX() + oneRow, m.getStartY() + oneColumn);
      while (path != m.getEnd()) {
         if (path.piecePresent()) return false;
         path = g.getField(path.getX() + oneRow, path.getY() + oneColumn); 
      }
      return true;
   };


   /*
    * returns a List containing all the fields with a piece of the given color
    */
   private static BiFunction<Game, Color, List<Field>> allOfColor = (g, c) -> {
      return Stream.of(g.getBoardState())
            .flatMap(Arrays::stream)
            .filter(f -> f.getPieceColor().isPresent() && f.getPieceColor().get() == c)
            .collect(Collectors.toList());
   };

   /*
    * Locates the position of the kings and returns a map where Color is the key and 
    * the field containing the King is the value.
    */
   public static Function<Game, Map<Color,Field>> searchForKings = g -> { 
      return Stream.of(g.getBoardState())											
            .flatMap(Arrays::stream)
            .filter(Field::piecePresent)
            .filter(f -> f.getPiece().get() instanceof King)
            .collect(Collectors.toMap(f -> f.getPieceColor().get(), f -> f));
   };

   /*
    * returns a List containing all moves the opponent can make, to attack the given field.
    */
   public static BiFunction<Game, Field, List<Move>> attackingMoves = (g, fieldUnderAttack) -> {
      Move testMove = new Move(null, fieldUnderAttack);
      return allOfColor.apply(g, fieldUnderAttack.getPieceColor().get() == WHITE ? BLACK : WHITE)
            .stream()
            .map(f -> {
               testMove.setStart(f);
               testMove.setPiece(f.getPiece().get());
               return testMove;
            })
            .filter(move -> move.getPiece().checkMove(g, move))
            .filter(move -> pathUnobstructed.test(g, move))
            .collect(Collectors.toList());
   };

   /*
    * test if the player is himself not in check by making the move. Returns true if the
    * king is save (not in check).
    */
   public static BiPredicate<Game, Move> kingSave = (g, m) -> {
      //do the move.
      Optional<Piece> enemy = g.rewindableMove(m);
      //see if the king is save.
      boolean kingSave = m.getColor() == WHITE ? 
            attackingMoves
            .apply(g, searchForKings.apply(g).get(WHITE))
            .isEmpty() : 
               attackingMoves
               .apply(g, searchForKings.apply(g).get(BLACK))
               .isEmpty();
            //undo the move.		
            g.rewindMove(m, enemy);
            return kingSave;
   };


   /*
    * The Halfmove Clock inside a chess position object takes care of enforcing the fifty-move rule.
    * This counter is reset after captures or pawn moves, and incremented otherwise. Also moves which lose
    * the castling rights, that is rook- and king moves from their initial squares, including castling 
    * itself, increment the Halfmove Clock.
    */
   private Consumer<Move> count = m -> {
      if (m.endPiecePresent() || m.getPiece() instanceof Pawn) halfMove = 0;
      else halfMove++;
      if (m.getColor() == BLACK) fullMove++;
   };

   /*
    * returns true if the move results in a check on the opponent's king.
    */
   public Predicate<Move> checkOnOpponent = m -> {
      return !attackingMoves.apply(this, m.getColor() == Color.WHITE ?
            searchForKings.apply(this).get(BLACK) :
               searchForKings.apply(this).get(WHITE)).isEmpty();
   };

   /*
    * sets hasMoved to true when a piece is moved for the first time.
    * This is only necessary for rooks, pawns and kings.
    */
   private Consumer<Move> hasMoved = m -> {
      if (!m.getPiece().hasMoved() &&
            (m.getPiece() instanceof Rook 
                  || m.getPiece() instanceof King 
                  || m.getPiece() instanceof Pawn)) m.getPiece().setHasMoved(true);		
   };

   /*
    * returns a list containing all the empty positions and the positions with an enemy.
    * In other words, we return all possible fields where pieces of the given color
    * can move to.
    */
   private BiFunction<Game, Color, List<Field>> allPositionsFor = (g, c) -> {
      return Stream.of(g.getBoardState())											
            .flatMap(Arrays::stream)													 
            .filter(f ->   
            !f.piecePresent()
            || f.piecePresent()
            && f.getPiece().get().getColor() == (c == Color.WHITE ? Color.BLACK : Color.WHITE))
            .collect(Collectors.toList());
   };


   /*
    * returns all the moves a piece on a given field can make, given a list with
    * all fields that are empty or have an opponent on them
    */
   private BiFunction<Field, List<Field>, List<Move>> allMovesForPiece = (f, l) -> {
      Move testMove = new Move(f, null);
      return l.stream()
            .map(field -> {
               testMove.setEnd(field);
               return testMove;
            })
            .filter(move -> f.getPiece().isPresent())
            .filter(move -> f.getPiece().get().checkMove(this, move))
            .filter(move -> pathUnobstructed.test(this, move))
            .filter(move -> kingSave.test(this, move))
            .collect(Collectors.toList());
   };

   /*
    * returns a list containing all the moves the opponent can make.
    */
   private Function<Move, List<Move>> allMovesForOpponent = m -> {
      //allMovesForPiece returns legal moves = a move where kingSave() true, so if there
      //are moves, then it's not checkmate.
      return 	allOfColor.apply(this, m.getColor() == WHITE ? BLACK : WHITE)
            .stream()
            .map(f -> allMovesForPiece.apply(f, allPositionsFor.apply(this, m.getColor() == WHITE ? BLACK : WHITE)))
            .flatMap(List::stream) 
            .collect(Collectors.toList());
   };

   /*
    * returns true if checkmate.
    */
   public Predicate<Move> checkmate = m -> {
      return allMovesForOpponent.apply(m).isEmpty() && checkOnOpponent.test(m);
   };

   /*
    * returns true if stalemate.
    */
   public Predicate<Move> stalemate = m -> {
      return allMovesForOpponent.apply(m).isEmpty() && !checkOnOpponent.test(m);
   };

   /*
    * returns true if the given color doesn't have enough material to reach checkmate.
    */
   public Predicate<Color> insufficientMaterialColor = c -> {
      List<Piece> allOfThisColor = allOfColor.apply(this, c)
            .stream()
            .map(f -> f.getPiece().get())
            .collect(Collectors.toList());
      return allOfThisColor.size() == 1 || 
            (allOfThisColor.size() == 2 && 
            allOfThisColor.stream().anyMatch(p -> p instanceof Bishop || p instanceof Knight));
   };


   /*
    * returns true if insufficient material, means the game is a draw because there is no player with sufficient material
    * to reach a checkmate. K + minor piece that's not a pawn (so B, N) = insufficient material. 
    */
   public Predicate<Move> insufficientMaterial = m -> {
      return insufficientMaterialColor.test(WHITE) 
            && insufficientMaterialColor.test(BLACK);
   };

   /*
    * returns true if the 50 moves rule is applicable.
    */
   public Supplier<Boolean> fiftyMoves = () -> {
      return halfMove >= 50;
   };

   /*
    * returns true if the 3 fold repetition rule is applicable.
    */
   public Supplier<Boolean> threeFoldRepetition = () -> {
      return threeFoldRepetionList
            .stream()
            .filter(s -> s.equals(gameToFEN3FoldRule.apply(this)))
            .count() >= 3;
   };

   /*
    * sets the en passant field, needed for the FEN notation.
    */
   private Consumer<Move> setEnpassantField = m -> {
      List<String> columnLetters = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h");
      int enpassantRow = m.getColor() == WHITE ? ENPASSANT_ROW_WHITE : ENPASSANT_ROW_BLACK;
      enpassantField = m.getPiece() instanceof Pawn && twoRows.test(m) ? " "+columnLetters.get(7 - m.getEndY())+enpassantRow : " -";
   };

   /*
    * checks if piece is en passant and captures the
    * necessary piece and sets ObservableMap action 
    * which is necessary to update the view.
    */
   private Consumer<Move> enPassant = m ->  { 
      int oneRow = m.getColor() == WHITE ? -1 : 1;
      if(m.getPiece() instanceof Pawn && ((Pawn) m.getPiece()).checkForEnPassant(this, m)) {
         getField(m.getEndX() + oneRow, m.getEndY())
         .getPiece()
         .ifPresent(p -> {
            takenPieces.add(convert(p));
            getField(m.getEndX() + oneRow, m.getEndY()).setPiece(null);
         });
         Move move = new Move(getField(m.getEndX() + oneRow, m.getEndY()),getField(m.getEndX() + oneRow, m.getEndY()));
         action.put(ENPASSANT, move);
      }
   };

   /*
    * checks if the king made a castling move and executes the
    * castling move for the required rook. And sets ObservableMap action.
    */
   private Consumer<Move> castling = m -> { 
      Move move = new Move(null, null);
      if (m.getPiece() instanceof King && twoColumns.test(m)) {
         if (right.test(m)) {
            move = m.getColor() == WHITE ? new Move(getField(0, 0), getField(0, 2))
                  : new Move(getField(7, 0), getField(7, 2));
            execute(move);
            halfMove++;
         }
         else if (left.test(m)) {
            move = m.getColor() == WHITE ?
                  new Move(getField(0, 7), getField(0, 4))
                  : new Move(getField(7, 7), getField(7, 4));
                  execute(move);
                  halfMove++;
         }
         action.put(CASTLING, move);
      }
   };

   /*
    * returns true if the move is a promotion move.
    */
   public Predicate<Move> isPromotion = m -> {
      return m.getPiece() instanceof Pawn
            && ((m.getColor() == WHITE && m.getEndX() == 7)
                  || (m.getColor() == BLACK && m.getEndX() == 0));
   };

   /*
    * checks if a pawn reaches a promotion position and sets ObservableMap action.
    */
   private Consumer<Move> promotion = m -> {
      if (isPromotion.test(m)) action.put(PROMOTION, m);
   };

   /*
    * collects copies
    */
   private GameState collectGameState() {
      return new GameState(gameToFEN.apply(this), lastMove.getCopy(), getCopyOfTakenPieces());
   }

   /*
    * places the promotion piece on it's field and evaluates the new situation.
    */
   public void setPromotionPiece(Field field, Piece piece) {
      field.setPiece(piece);
      evaluateGame.accept(new Move(field, field));
   }

   /*
    * method used to get the actual move in the model. 
    */
   public Move viewToModel(Move move) {
      Move modelMove = new Move(getField(move.getStartX(), move.getStartY()), getField(move.getEndX(), move.getEndY()));
      modelMove.setPiece(getPiece(move.getStartX(), move.getStartY()).get());
      return modelMove;
   }

   /*
    * returns an Optional taken piece. This method in combination with
    * rewindMove executes a move and then undoes this move. 
    */
   private Optional<Piece> rewindableMove(Move move) {
      move.getStart().setPiece(null);
      Optional<Piece> takenPiece = move.getEnd().getPiece();
      move.getEnd().setPiece(move.getPiece());
      return takenPiece;
   }
   /*
    * undoes the move made by method rewindableMove.
    */
   private void rewindMove(Move move, Optional<Piece> takenPiece) {
      move.getStart().setPiece(move.getPiece());
      move.getEnd().setPiece(takenPiece.orElse(null));

   }

   /*
    * executes the move and if a piece is taken, adds the piece
    * to list takenPieces.
    */
   private void execute(Move move) {
      move.getStart().setPiece(null);
      move.getEnd().getPiece().ifPresent(p -> takenPieces.add(convert(p)));
      move.getEnd().setPiece(move.getPiece());
   }

   //prints the FEN notation of the game to the console.
   private void printGameToConsole(Move move) {
      System.out.println(gameToFEN.apply(this));
   }

   //GETTERS & SETTERS
   public Optional<Piece> getPiece(int x, int y){
      return getBoardState()[x][y].getPiece();
   }

   public Field getField(int x, int y) {
      return getBoardState()[x][y];
   }

   public Player getActivePlayer() {
      return activePlayer;	
   }

   public void setActivePlayer(Player player) {
      activePlayer = player;
   }

   public Optional<GameState> getLastState() {
      return gameStates.size() > 0 ? Optional.of(gameStates.get(gameStates.size() - 1)) : Optional.empty();
   }

   public Field[][] getBoardState() {
      return board.getBoardState();
   }

   public ArrayList<Piece> getCopyOfTakenPieces() {
      return takenPieces.stream()
            .map(str -> convert(str))
            .collect(Collectors.toCollection(ArrayList::new));
   }

   public Move getLastMove() {
      return lastMove;
   }

   public int getHalfMove() {
      return halfMove;
   }

   public int getFullMove() {
      return fullMove;
   }

   public String getEnpassantField() {
      return enpassantField;
   }

   public ObservableList<String> getTakenPieces(){
      return takenPieces;
   }

   public  ObservableMap<Enum<Action>, Move> getAction() {
      return action;
   }

   public ObservableList<EvaluationResult> getEvaluationResult() {
      return evaluationResult;
   }

   public ObservableList<ValidationResult> getValidationResult(){
      return validationResult;
   }
}
