package org.studentrobotics.matchscheduler.scheduling.annealing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.studentrobotics.matchscheduler.Match;
import org.studentrobotics.matchscheduler.Team;

public class MatchAnnealer {
    /**
     * this will improve the match schedule without resizing any of the matches
     * 
     * @param matches
     */
    private static final Random RNG = new Random();

    private static final int MUTATION_STEPS = 8;

    private static final boolean DEBUG = true;

    public static List<Match> anneal(List<Match> matches, int seconds, int fixed) {
        // record when we started
        long now = System.currentTimeMillis();

        List<Team> teams = Team.generateTeamList(matches);

        // working values for computations
        int opponentSum = computeOpponentSum(teams);
        float currStdev = stdevComputation(teams);
        float cornerdev = computeSumOfTeamVariances(matches, teams);
        

        // keep a seperate working list of teams
        // deep copy
        List<Team> workingTeams = teamCopy(teams);

        // keep a working list of matches
        // do a deep copy
        List<Match> workingMatches = matchCopy(matches, workingTeams);

        int k = 0, j = 0;
        while (System.currentTimeMillis() - now < seconds * 1000) {
            roundMutate(workingTeams, workingMatches, fixed);
            // compute the new sum of opponents and the standard deviation of
            // match times

            // opponent sum is the sum of the number of teams each team faces
            int newOpponentSum = computeOpponentSum(workingTeams);
            // stdev is the standard deviation of number of matches between each
            // match
            float newStdev = stdevComputation(workingTeams);
            float newCornerDev = computeSumOfTeamVariances(workingMatches, workingTeams);
            
             

            // if the mutated match is better than the previous one, use it as
            // the new parent

            // standard deviation is lower, and we face more opponents
            // and one of the optimisations has changed.
            if (newStdev <= currStdev && newOpponentSum >= opponentSum
                    && newCornerDev <= cornerdev
                    && (newStdev != currStdev || newOpponentSum != opponentSum || newCornerDev != cornerdev)) {
                currStdev = newStdev;
                opponentSum = newOpponentSum;
                matches = workingMatches;
                teams = workingTeams;
                cornerdev = newCornerDev;
                j++;
            }

            workingTeams = teamCopy(teams);
            workingMatches = matchCopy(matches, workingTeams);
            k++;
        }

        if (MatchAnnealer.DEBUG) {
            System.out.println("ratio:" + (1.0f * j / k) + " steps:" + k + " improvements:" + j);
            System.out.println("currsum: " + opponentSum);
        }

        return matches;
    }

    private static void roundMutate(List<Team> workingTeams, List<Match> workingMatches, int fixed) {
        for (int i = 0; i < RNG.nextInt(MUTATION_STEPS); i++) {
            mutate(workingMatches, workingTeams, fixed);
        }
    }

    private static List<Match> matchCopy(List<Match> matches, List<Team> workingTeams) {
        List<Match> workingMatches = new ArrayList<Match>(matches.size());
        for (Match m : matches) {
            workingMatches.add(new Match(m, workingTeams));
        }
        return workingMatches;
    }

    private static List<Team> teamCopy(List<Team> teams) {
        List<Team> workingTeams = new ArrayList<Team>(teams.size());
        for (Team t : teams) {
            workingTeams.add(new Team(t));
        }
        return workingTeams;
    }

    private static float stdevComputation(List<Team> teams) {
        List<Float> values = new ArrayList<Float>();
        float currStdev = 0;
        generateMatchDiffs(teams, values);
        currStdev = computeStdev(values);
        return currStdev;
    }

    private static void mutate(List<Match> workingMatches, List<Team> workingTeams,
            int unchangeableMatches) {
        // pick two random matches, swap one team in a for a team in b
        workingMatches = workingMatches.subList(unchangeableMatches, workingMatches.size());
        int choice1 = RNG.nextInt(workingMatches.size());
        Match m1 = workingMatches.get(choice1);

        int choice2;
        Match m2;

        do {
            choice2 = RNG.nextInt(workingMatches.size());
            m2 = workingMatches.get(choice2);
        } while (m2 == m1 || compareTeams(m1, m2));

        Team t1;
        do {
            choice1 = RNG.nextInt(m1.getTeams().size());
            t1 = m1.getTeams().get(choice1);
        } while (m2.hasTeam(t1));

        Team t2;
        do {
            choice2 = RNG.nextInt(m2.getTeams().size());
            t2 = m2.getTeams().get(choice2);
        } while (m1.hasTeam(t2) || t2 == t1);

        // ok so we've got our teams and matches
        // swap them
        m2.removeTeam(t2);
        m1.removeTeam(t1);
        m1.addTeam(t2);
        m2.addTeam(t1);

    }

    private static boolean compareTeams(Match m1, Match m2) {
        int size = -1;
        Match main, other;
        if (m1.getTeams().size() < m2.getTeams().size()) {
            size = m1.getTeams().size();
            main = m1;
            other = m2;
        } else {
            size = m2.getTeams().size();
            main = m2;
            other = m1;
        }

        for (int i = 0; i < size; i++) {
            if (!other.getTeams().contains(main.getTeams().get(i))) return false;
        }

        return true;
    }

    private static int computeOpponentSum(List<Team> teams) {
        int sum = 0;

        for (Team t : teams) {
            sum += t.getNumberOfOpponents();
        }

        return sum;
    }

    private static float computeStdev(List<Float> values) {
        // stdev = sqrt(sum(x^2)-(sum(x)/n)^2)/(n-1)
        float sumX = 0;
        float sumSquaredX = 0;

        for (Float f : values) {
            sumX += f;
            sumSquaredX += f * f;
        }

        float meanX = sumX / values.size();

        return (float) (Math.sqrt(sumSquaredX - (meanX * meanX)) / (values.size() - 1));
    }

    private static void generateMatchDiffs(List<Team> teams, List<Float> values) {
        for (Team t : teams) {
            int last = 0;

            for (Match m : t.getMatches()) {
                values.add(new Float(m.getNumber() - last));
                last = m.getNumber();
            }

        }

    }

    private static float computeSumOfTeamVariances(List<Match> matches, List<Team> teams) {
        float sum = 0;
        for (Team t : teams) {
            sum += computeTeamCornerDistribution(t, matches);
        }
        return sum;
    }
    
    private static float computeTeamCornerDistribution(Team t, List<Match> matches) {
        float sum_c = 0;
        float sum_c_squares = 0;
        float n = 0;
        
        for (Match m : matches) {
            if (m.hasTeam(t)) {
                int c = m.getCorner(t);
                n++;
                sum_c += c;
                sum_c_squares += c*c;
            }
        }
        
        float mean = sum_c/n;
        float variance = (sum_c_squares - (n * mean))/n-1;
        
        return variance;
    }
}
