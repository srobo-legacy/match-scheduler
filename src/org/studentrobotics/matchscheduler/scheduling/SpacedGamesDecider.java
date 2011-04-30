package org.studentrobotics.matchscheduler.scheduling;

import java.util.List;

import org.studentrobotics.matchscheduler.Match;
import org.studentrobotics.matchscheduler.Team;

public class SpacedGamesDecider implements ScheduleDecider {

    @Override
    public List<Match> bestSchedule(List<Match> a, List<Match> b, List<Team> teams) {
        float sumA = computeStdevSum(a, teams);
        float sumB = computeStdevSum(b, teams);
        //if (sumA < sumB) System.err.println("cows");
        return (sumA < sumB ? a : b);
    }

    public float computeStdevSum(List<Match> a, List<Team> teams) {
        float stdevSum = 0;
        for (Team t : teams) {
            float stdev = computestdev(a, t);
            stdevSum += stdev;
        }

        return stdevSum;
    }

    private float computestdev(List<Match> a, Team t) {
        int sumGameSeparation = 0, sxx = 0, n = 0;
        int lastGame = 0;
        for (int i = 0; i < a.size(); i++) {
            Match m = a.get(i);
            
            if (m.hasTeam(t)) {
                int x = i - lastGame;
                sumGameSeparation += x;
                sxx += x * x;
                n += 1;
                lastGame = i;
            }
        }

        float mean = sumGameSeparation * 1.0f / n;
        float stdev = (sxx - n * mean * mean) / (n - 1);
        stdev = (float) Math.sqrt(stdev);
        return stdev;
    }

}
