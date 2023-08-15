package Meritocracy;

public class Main {

  static final String ID = "MCFirst";

  static final long TIC = System.currentTimeMillis();

  static final boolean IS_GREEDY = true;
  static final boolean IS_PREJOIN_RANDOM = false;
  static final boolean IS_PREJOIN_GREEDY = false;

  static final int ITERATION = 1000;
  static final int TIME = 300 + 1;

  static final int N = 5; // Number of alternatives
  static final int M = 30; // Number of individuals
  static final int G = 25; // Length of Prejoin Learning

  static final double TAU = .01;
  static final int DECISION_STRUCTURE_LENGTH = 5;

  static String FILENAME = ID
      + "Greed" + IS_GREEDY
      + (!IS_GREEDY ? ("TAU" + TAU) : "")
      + "PrejoinRand" + IS_PREJOIN_RANDOM
      + "PrejoinGreed" + IS_PREJOIN_GREEDY
      + "I" + ITERATION
      + "T" + TIME
      + "N" + N
      + "M" + M
      + "G" + G;

  public static void main(String[] args) {
    Computation c = new Computation();
    new MatWriter(c);
  }

  static final int[] RESULT_KEY_VALUE = {
      DECISION_STRUCTURE_LENGTH,
      TIME
  };

  static final int[] RESULT_KEY_VALUE_ARM = {
      DECISION_STRUCTURE_LENGTH,
      TIME,
      N
  };

  static final int[] RESULT_KEY_VALUE_IND = {
      DECISION_STRUCTURE_LENGTH,
      TIME,
      M
  };

  static final int[] RESULT_KEY_VALUE_IND_ARM = {
      DECISION_STRUCTURE_LENGTH,
      TIME,
      M,
      N
  };

}
