package Meritocracy;

public class Main {
  static final String ID = "Meritocracy";

  static final long TIC = System.currentTimeMillis();

  static final boolean IS_DU1 = false;
  static final boolean IS_GREEDY = true;
  static final boolean IS_LEARNING = true;
  static final boolean IS_INITIAL_RANDOM = true;
  static final boolean IS_PREJOIN_GREEDY = true;

  static final int ITERATION = 10_000;
  static final int TIME = 100 + 1;

  static final int[][] COMPOSITION = new int[][]{
      {7, 1},
      {6, 2},
      {5, 3},
      {4, 4},
      {3, 5},
      {2, 6},
      {1, 7}
  };

  static int MAX_M = 8;
//    static int MAX_M = 16;

//    static int[] WEIGHTED_VOTING_COMPOSITION = new int[]{4,4};

  static final int M_LENGTH = COMPOSITION.length;
  static final int[] M = new int[COMPOSITION.length];
  static final int[] M0 = new int[COMPOSITION.length];
  static final int[] M1 = new int[COMPOSITION.length];
  //Arms
//    static int G = 1; // Prejoin Learning
  static final int[][] G = new int[][]{
      {0, 0},
      {5, 0},
      {100, 0},
      {5, 5},
      {5, 100},
      {100, 100}
  };
  static final int G_LENGTH = G.length;

  static final double TAU = .01;
  static final int DECISION_STRUCTURE_LENGTH = 5;

  static final double[] P = new double[]{.2, .5};
  static final double[] DELTA_U = new double[]{.125, .25};

//    static final double[] SET_P = new double[]{.5};
//    static final double[] DELTA_U = new double[]{.125};

  static final double REWARD_TO_COOP = .0;
  static final int P_LENGTH = P.length;
  static final int DELTA_U_LENGTH = DELTA_U.length;

  static final int N = 2;

  static String LABEL = "DG_";
  static String PARAMS = ""
      + "IsD1" + IS_DU1
      + "Learning" + IS_LEARNING
      + "Greed" + IS_GREEDY
      + "InitRand" + IS_INITIAL_RANDOM
      + "PreGreed" + IS_PREJOIN_GREEDY
      + "I" + ITERATION
      + "T" + TIME
      + "nM" + M_LENGTH
      + "mxM" + MAX_M
      + "N" + N
      + "G" + G_LENGTH
      + (!IS_GREEDY ? ("TAU" + TAU) : "")
      + "nSP" + P_LENGTH
      + "nDU" + DELTA_U_LENGTH
      + (REWARD_TO_COOP > 0 ? ("Rcp" + REWARD_TO_COOP) : "");
  static String FILENAME = LABEL + PARAMS;
//    static String PATH_CSV = new File(".").getAbsolutePath() + "\\" + FILENAME + "\\";

  // These initial priors are developed in a prejoin learning phase of g periods. Each
  // individual starts the prejoin phase with uninformed
  // beliefs in the sense that each alternative is believed to
  // have a payoff probability of 0.5. They then engage in
  // individual learning about the alternatives for the du-
  // ration of the prejoin phase by sampling alternatives in
  // a highly exploratory manner and receiving feedback
  // on their choices.

  public static void main(String[] args) {
    Computation c = new Computation();
    new MatWriter(c);

//        try {
//            new FTComputationBelief();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
  }

  static final int[] RESULT_KEY_VALUE = {
      DECISION_STRUCTURE_LENGTH,
      P_LENGTH,
      DELTA_U_LENGTH,
      M_LENGTH,
      G_LENGTH,
      TIME
  };

  static final int[] RESULT_KEY_VALUE_TYPE = {
      DECISION_STRUCTURE_LENGTH,
      P_LENGTH,
      DELTA_U_LENGTH,
      M_LENGTH,
      G_LENGTH,
      TIME,
      2
  };

  static final int[] RESULT_KEY_VALUE_ARM = {
      DECISION_STRUCTURE_LENGTH,
      P_LENGTH,
      DELTA_U_LENGTH,
      M_LENGTH,
      G_LENGTH,
      TIME,
      N
  };

  static final int[] RESULT_KEY_VALUE_IND = {
      DECISION_STRUCTURE_LENGTH,
      P_LENGTH,
      DELTA_U_LENGTH,
      M_LENGTH,
      G_LENGTH,
      TIME,
      MAX_M
  };

  static final int[] RESULT_KEY_VALUE_IND_ARM = {
      DECISION_STRUCTURE_LENGTH,
      P_LENGTH,
      DELTA_U_LENGTH,
      M_LENGTH,
      G_LENGTH,
      TIME,
      MAX_M,
      N
  };

  static final int[] RESULT_KEY_VALUE_ARM_ARM = {
      DECISION_STRUCTURE_LENGTH,
      P_LENGTH,
      DELTA_U_LENGTH,
      M_LENGTH,
      G_LENGTH,
      TIME,
      N,
      N
  };

  static final int[] RESULT_KEY_VALUE_ARM_CNT = {
      DECISION_STRUCTURE_LENGTH,
      P_LENGTH,
      DELTA_U_LENGTH,
      M_LENGTH,
      G_LENGTH,
      TIME,
      N,
      MAX_M + 1,
      MAX_M + 1
  };

  static final int[] RESULT_KEY_VALUE_HISTORY = {
      DECISION_STRUCTURE_LENGTH,
      P_LENGTH,
      DELTA_U_LENGTH,
      M_LENGTH,
      G_LENGTH,
      TIME,
      N * 2
  };

}
