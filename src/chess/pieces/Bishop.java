//Jesse
package chess.pieces;


import static chess.util.Movement.diagonal;

import chess.Game;
import chess.Move;
import javafx.scene.paint.Color;

public class Bishop extends Piece {

	//CONSTUCTOR
	public Bishop(Color color) {
		super(color);
	}

	//METHODS
	public boolean checkMove(Game g, Move m) {
		return super.checkMove(g, m) && diagonal.test(m);
	}

	@Override
	public Piece makeCopy() {
		return new Bishop(getColor());
	}

}

