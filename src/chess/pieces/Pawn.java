//Jesse
package chess.pieces;


import static chess.util.Movement.down;
import static chess.util.Movement.noColumns;
import static chess.util.Movement.oneColumn;
import static chess.util.Movement.oneRow;
import static chess.util.Movement.twoRows;
import static chess.util.Movement.up;
import static javafx.scene.paint.Color.WHITE;

import java.util.Optional;
import java.util.function.Predicate;

import chess.Game;
import chess.GameState;
import chess.Move;
import javafx.scene.paint.Color;

public class Pawn extends Piece {

	//CONSTRUCTOR
	public Pawn(Color color) {
		super(color);
	}

	//METHODS
	/*
	 * checks legality of the move for a pawn. Pawns can move in 3 
	 * distinct ways.
	 * 1. Take a piece. 
	 * 2. A regular move. 
	 * 3. A big first step.
	 */
	public boolean checkMove(Game g, Move m) {
		Predicate<Move> directionForColor = m.getColor() == WHITE ? up : down;
		return super.checkMove(g, m)
				&& (checkForTakingAPiece(g, m, directionForColor)
				    || checkForRegularMove(m, directionForColor)
				    || checkForBigFirstStep(g, m, directionForColor));
	}

	/*
	 * returns true if the pawn makes a legal move where an enemy is taken.
	 * This means: pawn has to switch 1 column & has to move 1 row in the right direction
	 * (up for white, down for black). The end field must contain a piece (super checks that it is opponents)
	 *  or it must be an en passant move.
	 */
	private boolean checkForTakingAPiece(Game g, Move m, Predicate<Move> directionForColor) {
		return oneColumn.test(m) 
				&& oneRow.test(m)
				&& directionForColor.test(m)
				&& m.endPiecePresent() ? true : checkForEnPassant(g, m);
	}

	/*
	 * returns true if the pawn makes a regular move (moving one row and no columns
	 * to an empty field).
	 */
	private boolean checkForRegularMove(Move m, Predicate<Move> directionForColor) {
		return oneRow.test(m) 
				&& noColumns.test(m)
				&& directionForColor.test(m) 
				&& !m.endPiecePresent();
	}

	/*
	 * returns true if the move is a legal "big step" move for a pawn. This means that the
	 * pawn moves two rows in the right direction. No columns. That it is the first move the
	 * pawn makes. That there is no piece blocking the path of the pawn. And the end field must
	 * be empty.
	 */
	private boolean checkForBigFirstStep(Game g, Move m, Predicate<Move> directionForColor) {
		int moveOneRow = getColor() == WHITE ? 1 : -1;
			return noColumns.test(m)
				&& twoRows.test(m)
				&& directionForColor.test(m)
				&& !hasMoved()
				&& !g.getBoardState()[m.getStartX() + moveOneRow][m.getEndY()].piecePresent()
				&& !m.endPiecePresent();
	}

	/*
	 * return true if the move is a valid en passant move,
	 */
	public boolean checkForEnPassant(Game g, Move m) {
		int rowForEnPassant    = getColor() == WHITE ?  5 :  2;
		int oneRowBehind       = getColor() == WHITE ? -1 :  1;
		int lastMoveWasTwoRows = getColor() == WHITE ? -2 :  2;
		int sameColumn         = 0;
		Optional<GameState> gs = g.getLastState();
		//no previous gameState means first move. En passant not possible on first move.
		return gs.isPresent() 
				&& m.getEndX() == rowForEnPassant
				&& gs.get().getLastMEndX() - m.getEndX() == oneRowBehind 
				&& gs.get().getLastMEndY() - m.getEndY() == sameColumn
				&& gs.get().getLastMEndX() - gs.get().getLastMStartX() == lastMoveWasTwoRows
				&& gs.get().getPieceLastM() instanceof Pawn;
		}


	@Override
	public Piece makeCopy() {
		return new Pawn(getColor());
	}
}
