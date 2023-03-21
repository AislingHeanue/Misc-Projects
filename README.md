# Projects
Some miscellanious projects I have completed in the past.

## Board Game
A projcect I completed as part of my Java programming course. The libraries used for this project are corretto-1.8_352 and Junit5.8.1, though any version of Java 1.8 with support for JavaFX should work.

To run this program, first compile it and then do::

    $ cd out/production/Simon-Race
    $ java boardGame.Launcher

then enter the board size and number of players.
Alternatively, the included JAR file can be used.

    $ java -jar Board_Game.jar

## Rubik's Cube simulator
A 3D simulator for a 3x3 Rubik's Cube, with keyboard and mouse controls. Hold Shift to rotate a face anticlockwise, and hold Control to do a wide move. Press + and - to change the camera zoom.

It can be run using the included JAR file. It was compiled with corretto-1.8_362, and should be run with Java 1.8 and JavaFX. To run this program, do:

    $ java -Dprism.forceGPU=true -jar Rubiks\ Cube.jar

## Game of Life
A simulator for Conway's Game of Life. To run it, make sure you have pygame installed with ``pip install pygame`` and then either do:

    $ python Game\ of\ Life.py
or

    $ python Game\ of\ Life.py /path/to/txt/file

if you would like to run one of the preloaded patterns. There are many more patterns available on the [Conway's Game of Life Wiki](https://conwaylife.com/wiki/) (download files in plaintext format for use here).

## Sine Graph Generator
A simple script for generating the graph of some pendulum animtations. Its output is the GIF contained in its folder.

## QR Code Generator
This notebook can generate QR codes for any prompts you give it up to a certain size. The arguments for its main function (which can be edited in the last line of the last cell) are the input string, the size of the QR Code (starting from 1, bigger nunbers create a larger QR code and can encode more data), and the level of data redundancy/correction (0 is lowest, 3 is highest). This code is a bit of a mess and I might refactor it later.
