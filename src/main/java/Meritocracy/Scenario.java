package Meritocracy;

import static org.apache.commons.math3.util.FastMath.exp;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

class Scenario {

  RandomGenerator r;
  PearsonsCorrelation pc;

  int decisionRuleIndex;
  DecisionRule decisoinRule;

  int n; // Number of alternatives
  int m; // Number of individuals
  int g; // Length of prejoin experience

  double[][] beliefNumerator; //230731 Change into double to accommodate Henning's Request. It might be computationally costly.
  double[][] beliefDenominator;
  double[][] belief;
  double[][] beliefRank;

  double[] competenceBestMatching;
  double[] competenceRankCorrelation;
  double[] competenceBeliefCorrelation;
  double[] rankBestMatching;
  double[] rankRankCorrelation;
  double[] rankBeliefCorrelation;

  double[] power;
  double[] powerRank;
  double[] reality;
  double[] realityRank;

  int[] individualDecision;
  int organizationalDecision;
  boolean[] isSuccessfulIndividual;
  boolean isSuccessfulOrganization;

  int[] alternativeIndexArray;
  int[] memberIndexArray;

  double averageCompetenceBestMatching;
  double averageCompetenceRankCorrelation;
  double averageCompetenceBeliefCorrelation;
  double meritocracyScoreBestMatching;
  double meritocracyScoreRankCorrelation;
  double meritocracyScoreBeliefCorrelation;

  Scenario(
      int decisionRuleIndex,
      int n,
      int m,
      int g
  ) {
    r = new MersenneTwister();
    pc = new PearsonsCorrelation();

    this.decisionRuleIndex = decisionRuleIndex;
    setDecisionRule(decisionRuleIndex);

    this.n = n;
    this.m = m;
    this.g = g;

    initializeIDArrays();
    initializeReality();
    initializeOrganization();
    setOutcome();
  }

  void setDecisionRule(int decisionRuleIndex) {
    switch (decisionRuleIndex) {
      case 0 -> decisoinRule = new Autonomous();
      case 1 -> decisoinRule = new RotatingDictatorship();
      case 2 -> decisoinRule = new PluralityVoting();
      case 3 -> decisoinRule = new TwoStageVoting();
      case 4 -> decisoinRule = new AverageBelief();
    }
  }

