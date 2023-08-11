package Meritocracy;

public class Main {
  static final String ID = "Meritocracy";

  static final long TIC = System.currentTimeMillis();

  static final boolean IS_GREEDY = true;
  static final boolean IS_INITIAL_RANDOM = true;
  static final boolean IS_PREJOIN_GREEDY = true;

  static final int ITERATION = 10_000;
  static final int TIME = 100 + 1;

  static final int N = 5; // Number of alternatives
  static final int M = 5; // Number of individuals

  static final double TAU = .01;
  static final int DECISION_STRUCTURE_LENGTH = 5;

  static final double REWARD_TO_COOP = .0;

  static String LABEL = "DG_";
  static String PARAMS = ""
      + "Greed" + IS_GREEDY
      + (!IS_GREEDY ? ("TAU" + TAU) : "")
      + "InitRand" + IS_INITIAL_RANDOM
      + "PreGreed" + IS_PREJOIN_GREEDY
      + "I" + ITERATION
      + "T" + TIME
      + "N" + N
      + "M" + M
      + (REWARD_TO_COOP > 0 ? ("Rcp" + REWARD_TO_COOP) : "");
  static String FILENAME = LABEL + PARAMS;

  public static void main(String[] args) {
    Computation c = new Computation();
    new MatWriter(c);
  }

  static final int[] RESULT_KEY_VALUE = {
      DECISION_STRUCTURE_LENGTH,
      P_LENGTH,
      DELTA_U_LENGTH,
      M_LENGTH,
      G_LENGTH,
      TIME
  };

}
