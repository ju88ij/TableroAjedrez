//Yasin
package chess;

import chess.pieces.Piece;
import javafx.scene.paint.Color;

public class Move {
	private Field start, end;
	private Piece piece;
	
	//CONSTRUCTOR
	public Move(Field start, Field end){
		this.start = start;
		this.end   = end;
		if (start != null) start.getPiece().ifPresent(p -> this.piece = p);
	}

	//METHODS
	//return true when end- and start field different & start field has piece.
	public Boolean checkMove() {
		return (start != end && piece != null);
	}

	//GETTERS & SETTERS
	public Field getStart() {
		return start;
	}

	public void setStart(Field start) {
		this.start = start;
	}

	public Field getEnd() {
		return end;
	}

	public void setEnd(Field end) {
		this.end = end;
	}
	
	public int getStartX() {
		return start.getX();
	}
	
	public int getStartY() {
		 return start.getY();
	}
	
	public int getEndX() {
		return end.getX();
	}
	
	public int getEndY() {
		return end.getY();
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public void setPiece(Piece piece) {
		this.piece = piece;
	}
	
	public boolean endPiecePresent() {
		return end.getPiece().isPresent();
	}
	
	public Move getCopy() {
		Field startCopy = new Field(start.getX(),start.getY());
		Field endCopy   = new Field(end.getX(),end.getY());
		Piece pieceCopy = piece.makeCopy();
		startCopy.setPiece(pieceCopy);
		return new Move(startCopy, endCopy);
	}
	
	//returns number of rows of the move 
	public int numberOfRows() {
		return Math.abs(getEndX() - getStartX());
	}
	
	//returns number of columns of the move
	public int numberOfColumns() {
		return Math.abs(getEndY() - getStartY());
	}
	
	public Color getColor() {
		return piece.getColor();
	}
	
}