  void initializeIDArrays() {
    individualDecision = new int[m];
    alternativeIndexArray = new int[n];
    for (int choice = 0; choice < n; choice++) {
      alternativeIndexArray[choice] = choice;
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

  void initializeOrganization() {
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
        for (int choice : alternativeIndexArray) {
          beliefNumeratorIndividual[choice] = r.nextInt(3);
          beliefDenominatorIndividual[choice] = 2D;
        }
      } else {
        for (int choice : alternativeIndexArray) {
          // One success one failure
          beliefNumeratorIndividual[choice] = 1D;
          beliefDenominatorIndividual[choice] = 2D;
        }
      }
      if (Main.IS_PREJOIN_GREEDY) {
        for (int choice : alternativeIndexArray) {
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
        for (int choice : alternativeIndexArray) {
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
    powerRank = getRank(power);
    setIndividualDecision();
    setOrganizationalDecision();
  }

  void stepForward() {
    setIndividualDecision();
    setOrganizationalDecision();
    doLearning();
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
      shuffleFisherYates(alternativeIndexArray);
      for (int choice : alternativeIndexArray) {
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
    for (int choice : alternativeIndexArray) {
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

  void doLearning() {
    if (decisionRuleIndex == 0) {
      doAutonomousLearning();
    } else {
      doOrganizationalLearning();
    }
  }

  void doOrganizationalLearning() {
    double beliefNumeratorChange = isSuccessfulOrganization ? 1 : 0;
    for (int member : memberIndexArray) {
      beliefNumerator[member][organizationalDecision] += beliefNumeratorChange;
      beliefDenominator[member][organizationalDecision]++;
      belief[member][organizationalDecision] = beliefNumerator[member][organizationalDecision] / (double) beliefDenominator[member][organizationalDecision];
      beliefRank[member] = getRank(belief[member]);
    }
  }

  void doAutonomousLearning() {
    for (int member : memberIndexArray) {
      if (isSuccessfulIndividual[member]) {
        beliefNumerator[member][organizationalDecision]++;
      }
      beliefDenominator[member][organizationalDecision]++;
      belief[member][organizationalDecision] = beliefNumerator[member][organizationalDecision] / (double) beliefDenominator[member][organizationalDecision];
      beliefRank[member] = getRank(belief[member]);
    }
  }

  void updateIndividualCompetence() {
    competenceBestMatching = new double[m];
    competenceRankCorrelation = new double[m];
    competenceBeliefCorrelation = new double[m];

    for (int member : memberIndexArray) {
      competenceBestMatching[member] = beliefRank[member][0] == realityRank[0] ? 1 : 0;
      competenceRankCorrelation[member] = pc.correlation(beliefRank[member], realityRank);
      competenceBeliefCorrelation[member] = pc.correlation(belief[member], reality);
    }

    rankBestMatching = getRank(competenceBestMatching);
    rankRankCorrelation = getRank(competenceRankCorrelation);
    rankBeliefCorrelation = getRank(competenceBeliefCorrelation);
  }

  void setOutcome() {
    averageCompetenceBestMatching = getAverage(competenceBestMatching);
    averageCompetenceRankCorrelation = getAverage(competenceRankCorrelation);
    averageCompetenceBeliefCorrelation = getAverage(competenceBeliefCorrelation);
    meritocracyScoreBestMatching = pc.correlation(rankBestMatching, powerRank);
    meritocracyScoreRankCorrelation = pc.correlation(rankRankCorrelation, powerRank);
    meritocracyScoreBeliefCorrelation = pc.correlation(rankBeliefCorrelation, powerRank);
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
      shuffleFisherYates(alternativeIndexArray);
      for (int choice : alternativeIndexArray) {
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
      double threshold = r.nextDouble();
      double cumulative = 0;
      for (int member : memberIndexArray) {
        // @Does the loop order matter, really?
        cumulative += power[member];
        if (cumulative > threshold) {
          decision = individualDecision[member];
          break;
        }
      }
      return decision;
    }
  }

  class PluralityVoting implements DecisionRule {

    @Override
    public int getId() {
      return 2;
    }

    @Override
    public int decide() {
      int decision = -1;
      double maxCount = Double.MIN_VALUE;
      double[] countVote = new double[n];
      for (int member : memberIndexArray) {
        countVote[individualDecision[member]] += power[member];
      }
      shuffleFisherYates(alternativeIndexArray);
      for (int choice : alternativeIndexArray) {
        if (countVote[choice] > maxCount) {
          decision = choice;
          maxCount = countVote[choice];
        }
      }
      return decision;
    }
  }


  class TwoStageVoting implements DecisionRule {

    @Override
    public int getId() {
      return 3;
    }

    @Override
    public int decide() {
      int decision;
      int bestChoice = -1;
      int secondBestChoice = -1;
      double maxCount = Double.MIN_VALUE;
      double secondMaxCount = Double.MIN_VALUE;
      // First Round
      double[] countVote = new double[n];
      for (int member : memberIndexArray) {
        countVote[individualDecision[member]] += power[member];
      }
      shuffleFisherYates(alternativeIndexArray);
      for (int alternative : alternativeIndexArray) {
        if (countVote[alternative] > maxCount) {
          secondBestChoice = bestChoice;
          secondMaxCount = maxCount;
          bestChoice = alternative;
          maxCount = countVote[alternative];
        } else if (countVote[alternative] > secondMaxCount) {
          secondBestChoice = alternative;
          secondMaxCount = countVote[alternative];
        }
      }
      // Secound Round
      countVote = new double[2];
      for (int member : memberIndexArray) {
        if (belief[member][bestChoice] > belief[member][secondBestChoice]) {
          countVote[0] += power[member];
        } else if (belief[member][bestChoice] < belief[member][secondBestChoice]) {
          countVote[1] += power[member];
        } else {
          if (r.nextBoolean()) {
            countVote[0] += power[member];
          } else {
            countVote[1] += power[member];
          }
        }
      }
      // Finalizing Org Decision
      if (countVote[0] > countVote[1]) {
        decision = bestChoice;
      } else if (countVote[0] < countVote[1]) {
        decision = secondBestChoice;
      } else {
        decision = r.nextBoolean() ? bestChoice : secondBestChoice;
      }
      return decision;
    }

  }

  class AverageBelief implements DecisionRule {

    @Override
    public int getId() {
      return 4;
    }

    @Override
    public int decide() {
      double[] averageBelief = new double[n];
      for (int alternative : alternativeIndexArray) {
        for (int member : memberIndexArray) {
          averageBelief[alternative] += belief[member][alternative];
        }
        averageBelief[alternative] /= m;
      }
      return transformBelief2Decision(averageBelief);
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

  double[] getRank(double[] arr) {
    double[] rank = new double[arr.length];
    for (int focal = 0; focal < arr.length; focal++) {
      double rankOf = 0;  // Start rank from 0
      for (int target = 0; target < arr.length; target++) {
//        if (arr[target] < arr[focal] || (arr[target] == arr[focal] && target < focal)) {
//          rankOf++;
//        }
        if (arr[target] < arr[focal]) {
          rankOf++;
        } else if (arr[target] == arr[focal]) {
          rankOf += .5;
        }
      }
      rank[focal] = rankOf;
    }
    return rank;
  }

  double getAverage(double[] arr) {
    double average = 0;
    for (double value : arr) {
      average += value;
    }
    return average / (double) arr.length;
  }

}