package Meritocracy;

import static org.apache.commons.math3.util.FastMath.pow;

import com.google.common.util.concurrent.AtomicDouble;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

public class Computation {

  ExecutorService workStealingPool;
  RandomGenerator r;

  AtomicDouble[][][][][][] consensusShareAtomic;
  AtomicDouble[][][][][][] consensusType0ShareAtomic;
  AtomicDouble[][][][][][] consensusType1ShareAtomic;
  AtomicDouble[][][][][][] perfectShareAtomic;
  AtomicDouble[][][][][][] perfectType0ShareAtomic;
  AtomicDouble[][][][][][] perfectType1ShareAtomic;
  AtomicDouble[][][][][][] mixedCoalitionShareAtomic;
  AtomicDouble[][][][][][][] decisionAgainstPreferenceAVGAtomic;
  AtomicDouble[][][][][][][] decisionAgainstPreferenceSTDAtomic;
  AtomicDouble[][][][][][][] choiceRateAVGAtomic;
  AtomicDouble[][][][][][][] choiceRateSTDAtomic;
  AtomicDouble[][][][][][][][] choiceRateIndAVGAtomic;
  AtomicDouble[][][][][][][][] choiceRateIndSTDAtomic;
  AtomicDouble[][][][][][][][] falsePositiveAVGAtomic;
  AtomicDouble[][][][][][][][] falsePositiveSTDAtomic;
  AtomicDouble[][][][][][][][] falsePositiveCNTAtomic;
  AtomicDouble[][][][][][][][] falseNegativeAVGAtomic;
  AtomicDouble[][][][][][][][] falseNegativeSTDAtomic;
  AtomicDouble[][][][][][][][] falseNegativeCNTAtomic;
  AtomicDouble[][][][][][][][][] winningCoalitionNVoteByTypeCNTAtomic;

  AtomicDouble[][][][][][] vote2Win00AVGAtomic;
  AtomicDouble[][][][][][] vote2Win00STDAtomic;
  AtomicDouble[][][][][][] vote2Win01AVGAtomic;
  AtomicDouble[][][][][][] vote2Win01STDAtomic;
  AtomicDouble[][][][][][] vote2Win10AVGAtomic;
  AtomicDouble[][][][][][] vote2Win10STDAtomic;
  AtomicDouble[][][][][][] vote2Win11AVGAtomic;
  AtomicDouble[][][][][][] vote2Win11STDAtomic;

  AtomicDouble[][][][][][] organizationalDecision0AVGAtomic;
  AtomicDouble[][][][][][] organizationalDecision0STDAtomic;
  AtomicDouble[][][][][][] organizationalDecision1AVGAtomic;
  AtomicDouble[][][][][][] organizationalDecision1STDAtomic;
  AtomicDouble[][][][][][] femaleVote0AVGAtomic;
  AtomicDouble[][][][][][] femaleVote0STDAtomic;
  AtomicDouble[][][][][][] femaleVote1AVGAtomic;
  AtomicDouble[][][][][][] femaleVote1STDAtomic;

  AtomicDouble[][][][][][] utilityAVGAtomic;
  AtomicDouble[][][][][][] utilitySTDAtomic;

  double[][][][][][] consensusShare;
  double[][][][][][] consensusType0Share;
  double[][][][][][] consensusType1Share;
  double[][][][][][] perfectShare;
  double[][][][][][] perfectType0Share;
  double[][][][][][] perfectType1Share;
  double[][][][][][] mixedCoalitionShare;
  double[][][][][][][] decisionAgainstPreferenceAVG;
  double[][][][][][][] decisionAgainstPreferenceSTD;
  double[][][][][][][] choiceRateAVG;
  double[][][][][][][] choiceRateSTD;
  double[][][][][][][][] choiceRateIndAVG;
  double[][][][][][][][] choiceRateIndSTD;
  double[][][][][][][][] falsePositiveAVG;
  double[][][][][][][][] falsePositiveSTD;
  double[][][][][][][][] falsePositiveCNT;
  double[][][][][][][][] falseNegativeAVG;
  double[][][][][][][][] falseNegativeSTD;
  double[][][][][][][][] falseNegativeCNT;
  double[][][][][][][][][] winningCoalitionNVoteByTypeCNT;

  double[][][][][][] vote2Win00AVG;
  double[][][][][][] vote2Win00STD;
  double[][][][][][] vote2Win01AVG;
  double[][][][][][] vote2Win01STD;
  double[][][][][][] vote2Win10AVG;
  double[][][][][][] vote2Win10STD;
  double[][][][][][] vote2Win11AVG;
  double[][][][][][] vote2Win11STD;
  
  double[][][][][][] organizationalDecision0AVG;
  double[][][][][][] organizationalDecision0STD;
  double[][][][][][] organizationalDecision1AVG;
  double[][][][][][] organizationalDecision1STD;
  double[][][][][][] femaleVote0AVG;
  double[][][][][][] femaleVote0STD;
  double[][][][][][] femaleVote1AVG;
  double[][][][][][] femaleVote1STD;

