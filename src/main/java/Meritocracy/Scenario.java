package Meritocracy;

import static org.apache.commons.math3.util.FastMath.exp;
import static org.apache.commons.math3.util.FastMath.max;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

class Scenario {

  RandomGenerator r;

  int decisionRuleIndex;
  DecisionRule decisoinRule;

  int n; // Number of alternatives
  int m; // Number of individuals
  int g; // Length of prejoin experience

  double[][] beliefNumerator; //230731 Change into double to accommodate Henning's Request. It might be computationally costly.
  double[][] beliefDenominator;
  double[][] belief;
  int[][] beliefRank;

  double[] competenceBestMatching;
  double[] competenceRankCorrelation;
  double[] competenceBeliefCorrelation;
  int[] rankBestMatching;
  int[] rankRankCorrelation;
  int[] rankBeliefCorrelation;

  double[] power;
  double[] reality;
  int[] realityRank;

  int[] individualDecision;
  int organizationalDecision;
  boolean[] isSuccessfulIndividual;
  boolean isSuccessfulOrganization;

  double[][] falsePositive;
  double[][] falseNegative;

  int[] armIndexArray;
  int[] memberIndexArray;

  double meritocracyScore;

  Scenario(
      int decisionRuleIndex,
      int n,
      int m,
      int g
  ) {
    r = new MersenneTwister();

    this.decisionRuleIndex = decisionRuleIndex;
    setDecisionRule(decisionRuleIndex);

    this.n = n;
    this.m = m;
    this.g = g;

    initializeIDArrays();
    initializeReality();
    initializeIndividual();
  }

  void setDecisionRule(int decisionRuleIndex) {
    switch (decisionRuleIndex) {
      case 0 -> decisoinRule = new Autonomous();
      case 1 -> decisoinRule = new RotatingDictatorship();
      case 2 -> decisoinRule = new WeightedVoting();
      case 3 -> decisoinRule = new TwoStageVoting();
      case 4 -> decisoinRule = new TwoStageWeightedVoting();
    }
  }

  void initializeIDArrays() {
    individualDecision = new int[m];
    armIndexArray = new int[n];
    for (int choice = 0; choice < n; choice++) {
      armIndexArray[choice] = choice;
    }
    memberIndexArray = new int[m];
    for (int individual = 0; individual < m; individual++) {
      memberIndexArray[individual] = individual;
    }
  }

  void initializeReality() {
    reality = new double[n];
    for (int p = 0; p < n; p++) {
      reality[p] = r.nextDouble();
    }
    realityRank = getRank(reality);
  }

  void initializeIndividual() {
    beliefNumerator = new double[m][n];
    beliefDenominator = new double[m][n];
    belief = new double[m][n];
    power = new double[m];
    double powerSum = 0;

    for (int member : memberIndexArray) {
      double[] beliefNumeratorIndividual = beliefNumerator[member];
      double[] beliefDenominatorIndividual = beliefDenominator[member];
      power[member] = r.nextDouble();
      powerSum += power[member];
      if (Main.IS_INITIAL_RANDOM) {
        for (int choice : armIndexArray) {
          beliefNumeratorIndividual[choice] = r.nextInt(3);
          beliefDenominatorIndividual[choice] = 2D;
        }
      } else {
        for (int choice : armIndexArray) {
          // One success one failure
          beliefNumeratorIndividual[choice] = 1D;
          beliefDenominatorIndividual[choice] = 2D;
        }
      }
      if (Main.IS_PREJOIN_GREEDY) {
        for (int choice : armIndexArray) {
          if (beliefDenominatorIndividual[choice] != 0) {
            belief[member][choice] = beliefNumeratorIndividual[choice] / beliefDenominatorIndividual[choice];
          } else {
            belief[member][choice] = .5D;
          }
        }
        for (int sample = 0; sample < g; sample++) {
          int choice = transformBelief2Decision(belief[member]);
          if (r.nextDouble() < reality[choice]) {
            beliefNumeratorIndividual[choice]++;
          }
          beliefDenominatorIndividual[choice]++;
          belief[member][choice] = beliefNumeratorIndividual[choice] / beliefDenominatorIndividual[choice];
        }
      } else {
        for (int prejoin = 0; prejoin < g; prejoin++) {
          int choice = r.nextInt(n);
          if (r.nextDouble() < reality[choice]) {
            beliefNumeratorIndividual[choice]++;
          }
          beliefDenominatorIndividual[choice]++;
        }
        for (int choice : armIndexArray) {
          if (beliefDenominatorIndividual[choice] != 0) {
            belief[member][choice] = beliefNumeratorIndividual[choice] / beliefDenominatorIndividual[choice];
          } else {
            belief[member][choice] = .5D;
          }
        }
      }
    }
    for (int member : memberIndexArray) {
      power[member] /= powerSum;
    }
    setIndividualDecision();
    setOrganizationalDecision();
  }

