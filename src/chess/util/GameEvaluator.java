package chess.util;

import static chess.util.GameEvaluator.EvaluationResult.CHECK;
import static chess.util.GameEvaluator.EvaluationResult.CHECKMATE;
import static chess.util.GameEvaluator.EvaluationResult.CHECK_AND_FIFTY_MOVES_RULE;
import static chess.util.GameEvaluator.EvaluationResult.CHECK_AND_THREE_FOLD_REPETION_RULE;
import static chess.util.GameEvaluator.EvaluationResult.FIFTY_MOVES_RULE;
import static chess.util.GameEvaluator.EvaluationResult.INSUFFICIENT_MATERIAL;
import static chess.util.GameEvaluator.EvaluationResult.NORMAL_GAME_SITUATION;
import static chess.util.GameEvaluator.EvaluationResult.STALEMATE;
import static chess.util.GameEvaluator.EvaluationResult.THREE_FOLD_REPETION_RULE;

import java.util.Objects;
import java.util.function.BiFunction;

import chess.Game;
import chess.Move;
import chess.util.GameEvaluator.EvaluationResult;


@FunctionalInterface
public interface GameEvaluator extends BiFunction<Game, Move, EvaluationResult>{

   public enum EvaluationResult{
      //used in the model
      CHECK,
      CHECKMATE,
      STALEMATE,
      INSUFFICIENT_MATERIAL,
      FIFTY_MOVES_RULE,
      THREE_FOLD_REPETION_RULE,
      CHECK_AND_FIFTY_MOVES_RULE,
      CHECK_AND_THREE_FOLD_REPETION_RULE,
      NORMAL_GAME_SITUATION,
      //used in the view
      OFFER_DRAW,
      RESIGN,
      CLAIM_DRAW,
      OUT_OF_TIME;

      @Override
      public String toString() {
         return name().substring(0, 1) + name().substring(1).replace('_', ' ').toLowerCase()+".";
      }
   }

   static GameEvaluator checkmate() {
      return (g, m) -> g.checkmate.test(m) ?
            CHECKMATE: NORMAL_GAME_SITUATION;
   }

   static GameEvaluator stalemate() {
      return (g, m) -> g.stalemate.test(m) ?
            STALEMATE: NORMAL_GAME_SITUATION;
   }

   static GameEvaluator insufficientMaterial() {
      return (g, m) -> g.insufficientMaterial.test(m) ?
            INSUFFICIENT_MATERIAL: NORMAL_GAME_SITUATION;
   }

   static GameEvaluator check() {
      return (g, m) -> g.checkOnOpponent.test(m) && !g.fiftyMoves.get() && !g.threeFoldRepetition.get() ?
            CHECK: NORMAL_GAME_SITUATION;
   }

   static GameEvaluator checkAndFiftyMovesRule() {
      return (g, m) ->  g.checkOnOpponent.test(m) && g.fiftyMoves.get() ?
            CHECK_AND_FIFTY_MOVES_RULE: NORMAL_GAME_SITUATION;
   }

   static GameEvaluator checkAndThreeFoldRepetitionRule() {
      return (g, m) ->  g.checkOnOpponent.test(m) && g.threeFoldRepetition.get() ?
            CHECK_AND_THREE_FOLD_REPETION_RULE: NORMAL_GAME_SITUATION;
   }

   static GameEvaluator fiftyMovesRule() {
      return (g, m) -> g.fiftyMoves.get() ?
            FIFTY_MOVES_RULE : NORMAL_GAME_SITUATION;
   }

   static GameEvaluator threeFoldRepetitionRule() {
      return (g,m) -> g.threeFoldRepetition.get() ?
            THREE_FOLD_REPETION_RULE : NORMAL_GAME_SITUATION;
   }

   default GameEvaluator and (GameEvaluator other) {
      //fail safe
      Objects.requireNonNull(other);
      return (g, m) -> {
         EvaluationResult result = this.apply(g, m);
         return result.equals(NORMAL_GAME_SITUATION) ? other.apply(g, m) : result;
      };
   }
}