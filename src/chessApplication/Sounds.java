//Austin
package chessApplication;

import javafx.scene.media.AudioClip;

public final class Sounds {

   private static final AudioClip MOVE                  = new AudioClip(Sounds.class.getResource("../resources/sounds/move.mp3").toString());
   private static final AudioClip DRAW                  = new AudioClip(Sounds.class.getResource("../resources/sounds/draw.mp3").toString());
   private static final AudioClip CHECKMATE             = new AudioClip(Sounds.class.getResource("../resources/sounds/checkmate.mp3").toString());
   private static final AudioClip STALEMATE             = new AudioClip(Sounds.class.getResource("../resources/sounds/stalemate.mp3").toString());
   private static final AudioClip OUT_OF_TIME           = new AudioClip(Sounds.class.getResource("../resources/sounds/outoftime.mp3").toString());
   private static final AudioClip INSUFFICIENT_MATERIAL = new AudioClip(Sounds.class.getResource("../resources/sounds/insufficientmaterial.mp3").toString());	
   private static final AudioClip RESIGN                = new AudioClip(Sounds.class.getResource("../resources/sounds/resign.mp3").toString());

   //CONSTRUCTOR
   private Sounds() {}

   public static void lagWorkAround() {
      MOVE.play(0.0);
   }

   public static void move() {
      MOVE.play();
   }

   public static void draw() {
      DRAW.play();
   }

   public static void checkmate() {
      CHECKMATE.play();
   }

   public static void stalemate() {
      STALEMATE.play();
   }

   public static void outOfTime() {
      OUT_OF_TIME.play();
   }

   public static void insufficientMaterial() {
      INSUFFICIENT_MATERIAL.play();
   }

   public static void resign() {
      RESIGN.play();
   }
}
