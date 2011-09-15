package org.studentrobotics.matchscheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Team implements Comparable<Team> {

    public static List<Team> generateTeamList(List<Match> matches) {
        SortedSet<Team> teams = new TreeSet<Team>();
        for (Match m : matches) {
            for (Team t : m.getTeams()) {
                teams.add(t);
            }

        }

        return Collections.unmodifiableList(new ArrayList<Team>(teams));
    }

    private int mNumber;

    public Team(int teamNumber) {
        mNumber = teamNumber;
        assert this.equals(this);
    }

    public Team(Team t) {
        mNumber = t.mNumber;
        assert this.equals(this);
    }

    @Override
    public int compareTo(Team o) {
        if (mNumber < o.mNumber) {
            return -1;
        } else if (mNumber == o.mNumber) {
            return 0;
        } else {
            return 1;
        }

    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Team && ((Team) obj).mNumber == mNumber;
    }

    public int getNumber() {
        return mNumber;
    }

    /**
     * notify the team is not in the match
     * 
     * @param m
     */

    @Override
    public int hashCode() {
        return mNumber;
    }

    @Override
    public String toString() {
        return "" + mNumber;
    }

}
