package Meritocracy;

import static org.apache.commons.math3.util.FastMath.pow;

import com.google.common.util.concurrent.AtomicDouble;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Computation {

  ExecutorService workStealingPool;

  AtomicDouble[][] averageBestMatchingAVGAtomic;
  AtomicDouble[][] averageBestMatchingSTDAtomic;
  AtomicDouble[][] averageRankCorrelationAVGAtomic;
  AtomicDouble[][] averageRankCorrelationSTDAtomic;
  AtomicDouble[][] averageBeliefCorrelationAVGAtomic;
  AtomicDouble[][] averageBeliefCorrelationSTDAtomic;

  AtomicDouble[][] meritocracyScoreBestMatchingAVGAtomic;
  AtomicDouble[][] meritocracyScoreBestMatchingSTDAtomic;
  AtomicDouble[][] meritocracyScoreRankCorrelationAVGAtomic;
  AtomicDouble[][] meritocracyScoreRankCorrelationSTDAtomic;
  AtomicDouble[][] meritocracyScoreBeliefCorrelationAVGAtomic;
  AtomicDouble[][] meritocracyScoreBeliefCorrelationSTDAtomic;

  double[][] averageBestMatchingAVG;
  double[][] averageBestMatchingSTD;
  double[][] averageRankCorrelationAVG;
  double[][] averageRankCorrelationSTD;
  double[][] averageBeliefCorrelationAVG;
  double[][] averageBeliefCorrelationSTD;

  double[][] meritocracyScoreBestMatchingAVG;
  double[][] meritocracyScoreBestMatchingSTD;
  double[][] meritocracyScoreRankCorrelationAVG;
  double[][] meritocracyScoreRankCorrelationSTD;
  double[][] meritocracyScoreBeliefCorrelationAVG;
  double[][] meritocracyScoreBeliefCorrelationSTD;

  ProgressBar pb;

  Computation() {
    workStealingPool = Executors.newWorkStealingPool();
    pb = new ProgressBar("Full Experiment: Computation", Main.ITERATION);

    setResultSpace();
    runFullExperiment();
    averageFullExperiment();
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
    averageBestMatchingAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageBestMatchingSTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageRankCorrelationAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageRankCorrelationSTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageBeliefCorrelationAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageBeliefCorrelationSTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];

    meritocracyScoreBestMatchingAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreBestMatchingSTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreRankCorrelationAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreRankCorrelationSTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreBeliefCorrelationAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreBeliefCorrelationSTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];

    for (int dr = 0; dr < Main.DECISION_STRUCTURE_LENGTH; dr++) {
      for (int t = 0; t < Main.TIME; t++) {
        averageBestMatchingAVGAtomic[dr][t] = new AtomicDouble();
        averageBestMatchingSTDAtomic[dr][t] = new AtomicDouble();
        averageRankCorrelationAVGAtomic[dr][t] = new AtomicDouble();
        averageRankCorrelationSTDAtomic[dr][t] = new AtomicDouble();
        averageBeliefCorrelationAVGAtomic[dr][t] = new AtomicDouble();
        averageBeliefCorrelationSTDAtomic[dr][t] = new AtomicDouble();
        meritocracyScoreBestMatchingAVGAtomic[dr][t] = new AtomicDouble();
        meritocracyScoreBestMatchingSTDAtomic[dr][t] = new AtomicDouble();
        meritocracyScoreRankCorrelationAVGAtomic[dr][t] = new AtomicDouble();
        meritocracyScoreRankCorrelationSTDAtomic[dr][t] = new AtomicDouble();
        meritocracyScoreBeliefCorrelationAVGAtomic[dr][t] = new AtomicDouble();
        meritocracyScoreBeliefCorrelationSTDAtomic[dr][t] = new AtomicDouble();
      }
    }

    averageBestMatchingAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageBestMatchingSTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageRankCorrelationAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageRankCorrelationSTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageBeliefCorrelationAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageBeliefCorrelationSTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];

    meritocracyScoreBestMatchingAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreBestMatchingSTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreRankCorrelationAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreRankCorrelationSTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreBeliefCorrelationAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreBeliefCorrelationSTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
  }

  private void averageFullExperiment() {
    for (int dr = 0; dr < Main.DECISION_STRUCTURE_LENGTH; dr++) {
      for (int t = 0; t < Main.TIME; t++) {
        averageBestMatchingAVG[dr][t] = averageBestMatchingAVGAtomic[dr][t].get() / Main.ITERATION;
        averageBestMatchingSTD[dr][t] = averageBestMatchingSTDAtomic[dr][t].get() / Main.ITERATION;
        averageBestMatchingSTD[dr][t] = pow(averageBestMatchingSTD[dr][t] - pow(averageBestMatchingAVG[dr][t], 2), .5);
        averageRankCorrelationAVG[dr][t] = averageRankCorrelationAVGAtomic[dr][t].get() / Main.ITERATION;
        averageRankCorrelationSTD[dr][t] = averageRankCorrelationSTDAtomic[dr][t].get() / Main.ITERATION;
        averageRankCorrelationSTD[dr][t] = pow(averageRankCorrelationSTD[dr][t] - pow(averageRankCorrelationAVG[dr][t], 2), .5);
        averageBeliefCorrelationAVG[dr][t] = averageBeliefCorrelationAVGAtomic[dr][t].get() / Main.ITERATION;
        averageBeliefCorrelationSTD[dr][t] = averageBeliefCorrelationSTDAtomic[dr][t].get() / Main.ITERATION;
        averageBeliefCorrelationSTD[dr][t] = pow(averageBeliefCorrelationSTD[dr][t] - pow(averageBeliefCorrelationAVG[dr][t], 2), .5);
        meritocracyScoreBestMatchingAVG[dr][t] = meritocracyScoreBestMatchingAVGAtomic[dr][t].get() / Main.ITERATION;
        meritocracyScoreBestMatchingSTD[dr][t] = meritocracyScoreBestMatchingSTDAtomic[dr][t].get() / Main.ITERATION;
        meritocracyScoreBestMatchingSTD[dr][t] = pow(meritocracyScoreBestMatchingSTD[dr][t] - pow(meritocracyScoreBestMatchingAVG[dr][t], 2), .5);
        meritocracyScoreRankCorrelationAVG[dr][t] = meritocracyScoreRankCorrelationAVGAtomic[dr][t].get() / Main.ITERATION;
        meritocracyScoreRankCorrelationSTD[dr][t] = meritocracyScoreRankCorrelationSTDAtomic[dr][t].get() / Main.ITERATION;
        meritocracyScoreRankCorrelationSTD[dr][t] = pow(meritocracyScoreRankCorrelationSTD[dr][t] - pow(meritocracyScoreRankCorrelationAVG[dr][t], 2), .5);
        meritocracyScoreBeliefCorrelationAVG[dr][t] = meritocracyScoreBeliefCorrelationAVGAtomic[dr][t].get() / Main.ITERATION;
        meritocracyScoreBeliefCorrelationSTD[dr][t] = meritocracyScoreBeliefCorrelationSTDAtomic[dr][t].get() / Main.ITERATION;
        meritocracyScoreBeliefCorrelationSTD[dr][t] = pow(meritocracyScoreBeliefCorrelationSTD[dr][t] - pow(meritocracyScoreBeliefCorrelationAVG[dr][t], 2), .5);
      }
    }
  }

  class experimentWrapper implements Runnable {

    experimentWrapper() {
    }

    @Override
    public void run() {
      for (int dr = 0; dr < Main.DECISION_STRUCTURE_LENGTH; dr++) {
        new runScenario(dr);
      }
      pb.stepNext();
    }
  }

  class runScenario {

    int decisionRuleIndex;
    int n; // Number of alternatives
    int m; // Number of individuals
    int g; // Length of prejoin experience

    AtomicDouble[] averageBestMatchingAVGAtomicPart;
    AtomicDouble[] averageBestMatchingSTDAtomicPart;
    AtomicDouble[] averageRankCorrelationAVGAtomicPart;
    AtomicDouble[] averageRankCorrelationSTDAtomicPart;
    AtomicDouble[] averageBeliefCorrelationAVGAtomicPart;
    AtomicDouble[] averageBeliefCorrelationSTDAtomicPart;

    AtomicDouble[] meritocracyScoreBestMatchingAVGAtomicPart;
    AtomicDouble[] meritocracyScoreBestMatchingSTDAtomicPart;
    AtomicDouble[] meritocracyScoreRankCorrelationAVGAtomicPart;
    AtomicDouble[] meritocracyScoreRankCorrelationSTDAtomicPart;
    AtomicDouble[] meritocracyScoreBeliefCorrelationAVGAtomicPart;
    AtomicDouble[] meritocracyScoreBeliefCorrelationSTDAtomicPart;

    runScenario(
        int decisionRuleIndex
    ) {
      this.decisionRuleIndex = decisionRuleIndex;
      this.n = Main.N;
      this.m = Main.M;
      this.g = Main.G;
      setResultSpacePart();
      run();
    }

    void setResultSpacePart() {
      averageBestMatchingAVGAtomicPart = averageBestMatchingAVGAtomic[decisionRuleIndex];
      averageBestMatchingSTDAtomicPart = averageBestMatchingSTDAtomic[decisionRuleIndex];
      averageRankCorrelationAVGAtomicPart = averageRankCorrelationAVGAtomic[decisionRuleIndex];
      averageRankCorrelationSTDAtomicPart = averageRankCorrelationSTDAtomic[decisionRuleIndex];
      averageBeliefCorrelationAVGAtomicPart = averageBeliefCorrelationAVGAtomic[decisionRuleIndex];
      averageBeliefCorrelationSTDAtomicPart = averageBeliefCorrelationSTDAtomic[decisionRuleIndex];
      meritocracyScoreBestMatchingAVGAtomicPart = meritocracyScoreBestMatchingAVGAtomic[decisionRuleIndex];
      meritocracyScoreBestMatchingSTDAtomicPart = meritocracyScoreBestMatchingSTDAtomic[decisionRuleIndex];
      meritocracyScoreRankCorrelationAVGAtomicPart = meritocracyScoreRankCorrelationAVGAtomic[decisionRuleIndex];
      meritocracyScoreRankCorrelationSTDAtomicPart = meritocracyScoreRankCorrelationSTDAtomic[decisionRuleIndex];
      meritocracyScoreBeliefCorrelationAVGAtomicPart = meritocracyScoreBeliefCorrelationAVGAtomic[decisionRuleIndex];
      meritocracyScoreBeliefCorrelationSTDAtomicPart = meritocracyScoreBeliefCorrelationSTDAtomic[decisionRuleIndex];
    }

    void run() {
      Scenario s = new Scenario(decisionRuleIndex, n, m, g);
      for (int t = 0; t < Main.TIME; t++) {
        s.setOutcome();
        synchronized (this) {
          averageBestMatchingAVGAtomicPart[t].addAndGet(s.averageCompetenceBestMatching);
          averageBestMatchingSTDAtomicPart[t].addAndGet(pow(s.averageCompetenceBestMatching, 2));
          averageRankCorrelationAVGAtomicPart[t].addAndGet(s.averageCompetenceRankCorrelation);
          averageRankCorrelationSTDAtomicPart[t].addAndGet(pow(s.averageCompetenceRankCorrelation, 2));
          averageBeliefCorrelationAVGAtomicPart[t].addAndGet(s.averageCompetenceBeliefCorrelation);
          averageBeliefCorrelationSTDAtomicPart[t].addAndGet(pow(s.averageCompetenceBeliefCorrelation, 2));

          meritocracyScoreBestMatchingAVGAtomicPart[t].addAndGet(s.meritocracyScoreBestMatching);
          meritocracyScoreBestMatchingSTDAtomicPart[t].addAndGet(pow(s.meritocracyScoreBestMatching, 2));
          meritocracyScoreRankCorrelationAVGAtomicPart[t].addAndGet(s.meritocracyScoreRankCorrelation);
          meritocracyScoreRankCorrelationSTDAtomicPart[t].addAndGet(pow(s.meritocracyScoreRankCorrelation, 2));
          meritocracyScoreBeliefCorrelationAVGAtomicPart[t].addAndGet(s.meritocracyScoreBeliefCorrelation);
          meritocracyScoreBeliefCorrelationSTDAtomicPart[t].addAndGet(pow(s.meritocracyScoreBeliefCorrelation, 2));
        }
        s.stepForward();
      }
    }
  }
}