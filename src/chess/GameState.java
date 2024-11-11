//Yasin
package chess;

import java.util.List;

import chess.pieces.Piece;

public class GameState {
	
	private String      gameState;
	private Move        lastMove;
	private List<Piece> takenPieces;

	//CONSTRUCTOR
	public GameState(String gameState, Move lastMoveMade, List<Piece> takenPieces) {
		this.gameState   = gameState;
		this.lastMove    = lastMoveMade;
		this.takenPieces = takenPieces;
	}

	//GETTERS & SETTERS
	public int getLastMEndX() {
		return lastMove.getEndX();
	}
	
	public int getLastMEndY() {
		return lastMove.getEndY();
	}
	
	public int getLastMStartX() {
		return lastMove.getStartX();
	}
	
	public int getLastMStartY() {
		return lastMove.getStartY();
	}
		
	public Piece getPieceLastM() {
		return lastMove.getPiece();
	}
	
	public List<Piece> getTakenPieces() {
		return takenPieces;
	}

	public String getGameState() {
		return gameState;
	}

}


