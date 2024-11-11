//Jesse
package chess.pieces;


import static chess.Game.attackingMoves;
import static chess.Game.kingSave;
import static chess.Game.searchForKings;
import static chess.util.FENBuilder.rookOKToCastle;
import static chess.util.Movement.left;
import static chess.util.Movement.noRows;
import static chess.util.Movement.oneRowOrOneColumnOrBoth;
import static chess.util.Movement.right;
import static chess.util.Movement.twoColumns;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import chess.Game;
import chess.Move;
import javafx.scene.paint.Color;

public class King extends Piece {

	//CONSTRUCTOR
	public King(Color color) {
		super(color);
	}

	//METHODS
	//returns true if move legal.
	public boolean checkMove(Game g, Move m) {
		return super.checkMove(g, m) 
				&& (oneRowOrOneColumnOrBoth.test(m) || castling(g,m));
	}

	//returns true if the move is a legal castling move.
	public boolean castling(Game g, Move m) {
		return super.checkMove(g, m) 
				&& twoColumns.test(m)
				&& noRows.test(m) 
				&& (rookEligibleToCastle)
				.and(kingEligibleToCastle)
				.test(g, m);
	}

	//returns true if the king is not in check at the moment.
	public Predicate<Game> notInCheck = g -> {
		return attackingMoves
				.apply(g, searchForKings.apply(g).get(getColor()))
				.isEmpty();
	};

	//returns true if the king is eligible to castle.
	public BiPredicate<Game, Move> kingEligibleToCastle = (g, m) -> {
		BiPredicate<Move,Move> kingEligible = (m1, m2) -> {
			return  !hasMoved()
					&& notInCheck.test(g)
					&& kingSave.test(g, m1)
					&& kingSave.test(g, m2);
		};
		if (m.getColor() == WHITE) {
			if (right.test(m)) {
				return kingEligible.test(
						new Move(g.getField(0, 3), g.getField(0, 2)),
						new Move(g.getField(0, 3), g.getField(0, 1)));
			}
			if (left.test(m)) {
				return kingEligible.test(
						new Move(g.getField(0, 3), g.getField(0, 4)),
						new Move(g.getField(0, 3), g.getField(0, 5)));
			}
		}
		if (m.getColor() == BLACK) {
			if (left.test(m)) {
				return kingEligible.test(
						new Move(g.getField(7, 3), g.getField(7, 4)),
						new Move(g.getField(7, 3), g.getField(7, 5)));
			}
			if (right.test(m)) {
				return kingEligible.test(
						new Move(g.getField(7, 3), g.getField(7, 2)),
						new Move(g.getField(7, 3), g.getField(7, 1)));
			}
		}
		return false;
	};

	//returns true if the correct rook is eligible to castle.
	public BiPredicate<Game, Move> rookEligibleToCastle = (g, m) -> {
		if (m.getColor() == WHITE) {
			if (right.test(m)) {
				return rookOKToCastle.test(g.getField(0, 0));
			}
			//long castling, field next to Rook must be empty
			if (left.test(m)) {
				return g.getPiece(0, 6).isPresent() ? false : rookOKToCastle.test(g.getField(0, 7));
			}
		}
		if (m.getColor() == BLACK) {
			//long castling field next to Rook must be empty
			if (left.test(m)) {
				return g.getPiece(7, 6).isPresent() ? false : rookOKToCastle.test(g.getField(7, 7));
			}
			if (right.test(m)) {
				return rookOKToCastle.test(g.getField(7, 0));			
			}
		}
		return false;
	};

	@Override
	public Piece makeCopy() {
		return new King(getColor());
	}
}
