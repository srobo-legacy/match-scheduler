#!/usr/bin/env python
import sys
import random
import datetime, time

def tup_to_dt(t):
	s = datetime.datetime.now()
	h, m = t
	x = datetime.datetime( year = s.year,
			       month = s.month,
			       day = s.day,
			       hour = h,
			       minute = m )
	return x

MATCH_INTERVAL = datetime.timedelta( minutes = 8 )
LUNCH = tup_to_dt((12,30))
LUNCH_END = tup_to_dt((13,15))

class Team:
	def __init__(self, number):
		self.number = number
		self.matches = []
		self.opponents = []

	def get_number_of_matches(self):
		return len(self.matches)

	def get_matches(self):
		r = ""
		for i in self.matches:
			r += str(i._number) + " "

		return r

	def opponents_to_str(self):
		r = ""
		
		for team in self.opponents:
			r += str(team.number) + " "
		
		return r

class Match:
	def __init__(self, number):
		self._number = number
		self._teams = []

	def add_team(self, team):
		self._teams.append(team)
		team.matches.append(self)

	def get_number_of_teams(self):
		return len(self._teams)

	def __str__(self):
		return str(self._number)

	def __unicode__(self):
		return self.__str__()

	def teams(self):
		r = ""

		for t in self._teams:
			r += str(t.number) + " "

		return r
	
	def setup_opponents(self):
		for t1 in self._teams:
			for t2 in self._teams:
				if t1 != t2 and t2 not in t1.opponents:
					t1.opponents.append(t2)
					

def gcd(a, b):
	if b == 0:
		return a
	else:
		return gcd(b, a%b)

def min_idle_matches(teams):
	min = 5000
	for team in teams:
		prev = team.matches[0]
		for i in range(1, len(team.matches)):
			if team.matches[i]._number - prev._number - 1 < min:
				min = team.matches[i]._number - prev._number - 1
			prev = team.matches[i]
	return min

def average_idle_matches(teams):
	sum = 0
	n = 0
	for team in teams:
		prev = team.matches[0]
		for i in range(1, len(team.matches)):
			
			sum += team.matches[i]._number - prev._number - 1
			n += 1
			prev = team.matches[i]
	return (1.0*sum)/n

def deviation_idle_matches(teams):
	sumsquared = 0
	sum = 0
	n = 0
	
	for team in teams:
		prev = team.matches[0]._number
		for i in xrange(1, len(team.matches)):
			numb = team.matches[i]._number
			x = numb - prev - 1
			n += 1
			sum += x
			sumsquared += x*x
			prev = numb
	return ((sumsquared-((sum)**2/float(n)))/(n-1))**0.5

def allvall(teams):
	for team in teams:
		if len(team.opponents) < len(teams)-5:
			return False
	return True
	
def opponent_sum(teams):
	sum = 0
	for team in teams:
		sum += len(team.opponents)
	return sum

def findgoodschedule(desired_matches, number_of_teams, slots_per_match, byematches = False, iterations = 5000):
	match, teams = setup_matches(desired_matches, number_of_teams, slots_per_match, byematches = False)
	
	while opponent_sum(teams) < number_of_teams*2:
		match, teams = setup_matches(desired_matches, number_of_teams, slots_per_match, byematches)
		print opponent_sum(teams)
	i = 0
	print "cows"
	print deviation_idle_matches(teams)
	min = deviation_idle_matches(teams)
	best_match, best_teams = match, teams
	for i in range(0, iterations):
			match, teams = setup_matches(desired_matches, number_of_teams, slots_per_match, byematches)
			dev = deviation_idle_matches(teams)
			#print i
			#print opponent_sum(teams)
			if dev < min  and opponent_sum(teams) > opponent_sum(best_teams):
				best_match = match
				best_teams = teams
				min = dev
				print dev
				print opponent_sum(best_teams)/(1.0*len(teams))
				print i
			#if (i % 400000000000 == 0):
			#	print i
			#	print min
			#	print dev
			
	print opponent_sum(best_teams)/(1.0*len(best_teams))
	print deviation_idle_matches(best_teams)
	return best_match, best_teams


def max_matches_per_team(desired_matches, teams, slots_per_match):
	return (desired_matches*slots_per_match) / teams

