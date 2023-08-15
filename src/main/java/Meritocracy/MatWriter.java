package Meritocracy;

import java.io.File;
import java.io.IOException;
import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.types.Matrix;
import us.hebi.matlab.mat.types.Sinks;

public class MatWriter {

  MatWriter(Computation c) {

    Matrix averageCompetenceBestMatchingAVGMatrix = Mat5.newMatrix(Main.RESULT_KEY_VALUE);
    Matrix averageCompetenceBestMatchingSTDMatrix = Mat5.newMatrix(Main.RESULT_KEY_VALUE);
    Matrix averageCompetenceRankCorrelationAVGMatrix = Mat5.newMatrix(Main.RESULT_KEY_VALUE);
    Matrix averageCompetenceRankCorrelationSTDMatrix = Mat5.newMatrix(Main.RESULT_KEY_VALUE);
    Matrix averageCompetenceBeliefCorrelationAVGMatrix = Mat5.newMatrix(Main.RESULT_KEY_VALUE);
    Matrix averageCompetenceBeliefCorrelationSTDMatrix = Mat5.newMatrix(Main.RESULT_KEY_VALUE);

    Matrix meritocracyScoreBestMatchingAVGMatrix = Mat5.newMatrix(Main.RESULT_KEY_VALUE);
    Matrix meritocracyScoreBestMatchingSTDMatrix = Mat5.newMatrix(Main.RESULT_KEY_VALUE);
    Matrix meritocracyScoreRankCorrelationAVGMatrix = Mat5.newMatrix(Main.RESULT_KEY_VALUE);
    Matrix meritocracyScoreRankCorrelationSTDMatrix = Mat5.newMatrix(Main.RESULT_KEY_VALUE);
    Matrix meritocracyScoreBeliefCorrelationAVGMatrix = Mat5.newMatrix(Main.RESULT_KEY_VALUE);
    Matrix meritocracyScoreBeliefCorrelationSTDMatrix = Mat5.newMatrix(Main.RESULT_KEY_VALUE);

    for (int dr = 0; dr < Main.DECISION_STRUCTURE_LENGTH; dr++) {
      for (int t = 0; t < Main.TIME; t++) {
        int[] indices = {dr, t};
        averageCompetenceBestMatchingAVGMatrix.setDouble(indices, c.averageCompetenceBestMatchingAVG[dr][t]);
        averageCompetenceBestMatchingSTDMatrix.setDouble(indices, c.averageCompetenceBestMatchingSTD[dr][t]);
        averageCompetenceRankCorrelationAVGMatrix.setDouble(indices, c.averageCompetenceRankCorrelationAVG[dr][t]);
        averageCompetenceRankCorrelationSTDMatrix.setDouble(indices, c.averageCompetenceRankCorrelationSTD[dr][t]);
        averageCompetenceBeliefCorrelationAVGMatrix.setDouble(indices, c.averageCompetenceBeliefCorrelationAVG[dr][t]);
        averageCompetenceBeliefCorrelationSTDMatrix.setDouble(indices, c.averageCompetenceBeliefCorrelationSTD[dr][t]);
        meritocracyScoreBestMatchingAVGMatrix.setDouble(indices, c.meritocracyScoreBestMatchingAVG[dr][t]);
        meritocracyScoreBestMatchingSTDMatrix.setDouble(indices, c.meritocracyScoreBestMatchingSTD[dr][t]);
        meritocracyScoreRankCorrelationAVGMatrix.setDouble(indices, c.meritocracyScoreRankCorrelationAVG[dr][t]);
        meritocracyScoreRankCorrelationSTDMatrix.setDouble(indices, c.meritocracyScoreRankCorrelationSTD[dr][t]);
        meritocracyScoreBeliefCorrelationAVGMatrix.setDouble(indices, c.meritocracyScoreBeliefCorrelationAVG[dr][t]);
        meritocracyScoreBeliefCorrelationSTDMatrix.setDouble(indices, c.meritocracyScoreBeliefCorrelationSTD[dr][t]);
      }
    }

    try {
      Mat5.newMatFile()
          .addArray("para_is_greedy", Mat5.newScalar(Main.IS_GREEDY ? 1 : 0))

          .addArray("para_iteration", Mat5.newScalar(Main.ITERATION))
          .addArray("para_time", Mat5.newScalar(Main.TIME))
          .addArray("para_n", Mat5.newScalar(Main.N))
          .addArray("para_m", Mat5.newScalar(Main.M))
          .addArray("para_g", Mat5.newScalar(Main.G))

          .addArray("r_abm_avg", averageCompetenceBestMatchingAVGMatrix)
          .addArray("r_abm_std", averageCompetenceBestMatchingSTDMatrix)
          .addArray("r_arc_avg", averageCompetenceRankCorrelationAVGMatrix)
          .addArray("r_arc_std", averageCompetenceRankCorrelationSTDMatrix)
          .addArray("r_abc_avg", averageCompetenceBeliefCorrelationAVGMatrix)
          .addArray("r_abc_std", averageCompetenceBeliefCorrelationSTDMatrix)

          .addArray("r_mbm_avg", meritocracyScoreBestMatchingAVGMatrix)
          .addArray("r_mbm_std", meritocracyScoreBestMatchingSTDMatrix)
          .addArray("r_mrc_avg", meritocracyScoreRankCorrelationAVGMatrix)
          .addArray("r_mrc_std", meritocracyScoreRankCorrelationSTDMatrix)
          .addArray("r_mbc_avg", meritocracyScoreBeliefCorrelationAVGMatrix)
          .addArray("r_mbc_std", meritocracyScoreBeliefCorrelationSTDMatrix)

          .addArray("perf_seconds", Mat5.newScalar((double) (System.currentTimeMillis() - Main.TIC) / 1000))

          .writeTo(Sinks.newStreamingFile(new File(Main.FILENAME + ".mat")));

      System.out.println("File Printed: " + Main.FILENAME);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
