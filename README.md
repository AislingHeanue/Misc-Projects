# Projects
Some miscellaneous projects I have completed in the past.

## Rubik's Cube simulator
A 3D simulator for a 3x3 Rubik's Cube, with keyboard and mouse controls. Hold Shift to rotate a face anticlockwise, and hold Control to do a wide move. Press + and - to change the camera zoom.

It can be run using the included JAR file. It was compiled with corretto-1.8_362, and should be run with Java 1.8 and JavaFX. To run this program, do:

    $ java -Dprism.forceGPU=true -jar Rubiks\ Cube.jar

## Game of Life
A simulator for Conway's Game of Life. There isn't much of a user interface right now but for a demo with the default settings you can do `make run` inside Game of Life/Go. This requires Go and Make to be installed.

## Sine Graph Generator
A simple script for generating the graph of some pendulum animations. Its output is the GIF contained in its folder.

## QR Code Generator
This notebook can generate QR codes for any prompts you give it up to a certain size. The arguments for its main function (which can be edited in the last line of the last cell) are the input string, the size of the QR Code (starting from 1, bigger numbers create a larger QR code and can encode more data), and the level of data redundancy/correction (0 is lowest, 3 is highest). This code is a bit of a mess and I might refactor it later.
