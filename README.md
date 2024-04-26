# Abstract Visual Story of a Song Team Project

Name: Richard Lazarica

Student Number: C22739341
 
Class Group: B

Github: https://github.com/RichardLazarica/Assignment

# Video

[![YouTube](https://github.com/RichardLazarica/Assignment/blob/master/java/data/ThreeVisuals.PNG)](https://www.youtube.com/watch?v=nitplYxNNro)

# Screenshots

![First Visual](https://github.com/RichardLazarica/Assignment/blob/master/java/data/FirstVisual.PNG)


![Second Visual](https://github.com/RichardLazarica/Assignment/blob/master/java/data/SecondVisual.PNG)


![Third Visual](https://github.com/RichardLazarica/Assignment/blob/master/java/data/ThreeVisuals.PNG)

# Description of the project
Program that visualizes audio data using Processing and Minim libraries.
The program reads audio data from a file and displays it in various ways based on the user's input.
The user can switch between different visualizations by pressing keys from 0 to 2.

# How it works:
The user chooses between various visualisations by pressing keyboards keys between 0 - 2

First visualisation (Ellipses with circle radius): 
Draws ellipses(circles) whose radii are determined by the amplitude of audio samples.
It calculates the maximum radius of the circle based on screen size and divides the circle into equal parts based on the audio buffer size.
Each circle's radius corresponds to the amplitude of the audio at a particular point in the buffer.


Second visualisation (Lines and circles DNA Strain):
Lines and circles are drawn based on the amplitude of audio samples.
Lines are drawn vertically with heights determined by the amplitude, and circles are drawn at the endpoints of each line.
The entire set of lines and circles rotates gradually, imitating a DNA strain.

Third visualisation (Cubes):
Draws cubes arranged in a circular pattern, where the size and position of each cube are influenced by the amplitude of the audio.
Cubes are drawn in a circle, and their positions are calculated based on trigonometric functions.
The color of the cubes is mapped to the smoothed amplitude of the audio.


# List of classes/assets in the project

| Class/asset | Source |
|-----------|-----------|
| Assignment.java | Self written |


# What I am most proud of in the assignment

Richard Lazarica: I am proud that I was able to create a music visualiser even though I'm not good at handling problems or projects that have or represents something graphical.

# What I learned

Richard Lazarica: I have learned about OOP and how to use them adequately with graphic library in order to represent frequencies visually. It helped refine my knowledge I previously had about OOP principles and learned how to create graphical animated objects from scratch.

