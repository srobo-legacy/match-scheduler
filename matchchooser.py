#!/usr/bin/env python
import sys
import random

def gcd(a, b):
	if b == 0:
		return a
	else:
		return gcd(b, a%b)


if __name__ == "__main__":

	if len(sys.argv) < 3:
		print 'Usage: matchchooser TEAMS MATCHES [TPM]'
		print '\tTEAMS\t- The total number of teams competing'
		print '\tMATCHES\t- the total number of matches desired'
		print '\tTPM\t- The number of teams per match, defaults to 4'
		sys.exit(1)

	teams = int(sys.argv[1])
	teamsmatches = []
	desiredMatches = int(sys.argv[2])
	teamsPerMatch = 4 if len(sys.argv) < 4 else int(sys.argv[3])
	slots = desiredMatches*teamsPerMatch
	baseMatches = teams*teamsPerMatch/gcd(teams,teamsPerMatch)

	if (desiredMatches % baseMatches != 0):
		print "warning, inexact number of team slots for desired number of matches"
		print "closest number of matches for exact number of slots are"

		if (slots/baseMatches > 0):
			print (slots/baseMatches)*baseMatches/teamsPerMatch, "matches"

		print (slots/baseMatches+1)*baseMatches/teamsPerMatch, "matches"
		print "continuing with unfair number of matches"

	matches = [None]*desiredMatches
	for i in range(0,teams):
		teamsmatches.append([])

	teamindex = random.randint(0, teams -1)

	tlist = range(1, teams+1)
	for i in range(0, desiredMatches):
		match = []
		while len(match) < teamsPerMatch:
			choice = random.choice(tlist)
			tlist.remove(choice)
			match.append(choice)
			teamsmatches[choice-1].append(i+1)

			if tlist == []:
				tlist = range(1,teams+1)

		matches[i] = match

	for i in range(0, teams):
		print "Team %d is in %d matches: %s" % (i+1, len(teamsmatches[i]), teamsmatches[i])

	for i in range(0, desiredMatches):
		print "Match number: %d" % (i + 1)
		print "Teams:", matches[i]

	print "writing matches to matches.csv"
	csv = open("matches.csv", "w")

	for match in matches:
		for i in range(0, len(match)):
			csv.write(str(match[i]))

			if i != len(match)-1:
				csv.write(",")
			else:
				csv.write("\n")

