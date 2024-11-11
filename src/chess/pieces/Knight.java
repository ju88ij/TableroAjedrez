//Jesse 
package chess.pieces;

import static chess.util.Movement.jump;

import chess.Game;
import chess.Move;
import javafx.scene.paint.Color;

public class Knight extends Piece {

	//CONSTRUCTOR
	public Knight(Color color) {
		super(color);
	}

	//METHODS
	//knight can jump one row and two columns or the inverse.
	public boolean checkMove(Game g, Move m) {
		return super.checkMove(g, m) && jump.test(m);
	}

	@Override
	public Piece makeCopy() {
		return new Knight(getColor());
	}


}