  void stepForward() {
    setIndividualDecision();
    setOrganizationalDecision();
    doIndividualLearning();
    setOutcome();
  }

  void setIndividualDecision() {
    for (int individual : memberIndexArray) {
      individualDecision[individual] = transformBelief2Decision(belief[individual]);
      isSuccessfulIndividual[individual] = r.nextDouble() < reality[individualDecision[individual]];
    }
  }

  int transformBelief2Decision(double[] belief) {
    int decision = -1;
    if (Main.IS_GREEDY) {
      double bestBelief = Double.MIN_VALUE;
      shuffleFisherYates(armIndexArray);
      for (int choice : armIndexArray) {
        if (belief[choice] > bestBelief) {
          decision = choice;
          bestBelief = belief[choice];
        }
      }
      if (decision == -1) {
        decision = r.nextInt(n);
      }
    } else {
      double[] probability = transformBelief2Probability(belief);
      double random = r.nextDouble();
      for (int choice = 0; choice < n; choice++) {
        if (random <= probability[choice]) {
          decision = choice;
          break;
        }
      }
    }
    return decision;
  }

  double[] transformBelief2Probability(double[] belief) {
    double[] probability = belief.clone();
    double denominator = 0;
    for (int choice : armIndexArray) {
      probability[choice] = exp(probability[choice] / Main.TAU);
      denominator += probability[choice];
    }
    probability[0] /= denominator;
    for (int choice = 1; choice < n; choice++) {
      probability[choice] /= denominator;
      probability[choice] += probability[choice - 1];
    }
    return probability;
  }

  void setOrganizationalDecision() {
    organizationalDecision = decisoinRule.decide();
    isSuccessfulOrganization = r.nextDouble() < reality[organizationalDecision];
  }

  void doIndividualLearning() {
    if (isSuccessfulOrganization) {
      for (int individual : memberIndexArray) {
        beliefNumerator[individual][organizationalDecision]++;
        beliefDenominator[individual][organizationalDecision]++;
        belief[individual][organizationalDecision] =
            beliefNumerator[individual][organizationalDecision]
                / (double) beliefDenominator[individual][organizationalDecision];
      }
    } else {
      for (int individual : memberIndexArray) {
        beliefDenominator[individual][organizationalDecision]++;
        belief[individual][organizationalDecision] =
            beliefNumerator[individual][organizationalDecision]
                / (double) beliefDenominator[individual][organizationalDecision];
      }
    }
  }

  void setOutcome() {
    falsePositive = new double[m][n];
    falseNegative = new double[m][n];

    meritocracyStateBestMatching = 0;
    meritocracyStateRankCorrelation = 0;
    meritocracyStateBeliefCorrelation = 0;

  }

  public interface DecisionRule {

    default int getId() {
      return -1;
    }

    default int decide() {
      return -1;
    }

    default void setWeightType0(double weightType0) {
    }

    default void setWeightType1(double weightType1) {
    }
  }

  class Autonomous implements DecisionRule {

    @Override
    public int getId() {
      return 0;
    }

    @Override
    public int decide() {
      //Assume Plurality Voting
      int decision = -1;
      int maxCount = Integer.MIN_VALUE;
      int[] countMessage = new int[n];
      for (int individual : memberIndexArray) {
        countMessage[individualDecision[individual]]++;
      }
      shuffleFisherYates(armIndexArray);
      for (int choice : armIndexArray) {
        if (countMessage[choice] > maxCount) {
          decision = choice;
          maxCount = countMessage[choice];
        }
      }
      return decision;
    }
  }

  class RotatingDictatorship implements DecisionRule {
    @Override
    public int getId() {
      return 1;
    }

