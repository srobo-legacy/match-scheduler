package org.studentrobotics.matchscheduler.main;

import java.io.FileNotFoundException;
import java.util.List;

import org.studentrobotics.matchscheduler.Match;
import org.studentrobotics.matchscheduler.Team;
import org.studentrobotics.matchscheduler.byeresolver.MaxSizeByeResolutionStrategy;
import org.studentrobotics.matchscheduler.scheduling.MatchConstraints;
import org.studentrobotics.matchscheduler.scheduling.MatchSchedulerImpl;
import org.studentrobotics.matchscheduler.scheduling.annealing.MatchAnnealer;
import org.studentrobotics.matchscheduler.serializers.MsFileSerializer;

public class Rescheduler {
    public static void main(String[] args) {
        String fileName = args[0];
        int completeMatches = Integer.parseInt(args[1]);
        int newMatches = Integer.parseInt(args[2]);
        int nTeams = Integer.parseInt(args[3]);
        int teamsPerMatch = Integer.parseInt(args[4]);
        
        try {
            List<Match> passedMatches = new MsFileSerializer().parse(fileName);
            List<Match> upTo = passedMatches.subList(0, completeMatches);
            MatchSchedulerImpl msi = new MatchSchedulerImpl(new MaxSizeByeResolutionStrategy());
            MatchConstraints mc = new MatchConstraints();
            mc.setNumberOfMatches(newMatches);
            mc.setNumberOfTeams(nTeams);
            mc.setTeamsPerMatch(teamsPerMatch);
            mc.disableByes();
            List<Match> matches = msi.reschedule(upTo, mc);
            matches = MatchAnnealer.anneal(matches, 3, completeMatches); 
            MsFileSerializer msf = new MsFileSerializer();
            msf.serialize(matches, Team.generateTeamList(matches));
        } catch (FileNotFoundException fne) {
            System.err.println("couldn't find the matches file specified");
        }

    }
}
