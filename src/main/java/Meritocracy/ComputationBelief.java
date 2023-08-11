package Meritocracy;

import java.io.FileWriter;
import java.io.IOException;

public class ComputationBelief {

  String fileName = "NUBelief_";
  int iteration = 10000;
  int time = 100;
  int dr = 0; // Solo Woman
  //  int dr = 1; // Plurality Voting
  int m0 = 0;
  int m1 = 1;
  int g0 = 5;
  int g1 = 5;
  double payoffProbability = .8;
  double utilityDifference = .25;

  FileWriter csv;
  Scenario s;

  ComputationBelief() throws IOException {
    fileName = fileName
        + "i" + iteration
        + "t" + time
        + "dr" + dr
        + "p" + payoffProbability
        + "du" + utilityDifference
        + "m" + m0 + "," + m1
        + "g" + g0 + "," + g1;
    initializeCSV();
    for (int i = 0; i < iteration; i++) {
      if (dr == 0) {
        s = new FTScenarioSoloWoman(payoffProbability, utilityDifference, m0, m1, g0, g1);
      } else {
        s =
            new Scenario(
                payoffProbability,
                utilityDifference,
                dr,
                m0,
                m1,
                g0,
                g1
            );
      }
      for (int individual : s.memberIndexArray) {
        for (int arm : s.armIndexArray) {
          s.belief[individual][arm] = (double) s.beliefNumerator[individual][arm] / (double) s.beliefDenominator[individual][arm];
        }
      }

      for (int t = 0; t < time; t++) {
        s.stepForward();
      }
      addCSV();
    }
    csv.flush();
    csv.close();
    System.out.println("Belief CSV Printed: " + fileName);
  }

  void initializeCSV() throws IOException {
    csv = new FileWriter(fileName + ".csv");
    csv.append("m");
    csv.append(",");
    csv.append("m0");
    csv.append(",");
    csv.append("m1");
    csv.append(",");
    csv.append("dms");
    csv.append(",");
    csv.append("id");
    csv.append(",");
    csv.append("typeOf");
    csv.append(",");
    csv.append("isAgainst");
    csv.append(",");
    csv.append("arm");
    csv.append(",");
    csv.append("belief0");
    csv.append(",");
    csv.append("belief");
    csv.append(",");
    csv.append("p");
    csv.append("\n");
  }

  void addCSV() throws IOException {
    for (int individual : s.memberIndexArray) {
      for (int arm : s.armIndexArray) {
//        csv.append("m");
        csv.append(Integer.toString(s.m));
        csv.append(",");

//        csv.append("m0");
        csv.append(Integer.toString(s.m0));
        csv.append(",");

//        csv.append("m1");
        csv.append(Integer.toString(s.m1));
        csv.append(",");

//        csv.append("dms");
        switch (s.decisionRuleIndex) {
          case 0 -> csv.append("Solo Woman");
          case 1 -> csv.append("Plurality Voting");
          case 2 -> csv.append("Weighted Voting");
          case 3 -> csv.append("Two Stage PV");
          case 4 -> csv.append("Two Stage WV");
        }
        csv.append(",");

//        csv.append("id");
        csv.append(Integer.toString(individual));
        csv.append(",");

//        csv.append("typeOf");
        csv.append(Integer.toString(s.typeOf[individual]));
        csv.append(",");

//        csv.append("isAgainst");
        csv.append(Integer.toString(s.isAssimilated[individual] ? 1 : 0));
        csv.append(",");

//        csv.append("arm");
        switch (arm) {
          case 0 -> csv.append("Male");
          case 1 -> csv.append("Female");
        }
        csv.append(",");

//        csv.append("belief0");
        csv.append(Double.toString(s.belief0[individual][arm]));
        csv.append(",");

//        csv.append("belief");
        csv.append(Double.toString(s.belief[individual][arm]));
        csv.append(",");

//        csv.append("p");
        csv.append(Double.toString(s.payoffProbability));
        csv.append("\n");
      }
    }
  }

}
