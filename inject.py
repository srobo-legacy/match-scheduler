#!/usr/bin/env python
import redis
import datetime
import time
import sys
import json

r = redis.Redis()

def starttime_exists():
    return r.get("org.srobo.time.start") is not None
   
def get_matches_start_timestamp():
    if starttime_exists():
        print "The start time of the competition is set"
        print "matches will start at the first 10.30am after the provided time"
        start = float(r.get("org.srobo.time.start"))
        return start
    else:
        print "The start time of the competition is not set"
        print "This is test mode"
        print "Matches will start in 2 minutes"
        start = time.time() + 120
        return start

def update_matches(matchline):
    r.rpush("org.srobo.matches", matchline)

def main():
    print "deleting existing match schedule"
    r.delete("org.srobo.matches")
    start = get_matches_start_timestamp()
    matches = open("matches.ms").read().split("\n")[:-1]
    for line in matches:
        match_start_time = line.split(",")[0]
        match_start_time = int(match_start_time) * 60 + int(start)
        line = str(match_start_time) + "," + ",".join(line.split(",")[1:])
        update_matches(line.strip())
    print "match schedule insertion complete"
    print "notifying clients of match variable update"
    r.publish("org.srobo.matches", "")
    print "insertion and notification complete"

if __name__ == "__main__":
    main()
