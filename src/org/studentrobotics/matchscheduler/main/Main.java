package org.studentrobotics.matchscheduler.main;

import java.util.ArrayList;
import java.util.List;

import org.studentrobotics.matchscheduler.Match;
import org.studentrobotics.matchscheduler.Team;
import org.studentrobotics.matchscheduler.byeresolver.MaxSizeByeResolutionStrategy;
import org.studentrobotics.matchscheduler.scheduling.MatchConstraints;
import org.studentrobotics.matchscheduler.scheduling.MatchSchedulerImpl;
import org.studentrobotics.matchscheduler.scheduling.annealing.MatchAnnealer;
import org.studentrobotics.matchscheduler.serializers.MatchSerializer;
import org.studentrobotics.matchscheduler.serializers.MsFileSerializer;
import org.studentrobotics.matchscheduler.serializers.PrintMatchesSerializer;
import org.studentrobotics.matchscheduler.serializers.TeamSerializer;

public class Main {
    private static List<MatchSerializer> serializers = new ArrayList<MatchSerializer>();

    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args.length != 6 && args.length != 4) {
            System.out.println("Wrong number of arguments");
            System.out.println("usage is: matchscheduler [nteams] [nmatches]"
                    + " [teams per match] [allow byes] <[min number of teams in a bye match]"
                    + " [max number of teams in a bye match]>");
            System.exit(1);
        }

        else {

            // add some serializers
            setupSerializers();
            MatchConstraints mc = parseArgConstraints(args);
            MatchSchedulerImpl ms = new MatchSchedulerImpl(new MaxSizeByeResolutionStrategy());
            List<Match> matches = ms.schedule(mc);
            
            int num_matches = mc.getNumberOfMatches();
            int match_size = mc.getTeamsPerMatch();
            int num_teams = mc.getNumberOfTeams();

            List<Match> finalMatches = matches;
            if (num_matches > 1 && match_size != num_teams) {
                int annealSeconds = 3;
                System.out.println("hill climbing for " + annealSeconds + " seconds");
                finalMatches = MatchAnnealer.anneal(matches, annealSeconds);
            }
            
            List<Team> teams = Team.generateTeamList(finalMatches);
            
            for (MatchSerializer serializer : serializers) {
                serializer.serialize(finalMatches, teams);
            }

        }

    }

    private static void setupSerializers() {
        serializers.add(new PrintMatchesSerializer());
        serializers.add(new TeamSerializer());
        serializers.add(new MsFileSerializer());
    }

    private static MatchConstraints parseArgConstraints(String[] args) {
        int num_teams = Integer.parseInt(args[0]);
        int num_matches = Integer.parseInt(args[1]);
        int match_size = Integer.parseInt(args[2]);
        String byes = args[3];

        // setup match constraints
        MatchConstraints mc = new MatchConstraints();

        mc.setNumberOfTeams(num_teams);
        mc.setNumberOfMatches(num_matches);
        mc.setTeamsPerMatch(match_size);
        if (byes.equals("true")) {
            int min_byes = Integer.parseInt(args[4]);
            int max_byes = Integer.parseInt(args[5]);
            mc.enableByes();
            mc.setMinByeSize(min_byes);
            mc.setMaxByeSize(max_byes);
        }
        return mc;
    }

}
