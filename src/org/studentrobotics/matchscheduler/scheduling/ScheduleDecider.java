package org.studentrobotics.matchscheduler.scheduling;

import java.util.List;

import org.studentrobotics.matchscheduler.Match;
import org.studentrobotics.matchscheduler.Team;

public interface ScheduleDecider {
    public List<Match> bestSchedule(List<Match> a, List<Match> b, List<Team> teams);
}
