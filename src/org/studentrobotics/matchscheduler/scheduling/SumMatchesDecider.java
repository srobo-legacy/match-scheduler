package org.studentrobotics.matchscheduler.scheduling;

import java.util.List;

import org.studentrobotics.matchscheduler.Match;
import org.studentrobotics.matchscheduler.Team;

public class SumMatchesDecider implements ScheduleDecider {

    @Override
    public List<Match> bestSchedule(List<Match> a, List<Match> b, List<Team> teams) {
        float devA = getMatchCountStdev(a, teams);
        float devB = getMatchCountStdev(b, teams);
        return (devA < devB ? a : b);
    }

    public float getMatchCountStdev(List<Match> a, List<Team> teams) {
        int sumMatchCounts = 0;
        int sxx = 0;
        int n = 0;
        for (Team t : teams) {
            int x = getMatchCount(t, a);
            sumMatchCounts += x;
            sxx += x*x;
            n += 1;
        }
        
        float mean = sumMatchCounts * 1.0f / n;
        
        float stdev = (sxx - n * mean * mean) / (n - 1);
        stdev = (float) Math.sqrt(stdev);
        return stdev;
    }
    
    private int getMatchCount(Team t, List<Match> matches) {
        int count = 0;
        for (Match m : matches) {
            if (m.hasTeam(t)) count++;
        }
        
        return count;
    }

}
