package Meritocracy;

import static org.apache.commons.math3.util.FastMath.exp;
import static org.apache.commons.math3.util.FastMath.max;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

class Scenario {

  RandomGenerator r;

  double payoffProbability;
  double utilityDifference;
  int decisionRuleIndex;
  DecisionRule dr;
  int m; // Number of individuals
  int m0; // Number of individuals in type 0
  int m1; // Number of individuals in type 1
  int g0;
  int g1;

  double[][] beliefNumerator; //230731 Change into double to accommodate Henning's Request. It might be computationally costly.
  double[][] beliefDenominator;
  double[][] belief;
  double[][] belief0;
  double[][] expectedUtility;

  int[] typeOf; // n;

  double[] armPSuccess; // 6 arms; 123 low 456 high success rate
  double[][] armValueDimension; // 6 arms by 2 dimensions; 123 low 456 high success rate.
  int[] armFavor; // 6 arms; which group it favors
  double[][] utility; // individual's utility from each arm; k, n

  int winningCoalitionDecision; // Most popular
  int organizationalDecision; // By decision structure
  int winningCoalitionNVoteByType0;
  int winningCoalitionNVoteByType1;
  int[] individualDecision;
  boolean isSuccessful;

  boolean isConsensus;
  boolean isConsensusType0;
  boolean isConsensusType1;
  boolean isPerfect;
  boolean isPerfectType0;
  boolean isPerfectType1;
  boolean isHomogeneousCoalition;
  boolean isHomogenousCoalitionType0;
  boolean isHomogenousCoalitionType1;

  int nVoteAgainstPreferenceType0;
  int nVoteAgainstPreferenceType1;

  double[][] falsePositive;
  double[][] falseNegative;

  int[] armIndexArray;
  int[] memberIndexArray;

  boolean[] isAssimilated;
  boolean[] wasAssimilated;

  double vote2Win00;
  double vote2Win01;
  double vote2Win10;
  double vote2Win11;

  double averageUtility;

  Scenario(
      double payoffProbability,
      double utilityDifference,
      int decisionRuleIndex,
      int m0,
      int m1,
      int g0,
      int g1) {
    r = new MersenneTwister();

    this.payoffProbability = payoffProbability;
    this.utilityDifference = utilityDifference;
    this.decisionRuleIndex = decisionRuleIndex;
    this.m0 = m0;
    this.m1 = m1;
    this.m = m0 + m1;
    this.g0 = g0;
    this.g1 = g1;

    setDecisionRule(decisionRuleIndex);

    individualDecision = new int[m];
    armIndexArray = new int[Main.N];
    for (int choice = 0; choice < Main.N; choice++) {
      armIndexArray[choice] = choice;
    }
    memberIndexArray = new int[m];
    for (int member = 0; member < m; member++) {
      memberIndexArray[member] = member;
    }

    initializeTaskEnvironment();
    initializePreference();
    initializeBelief();
  }

  void setDecisionRule(int decisionRuleIndex) {
    switch (decisionRuleIndex) {
      case 0 -> dr = new UnitaryActor();
      case 1 -> dr = new PluralityVoting();
      case 2 -> dr = new WeightedVoting();
      case 3 -> dr = new TwoStagePluralityVoting();
      case 4 -> dr = new TwoStageWeightedVoting();
//      case 3 -> dr = new EqualRepresentation();
//      case 4 -> dr = new RotatingDictatorShip();
//      case 5 -> dr = new WeightedDictatorship();
//      case 6 -> dr = new EqualDictatorship();
    }
  }

  void initializeTaskEnvironment() {
    armPSuccess = new double[Main.N];
    armValueDimension = new double[Main.N][2];
    armFavor = new int[Main.N];

    for (int p = 0; p < Main.N; p++) {
      armPSuccess[p] = payoffProbability;
    }

    armFavor[0] = 0; // Male
    armFavor[1] = 1; // Female
  }

  void initializePreference() {
    typeOf = new int[m];
    utility = new double[2][Main.N];

    for (int individual = 0; individual < m0; individual++) {
      typeOf[individual] = 0;
    }
    for (int individual = m0; individual < m; individual++) {
      typeOf[individual] = 1;
    }

    double utilMin = (1D - utilityDifference) / 2;
    double utilMax = utilMin + utilityDifference;

    if (Main.IS_DU1) {
      utilMin = 1D - utilityDifference / 2D;
      utilMax = utilMin + utilityDifference;
    }

    for (int choice : armIndexArray) {
      switch (armFavor[choice]) {
        case 0:
          utility[0][choice] = utilMax;
          utility[1][choice] = utilMin;
          break;
        case 1:
          utility[0][choice] = utilMin;
          utility[1][choice] = utilMax;
          break;
      }
    }
  }

