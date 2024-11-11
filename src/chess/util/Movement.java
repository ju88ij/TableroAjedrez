package chess.util;


import java.util.function.Predicate;

import chess.Move;


public final class Movement {

	public static Predicate<Move> noRows =  m -> {
		return m.getEndX() - m.getStartX() == 0;
	};

	public static Predicate<Move> noColumns =  m -> {
		return m.getEndY() - m.getStartY() == 0;
	};

	public static Predicate<Move> diagonal =  m -> {
		return m.numberOfRows() == m.numberOfColumns();
	};

	public static Predicate<Move> straight =  m -> {
		return m.numberOfRows() == 0 ^ m.numberOfColumns() == 0;
	};

	public static Predicate<Move> jump =  m -> {
		return m.numberOfRows() == 2 && m.numberOfColumns() == 1
				|| m.numberOfRows() == 1 && m.numberOfColumns() == 2;
	};

	public static Predicate<Move> oneRow =  m -> {
		return m.numberOfRows() == 1;
	};

	public static Predicate<Move> oneColumn =  m -> {
		return m.numberOfColumns() == 1;
	};

	public static Predicate<Move> twoRows =  m -> {
		return m.numberOfRows() == 2;
	};

	public static Predicate<Move> twoColumns =  m -> {
		return m.numberOfColumns() == 2 ;
	};

	public static Predicate<Move> oneRowOrOneColumnOrBoth = m -> {
		return m.numberOfRows() <= 1 &&  m.numberOfColumns() <= 1;
	};

	public static Predicate<Move> left = m -> {
		return m.getEndY() - m.getStartY() >= 1;
	};

	public static Predicate<Move> right = m -> {
		return m.getStartY() - m.getEndY() >= 1;
	};
	public static Predicate<Move> up = m -> {
		return m.getEndX() - m.getStartX() >= 1;
	};

	public static Predicate<Move> down = m -> {
		return m.getStartX() - m.getEndX() >= 1;
	};
}




