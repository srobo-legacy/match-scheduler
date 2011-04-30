package org.studentrobotics.matchscheduler.scheduling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.studentrobotics.matchscheduler.Match;
import org.studentrobotics.matchscheduler.Team;

public class AllVAllDecider implements ScheduleDecider {

    @Override
    public List<Match> bestSchedule(List<Match> a, List<Match> b, List<Team> teams) {
        int oppSumA = computeOppositionSum(a, teams);
        int oppSumB = computeOppositionSum(b, teams);
        return (oppSumA > oppSumB ? a : b);
    }

    public int computeOppositionSum(List<Match> a, List<Team> teams) {
        Map<Team, Set<Team>> oppositionMap = new HashMap<Team, Set<Team>>();

        for (Team t : teams) {
            oppositionMap.put(t, new HashSet<Team>());
        }

        buildOppositonMap(a, oppositionMap);

        int sum = 0;
        for (Set<Team> oppositions : oppositionMap.values()) {
            sum += oppositions.size();
        }

        return sum;
    }

    private void buildOppositonMap(List<Match> a, Map<Team, Set<Team>> oppositionMap) {
        for (Match m : a) {
            List<Team> teams = m.getTeams();
            for (Team t1 : teams) {
                for (Team t2 : teams) {
                    if (t1 != t2) {
                        addOpposition(oppositionMap, t1, t2);
                    }

                }

            }

        }
        
    }

    private void addOpposition(Map<Team, Set<Team>> oppositionMap, Team t1, Team t2) {
        oppositionMap.get(t1).add(t2);

    }

}