  void initializeBelief() {
    // These initial priors are devel-
    // oped in a prejoin learning phase of g periods. Each
    // individual starts the prejoin phase with uninformed
    // beliefs in the sense that each alternative is believed to
    // have a payoff probability of 0.5. They then engage in
    // individual learning about the alternatives for the du-
    // ration of the prejoin phase by sampling alternatives in
    // a highly exploratory manner and receiving feedback
    // on their choices. If the prejoin period is of length g
    // 0, then individuals’ initial beliefs at the start of
    // organizational decision making are identical and un-
    // informed.
    beliefNumerator = new double[m][Main.N];
    beliefDenominator = new double[m][Main.N];
    belief = new double[m][Main.N];
    expectedUtility = new double[m][Main.N];
    belief0 = new double[m][];
    isAssimilated = new boolean[m];
    wasAssimilated = new boolean[m];

    for (int individual : memberIndexArray) {
      double[] beliefNumeratorIndividual = beliefNumerator[individual];
      double[] beliefDenominatorIndividual = beliefDenominator[individual];
      int prejoinExperience = typeOf[individual]==0?g0:g1;
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
        for( int choice : armIndexArray ){
          if (beliefDenominatorIndividual[choice] != 0) {
            belief[individual][choice] = beliefNumeratorIndividual[choice] / beliefDenominatorIndividual[choice];
          } else {
            belief[individual][choice] = .5D;
          }
        }
        for (int prejoin = 0; prejoin < prejoinExperience; prejoin++) {
          int choice = transformBelief2Message(belief[individual], utility[typeOf[individual]]);
          if (r.nextDouble() < armPSuccess[choice]) {
            beliefNumeratorIndividual[choice]++;
          }
          beliefDenominatorIndividual[choice]++;
          belief[individual][choice] = beliefNumeratorIndividual[choice] / beliefDenominatorIndividual[choice];
        }
      } else {
        for (int prejoin = 0; prejoin < prejoinExperience; prejoin++) {
          int choice = r.nextInt(Main.N);
          if (r.nextDouble() < armPSuccess[choice]) {
            beliefNumeratorIndividual[choice]++;
          }
          beliefDenominatorIndividual[choice]++;
        }
        for (int choice : armIndexArray) {
          if (beliefDenominatorIndividual[choice] != 0) {
            belief[individual][choice] = beliefNumeratorIndividual[choice] / beliefDenominatorIndividual[choice];
          } else {
            belief[individual][choice] = .5D;
          }
        }
      }
      belief0[individual] = belief[individual].clone();
    }

