package org.studentrobotics.matchscheduler.main;

import java.io.FileNotFoundException;
import java.util.List;

import org.studentrobotics.matchscheduler.Match;
import org.studentrobotics.matchscheduler.Team;
import org.studentrobotics.matchscheduler.scheduling.GreedyScheduler;
import org.studentrobotics.matchscheduler.scheduling.MatchConstraints;
import org.studentrobotics.matchscheduler.scheduling.MatchScheduler;
import org.studentrobotics.matchscheduler.serializers.MsFileSerializer;

public class Rescheduler {
    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("usage is java -jar rescheduler.jar [matchfile] "
                    + "[number of matches completed] [number of new matches to schedule] [number of teams] [number of teams per match]");
            System.exit(1);
        }
        String fileName = args[0];
        int completeMatches = Integer.parseInt(args[1]);
        int newMatches = Integer.parseInt(args[2]);
        int nTeams = Integer.parseInt(args[3]);
        int teamsPerMatch = Integer.parseInt(args[4]);

        try {
            List<Match> passedMatches = new MsFileSerializer().parse(fileName);
            List<Match> upTo = passedMatches.subList(0, completeMatches);
            MatchScheduler msi = new GreedyScheduler();
            MatchConstraints mc = new MatchConstraints();
            mc.setNumberOfMatches(newMatches);
            mc.setNumberOfTeams(nTeams);
            mc.setTeamsPerMatch(teamsPerMatch);
            mc.disableByes();
            List<Match> matches = msi.reschedule(upTo, mc);
            MsFileSerializer msf = new MsFileSerializer();
            msf.serialize(matches, Team.generateTeamList(matches));
        } catch (FileNotFoundException fne) {
            System.err.println("couldn't find the matches file specified");
        }

    }
}
