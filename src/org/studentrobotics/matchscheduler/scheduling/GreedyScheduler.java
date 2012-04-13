package org.studentrobotics.matchscheduler.scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.studentrobotics.matchscheduler.Match;
import org.studentrobotics.matchscheduler.Team;

public class GreedyScheduler implements MatchScheduler {

    private static final int SHUFFLE_ATTEMPTS = 500000;

    private static Random sRng = new Random();

    private static List<ScheduleDecider> sScheduleDeciders = new ArrayList<ScheduleDecider>();

    private static int TEST_MATCHES = 2000;

    static {
        sScheduleDeciders.add(new AllVAllDecider());
        sScheduleDeciders.add(new SumMatchesDecider());
        //sScheduleDeciders.add(new SpacedGamesDecider());
    }

    private List<Team> mTeams = new ArrayList<Team>();

    @Override
    public List<Match> reschedule(List<Match> upTo, MatchConstraints mc) {
        for (int i = 0; i < mc.getNumberOfTeams(); i++) {
            mTeams.add(new Team(i));
        }
        createMatches(mc, upTo, new Match[TEST_MATCHES], upTo.size());
        upTo = bestShuffle(upTo);
        return upTo;
    }

    @Override
    public List<Match> schedule(MatchConstraints matchConstraints) {
        List<Match> schedule = greedyGenerate(matchConstraints);
        schedule = bestShuffle(schedule);
        return schedule;
    }

    private List<Match> bestShuffle(List<Match> schedule) {
        List<Match> working = new ArrayList<Match>(schedule);
        List<Match> best = new ArrayList<Match>(schedule);
        SpacedGamesDecider sgd = new SpacedGamesDecider();
        for (int i = 0; i < SHUFFLE_ATTEMPTS; i++) {
            this.shuffle(working);
            List<Match> m = sgd.bestSchedule(working, best, mTeams);
            if (m != best) {
                List<Match> tmp = best;
                best = m;
                working = tmp;
            }

            assert best != working;
        }
        correctIndices(best);
        return best;
    }

    private void correctIndices(List<Match> matches) {
        for (int i = 0; i < matches.size(); i++) {
            Match m = matches.get(i);
            m.setNumber(i);
        }
    }

    private void fillMatches(Match[] workingMatches, MatchConstraints mc, int n) {
        for (int i = 0; i < workingMatches.length; i++) {
            workingMatches[i] = generateRandomMatch(mc, n);
        }
    }

    private Match generateRandomMatch(MatchConstraints mc, int matchNumber) {
        Match m = new Match(matchNumber);
        for (int i = 0; i < mc.getTeamsPerMatch(); i++) {
            Team t;
            do {
                t = mTeams.get(sRng.nextInt(mTeams.size()));
            } while (m.hasTeam(t));
            m.addTeam(t);

        }
        return m;
    }

    private List<Match> greedyGenerate(MatchConstraints matchConstraints) {
        for (int i = 0; i < matchConstraints.getNumberOfTeams(); i++) {
            mTeams.add(new Team(i));
        }

        List<Match> result = new ArrayList<Match>();
        Match[] workingMatches = new Match[TEST_MATCHES];
        createMatches(matchConstraints, result, workingMatches, 0);


        return result;
    }

    private void createMatches(MatchConstraints matchConstraints, List<Match> result,
            Match[] workingMatches, int n) {
        
        int matchNumber = n;
        while (result.size() < matchConstraints.getNumberOfMatches()) {
            fillMatches(workingMatches, matchConstraints, matchNumber++);
            Match best = pickBestMatch(workingMatches, result);
            result.add(best);
        }
    }

    private boolean isBetterSchedule(List<Match> workingMatchSet, List<Match> bestSchedule) {
        for (ScheduleDecider sd : sScheduleDeciders) {
            if (sd.bestSchedule(workingMatchSet, bestSchedule, mTeams) == bestSchedule) {
                return false;
            }
        }

        return true;
    }

    private Match pickBestMatch(Match[] workingMatches, List<Match> result) {
        List<Match> workingMatchSet = new ArrayList<Match>(result.size() + 1);
        Match best = null;
        List<Match> bestSchedule = null;
        for (int i = 0; i < workingMatches.length; i++) {
            workingMatchSet.addAll(result);
            workingMatchSet.add(workingMatches[i]);

            if (best == null || isBetterSchedule(workingMatchSet, bestSchedule)) {
                best = workingMatches[i];
                bestSchedule = new ArrayList<Match>(workingMatchSet);
            }
            workingMatchSet.clear();
        }

        return best;
    }

    private void shuffle(List<Match> matches) {
        for (int i = 0; i < 2; i++) {
            Collections.swap(matches, sRng.nextInt(matches.size()), sRng.nextInt(matches.size()));
        }
        
    }

}
