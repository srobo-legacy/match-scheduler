package org.studentrobotics.matchscheduler.serializers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import org.studentrobotics.matchscheduler.Match;
import org.studentrobotics.matchscheduler.Team;

public class MsFileSerializer implements MatchSerializer {

    @Override
    public void serialize(List<Match> matches, List<Team> teams) {
        try {

            BufferedOutputStream bufferedOutput = new BufferedOutputStream(new FileOutputStream(
                    "matches.ms"));
            PrintStream ps = new PrintStream(bufferedOutput);
            Date d = new Date(2011, 4, 10, 10, 30);
            for (Match m : matches) {

                ps.printf("%02d:%02d,", d.getHours(), d.getMinutes());
                for (int i = 0; i < m.getNumberOfTeams(); i++) {
                    ps.print(m.getTeams().get(i));
                    if (i != m.getNumberOfTeams() - 1) ps.print(",");

                }

                int nowMin = d.getMinutes();
                int nowHour = d.getHours();
                if (nowHour == 12 && nowMin == 30) {
                    nowHour = 13;
                    nowMin = 5;
                }
                
                if (nowMin == 50) {
                    nowMin = 0;
                    nowHour++;
                } else if (nowMin == 55) {
                    nowMin = 5;
                    nowHour++;
                } else {
                    nowMin += 10;
                }

                d.setMinutes(nowMin);
                d.setHours(nowHour);

                ps.print("\n");
            }
            ps.flush();
            ps.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