  double[][][][][][] utilityAVG;
  double[][][][][][] utilitySTD;

  ProgressBar pb;

  Computation() {
    fillParameterArray();

    r = new MersenneTwister();
    workStealingPool = Executors.newWorkStealingPool();
    pb = new ProgressBar("Full Experiment: Computation", Main.ITERATION);

    setResultSpace();
    runFullExperiment();
    averageFullExperiment();
  }

  private void fillParameterArray() {
    for (int m = 0; m < Main.M_LENGTH; m++) {
      Main.M0[m] = Main.COMPOSITION[m][0];
      Main.M1[m] = Main.COMPOSITION[m][1];
      Main.M[m] = Main.M0[m] + Main.M1[m];
    }
  }

  private void runFullExperiment() {
    for (int iteration = 0; iteration < Main.ITERATION; iteration++) {
      experimentWrapper experimentWrap = new experimentWrapper();
      workStealingPool.execute(experimentWrap);
    }
    workStealingPool.shutdown();
    try {
      workStealingPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    } catch (InterruptedException e) {
      System.out.println(e);
    }
  }

  void setResultSpace() {
    consensusShareAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    consensusType0ShareAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    consensusType1ShareAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    perfectShareAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    perfectType0ShareAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];perfectType1ShareAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    mixedCoalitionShareAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];

    decisionAgainstPreferenceAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][2];
    decisionAgainstPreferenceSTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][2];
    choiceRateAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][Main.N];
    choiceRateSTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][Main.N];
    choiceRateIndAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][][];
    choiceRateIndSTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][][];

    falsePositiveAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][][];
    falsePositiveSTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][][];
    falsePositiveCNTAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][][];

    falseNegativeAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][][];
    falseNegativeSTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][][];
    falseNegativeCNTAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][][];

    winningCoalitionNVoteByTypeCNTAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][Main.N][][];

    vote2Win00AVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    vote2Win00STDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    vote2Win01AVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    vote2Win01STDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    vote2Win10AVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    vote2Win10STDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    vote2Win11AVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    vote2Win11STDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];

    organizationalDecision0AVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    organizationalDecision0STDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    organizationalDecision1AVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    organizationalDecision1STDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    femaleVote0AVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    femaleVote0STDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    femaleVote1AVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    femaleVote1STDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];

    utilityAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    utilitySTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];

    for (int dp = 0; dp < Main.P_LENGTH; dp++) {
      for (int du = 0; du < Main.DELTA_U_LENGTH; du++) {
        for (int dr = 0; dr < Main.DECISION_STRUCTURE_LENGTH; dr++) {
          for (int m = 0; m < Main.M_LENGTH; m++) {
            for (int g = 0; g < Main.G_LENGTH; g++) {
              for (int t = 0; t < Main.TIME; t++) {
                decisionAgainstPreferenceAVGAtomic[dr][dp][du][m][g][t][0] = new AtomicDouble();
                decisionAgainstPreferenceSTDAtomic[dr][dp][du][m][g][t][0] = new AtomicDouble();
                decisionAgainstPreferenceAVGAtomic[dr][dp][du][m][g][t][1] = new AtomicDouble();
                decisionAgainstPreferenceSTDAtomic[dr][dp][du][m][g][t][1] = new AtomicDouble();
                consensusShareAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                consensusType0ShareAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                consensusType1ShareAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                perfectShareAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                perfectType0ShareAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                perfectType1ShareAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                mixedCoalitionShareAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                choiceRateIndAVGAtomic[dr][dp][du][m][g][t] = new AtomicDouble[Main.MAX_M][Main.N];
                choiceRateIndSTDAtomic[dr][dp][du][m][g][t] = new AtomicDouble[Main.MAX_M][Main.N];
                falsePositiveAVGAtomic[dr][dp][du][m][g][t] = new AtomicDouble[Main.MAX_M][Main.N];
                falsePositiveSTDAtomic[dr][dp][du][m][g][t] = new AtomicDouble[Main.MAX_M][Main.N];
                falsePositiveCNTAtomic[dr][dp][du][m][g][t] = new AtomicDouble[Main.MAX_M][Main.N];
                falseNegativeAVGAtomic[dr][dp][du][m][g][t] = new AtomicDouble[Main.MAX_M][Main.N];
                falseNegativeSTDAtomic[dr][dp][du][m][g][t] = new AtomicDouble[Main.MAX_M][Main.N];
                falseNegativeCNTAtomic[dr][dp][du][m][g][t] = new AtomicDouble[Main.MAX_M][Main.N];

                vote2Win00AVGAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                vote2Win00STDAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                vote2Win01AVGAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                vote2Win01STDAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                vote2Win10AVGAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                vote2Win10STDAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                vote2Win11AVGAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                vote2Win11STDAtomic[dr][dp][du][m][g][t] = new AtomicDouble();

                organizationalDecision0AVGAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                organizationalDecision0STDAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                organizationalDecision1AVGAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                organizationalDecision1STDAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                femaleVote0AVGAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                femaleVote0STDAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                femaleVote1AVGAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                femaleVote1STDAtomic[dr][dp][du][m][g][t] = new AtomicDouble();

                utilityAVGAtomic[dr][dp][du][m][g][t] = new AtomicDouble();
                utilitySTDAtomic[dr][dp][du][m][g][t] = new AtomicDouble();

                for (int choice = 0; choice < Main.N; choice++) {
                  choiceRateAVGAtomic[dr][dp][du][m][g][t][choice] = new AtomicDouble();
                  choiceRateSTDAtomic[dr][dp][du][m][g][t][choice] = new AtomicDouble();
                  winningCoalitionNVoteByTypeCNTAtomic[dr][dp][du][m][g][t][choice] =
//                  new AtomicDouble[FTMain.M0[m] + 1][FTMain.M1[m] + 1];
                      new AtomicDouble[Main.MAX_M + 1][Main.MAX_M + 1];
                  for (int member = 0; member < Main.MAX_M; member++) {
                    choiceRateIndAVGAtomic[dr][dp][du][m][g][t][member][choice] = new AtomicDouble();
                    choiceRateIndSTDAtomic[dr][dp][du][m][g][t][member][choice] = new AtomicDouble();
                    falsePositiveAVGAtomic[dr][dp][du][m][g][t][member][choice] = new AtomicDouble();
                    falsePositiveSTDAtomic[dr][dp][du][m][g][t][member][choice] = new AtomicDouble();
                    falsePositiveCNTAtomic[dr][dp][du][m][g][t][member][choice] = new AtomicDouble();
                    falseNegativeAVGAtomic[dr][dp][du][m][g][t][member][choice] = new AtomicDouble();
                    falseNegativeSTDAtomic[dr][dp][du][m][g][t][member][choice] = new AtomicDouble();
                    falseNegativeCNTAtomic[dr][dp][du][m][g][t][member][choice] = new AtomicDouble();
                  }
                  for (int type1 = 0; type1 < Main.MAX_M + 1; type1++) {
                    for (int type2 = 0; type2 < Main.MAX_M + 1; type2++) {
                      winningCoalitionNVoteByTypeCNTAtomic[dr][dp][du][m][g][t][choice][type1][type2] =
                          new AtomicDouble();
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    consensusShare = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    consensusType0Share = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    consensusType1Share = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    perfectShare = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    perfectType0Share = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    perfectType1Share = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    mixedCoalitionShare = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];

    decisionAgainstPreferenceAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][2];
    decisionAgainstPreferenceSTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][2];
    choiceRateAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][Main.N];
    choiceRateSTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][Main.N];
    choiceRateIndAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][][];
    choiceRateIndSTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][][];
    falsePositiveAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][][];
    falsePositiveSTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][][];
    falsePositiveCNT = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][][];
    falseNegativeAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][][];
    falseNegativeSTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][][];
    falseNegativeCNT = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][][];

    winningCoalitionNVoteByTypeCNT = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME][Main.N][][];

    vote2Win00AVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    vote2Win00STD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    vote2Win01AVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    vote2Win01STD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    vote2Win10AVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    vote2Win10STD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    vote2Win11AVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    vote2Win11STD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];

    organizationalDecision0AVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    organizationalDecision0STD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    organizationalDecision1AVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    organizationalDecision1STD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    femaleVote0AVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    femaleVote0STD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    femaleVote1AVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    femaleVote1STD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];

    utilityAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
    utilitySTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.P_LENGTH][Main.DELTA_U_LENGTH][Main.M_LENGTH][Main.G_LENGTH][Main.TIME];
  }

  private void averageFullExperiment() {
    for (int dr = 0; dr < Main.DECISION_STRUCTURE_LENGTH; dr++) {
      for (int dp = 0; dp < Main.P_LENGTH; dp++) {
        for (int du = 0; du < Main.DELTA_U_LENGTH; du++) {
          for (int m = 0; m < Main.M_LENGTH; m++) {
            for (int g = 0; g < Main.G_LENGTH; g++) {
              for (int t = 0; t < Main.TIME; t++) {
                consensusShare[dr][dp][du][m][g][t] =
                    consensusShareAtomic[dr][dp][du][m][g][t].get() / (double) Main.ITERATION;
                consensusType0Share[dr][dp][du][m][g][t] =
                    consensusType0ShareAtomic[dr][dp][du][m][g][t].get() / (double) Main.ITERATION;
                consensusType1Share[dr][dp][du][m][g][t] =
                    consensusType1ShareAtomic[dr][dp][du][m][g][t].get() / (double) Main.ITERATION;
                perfectShare[dr][dp][du][m][g][t] =
                    perfectShareAtomic[dr][dp][du][m][g][t].get() / (double) Main.ITERATION;
                perfectType0Share[dr][dp][du][m][g][t] =
                    perfectType0ShareAtomic[dr][dp][du][m][g][t].get() / (double) Main.ITERATION;
                perfectType1Share[dr][dp][du][m][g][t] =
                    perfectType1ShareAtomic[dr][dp][du][m][g][t].get() / (double) Main.ITERATION;
                mixedCoalitionShare[dr][dp][du][m][g][t] =
                    mixedCoalitionShareAtomic[dr][dp][du][m][g][t].get() / (double) Main.ITERATION;

                decisionAgainstPreferenceAVG[dr][dp][du][m][g][t][0] = decisionAgainstPreferenceAVGAtomic[dr][dp][du][m][g][t][0].get() / Main.ITERATION;
                decisionAgainstPreferenceSTD[dr][dp][du][m][g][t][0] = decisionAgainstPreferenceSTDAtomic[dr][dp][du][m][g][t][0].get() / Main.ITERATION;
                decisionAgainstPreferenceSTD[dr][dp][du][m][g][t][0] = pow(decisionAgainstPreferenceSTD[dr][dp][du][m][g][t][0] - pow(decisionAgainstPreferenceAVG[dr][dp][du][m][g][t][0], 2), .5);
                decisionAgainstPreferenceAVG[dr][dp][du][m][g][t][1] = decisionAgainstPreferenceAVGAtomic[dr][dp][du][m][g][t][1].get() / Main.ITERATION;
                decisionAgainstPreferenceSTD[dr][dp][du][m][g][t][1] = decisionAgainstPreferenceSTDAtomic[dr][dp][du][m][g][t][1].get() / Main.ITERATION;
                decisionAgainstPreferenceSTD[dr][dp][du][m][g][t][1] = pow(decisionAgainstPreferenceSTD[dr][dp][du][m][g][t][1] - pow(decisionAgainstPreferenceAVG[dr][dp][du][m][g][t][1], 2), .5);

                choiceRateIndAVG[dr][dp][du][m][g][t] = new double[Main.M[m]][Main.N];
                choiceRateIndSTD[dr][dp][du][m][g][t] = new double[Main.M[m]][Main.N];
                falsePositiveAVG[dr][dp][du][m][g][t] = new double[Main.M[m]][Main.N];
                falsePositiveSTD[dr][dp][du][m][g][t] = new double[Main.M[m]][Main.N];
                falsePositiveCNT[dr][dp][du][m][g][t] = new double[Main.M[m]][Main.N];
                falseNegativeAVG[dr][dp][du][m][g][t] = new double[Main.M[m]][Main.N];
                falseNegativeSTD[dr][dp][du][m][g][t] = new double[Main.M[m]][Main.N];
                falseNegativeCNT[dr][dp][du][m][g][t] = new double[Main.M[m]][Main.N];

                vote2Win00AVG[dr][dp][du][m][g][t] = vote2Win00AVGAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                vote2Win00STD[dr][dp][du][m][g][t] = vote2Win00STDAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                vote2Win00STD[dr][dp][du][m][g][t] = pow(vote2Win00STD[dr][dp][du][m][g][t] - pow(vote2Win00AVG[dr][dp][du][m][g][t], 2), .5);
                vote2Win01AVG[dr][dp][du][m][g][t] = vote2Win01AVGAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                vote2Win01STD[dr][dp][du][m][g][t] = vote2Win01STDAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                vote2Win01STD[dr][dp][du][m][g][t] = pow(vote2Win01STD[dr][dp][du][m][g][t] - pow(vote2Win01AVG[dr][dp][du][m][g][t], 2), .5);
                vote2Win10AVG[dr][dp][du][m][g][t] = vote2Win10AVGAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                vote2Win10STD[dr][dp][du][m][g][t] = vote2Win10STDAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                vote2Win10STD[dr][dp][du][m][g][t] = pow(vote2Win10STD[dr][dp][du][m][g][t] - pow(vote2Win10AVG[dr][dp][du][m][g][t], 2), .5);
                vote2Win11AVG[dr][dp][du][m][g][t] = vote2Win11AVGAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                vote2Win11STD[dr][dp][du][m][g][t] = vote2Win11STDAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                vote2Win11STD[dr][dp][du][m][g][t] = pow(vote2Win11STD[dr][dp][du][m][g][t] - pow(vote2Win11AVG[dr][dp][du][m][g][t], 2), .5);

                organizationalDecision0AVG[dr][dp][du][m][g][t] = organizationalDecision0AVGAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                organizationalDecision0STD[dr][dp][du][m][g][t] = organizationalDecision0STDAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                organizationalDecision0STD[dr][dp][du][m][g][t] = pow(organizationalDecision0STD[dr][dp][du][m][g][t] - pow(organizationalDecision0AVG[dr][dp][du][m][g][t], 2), .5);
                organizationalDecision1AVG[dr][dp][du][m][g][t] = organizationalDecision1AVGAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                organizationalDecision1STD[dr][dp][du][m][g][t] = organizationalDecision1STDAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                organizationalDecision1STD[dr][dp][du][m][g][t] = pow(organizationalDecision1STD[dr][dp][du][m][g][t] - pow(organizationalDecision1AVG[dr][dp][du][m][g][t], 2), .5);
                femaleVote0AVG[dr][dp][du][m][g][t] = femaleVote0AVGAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                femaleVote0STD[dr][dp][du][m][g][t] = femaleVote0STDAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                femaleVote0STD[dr][dp][du][m][g][t] = pow(femaleVote0STD[dr][dp][du][m][g][t] - pow(femaleVote0AVG[dr][dp][du][m][g][t], 2), .5);
                femaleVote1AVG[dr][dp][du][m][g][t] = femaleVote1AVGAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                femaleVote1STD[dr][dp][du][m][g][t] = femaleVote1STDAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                femaleVote1STD[dr][dp][du][m][g][t] = pow(femaleVote1STD[dr][dp][du][m][g][t] - pow(femaleVote1AVG[dr][dp][du][m][g][t], 2), .5);

                utilityAVG[dr][dp][du][m][g][t] = utilityAVGAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                utilitySTD[dr][dp][du][m][g][t] = utilitySTDAtomic[dr][dp][du][m][g][t].get() / Main.ITERATION;
                utilitySTD[dr][dp][du][m][g][t] = pow(utilitySTD[dr][dp][du][m][g][t] - pow(utilitySTD[dr][dp][du][m][g][t], 2), .5);

                for (int choice = 0; choice < Main.N; choice++) {
                  choiceRateAVG[dr][dp][du][m][g][t][choice] =
                      choiceRateAVGAtomic[dr][dp][du][m][g][t][choice].get() / Main.ITERATION;
                  choiceRateSTD[dr][dp][du][m][g][t][choice] =
                      choiceRateSTDAtomic[dr][dp][du][m][g][t][choice].get()
                          / Main.ITERATION; // 220306 ADDED
                  choiceRateSTD[dr][dp][du][m][g][t][choice] =
                      pow(
                          choiceRateSTD[dr][dp][du][m][g][t][choice]
                              - pow(choiceRateAVG[dr][dp][du][m][g][t][choice], 2),
                          .5);

                  winningCoalitionNVoteByTypeCNT[dr][dp][du][m][g][t][choice] =
                      new double[Main.M0[m] + 1][Main.M1[m] + 1];

                  for (int member = 0; member < Main.M[m]; member++) {
                    choiceRateIndAVG[dr][dp][du][m][g][t][member][choice] =
                        choiceRateIndAVGAtomic[dr][dp][du][m][g][t][member][choice].get()
                            / Main.ITERATION;
                    choiceRateIndSTD[dr][dp][du][m][g][t][member][choice] =
                        choiceRateIndSTDAtomic[dr][dp][du][m][g][t][member][choice].get()
                            / Main.ITERATION;
                    choiceRateIndSTD[dr][dp][du][m][g][t][member][choice] =
                        pow(
                            choiceRateIndSTD[dr][dp][du][m][g][t][member][choice]
                                - pow(choiceRateIndAVG[dr][dp][du][m][g][t][member][choice], 2),
                            .5);

                    if (falsePositiveCNTAtomic[dr][dp][du][m][g][t][member][choice].get() > 0) {
                      falsePositiveCNT[dr][dp][du][m][g][t][member][choice] =
                          falsePositiveCNTAtomic[dr][dp][du][m][g][t][member][choice].get();
                      falsePositiveAVG[dr][dp][du][m][g][t][member][choice] =
                          falsePositiveAVGAtomic[dr][dp][du][m][g][t][member][choice].get()
                              / falsePositiveCNTAtomic[dr][dp][du][m][g][t][member][choice].get();
                      falsePositiveSTD[dr][dp][du][m][g][t][member][choice] =
                          falsePositiveSTDAtomic[dr][dp][du][m][g][t][member][choice].get()
                              / falsePositiveCNTAtomic[dr][dp][du][m][g][t][member][choice].get();
                      falsePositiveSTD[dr][dp][du][m][g][t][member][choice] =
                          pow(
                              falsePositiveSTD[dr][dp][du][m][g][t][member][choice]
                                  - pow(falsePositiveAVG[dr][dp][du][m][g][t][member][choice], 2),
                              .5);
                    }

                    if (falseNegativeCNTAtomic[dr][dp][du][m][g][t][member][choice].get() > 0) {
                      falseNegativeCNT[dr][dp][du][m][g][t][member][choice] =
                          falseNegativeCNTAtomic[dr][dp][du][m][g][t][member][choice].get();
                      falseNegativeAVG[dr][dp][du][m][g][t][member][choice] =
                          falseNegativeAVGAtomic[dr][dp][du][m][g][t][member][choice].get()
                              / falseNegativeCNTAtomic[dr][dp][du][m][g][t][member][choice].get();
                      falseNegativeSTD[dr][dp][du][m][g][t][member][choice] =
                          falseNegativeSTDAtomic[dr][dp][du][m][g][t][member][choice].get()
                              / falseNegativeCNTAtomic[dr][dp][du][m][g][t][member][choice].get();
                      falseNegativeSTD[dr][dp][du][m][g][t][member][choice] =
                          pow(
                              falseNegativeSTD[dr][dp][du][m][g][t][member][choice]
                                  - pow(falseNegativeAVG[dr][dp][du][m][g][t][member][choice], 2),
                              .5);
                    }
                  }

                  for (int type1 = 0; type1 < Main.M0[m] + 1; type1++) {
                    for (int type2 = 0; type2 < Main.M1[m] + 1; type2++) {
                      winningCoalitionNVoteByTypeCNT[dr][dp][du][m][g][t][choice][type1][type2] =
                          winningCoalitionNVoteByTypeCNTAtomic[dr][dp][du][m][g][t][choice][type1][type2]
                              .get();
                    }
                  }

                }
              }
            }
          }
        }
      }
    }
  }

  class experimentWrapper implements Runnable {

    experimentWrapper() {
    }

    @Override
    public void run() {
      for (int dr = 0; dr < Main.DECISION_STRUCTURE_LENGTH; dr++) {
        for (int dp = 0; dp < Main.P_LENGTH; dp++) {
          for (int du = 0; du < Main.DELTA_U_LENGTH; du++) {
            for (int m = 0; m < Main.M_LENGTH; m++) {
              for (int g = 0; g < Main.G_LENGTH; g++) {
                new runScenario(dr, dp, du, m, g);
              }
            }
          }
        }
      }
      pb.stepNext();
    }
  }

  class runScenario {

    int decisionRuleIndex;

    int probabilityDifferenceIndex;
    int utilityDifferenceIndex;
    int mIndex;
    int gIndex;

    double probabilityDifference;
    double utilityDifference;
    int m;
    int m0;
    int m1;
    int g0;
    int g1;

    AtomicDouble[] consensusShareAtomicPart;
    AtomicDouble[] consensusType0ShareAtomicPart;
    AtomicDouble[] consensusType1ShareAtomicPart;
    AtomicDouble[] perfectShareAtomicPart;
    AtomicDouble[] perfectType0ShareAtomicPart;
    AtomicDouble[] perfectType1ShareAtomicPart;
    AtomicDouble[] mixedCoalitionShareAtomicPart;

    AtomicDouble[][] decisionAgainstPreferenceAVGAtomicPart;
    AtomicDouble[][] decisionAgainstPreferenceSTDAtomicPart;
    AtomicDouble[][] choiceRateAVGAtomicPart;
    AtomicDouble[][] choiceRateSTDAtomicPart;
    AtomicDouble[][][] choiceRateIndAVGAtomicPart;
    AtomicDouble[][][] choiceRateIndSTDAtomicPart;

    AtomicDouble[][][] falsePositiveAVGAtomicPart;
    AtomicDouble[][][] falsePositiveSTDAtomicPart;
    AtomicDouble[][][] falsePositiveCNTAtomicPart;
    AtomicDouble[][][] falseNegativeAVGAtomicPart;
    AtomicDouble[][][] falseNegativeSTDAtomicPart;
    AtomicDouble[][][] falseNegativeCNTAtomicPart;

    AtomicDouble[][][][] winningCoalitionNVoteByTypeCNTAtomicPart;

    AtomicDouble[] vote2Win00AVGAtomicPart;
    AtomicDouble[] vote2Win00STDAtomicPart;
    AtomicDouble[] vote2Win01AVGAtomicPart;
    AtomicDouble[] vote2Win01STDAtomicPart;
    AtomicDouble[] vote2Win10AVGAtomicPart;
    AtomicDouble[] vote2Win10STDAtomicPart;
    AtomicDouble[] vote2Win11AVGAtomicPart;
    AtomicDouble[] vote2Win11STDAtomicPart;

    AtomicDouble[] organizationalDecision0AVGAtomicPart;
    AtomicDouble[] organizationalDecision0STDAtomicPart;
    AtomicDouble[] organizationalDecision1AVGAtomicPart;
    AtomicDouble[] organizationalDecision1STDAtomicPart;
    AtomicDouble[] femaleVote0AVGAtomicPart;
    AtomicDouble[] femaleVote0STDAtomicPart;
    AtomicDouble[] femaleVote1AVGAtomicPart;
    AtomicDouble[] femaleVote1STDAtomicPart;

    AtomicDouble[] utilityAVGAtomicPart;
    AtomicDouble[] utilitySTDAtomicPart;

    runScenario(
        int decisionRuleIndex,
        int probabilityDifferenceIndex,
        int utilityDifferenceIndex,
        int mIndex,
        int gIndex) {
      this.decisionRuleIndex = decisionRuleIndex;
      this.probabilityDifferenceIndex = probabilityDifferenceIndex;
      this.utilityDifferenceIndex = utilityDifferenceIndex;
      this.mIndex = mIndex;
      this.gIndex = gIndex;

      probabilityDifference = Main.P[probabilityDifferenceIndex];
      utilityDifference = Main.DELTA_U[utilityDifferenceIndex];
      m = Main.M[mIndex];
      g = Main.G[gIndex];
      setResultSpacePart();
      run();
    }

    void setResultSpacePart() {
      consensusShareAtomicPart = consensusShareAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      consensusType0ShareAtomicPart = consensusType0ShareAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      consensusType1ShareAtomicPart = consensusType1ShareAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      perfectShareAtomicPart = perfectShareAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      perfectType0ShareAtomicPart = perfectType0ShareAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      perfectType1ShareAtomicPart = perfectType1ShareAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      mixedCoalitionShareAtomicPart = mixedCoalitionShareAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];

      decisionAgainstPreferenceAVGAtomicPart = decisionAgainstPreferenceAVGAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      decisionAgainstPreferenceSTDAtomicPart = decisionAgainstPreferenceSTDAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      choiceRateAVGAtomicPart = choiceRateAVGAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      choiceRateSTDAtomicPart = choiceRateSTDAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      choiceRateIndAVGAtomicPart = choiceRateIndAVGAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      choiceRateIndSTDAtomicPart = choiceRateIndSTDAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];

      falsePositiveCNTAtomicPart = falsePositiveCNTAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      falsePositiveAVGAtomicPart = falsePositiveAVGAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      falsePositiveSTDAtomicPart = falsePositiveSTDAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      falseNegativeCNTAtomicPart = falseNegativeCNTAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      falseNegativeAVGAtomicPart = falseNegativeAVGAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      falseNegativeSTDAtomicPart = falseNegativeSTDAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];

      winningCoalitionNVoteByTypeCNTAtomicPart = winningCoalitionNVoteByTypeCNTAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];

      vote2Win00AVGAtomicPart = vote2Win00AVGAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      vote2Win00STDAtomicPart = vote2Win00STDAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      vote2Win01AVGAtomicPart = vote2Win01AVGAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      vote2Win01STDAtomicPart = vote2Win01STDAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      vote2Win10AVGAtomicPart = vote2Win10AVGAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      vote2Win10STDAtomicPart = vote2Win10STDAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      vote2Win11AVGAtomicPart = vote2Win11AVGAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      vote2Win11STDAtomicPart = vote2Win11STDAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];

      organizationalDecision0AVGAtomicPart = organizationalDecision0AVGAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      organizationalDecision0STDAtomicPart = organizationalDecision0STDAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      organizationalDecision1AVGAtomicPart = organizationalDecision1AVGAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      organizationalDecision1STDAtomicPart = organizationalDecision1STDAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      femaleVote0AVGAtomicPart = femaleVote0AVGAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      femaleVote0STDAtomicPart = femaleVote0STDAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      femaleVote1AVGAtomicPart = femaleVote1AVGAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      femaleVote1STDAtomicPart = femaleVote1STDAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];

      utilityAVGAtomicPart = utilityAVGAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
      utilitySTDAtomicPart = utilitySTDAtomic[decisionRuleIndex][probabilityDifferenceIndex][utilityDifferenceIndex][mIndex][gIndex];
    }

    void run() {
      Scenario s;
      if (decisionRuleIndex == 0) {
        s = new FTScenarioSoloWoman(probabilityDifference, utilityDifference, 0, 1, g0, g1);
//        System.out.println(Arrays.deepToString(s.belief0));
      } else {
        s =
            new Scenario(
                probabilityDifference,
                utilityDifference,
                decisionRuleIndex,
                m0,
                m1,
                g0,
                g1
            );
      }
      for (int t = 0; t < Main.TIME; t++) {
        s.setOutcome();
        synchronized (this) {
          double decisionAgainstPreferenceType0 =
              (double) s.nVoteAgainstPreferenceType0 / (double) s.m0;
          double decisionAgainstPreferenceType1 =
              (double) s.nVoteAgainstPreferenceType1 / (double) s.m1;
          decisionAgainstPreferenceAVGAtomicPart[t][0].addAndGet(decisionAgainstPreferenceType0);
          decisionAgainstPreferenceSTDAtomicPart[t][0].addAndGet(pow(decisionAgainstPreferenceType0, 2));
          decisionAgainstPreferenceAVGAtomicPart[t][1].addAndGet(decisionAgainstPreferenceType1);
          decisionAgainstPreferenceSTDAtomicPart[t][1].addAndGet(pow(decisionAgainstPreferenceType1, 2));

          utilityAVGAtomicPart[t].addAndGet(s.averageUtility);
          utilityAVGAtomicPart[t].addAndGet(pow(s.averageUtility, 2));
          if (s.isConsensus) {
            consensusShareAtomicPart[t].addAndGet(1);
          }
          if (s.isConsensusType0) {
            consensusType0ShareAtomicPart[t].addAndGet(1);
          }
          if (s.isConsensusType1) {
            consensusType1ShareAtomicPart[t].addAndGet(1);
          }
          if (s.isPerfect) {
            perfectShareAtomicPart[t].addAndGet(1);
          }
          if (s.isPerfectType0) {
            perfectType0ShareAtomicPart[t].addAndGet(1);
          }
          if (s.isPerfectType1) {
            perfectType1ShareAtomicPart[t].addAndGet(1);
          }
          if (!s.isHomogeneousCoalition) {
            mixedCoalitionShareAtomicPart[t].addAndGet(1);
          }

          vote2Win00AVGAtomicPart[t].addAndGet(s.vote2Win00);
          vote2Win00STDAtomicPart[t].addAndGet(s.vote2Win00 * s.vote2Win00);
          vote2Win01AVGAtomicPart[t].addAndGet(s.vote2Win01);
          vote2Win01STDAtomicPart[t].addAndGet(s.vote2Win01 * s.vote2Win01);
          vote2Win10AVGAtomicPart[t].addAndGet(s.vote2Win10);
          vote2Win10STDAtomicPart[t].addAndGet(s.vote2Win10 * s.vote2Win10);
          vote2Win11AVGAtomicPart[t].addAndGet(s.vote2Win11);
          vote2Win11STDAtomicPart[t].addAndGet(s.vote2Win11 * s.vote2Win11);

          organizationalDecision0AVGAtomicPart[t].addAndGet(s.vote2Win00 + s.vote2Win10);
          organizationalDecision0STDAtomicPart[t].addAndGet((s.vote2Win00 + s.vote2Win10) * (s.vote2Win00 + s.vote2Win10));
          organizationalDecision1AVGAtomicPart[t].addAndGet(s.vote2Win01 + s.vote2Win11);
          organizationalDecision1STDAtomicPart[t].addAndGet((s.vote2Win01 + s.vote2Win11) * (s.vote2Win01 + s.vote2Win11));
          femaleVote0AVGAtomicPart[t].addAndGet(s.vote2Win00 + s.vote2Win10);
          femaleVote0STDAtomicPart[t].addAndGet((s.vote2Win00 + s.vote2Win10) * (s.vote2Win00 + s.vote2Win10));
          femaleVote1AVGAtomicPart[t].addAndGet(s.vote2Win10 + s.vote2Win11);
          femaleVote1STDAtomicPart[t].addAndGet((s.vote2Win10 + s.vote2Win11) * (s.vote2Win10 + s.vote2Win11));

          if (s.decisionRuleIndex == 0) {
            for (int choice : s.individualDecision) {
              choiceRateAVGAtomicPart[t][choice].addAndGet(1D / s.m);
              choiceRateSTDAtomicPart[t][choice].addAndGet(1D / s.m);
            }
          } else {
            int organizationalDecision = s.organizationalDecision;
            choiceRateAVGAtomicPart[t][organizationalDecision].addAndGet(1);
            choiceRateSTDAtomicPart[t][organizationalDecision].addAndGet(1);
          }
          winningCoalitionNVoteByTypeCNTAtomicPart[t][s.winningCoalitionDecision][s.winningCoalitionNVoteByType0][s.winningCoalitionNVoteByType1].addAndGet(1);
          for (int member = 0; member < s.m; member++) {
            choiceRateIndAVGAtomicPart[t][member][s.individualDecision[member]].addAndGet(1);
            choiceRateIndSTDAtomicPart[t][member][s.individualDecision[member]].addAndGet(1);
            for (int arm = 0; arm < Main.N; arm++) {
              if (s.falsePositive[member][arm] != 0) {
                falsePositiveCNTAtomicPart[t][member][arm].addAndGet(1D);
                falsePositiveAVGAtomicPart[t][member][arm].addAndGet(s.falsePositive[member][arm]);
                falsePositiveSTDAtomicPart[t][member][arm].addAndGet(pow(s.falsePositive[member][arm], 2));
              }
              if (s.falseNegative[member][arm] != 0) {
                falseNegativeCNTAtomicPart[t][member][arm].addAndGet(1D);
                falseNegativeAVGAtomicPart[t][member][arm].addAndGet(s.falseNegative[member][arm]);
                falseNegativeSTDAtomicPart[t][member][arm].addAndGet(pow(s.falseNegative[member][arm], 2));
              }
            }
          }
        }
        s.stepForward();
      }
    }
  }
}