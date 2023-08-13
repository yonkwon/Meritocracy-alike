package Meritocracy;

import static org.apache.commons.math3.util.FastMath.pow;

import com.google.common.util.concurrent.AtomicDouble;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Computation {

  ExecutorService workStealingPool;

  AtomicDouble[][] averageCompetenceBestMatchingAVGAtomic;
  AtomicDouble[][] averageCompetenceBestMatchingSTDAtomic;
  AtomicDouble[][] averageCompetenceRankCorrelationAVGAtomic;
  AtomicDouble[][] averageCompetenceRankCorrelationSTDAtomic;
  AtomicDouble[][] averageCompetenceBeliefCorrelationAVGAtomic;
  AtomicDouble[][] averageCompetenceBeliefCorrelationSTDAtomic;

  AtomicDouble[][] meritocracyScoreBestMatchingAVGAtomic;
  AtomicDouble[][] meritocracyScoreBestMatchingSTDAtomic;
  AtomicDouble[][] meritocracyScoreRankCorrelationAVGAtomic;
  AtomicDouble[][] meritocracyScoreRankCorrelationSTDAtomic;
  AtomicDouble[][] meritocracyScoreBeliefCorrelationAVGAtomic;
  AtomicDouble[][] meritocracyScoreBeliefCorrelationSTDAtomic;

  double[][] averageCompetenceBestMatchingAVG;
  double[][] averageCompetenceBestMatchingSTD;
  double[][] averageCompetenceRankCorrelationAVG;
  double[][] averageCompetenceRankCorrelationSTD;
  double[][] averageCompetenceBeliefCorrelationAVG;
  double[][] averageCompetenceBeliefCorrelationSTD;

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
    averageCompetenceFullExperiment();
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
    averageCompetenceBestMatchingAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageCompetenceBestMatchingSTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageCompetenceRankCorrelationAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageCompetenceRankCorrelationSTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageCompetenceBeliefCorrelationAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageCompetenceBeliefCorrelationSTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];

    meritocracyScoreBestMatchingAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreBestMatchingSTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreRankCorrelationAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreRankCorrelationSTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreBeliefCorrelationAVGAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreBeliefCorrelationSTDAtomic = new AtomicDouble[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];

    for (int dr = 0; dr < Main.DECISION_STRUCTURE_LENGTH; dr++) {
      for (int t = 0; t < Main.TIME; t++) {
        averageCompetenceBestMatchingAVGAtomic[dr][t] = new AtomicDouble();
        averageCompetenceBestMatchingSTDAtomic[dr][t] = new AtomicDouble();
        averageCompetenceRankCorrelationAVGAtomic[dr][t] = new AtomicDouble();
        averageCompetenceRankCorrelationSTDAtomic[dr][t] = new AtomicDouble();
        averageCompetenceBeliefCorrelationAVGAtomic[dr][t] = new AtomicDouble();
        averageCompetenceBeliefCorrelationSTDAtomic[dr][t] = new AtomicDouble();
        meritocracyScoreBestMatchingAVGAtomic[dr][t] = new AtomicDouble();
        meritocracyScoreBestMatchingSTDAtomic[dr][t] = new AtomicDouble();
        meritocracyScoreRankCorrelationAVGAtomic[dr][t] = new AtomicDouble();
        meritocracyScoreRankCorrelationSTDAtomic[dr][t] = new AtomicDouble();
        meritocracyScoreBeliefCorrelationAVGAtomic[dr][t] = new AtomicDouble();
        meritocracyScoreBeliefCorrelationSTDAtomic[dr][t] = new AtomicDouble();
      }
    }

    averageCompetenceBestMatchingAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageCompetenceBestMatchingSTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageCompetenceRankCorrelationAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageCompetenceRankCorrelationSTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageCompetenceBeliefCorrelationAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    averageCompetenceBeliefCorrelationSTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];

    meritocracyScoreBestMatchingAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreBestMatchingSTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreRankCorrelationAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreRankCorrelationSTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreBeliefCorrelationAVG = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
    meritocracyScoreBeliefCorrelationSTD = new double[Main.DECISION_STRUCTURE_LENGTH][Main.TIME];
  }

  private void averageCompetenceFullExperiment() {
    for (int dr = 0; dr < Main.DECISION_STRUCTURE_LENGTH; dr++) {
      for (int t = 0; t < Main.TIME; t++) {
        averageCompetenceBestMatchingAVG[dr][t] = averageCompetenceBestMatchingAVGAtomic[dr][t].get() / Main.ITERATION;
        averageCompetenceBestMatchingSTD[dr][t] = averageCompetenceBestMatchingSTDAtomic[dr][t].get() / Main.ITERATION;
        averageCompetenceBestMatchingSTD[dr][t] = pow(averageCompetenceBestMatchingSTD[dr][t] - pow(averageCompetenceBestMatchingAVG[dr][t], 2), .5);
        averageCompetenceRankCorrelationAVG[dr][t] = averageCompetenceRankCorrelationAVGAtomic[dr][t].get() / Main.ITERATION;
        averageCompetenceRankCorrelationSTD[dr][t] = averageCompetenceRankCorrelationSTDAtomic[dr][t].get() / Main.ITERATION;
        averageCompetenceRankCorrelationSTD[dr][t] = pow(averageCompetenceRankCorrelationSTD[dr][t] - pow(averageCompetenceRankCorrelationAVG[dr][t], 2), .5);
        averageCompetenceBeliefCorrelationAVG[dr][t] = averageCompetenceBeliefCorrelationAVGAtomic[dr][t].get() / Main.ITERATION;
        averageCompetenceBeliefCorrelationSTD[dr][t] = averageCompetenceBeliefCorrelationSTDAtomic[dr][t].get() / Main.ITERATION;
        averageCompetenceBeliefCorrelationSTD[dr][t] = pow(averageCompetenceBeliefCorrelationSTD[dr][t] - pow(averageCompetenceBeliefCorrelationAVG[dr][t], 2), .5);
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

    AtomicDouble[] averageCompetenceBestMatchingAVGAtomicPart;
    AtomicDouble[] averageCompetenceBestMatchingSTDAtomicPart;
    AtomicDouble[] averageCompetenceRankCorrelationAVGAtomicPart;
    AtomicDouble[] averageCompetenceRankCorrelationSTDAtomicPart;
    AtomicDouble[] averageCompetenceBeliefCorrelationAVGAtomicPart;
    AtomicDouble[] averageCompetenceBeliefCorrelationSTDAtomicPart;

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
      averageCompetenceBestMatchingAVGAtomicPart = averageCompetenceBestMatchingAVGAtomic[decisionRuleIndex];
      averageCompetenceBestMatchingSTDAtomicPart = averageCompetenceBestMatchingSTDAtomic[decisionRuleIndex];
      averageCompetenceRankCorrelationAVGAtomicPart = averageCompetenceRankCorrelationAVGAtomic[decisionRuleIndex];
      averageCompetenceRankCorrelationSTDAtomicPart = averageCompetenceRankCorrelationSTDAtomic[decisionRuleIndex];
      averageCompetenceBeliefCorrelationAVGAtomicPart = averageCompetenceBeliefCorrelationAVGAtomic[decisionRuleIndex];
      averageCompetenceBeliefCorrelationSTDAtomicPart = averageCompetenceBeliefCorrelationSTDAtomic[decisionRuleIndex];
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
          averageCompetenceBestMatchingAVGAtomicPart[t].addAndGet(s.averageCompetenceBestMatching);
          averageCompetenceBestMatchingSTDAtomicPart[t].addAndGet(pow(s.averageCompetenceBestMatching, 2));
          averageCompetenceRankCorrelationAVGAtomicPart[t].addAndGet(s.averageCompetenceRankCorrelation);
          averageCompetenceRankCorrelationSTDAtomicPart[t].addAndGet(pow(s.averageCompetenceRankCorrelation, 2));
          averageCompetenceBeliefCorrelationAVGAtomicPart[t].addAndGet(s.averageCompetenceBeliefCorrelation);
          averageCompetenceBeliefCorrelationSTDAtomicPart[t].addAndGet(pow(s.averageCompetenceBeliefCorrelation, 2));

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