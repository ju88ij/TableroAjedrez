//Jesse
package chess.pieces;

import static chess.util.Movement.diagonal;
import static chess.util.Movement.straight;

import chess.Game;
import chess.Move;
import javafx.scene.paint.Color;

public class Queen extends Piece {

	//CONSTRUCTOR
	public Queen(Color color) {
		super(color);
	}

	//METHODS
	@Override
	public boolean checkMove(Game g, Move m) {
		return super.checkMove(g, m) && (straight.test(m) || diagonal.test(m));
	}

	@Override
	public Piece makeCopy() {
		return new Queen(getColor());
	}
}