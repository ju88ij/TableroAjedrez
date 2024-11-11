//Jesse
package chess;

import java.util.Optional;

import chess.pieces.Piece;
import javafx.scene.paint.Color;



public class Field {

	private final int row, column;
	private Piece     piece;

	//CONSTRUCTOR
	public Field (int row, int column) {
		this.row    = row;
		this.column = column;
	}

	//GETTERS & SETTERS
	public int getX() {
		return row;
	}

	public int getY() {
		return column;
	}

	public Optional<Piece> getPiece() {
		return Optional.ofNullable(piece);
	}
	
	public void setPiece(Piece piece) {
		this.piece = piece;
	}
	
	public Optional<Color> getPieceColor() {
		return piecePresent() ? Optional.of(piece.getColor()) : Optional.empty();
	}

	public boolean piecePresent() {
		return piece != null;
	}
}