    setIndividualDecision();
    setOrganizationalDecision();
    setSuccess();
    setIsAssimilated();
  }

  void stepForward() {
    setIndividualDecision();
    setOrganizationalDecision();
    setSuccess();
    if (Main.IS_LEARNING) {
      doIndividualLearning();
    }
  }

  void setIndividualDecision() {
    for (int individual : memberIndexArray) {
      individualDecision[individual] = transformBelief2Message(belief[individual], utility[typeOf[individual]]);
    }
  }

  void setIsAssimilated() {
    nVoteAgainstPreferenceType0 = 0;
    nVoteAgainstPreferenceType1 = 0;

    for (int individual = 0; individual < m; individual++) {
      wasAssimilated[individual] = isAssimilated[individual];
      if (armFavor[individualDecision[individual]] != typeOf[individual]) { // Assimilated
        isAssimilated[individual] = true;
        if (typeOf[individual] == 0) {
          nVoteAgainstPreferenceType0++;
        } else if (typeOf[individual] == 1) {
          nVoteAgainstPreferenceType1++;
        }
      }
    }
  }

  void setOrganizationalDecision() {
    organizationalDecision = dr.decide();
  }

  void setSuccess() {
    isSuccessful = r.nextDouble() < armPSuccess[organizationalDecision];
  }

  void setOutcome() {
    isConsensus = true;
    isConsensusType0 = true;
    isConsensusType1 = true;
    isPerfect = true;
    isPerfectType0 = true;
    isPerfectType1 = true;
    isHomogeneousCoalition = true;
    isHomogenousCoalitionType0 = false;
    isHomogenousCoalitionType1 = false;
    winningCoalitionDecision = organizationalDecision; //@ 220520 Fix
    falsePositive = new double[m][Main.N];
    falseNegative = new double[m][Main.N];
    averageUtility = 0;
    if (isSuccessful) {
      for (int individual : memberIndexArray) {
        averageUtility += utility[typeOf[individual]][organizationalDecision];
      }
      averageUtility /= (double) m;
    }

    setIsAssimilated();

    int winningCoalitionChoiceNVote = 0;
    winningCoalitionNVoteByType0 = 0;
    winningCoalitionNVoteByType1 = 0;
    for (int individual : memberIndexArray) {
      if (individualDecision[individual] == organizationalDecision) {
        winningCoalitionChoiceNVote++;
        if (typeOf[individual] == 0) {
          winningCoalitionNVoteByType0++;
        } else {
          winningCoalitionNVoteByType1++;
        }
      }
    }

    for (int t0 = 0; t0 < m0; t0++) {
      if (individualDecision[t0] != 0) {
        isPerfectType0 = false;
        break;
      }
    }
    for (int t1 = m0; t1 < m; t1++) {
      if (individualDecision[t1] != 1) {
        isPerfectType1 = false;
        break;
      }
    }
    isPerfect = isPerfectType0 & isPerfectType1;

    int voteReference = individualDecision[0];
    int type0VoteReference = individualDecision[0];
    int type1VoteReference = individualDecision[m - 1];
    for (int individual : memberIndexArray) {
      if (individualDecision[individual] != voteReference) {
        isConsensus = false;
        break;
      }
    }
    for (int t0 = 0; t0 < m0; t0++) {
      if (individualDecision[t0] != type0VoteReference) {
        isConsensusType0 = false;
        break;
      }
    }
    for (int t1 = m0; t1 < m; t1++) {
      if (individualDecision[t1] != type1VoteReference) {
        isConsensusType1 = false;
        break;
      }
    }

    if (winningCoalitionChoiceNVote != m) {
      // Homogeneous and Mix defined here; Only when it is not consensus
      int winningCoalitionFirstMemberType = -1;

      for (int individual : memberIndexArray) {
        if (winningCoalitionDecision == individualDecision[individual]) {
          winningCoalitionFirstMemberType = typeOf[individual];
          break;
        }
      }

      shuffleFisherYates(memberIndexArray); // Added 220518
      for (int individual : memberIndexArray) {
        if (individualDecision[individual] == winningCoalitionDecision &&
            typeOf[individual] != winningCoalitionFirstMemberType
        ) {
          isHomogeneousCoalition = false;
          break;
        }
      }
      if (isHomogeneousCoalition) {
        if (winningCoalitionFirstMemberType == 0) {
          isHomogenousCoalitionType0 = true;
        } else if (winningCoalitionFirstMemberType == 1) {
          isHomogenousCoalitionType1 = true;
        }
      }
    }

    for (int individual : memberIndexArray) {
      for (int arm : armIndexArray) {
        double deviation = belief[individual][arm] - armPSuccess[arm];
        if (deviation > 0) {
          falsePositive[individual][arm] = deviation;
        } else {
          falseNegative[individual][arm] = deviation;
        }
      }
    }

    vote2Win00 = 0;
    vote2Win01 = 0;
    vote2Win10 = 0;
    vote2Win11 = 0;
    for (int individual : memberIndexArray) {
      if (typeOf[individual] == 1) {
        // Assumption: Type 1 is minority, female
        if (armFavor[individualDecision[individual]] == 0) {
          if (armFavor[organizationalDecision] == 0) {
            vote2Win00++; // MM
          } else if (armFavor[organizationalDecision] == 1) {
            vote2Win01++; // MF
          }
        } else if (armFavor[individualDecision[individual]] == 1) {
          if (armFavor[organizationalDecision] == 0) {
            vote2Win10++; // FM
          } else if (armFavor[organizationalDecision] == 1) {
            vote2Win11++; // FF
          }
        }
      }
    }

    vote2Win00 /= (double) max(m1, 1);
    vote2Win01 /= (double) max(m1, 1);
    vote2Win10 /= (double) max(m1, 1);
    vote2Win11 /= (double) max(m1, 1);
  }

  void doIndividualLearning() {
    if (isSuccessful) {
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

  int transformBelief2Message(double[] belief, double[] utility) {
    int message = -1;
    if (Main.IS_GREEDY) {
      double bestExpectedValue = Double.MIN_VALUE;
      shuffleFisherYates(armIndexArray);
      for (int choice : armIndexArray) {
        double expectedValue = belief[choice] * utility[choice];
        if (expectedValue > bestExpectedValue) {
          message = choice;
          bestExpectedValue = expectedValue;
        }
      }
      if (message == -1) {
        message = r.nextInt(Main.N);
      }
    } else {
      double[] probability = transformBelief2Probability(belief, utility);
      double random = r.nextDouble();
      for (int choice = 0; choice < Main.N; choice++) {
        if (random <= probability[choice]) {
          message = choice;
          break;
        }
      }
    }
    return message;
  }

  double[] transformBelief2Probability(double[] belief, double[] utility) {
    double[] probability = belief.clone();
    double denominator = 0;
    for (int choice : armIndexArray) {
      probability[choice] = probability[choice] * utility[choice];
      probability[choice] = exp(probability[choice] / Main.TAU);
      denominator += probability[choice];
    }
    probability[0] /= denominator;
    for (int choice = 1; choice < Main.N; choice++) {
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
      int[] countMessage = new int[Main.N];
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
      double[] countMessage = new double[Main.N];
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
      double[] countMessage = new double[Main.N];
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
      double[] countMessageType0 = new double[Main.N];
      double[] countMessageType1 = new double[Main.N];
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
      double[] countMessageType0 = new double[Main.N];
      double[] countMessageType1 = new double[Main.N];
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
//      double[] countMessage = new double[FTMain.N];
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
//      double[] averageBelief = new double[FTMain.N];
//      double[] averageUtility = new double[FTMain.N];
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
//      int[] countMessage = new int[FTMain.N];
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