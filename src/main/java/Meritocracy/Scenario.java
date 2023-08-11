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
  double[] power;
  double[] pSuccess; // 6 arms; 123 low 456 high success rate

  int[] individualDecision;
  int organizationalDecision;
  boolean[] isSuccessfulIndividual;
  boolean isSuccessfulOrganization;

  double[][] falsePositive;
  double[][] falseNegative;

  int[] armIndexArray;
  int[] memberIndexArray;

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
    initializeTaskEnvironment();
    initializeIndividual();
  }

  void setDecisionRule(int decisionRuleIndex) {
    switch (decisionRuleIndex) {
      case 0 -> decisoinRule = new UnitaryActor();
      case 1 -> decisoinRule = new PluralityVoting();
      case 2 -> decisoinRule = new WeightedVoting();
      case 3 -> decisoinRule = new TwoStagePluralityVoting();
      case 4 -> decisoinRule = new TwoStageWeightedVoting();
    }
  }

  void initializeIDArrays(){
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

  void initializeTaskEnvironment() {
    pSuccess = new double[n];
    for (int p = 0; p < n; p++) {
      pSuccess[p] = r.nextDouble();
    }
  }

  void initializeIndividual() {
    beliefNumerator = new double[m][n];
    beliefDenominator = new double[m][n];
    belief = new double[m][n];
    power = new double[m];
    double powerSum = 0;

    for( int member : memberIndexArray ){
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
          if (r.nextDouble() < pSuccess[choice]) {
            beliefNumeratorIndividual[choice]++;
          }
          beliefDenominatorIndividual[choice]++;
          belief[member][choice] = beliefNumeratorIndividual[choice] / beliefDenominatorIndividual[choice];
        }
      } else {
        for (int prejoin = 0; prejoin < g; prejoin++) {
          int choice = r.nextInt(n);
          if (r.nextDouble() < pSuccess[choice]) {
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
    for( int member : memberIndexArray ){
      power[member] /= powerSum;
    }
    setIndividualDecision();
    setOrganizationalDecision();
    setSuccess();
  }

  void stepForward() {
    setIndividualDecision();
    setOrganizationalDecision();
    doIndividualLearning();
  }

  void setIndividualDecision() {
    for (int individual : memberIndexArray) {
      individualDecision[individual] = transformBelief2Decision(belief[individual]);
      isSuccessfulIndividual[individual] = r.nextDouble() < pSuccess[individualDecision[individual]];
    }
  }

  void setOrganizationalDecision() {
    organizationalDecision = decisoinRule.decide();
    isSuccessfulOrganization = r.nextDouble() < pSuccess[organizationalDecision];
  }

  void setOutcome() {
    falsePositive = new double[m][n];
    falseNegative = new double[m][n];

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

  void shuffleFisherYates(int[] nArray) {
    for (int i = nArray.length - 1; i > 0; i--) {
      int j = r.nextInt(i + 1);
      int temp = nArray[i];
      nArray[i] = nArray[j];
      nArray[j] = temp;
    }
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

    ;

    default void setWeightType1(double weightType1) {
    }

    ;

  }

  class UnitaryActor implements DecisionRule {

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

  class PluralityVoting implements DecisionRule {

    // each individual transforms these beliefs in a
    // binary manner such that the alternative with the high-
    // est belief is one and the others are set to zero.  In this
    // decision-making structure, individual inclusion is bal-
    // anced, so each individual has equal preference in the or-
    // ganizational decision. The output of decision making
    // results when the organization sums the individuals’
    // messages on each alternative and chooses the alterna-
    // tive with the highest value.
    @Override
    public int getId() {
      return 1;
    }

    @Override
    public int decide() {
      int decision = -1;
      double maxCount = Double.MIN_VALUE;
      double[] countMessage = new double[n];
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

  class WeightedVoting implements DecisionRule {
    // average beliefs decision-making structure functions as
    // plurality voting does but without any message trans-
    // formation.
//    double weightType1;

    @Override
    public int getId() {
      return 2;
    }

    @Override
    public int decide() {
      int decision = -1;
      double maxCount = Double.MIN_VALUE;
      double[] countMessage = new double[n];
      for (int individual : memberIndexArray) {
        if (typeOf[individual] == 0) {
          countMessage[individualDecision[individual]] += m1;
        } else {
          countMessage[individualDecision[individual]] += m0;
        }
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

//    @Override
//    public void setWeightType1(double weightType1){
//      this.weightType1 = weightType1;
//    }
  }

  class TwoStagePluralityVoting implements DecisionRule {
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

//    @Override
//    public void setWeightType1(double weightType1){
//      this.weightType1 = weightType1;
//    }
  }

//
//  class EqualRepresentation implements DecisionRule {
//    // average beliefs decision-making structure functions as
//    // plurality voting does but without any message trans-
//    // formation.
//    @Override
//    public int getId() {
//      return 3;
//    }
//
//    @Override
//    public int decide() {
//      int decision = -1;
//      double maxCount = Double.MIN_VALUE;
//      double[] countMessage = new double[FTn];
//      for (int individual : memberIndexArray) {
//        if( typeOf[individual] == 0 ){
//          countMessage[individualDecision[individual]] += m1;
//        }else{
//          countMessage[individualDecision[individual]] += m0;
//        }
//      }
//      shuffleFisherYates(armIndexArray);
//      for (int choice : armIndexArray) {
//        if (countMessage[choice] > maxCount) {
//          decision = choice;
//          maxCount = countMessage[choice];
//        }
//      }
//      return decision;
//    }
//  }
//
//  class RotatingDictatorShip implements DecisionRule {
//    // Finally, rotating dictatorship is like plurali-
//    // ty voting but with highly unbalanced individual
//    // inclusion; in each period, one randomly selected indi-
//    // vidual is assigned a preference of one and all other indi-
//    // viduals are weighted zero.
//    @Override
//    public int getId() {
//      return 4;
//    }
//
//    @Override
//    public int decide() {
//      int dictator = r.nextInt(m);
//      return individualDecision[dictator];
//    }
//  }
//
//  class WeightedDictatorship implements DecisionRule {
//    double weightType0;
//    double weightType1;
//
//    @Override
//    public int getId() {
//      return 5;
//    }
//
//    @Override
//    public int decide() {
//      int dictator;
//      // Men v. Women
//      double p = weightType0/(weightType0+weightType1);
//      if( r.nextDouble() < p ){
//        dictator = r.nextInt(m0);
//      }else{
//        dictator = m0 + r.nextInt(m1);
//      }
//      return individualDecision[dictator];
//    }
//
//    @Override
//    public void setWeightType0(double weightType0){
//      this.weightType0 = weightType0;
//    }
//
//    @Override
//    public void setWeightType1(double weightType1){
//      this.weightType1 = weightType1;
//    }
//
//  }
//
//  class EqualDictatorship implements DecisionRule {
//    @Override
//    public int getId() {
//      return 6;
//    }
//
//    @Override
//    public int decide() {
//      int dictator;
//      // Men v. Women
//      if( r.nextBoolean() ){
//        dictator = r.nextInt(m0);
//      }else{
//        dictator = m0 + r.nextInt(m1);
//      }
//      return individualDecision[dictator];
//    }
//  }
//
//  class AverageBelief implements DecisionRule {
//    // average beliefs decision-making structure functions as
//    // plurality voting does but without any message trans-
//    // formation.
//    @Override
//    public int getId() {
//      return -999;
//    }
//
//    @Override
//    public int decide() {
//      double[] averageBelief = new double[FTn];
//      double[] averageUtility = new double[FTn];
//      for (int choice : armIndexArray) {
//        for (int individual : memberIndexArray) {
//          averageBelief[choice] += belief[individual][choice];
//          int kind = typeOf[individual];
//          for (int dimension = 0; dimension < 2; dimension++) {
//            averageUtility[choice] += utility[kind][choice];
//          }
//        }
//        averageBelief[choice] /= m;
//        averageUtility[choice] /= m;
//      }
//      return transformBelief2Message(averageBelief, averageUtility);
//    }
//  }
//
//  class TwoStageVoting implements DecisionRule {
//    // Two-stage voting is
//    // similar to plurality voting, but the input and output of
//    // decision making occurs twice: the full set of alterna-
//    // tives is considered in the first round, and the top two
//    // alternatives are considered in the second round.
//    @Override
//    public int getId() {
//      return -999;
//    }
//
//    @Override
//    public int decide() {
//      int decision;
//      int bestChoice = -1;
//      int secondBestChoice = -1;
//      int maxCount = Integer.MIN_VALUE;
//      int secondMaxCount = Integer.MIN_VALUE;
//      // First Round
//      int[] countMessage = new int[FTn];
//      for (int individual : memberIndexArray) {
//        countMessage[individualDecision[individual]]++;
//      }
//      shuffleFisherYates(armIndexArray);
//      for (int choice : armIndexArray) {
//        if (countMessage[choice] > maxCount) {
//          secondBestChoice = bestChoice;
//          secondMaxCount = maxCount;
//          bestChoice = choice;
//          maxCount = countMessage[choice];
//        } else if (countMessage[choice] > secondMaxCount) {
//          secondBestChoice = choice;
//          secondMaxCount = countMessage[choice];
//        }
//      }
//      // Secound Round
//      countMessage = new int[2];
//      for (int individual : memberIndexArray) {
//        double first = belief[individual][bestChoice] * utility[typeOf[individual]][bestChoice];
//        double second =
//                belief[individual][secondBestChoice] * utility[typeOf[individual]][secondBestChoice];
//        if (first > second) {
//          countMessage[0]++;
//        } else if (first < second) {
//          countMessage[1]++;
//        }
//      }
//      if (countMessage[0] > countMessage[1]) {
//        decision = bestChoice;
//      } else if (countMessage[0] < countMessage[1]) {
//        decision = secondBestChoice;
//      } else {
//        decision = r.nextBoolean() ? bestChoice : secondBestChoice;
//      }
//      return decision;
//    }
//  }

}