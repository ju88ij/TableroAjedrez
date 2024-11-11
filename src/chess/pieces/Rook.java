//Jesse
package chess.pieces;

import static chess.util.Movement.straight;

import chess.Game;
import chess.Move;
import javafx.scene.paint.Color;

public class Rook extends Piece {

	//CONSTRUCTOR
	public Rook(Color color) {
		super(color);
	}

	//METHODS
	public boolean checkMove(Game g, Move m) {
		return super.checkMove(g, m) && straight.test(m); 
	}

	@Override
	public Piece makeCopy() {
		return new Rook(getColor());

	}
}