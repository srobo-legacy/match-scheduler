#!/usr/bin/env python
import random

def not_in_rectangle(left, top, width, height, i, j):
    in_rectangle = None
    in_x = i >= left and i < left+width
    in_y = j >= top and j < top+height
    in_rectangle = in_x and in_y
    return not in_rectangle

def getQuadrantPositions(startx, starty, width, height, rampleft, ramptop, rampwidth, rampheight):
        positions = []
        print width
        print height
        for i in range(startx, startx+width):
            for j in range(starty, starty+height):
                if (not_in_rectangle(rampleft, ramptop, rampwidth, rampheight, i, j)):
                    positions.append((i,j))
        return positions

def get_blocks(positions, blues):
    for i in range(0,2):
        c = random.choice(positions)
        blues.append(c)
        positions.remove(c)

def generateblocks(width, height, rampleft, ramptop, rampwidth, rampheight, blues = 8):
    positions_0_0 = getQuadrantPositions(0,0,width/2,height/2, rampleft, ramptop, rampwidth, rampheight)
    positions_0_1 = getQuadrantPositions(width/2,0,width/2+1,height/2, rampleft, ramptop, rampwidth, rampheight)
    positions_1_0 = getQuadrantPositions(0,height/2,width/2,height/2+1, rampleft, ramptop, rampwidth, rampheight)
    positions_1_1 = getQuadrantPositions(width/2, height/2,width/2+1,height/2+1, rampleft, ramptop, rampwidth, rampheight)
    
    blue_blocks = []
    get_blocks(positions_0_0, blue_blocks)
    get_blocks(positions_0_1, blue_blocks)
    get_blocks(positions_1_0, blue_blocks)
    get_blocks(positions_1_1, blue_blocks)
    
    return blue_blocks


if __name__ == "__main__":
    blues = generateblocks(7, 7, 2, 3, 3, 1)
    
    print "+--------------+"
    chars = []
    for j in range(0, 7):
        chars.append([])
        for i in range(0, 7):
            if (i,j) in blues:
                chars[j].append("B")
            elif (j == 3 and (i == 2 or i == 3 or i == 4)):
                chars[j].append("R")
            else:
                chars[j].append(".")
    
    
    for row in chars:
        r = ""
        for c in row:
            r += c + " "
        print "| "+r+"|"
    print "+--------------+"
                
        
    print blues

