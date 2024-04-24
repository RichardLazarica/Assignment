/*
 * Author: Richard Lazarica
 * 
 * Description: Program that visualizes audio data using Processing and Minim libraries.
 * The program reads audio data from a file and displays it in various ways based on the user's input.
 * The user can switch between different visualizations by pressing keys from 0 to 9.
 * 
 */


package ie.tudublin;

import ddf.minim.AudioBuffer;
import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.core.PShape;





public class Assignment extends PApplet{
    Minim minim;
    AudioPlayer ap;
    AudioInput ai;
    AudioBuffer ab;


    PShape cube;



    int mode = 0;

    float[] lerpedBuffer;
    float y = 0;
    float smoothedY = 0;
    float smoothedAmplitude = 0;

    float lerpedFrequency;


    //Case 9 VARIABLES
    int numCorners = 5; // Number of corners
    float radius1 = 150; // Radius of the first ring
    float radius2 = 100; // Radius of the second ring
    float angleOffset = 0; // Angle offset for the second ring

    //Global variable for the color
    float cc;




    public void keyPressed() 
    {
		if (key >= '0' && key <= '9')
        {
			mode = key - '0';
		}
		if (keyCode == ' ')
        {
            if (ap.isPlaying())
            {
                ap.pause();
            } 
            else
            {
                ap.rewind();
                ap.play();
            }
        }
	}

    public void settings()
    {
        size(1024, 1000, P3D);

        //fullScreen(P3D, SPAN);
    }

    public void setup()
    {
        minim = new Minim(this);
        
        ai = minim.getLineIn(Minim.MONO, width, 44100, 16);
        ab = ai.mix; 
        ap = minim.loadFile("InfectedMushroom.mp3", 1024);
        ap.play();
        ab = ap.mix;
        colorMode(HSB);

        y = height / 2;
        smoothedY = y;

        lerpedBuffer = new float[width];

        cube = createShape(BOX, 50);
        cube.setStroke(color(255));
        cube.setFill(color(255, 0, 0));
    }


    public void draw()
    {
        background(0);
        float halfH = height / 2;
        float average = 0;
        float sum = 0;
        frameRate(60);


        // Calculate sum and average of the samples
        // Also lerp each element of buffer;
        for(int i = 0 ; i < ab.size() ; i ++)
        {
            sum += abs(ab.get(i));
            lerpedBuffer[i] = lerp(lerpedBuffer[i], ab.get(i), 0.05f);
            cc = map(i, 0, ab.size(), 0, 255);
        }
        average= sum / (float) ab.size();

        // used for smoothing the amplitude to make it less jittery
        // and be used on the screen to make it more visually appealing
        smoothedAmplitude = lerp(smoothedAmplitude, average, 0.1f);


        switch (mode)
        {
		
            // Draw circles based on amplitude
            case 0:
            {                
                background(0);
                translate(width / 2, height / 2); // Move the origin to the center of the screen

                float maxRadius = min(width, height) * 4.2f; // Maximum radius for the circles
                float step = TWO_PI / ab.size(); // Angle step between each circle
                float angle = 0; // Initial angle

                for (int i = 0; i < ab.size(); i++) {
                    float radius = maxRadius * lerpedBuffer[i]; // Radius of the circle based on amplitude
                    float x = cos(angle) * radius; // Calculate x position
                    float y = sin(angle) * radius; // Calculate y position

                    float c = map(i, 0, ab.size(), 0, 255); // Map color based on index
                    stroke(c, 255, 255); // Set stroke color
                    noFill(); // No fill for circles
                    ellipse(x, y, 20, 20); // Draw the circle

                    angle += step; // Increment angle for the next circle
                }
                break;
            }
            
            
            // Draw lines and circles based on amplitude with rotation
            case 1:
            {
                background(0);
                strokeWeight(2);
                float angle2 = 0;

                // Loop through the audio buffer and draw lines and circles
                for(int i = 0 ; i < ab.size() ; i += 8)
                {
                    float cc = map(i, 0, ab.size(), 0, 255);
                    stroke(cc, 255, 255); 
                    float f = lerpedBuffer[i] * halfH * 2.0f; //height of the line based on the buffer value
                    
                    pushMatrix(); // Save the current transformation matrix
                    translate(width/15, height/15); // Translate to the center of the canvas
                    rotate(angle2); // Apply rotation
                    
                    line(halfH, i, halfH + f, i); 
                    line(halfH, i, halfH - f, i); 
                    fill(cc);

                    circle(halfH + f, i, 10); 
                    circle(halfH - f, i, 10); 
                    stroke(255);
                    noFill();
                    
                    popMatrix(); // Restore the transformation matrix
                }
                angle2 += 0.1; // Increment rotation angle
                break;
            }
            
            // Draw cubes in a circle based on amplitude
            case 2:
            {
                float colour = map(smoothedAmplitude, 0, 0.5f, 0, 255);
                background(0);
                strokeWeight(2);
                translate(width / 2, height / 2);
                rotateX(frameCount * 0.01f);
                rotateY(frameCount * 0.01f);
                noFill();
                stroke(colour, 255, 255);

                int numCubes = 5; // Number of cubes to display
                float spacing = 200; // Spacing between cubes

                // Draw cubes in a circle
                for (int i = 0; i < numCubes; i++)
                {
                    pushMatrix();
                    float angle = map(i, 0, numCubes, 0, TWO_PI);
                    float x = spacing * cos(angle);
                    float y = spacing * sin(angle);
                    translate(x, y, 100);
                    rotateZ(frameCount * 0.01f);
                    shape(cube);
                    box(200);
                    popMatrix();
                }
                break;
            }
            
        

        }

    }
}



