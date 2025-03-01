import numpy as np
from copy import deepcopy
import pygame
import random
import time
import sys

class Pixel:
    def __init__(self,state,x,y,pixelSize):
        self.pixelSize = pixelSize
        self.state = state
        self.deadage = 10
        self.x = x
        self.y = y
    def birth(self):
        self.state = 1
    def die(self):
        if self.state:
            self.state = 0
            self.deadage = 0
        else:
            self.deadage += 1
    def checkState(self):
        return self.state
    def show(self,t):
        smolbox = pygame.Surface((self.pixelSize,self.pixelSize))
        if self.state:
            smolbox.fill((255,255,255))
        elif self.deadage < 10:
            colour = (10-self.deadage)/20*np.abs(np.array([(255*np.sin(t)),255*np.sin(t+2*np.pi/3),255*np.sin(t+4*np.pi/3)]))
            smolbox.fill(colour)
        else:
            smolbox.fill((0,0,0))
        return smolbox

def Game(path="",pixelSize=8):
    t=0
    tempPixelSize = input("What is the size of each cell? (pixels: default = 8)\n")
    if tempPixelSize != "":
        pixelSize = int(tempPixelSize)
    else:
        pixelSize = 8
    if path:
        padding = 20
        pathdict = {".":"0","O":"1"}
        f = open(path, "r")
        code = f.readlines()
        width = 0
        height = 2*padding + 2
        for line in code:
            if line[0] != "!":
                height += 1
                if len(line)+2*padding + 2 > width:
                    width = len(line)+2*padding + 2
        start = [[0 for x in range(width-2)] for y in range(height-2)]
        p = 0
        for line in code:
            if line[0] != "!":            
                text = "0"*padding+"".join([pathdict[a] for a in line.strip()]) #left padding 1 pixel
                while len(text)<width:
                    text+="0"                                      #right padding 10 plus bonus
                start[p+padding][0:width-2] = [int(num) for num in text]
                # print(text)
                p += 1


    else:
        ran = True
        tempHeight = input("How many cells tall? (default = 100)\n")

        if tempHeight != "":  
            height = int(tempHeight) + 2 #extra for dead cells at edge
        else: height = 102
        tempWidth = input("How many cells wide? (default = 100)\n")
        if tempWidth != "":
            width  = int(tempWidth) + 2
        else: width = 102

        enterPrompt = input("Would you like to specify a shape? yes/no (default = no)\n")
        if enterPrompt.lower() in ["yes","y","yup"]:
            ran = False
            print(f"Enter up to {width-2} characters for each line, with 0 being a dead cell, 1 being an alive cell.\nType go once you are done entering lines.\nNote: input will be centred left-to-right, but not top-to-bottom.")
            start = [[0 for x in range(width-2)] for y in range(height-2)]
            for line in range(height-2):
                text = input(f'Line{line+1:3d}:')
                if text == "go":
                    break
                if text == "random":
                    ran = True
                    break

                if not text.isnumeric():
                    text = "0"*width

                elif len(text) < width:
                    steps = 0
                    text  = str(text)
                    while len(text) < width:
                        if steps%2:
                            text += "0" #right padding
                            steps += 1
                        else:
                            text = "0" + text #left padding, alternating so the input is centred
                            steps += 1
                start[line][0:width-2] = [int(num) for num in text]

        if ran:
            start = [[random.randint(0,1) for x in range(width)] for y in range(height)]

    pygame.init()
    screen = pygame.display.set_mode([pixelSize*width-2*pixelSize,pixelSize*height-2*pixelSize]) #-20 here
    playarea = np.empty((height,width),dtype=Pixel) #define a h*w matrix to store data from now on
    livingPixels = []

    for y in range(1,height-1):
            for x in range(1,width-1):
                playarea[y][x] = Pixel(start[y-1][x-1],x,y,pixelSize)
                if start[y-1][x-1] == 1:
                    livingPixels += [playarea[y][x]]

            playarea[y][0] = Pixel(0,0,y,pixelSize)
            playarea[y][width-1] = Pixel(0,width-1,y,pixelSize) #edges dead

    for x in range(width):
        playarea[0][x] = Pixel(0,x,0,pixelSize)
        playarea[-1][x] = Pixel(0,x,height-1,pixelSize) #top and bottom dead
    running = True

    #the main loop!
    while running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False

        #show screen and flip
        for y in range(0,height):
            for x in range(0,width):
                screen.blit(playarea[y][x].show(t),(pixelSize*x-pixelSize,pixelSize*y-pixelSize))
        pygame.display.flip()

        t += 0.1
        #ok new idea, lets have a second matrix which stores the number of neighbours, and use that to update each cell
        neighbourMatrix = np.zeros((height,width))
        for pixel in livingPixels:
            x,y = pixel.x,pixel.y
            neighbourMatrix[y-1:y+2,x-1:x+2] += [[1,1,1],[1,0,1],[1,1,1]]
        livingPixels = []
        for y in range(1,height-1):
            for x in range(1,width-1):
                if playarea[y][x].checkState():                 #is alive
                    if neighbourMatrix[y][x] not in [2,3]:      #THE RULES
                        playarea[y][x].die()
                    else:
                        livingPixels += [playarea[y][x]]
                else:                                           #is dead
                    if neighbourMatrix[y][x] == 3:              #ALSO THE RULES
                        playarea[y][x].birth()
                        livingPixels += [playarea[y][x]]
                    else:
                        playarea[y][x].die()                 #add 1 to how long dead

args = sys.argv
if len(args) != 1: Game(*args[1:],20)
else: Game()