    @Override
    public int decide() {
      int decision = -1;
      double maxCount = Double.MIN_VALUE;
      double[] countVote = new double[n];
      for (int member : memberIndexArray) {
        countVote[individualDecision[member]] += power[member];
      }
      shuffleFisherYates(armIndexArray);
      for (int choice : armIndexArray) {
        if (countVote[choice] > maxCount) {
          decision = choice;
          maxCount = countVote[choice];
        }
      }
      return decision;
    }
  }

  class PluralityVoting implements DecisionRule {
    @Override
    public int getId() {
      return 1;
    }

    @Override
    public int decide() {
      int decision = -1;
      double maxCount = Double.MIN_VALUE;
      double[] countVote = new double[n];
      for (int member : memberIndexArray) {
        countVote[individualDecision[member]] += power[member];
      }
      shuffleFisherYates(armIndexArray);
      for (int choice : armIndexArray) {
        if (countVote[choice] > maxCount) {
          decision = choice;
          maxCount = countVote[choice];
        }
      }
      return decision;
    }
  }

  
  class TwoStageVoting implements DecisionRule {
    // average beliefs decision-making structure functions as
    // plurality voting does but without any message trans-
    // formation.

    @Override
    public int getId() {
      return 3;
    }

    @Override
    public int decide() {
      int decisionType0 = -1;
      int decisionType1 = -1;
      int decision = -1;

      double maxCountType0 = Double.MIN_VALUE;
      double maxCountType1 = Double.MIN_VALUE;
      double[] countMessageType0 = new double[n];
      double[] countMessageType1 = new double[n];
      for (int individual : memberIndexArray) {
        if (typeOf[individual] == 0) {
          countMessageType0[individualDecision[individual]] += 1D;
        } else {
          countMessageType1[individualDecision[individual]] += 1D;
        }
      }
      shuffleFisherYates(armIndexArray);
      for (int choice : armIndexArray) {
        if (countMessageType0[choice] > maxCountType0) {
          decisionType0 = choice;
          maxCountType0 = countMessageType0[choice];
        }
      }
      shuffleFisherYates(armIndexArray);
      for (int choice : armIndexArray) {
        if (countMessageType1[choice] > maxCountType1) {
          decisionType1 = choice;
          maxCountType1 = countMessageType1[choice];
        }
      }
      if (m0 > m1) {
        decision = decisionType0;
      } else if (m0 < m1) {
        decision = decisionType1;
      } else {
        decision = r.nextBoolean() ? decisionType0 : decisionType1;
      }
      return decision;
    }
  }

  class TwoStageWeightedVoting implements DecisionRule {
    // average beliefs decision-making structure functions as
    // plurality voting does but without any message trans-
    // formation.

    @Override
    public int getId() {
      return 4;
    }

    @Override
    public int decide() {
      int decisionType0 = -1;
      int decisionType1 = -1;
      int decision = -1;

      double maxCountType0 = Double.MIN_VALUE;
      double maxCountType1 = Double.MIN_VALUE;
      double[] countMessageType0 = new double[n];
      double[] countMessageType1 = new double[n];
      for (int individual : memberIndexArray) {
        if (typeOf[individual] == 0) {
          countMessageType0[individualDecision[individual]] += 1D;
        } else {
          countMessageType1[individualDecision[individual]] += 1D;
        }
      }
      shuffleFisherYates(armIndexArray);
      for (int choice : armIndexArray) {
        if (countMessageType0[choice] > maxCountType0) {
          decisionType0 = choice;
          maxCountType0 = countMessageType0[choice];
        }
      }
      shuffleFisherYates(armIndexArray);
      for (int choice : armIndexArray) {
        if (countMessageType1[choice] > maxCountType1) {
          decisionType1 = choice;
          maxCountType1 = countMessageType1[choice];
        }
      }
      decision = r.nextBoolean() ? decisionType0 : decisionType1;
      return decision;
    }
  }

  void shuffleFisherYates(int[] arr) {
    for (int i = arr.length - 1; i > 0; i--) {
      int j = r.nextInt(i + 1);
      int temp = arr[i];
      arr[i] = arr[j];
      arr[j] = temp;
    }
  }

  int[] getRank(double[] arr) {
    int[] rank = new int[arr.length];
    for (int focal = 0; focal < arr.length; focal++) {
      int rankOf = 0;  // Start rank from 0
      for (int target = 0; target < arr.length; target++) {
        if (arr[target] < arr[focal] || (arr[target] == arr[focal] && target < focal)) {
          rankOf++;
        }
      }
      rank[focal] = rankOf;
    }
    return rank;
  }

}