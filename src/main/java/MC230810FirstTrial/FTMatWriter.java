package MC230810FirstTrial;

import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;
import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.types.Matrix;
import us.hebi.matlab.mat.types.Sinks;

public class FTMatWriter {

  FTMatWriter(FTComputation c) {
    Matrix matrixArrayM = Mat5.newMatrix(new int[]{1, FTMain.M_LENGTH});
    Matrix matrixArrayM0 = Mat5.newMatrix(new int[]{1, FTMain.M_LENGTH});
    Matrix matrixArrayM1 = Mat5.newMatrix(new int[]{1, FTMain.M_LENGTH});
    Matrix matrixArrayDeltaProbability = Mat5.newMatrix(new int[]{1, FTMain.P_LENGTH});
    Matrix matrixArrayDeltaUtility = Mat5.newMatrix(new int[]{1, FTMain.DELTA_U_LENGTH});

    IntStream.range(0, FTMain.M_LENGTH).forEach(i -> matrixArrayM.setDouble(new int[]{0, i}, FTMain.M[i]));
    IntStream.range(0, FTMain.M_LENGTH).forEach(i -> matrixArrayM0.setDouble(new int[]{0, i}, FTMain.M0[i]));
    IntStream.range(0, FTMain.M_LENGTH).forEach(i -> matrixArrayM1.setDouble(new int[]{0, i}, FTMain.M1[i]));
    IntStream.range(0, FTMain.P_LENGTH).forEach(i -> matrixArrayDeltaProbability.setDouble(new int[]{0, i}, FTMain.P[i]));
    IntStream.range(0, FTMain.DELTA_U_LENGTH).forEach(i -> matrixArrayDeltaUtility.setDouble(new int[]{0, i}, FTMain.DELTA_U[i]));

    Matrix matrixArrayComposition = Mat5.newMatrix(new int[]{FTMain.M_LENGTH, 2});
    for (int m = 0; m < FTMain.M_LENGTH; m++) {
      matrixArrayComposition.setDouble(new int[]{m, 0}, FTMain.COMPOSITION[m][0]);
      matrixArrayComposition.setDouble(new int[]{m, 1}, FTMain.COMPOSITION[m][1]);
    }

    Matrix matrixArrayG = Mat5.newMatrix(new int[]{FTMain.G_LENGTH, 2});
    for (int g = 0; g < FTMain.G_LENGTH; g++) {
      matrixArrayG.setDouble(new int[]{g, 0}, FTMain.G[g][0]);
      matrixArrayG.setDouble(new int[]{g, 1}, FTMain.G[g][1]);
    }

    Matrix consensusShareMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix consensusType0ShareMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix consensusType1ShareMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix perfectShareMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix perfectType0ShareMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix perfectType1ShareMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix mixedCoalitionShareMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);

    Matrix decisionAgainstPreferenceAVGMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE_TYPE);
    Matrix decisionAgainstPreferenceSTDMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE_TYPE);
    Matrix choiceRateAVGMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE_ARM);
    Matrix choiceRateSTDMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE_ARM);
    Matrix choiceRateIndAVGMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE_IND_ARM);
    Matrix choiceRateIndSTDMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE_IND_ARM);

    Matrix falsePositiveCNTMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE_IND_ARM);
    Matrix falsePositiveAVGMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE_IND_ARM);
    Matrix falsePositiveSTDMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE_IND_ARM);
    Matrix falseNegativeCNTMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE_IND_ARM);
    Matrix falseNegativeAVGMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE_IND_ARM);
    Matrix falseNegativeSTDMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE_IND_ARM);

    Matrix vote2Win00AVGMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix vote2Win00STDMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix vote2Win01AVGMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix vote2Win01STDMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix vote2Win10AVGMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix vote2Win10STDMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix vote2Win11AVGMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix vote2Win11STDMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);

    Matrix organizationalDecision0AVGMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix organizationalDecision0STDMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix organizationalDecision1AVGMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix organizationalDecision1STDMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix femaleVote0AVGMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix femaleVote0STDMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix femaleVote1AVGMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix femaleVote1STDMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);

    Matrix utilityAVGMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);
    Matrix utilitySTDMatrix = Mat5.newMatrix(FTMain.RESULT_KEY_VALUE);

    for (int dr = 0; dr < FTMain.DECISION_STRUCTURE_LENGTH; dr++) {
      for (int dp = 0; dp < FTMain.P_LENGTH; dp++) {
        for (int du = 0; du < FTMain.DELTA_U_LENGTH; du++) {
          for (int m = 0; m < FTMain.M_LENGTH; m++) {
            for (int g = 0; g < FTMain.G_LENGTH; g++) {
              for (int t = 0; t < FTMain.TIME; t++) {
                int[] indices = {dr, dp, du, m, g, t};
                int[] indicesType0 = {dr, dp, du, m, g, t, 0};
                int[] indicesType1 = {dr, dp, du, m, g, t, 1};
                consensusShareMatrix.setDouble(indices, c.consensusShare[dr][dp][du][m][g][t]);
                consensusType0ShareMatrix.setDouble(indices, c.consensusType0Share[dr][dp][du][m][g][t]);
                consensusType1ShareMatrix.setDouble(indices, c.consensusType1Share[dr][dp][du][m][g][t]);
                perfectShareMatrix.setDouble(indices, c.perfectShare[dr][dp][du][m][g][t]);
                perfectType0ShareMatrix.setDouble(indices, c.perfectType0Share[dr][dp][du][m][g][t]);
                perfectType1ShareMatrix.setDouble(indices, c.perfectType1Share[dr][dp][du][m][g][t]);
                mixedCoalitionShareMatrix.setDouble(indices, c.mixedCoalitionShare[dr][dp][du][m][g][t]);

                utilityAVGMatrix.setDouble(indices, c.utilityAVG[dr][dp][du][m][g][t]);
                utilitySTDMatrix.setDouble(indices, c.utilitySTD[dr][dp][du][m][g][t]);

                decisionAgainstPreferenceAVGMatrix.setDouble(indicesType0, c.decisionAgainstPreferenceAVG[dr][dp][du][m][g][t][0]);
                decisionAgainstPreferenceSTDMatrix.setDouble(indicesType0, c.decisionAgainstPreferenceSTD[dr][dp][du][m][g][t][0]);
                decisionAgainstPreferenceAVGMatrix.setDouble(indicesType1, c.decisionAgainstPreferenceAVG[dr][dp][du][m][g][t][1]);
                decisionAgainstPreferenceSTDMatrix.setDouble(indicesType1, c.decisionAgainstPreferenceSTD[dr][dp][du][m][g][t][1]);

                vote2Win00AVGMatrix.setDouble(indices, c.vote2Win00AVG[dr][dp][du][m][g][t]);
                vote2Win00STDMatrix.setDouble(indices, c.vote2Win00STD[dr][dp][du][m][g][t]);
                vote2Win01AVGMatrix.setDouble(indices, c.vote2Win01AVG[dr][dp][du][m][g][t]);
                vote2Win01STDMatrix.setDouble(indices, c.vote2Win01STD[dr][dp][du][m][g][t]);
                vote2Win10AVGMatrix.setDouble(indices, c.vote2Win10AVG[dr][dp][du][m][g][t]);
                vote2Win10STDMatrix.setDouble(indices, c.vote2Win10STD[dr][dp][du][m][g][t]);
                vote2Win11AVGMatrix.setDouble(indices, c.vote2Win11AVG[dr][dp][du][m][g][t]);
                vote2Win11STDMatrix.setDouble(indices, c.vote2Win11STD[dr][dp][du][m][g][t]);

                organizationalDecision0AVGMatrix.setDouble(indices, c.organizationalDecision0AVG[dr][dp][du][m][g][t]);
                organizationalDecision0STDMatrix.setDouble(indices, c.organizationalDecision0STD[dr][dp][du][m][g][t]);
                organizationalDecision1AVGMatrix.setDouble(indices, c.organizationalDecision1AVG[dr][dp][du][m][g][t]);
                organizationalDecision1STDMatrix.setDouble(indices, c.organizationalDecision1STD[dr][dp][du][m][g][t]);
                femaleVote0AVGMatrix.setDouble(indices, c.femaleVote0AVG[dr][dp][du][m][g][t]);
                femaleVote0STDMatrix.setDouble(indices, c.femaleVote0STD[dr][dp][du][m][g][t]);
                femaleVote1AVGMatrix.setDouble(indices, c.femaleVote1AVG[dr][dp][du][m][g][t]);
                femaleVote1STDMatrix.setDouble(indices, c.femaleVote1STD[dr][dp][du][m][g][t]);

                for (int choice = 0; choice < FTMain.N; choice++) {
                  int[] indicesChoice = {dr, dp, du, m, g, t, choice};
                  choiceRateAVGMatrix.setDouble(indicesChoice, c.choiceRateAVG[dr][dp][du][m][g][t][choice]);
                  choiceRateSTDMatrix.setDouble(indicesChoice, c.choiceRateSTD[dr][dp][du][m][g][t][choice]);
                  for (int member = 0; member < FTMain.M[m]; member++) {
                    int[] indicesChoiceInd = {dr, dp, du, m, g, t, member, choice};
                    choiceRateIndAVGMatrix.setDouble(indicesChoiceInd, c.choiceRateIndAVG[dr][dp][du][m][g][t][member][choice]);
                    choiceRateIndSTDMatrix.setDouble(indicesChoiceInd, c.choiceRateIndSTD[dr][dp][du][m][g][t][member][choice]);
                    falsePositiveAVGMatrix.setDouble(indicesChoiceInd, c.falsePositiveAVG[dr][dp][du][m][g][t][member][choice]);
                    falsePositiveSTDMatrix.setDouble(indicesChoiceInd, c.falsePositiveSTD[dr][dp][du][m][g][t][member][choice]);
                    falsePositiveCNTMatrix.setDouble(indicesChoiceInd, c.falsePositiveCNT[dr][dp][du][m][g][t][member][choice]);
                    falseNegativeAVGMatrix.setDouble(indicesChoiceInd, c.falseNegativeAVG[dr][dp][du][m][g][t][member][choice]);
                    falseNegativeSTDMatrix.setDouble(indicesChoiceInd, c.falseNegativeSTD[dr][dp][du][m][g][t][member][choice]);
                    falseNegativeCNTMatrix.setDouble(indicesChoiceInd, c.falseNegativeCNT[dr][dp][du][m][g][t][member][choice]);
                  }
                }
              }
            }
          }
        }
      }
    }

    try {
      Mat5.newMatFile()
          .addArray("para_is_learning", Mat5.newScalar(FTMain.IS_LEARNING ? 1 : 0))
          .addArray("para_is_greedy", Mat5.newScalar(FTMain.IS_GREEDY ? 1 : 0))
          .addArray("para_is_prejoin_random", Mat5.newScalar(FTMain.IS_INITIAL_RANDOM ? 1 : 0))

          .addArray("para_iteration", Mat5.newScalar(FTMain.ITERATION))
          .addArray("para_time", Mat5.newScalar(FTMain.TIME))
          .addArray("para_n", Mat5.newScalar(FTMain.N))
          .addArray("para_a_m", matrixArrayM)
          .addArray("para_a_m0", matrixArrayM0)
          .addArray("para_a_m1", matrixArrayM1)
          .addArray("para_g_m", Mat5.newScalar(FTMain.M_LENGTH))
          .addArray("para_a_comp", matrixArrayComposition)
          .addArray("para_a_dp", matrixArrayDeltaProbability)
          .addArray("para_g_dp", Mat5.newScalar(FTMain.P_LENGTH))
          .addArray("para_a_du", matrixArrayDeltaUtility)
          .addArray("para_g_du", Mat5.newScalar(FTMain.DELTA_U_LENGTH))
          .addArray("para_a_g", matrixArrayG)
          .addArray("para_g_g", Mat5.newScalar(FTMain.G_LENGTH))
          .addArray("para_tau", Mat5.newScalar(FTMain.TAU))
          .addArray("para_g_dr", Mat5.newScalar(FTMain.DECISION_STRUCTURE_LENGTH))

          .addArray("r_cs_shr", consensusShareMatrix)
          .addArray("r_c0_shr", consensusType0ShareMatrix)
          .addArray("r_c1_shr", consensusType1ShareMatrix)
          .addArray("r_pf_shr", perfectShareMatrix)
          .addArray("r_p0_shr", perfectType0ShareMatrix)
          .addArray("r_p1_shr", perfectType1ShareMatrix)
          .addArray("r_mc_shr", mixedCoalitionShareMatrix)

          .addArray("r_as_avg", decisionAgainstPreferenceAVGMatrix)
          .addArray("r_as_std", decisionAgainstPreferenceSTDMatrix)

          .addArray("r_cr_avg", choiceRateAVGMatrix)
          .addArray("r_cr_std", choiceRateSTDMatrix)
          .addArray("r_ci_avg", choiceRateIndAVGMatrix)
          .addArray("r_ci_std", choiceRateIndSTDMatrix)

          .addArray("r_fp_cnt", falsePositiveCNTMatrix)
          .addArray("r_fp_avg", falsePositiveAVGMatrix)
          .addArray("r_fp_std", falsePositiveSTDMatrix)

          .addArray("r_fn_cnt", falseNegativeCNTMatrix)
          .addArray("r_fn_avg", falseNegativeAVGMatrix)
          .addArray("r_fn_std", falseNegativeSTDMatrix)

          .addArray("r_00_avg", vote2Win00AVGMatrix)
          .addArray("r_00_std", vote2Win00STDMatrix)
          .addArray("r_01_avg", vote2Win01AVGMatrix)
          .addArray("r_01_std", vote2Win01STDMatrix)
          .addArray("r_10_avg", vote2Win10AVGMatrix)
          .addArray("r_10_std", vote2Win10STDMatrix)
          .addArray("r_11_avg", vote2Win11AVGMatrix)
          .addArray("r_11_std", vote2Win11STDMatrix)

          .addArray("r_o0_avg", organizationalDecision0AVGMatrix)
          .addArray("r_o0_std", organizationalDecision0STDMatrix)
          .addArray("r_o1_avg", organizationalDecision1AVGMatrix)
          .addArray("r_o1_std", organizationalDecision1STDMatrix)
          .addArray("r_f0_avg", femaleVote0AVGMatrix)
          .addArray("r_f0_std", femaleVote0STDMatrix)
          .addArray("r_f1_avg", femaleVote1AVGMatrix)
          .addArray("r_f1_std", femaleVote1STDMatrix)

          .addArray("r_ut_avg", utilityAVGMatrix)
          .addArray("r_ut_std", utilitySTDMatrix)

          .addArray("perf_seconds", Mat5.newScalar((System.currentTimeMillis() - FTMain.TIC) / 1000))

          .writeTo(Sinks.newStreamingFile(new File(FTMain.FILENAME + ".mat")));
      System.out.println("File Printed: "+ FTMain.FILENAME);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