def setup_matches(desired_matches, number_of_teams, slots_per_match, byematches = False):
	teams = []

	for i in range(1, number_of_teams+1):
		t = Team(i)
		teams.append(t)

	tlist = teams[0:]

	max_matches = max_matches_per_team(desired_matches, number_of_teams, slots_per_match)
	matches = []

	for i in range(0, desired_matches):
		match = Match(i+1)
		random.shuffle(tlist)

		while match.get_number_of_teams() < slots_per_match:
			#get a choice we can actually use
			choice = random.choice(tlist)

			while (choice in match._teams):
				choice = random.choice(tlist)

			tlist.remove(choice)

			#check the team can actually go into the match
			if byematches == False or choice.get_number_of_matches() < max_matches:
				match.add_team(choice)

			#reinitialise teams if we've used them all
			if len(tlist) == 0:
				tlist = teams[0:]

			if byematches == True:
				broken = 0
				for i in teams:
					if i.get_number_of_matches() != max_matches:
						broken = 1
				if not broken:
					matches.append(match)
					return matches,teams
		match.setup_opponents()
		matches.append(match)

	return matches, teams

if __name__ == "__main__":

	if len(sys.argv) < 3:
		print 'Usage: matchchooser TEAMS MATCHES [--tpm=num] [--allow-byes]'
		print '\tTEAMS\t- The total number of teams competing'
		print '\tMATCHES\t- the total number of matches desired'
		print '\t--tpm\t- The number of teams per match, defaults to 4'
		print '\t--allow-byes\t- If present allow bye matches'
		print '\t--start-time=HH:MM\t - The time the first match should start.'
		print "\t--skip-lunch\t - Don't schedule matches between 12:30 and 13:15"
		sys.exit(1)

	teams = int(sys.argv[1])
	teamsmatches = []
	desiredMatches = int(sys.argv[2])
	start_time = datetime.datetime.fromtimestamp(0)

	#Optional command line args
	allowByes = False
	teamsPerMatch = 4
	skip_lunch = False
	for arg in sys.argv[3:]:
		if arg[:6] == '--tpm=':
			teamsPerMatch = int(arg[6:])
		elif arg == '--allow-byes':
			allowByes = True
		elif arg[0:13] == "--start-time=":
			s = arg[-5:].split( ":" )
			h = int(s[0])
			m = int(s[1])
			start_time = tup_to_dt( (h,m) )
		elif arg == "--skip-lunch":
			skip_lunch = True

	slots = desiredMatches*teamsPerMatch
	baseMatches = teams*teamsPerMatch/gcd(teams,teamsPerMatch)

	if (slots % baseMatches != 0):
		print "warning, inexact number of team slots for desired number of matches"
		print "closest number of matches for exact number of slots are"

		if (slots/baseMatches > 0):
			print (slots/baseMatches)*baseMatches/teamsPerMatch, "matches"

		print (slots/baseMatches+1)*baseMatches/teamsPerMatch, "matches"
		print "continuing with unfair number of matches"

	matches, team_o = findgoodschedule(desiredMatches, teams, teamsPerMatch, allowByes)

	for i in team_o:
		print "Team %d in %d matches:" % (i.number, i.get_number_of_matches()), i.get_matches()
#   random.shuffle(matches)
	for m in matches:
		print "Match %d:" % m._number, m.teams()

	for team in team_o:
			print "Team number %d opposes:" % (team.number), team.opponents_to_str(), "total of %d opponents" % len(team.opponents)
			
	print "writing matches to matches.csv and matches.sql"
	csv = open("matches.csv", "w")
	sql = open("matches.sql", "w")

	n = 0
	match_time = start_time
	for match in matches:
		n = n + 1
		for i in range(0, len(match._teams)):
			csv.write(str(match._teams[i].number))

			if i != len(match._teams)-1:
				csv.write(",")
			else:
				csv.write("\n")
		
		# Generate sql
		colours = [ "red", "green", "blue", "yellow" ]

		s = "insert into matches set number = %i, time = %i" % (n, time.mktime(match_time.timetuple()))
		match_time += MATCH_INTERVAL

		if skip_lunch and match_time >= LUNCH and match_time < LUNCH_END:
			match_time = LUNCH_END

		cnum = 0

		for team in [x.number for x in match._teams]:
			s += ", %s = %i" % (colours[cnum], team)
			cnum += 1
		s += ";\n"

		# Generate sql
		colours = [ "red", "green", "blue", "yellow" ]

		s = "insert into matches set number = %i, time = %i" % (n, time.mktime(match_time.timetuple()))
		match_time += MATCH_INTERVAL

		if skip_lunch and match_time >= LUNCH and match_time < LUNCH_END:
			match_time = LUNCH_END

		cnum = 0

		for team in [x.number for x in match._teams]:
			s += ", %s = %i" % (colours[cnum], team)
			cnum += 1
		s += ";\n"

		sql.write( s )
