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

  static final double[] G = new double[]{.2, .5};
  static final double[] DELTA_U = new double[]{.125, .25};

//    static final double[] SET_P = new double[]{.5};
//    static final double[] DELTA_U = new double[]{.125};

  static final double REWARD_TO_COOP = .0;
  static final int P_LENGTH = G.length;
  static final int DELTA_U_LENGTH = DELTA_U.length;

  static final int N = 2;

  static String LABEL = "DG_";
  static String PARAMS = ""
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
