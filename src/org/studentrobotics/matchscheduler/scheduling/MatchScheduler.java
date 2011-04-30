package org.studentrobotics.matchscheduler.scheduling;

import java.util.List;

import org.studentrobotics.matchscheduler.Match;

public interface MatchScheduler {
	/**
	 * should generate a match schedule, compliant with the match constraints
	 * 
	 * @param matchConstraints
	 * @return
	 */
	public List<Match> schedule(MatchConstraints matchConstraints);

    public List<Match> reschedule(List<Match> upTo, MatchConstraints mc);
}
