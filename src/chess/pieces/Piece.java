//Jesse
package chess.pieces;
import chess.Game;
import chess.Move;
import javafx.scene.paint.Color;

public abstract class Piece {
	private final Color color;
	private boolean hasMoved = false;

	
	//CONSTRUCTOR
	protected Piece (Color color) {
		this.color = color;
	}

	//METHODS
	//the end field has to be empty or it has to contain a piece with a different color.
	public boolean checkMove(Game g, Move m) {
		return  !m.endPiecePresent() || m.getEnd().getPiece().get().getColor() != getColor();
	}	
	
	public abstract Piece makeCopy();
	
	//GETTERS & SETTERS
	public Color getColor() {
		return color;
	}
	
	public boolean hasMoved() {
		return hasMoved;
	}


	public void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}
}
