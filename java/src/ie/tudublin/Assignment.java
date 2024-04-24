package ie.tudublin;

import ddf.minim.AudioBuffer;
import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;
import processing.core.PShape;
import java.util.*;


public class Assignment extends PApplet{
    Minim minim;
    AudioPlayer ap;
    AudioInput ai;
    AudioBuffer ab;

    //AudioIn input;
    FFT fft;
    PShape cube;


    int mode = 0;

    float[] lerpedBuffer;
    float y = 0;
    float smoothedY = 0;
    float smoothedAmplitude = 0;

    float lerpedFrequency;


    float scoreLow = 0;
    float scoreMid = 0;
    float scoreHi = 0;

    int numSpheres = 5; // Number of spheres in each ring
    float radius1 = 150; // Radius of the first ring
    float radius2 = 100; // Radius of the second ring
    float angleOffset = 0; // Angle offset for the second ring
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

        // Setup FFT for audio analysis
        fft = new FFT(width, 44100);

        frameRate(60);
    }


    public void draw()
    {
        //background(0);
        float halfH = height / 2;
        float average = 0;
        float sum = 0;
        
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
        
        float cx = width / 2;
        float cy = height / 2;

        switch (mode)
        {
			case 0:
            {
                background(0);
                for(int i = 0 ; i < ab.size() ; i ++)
                {
                    //float c = map(ab.get(i), -1, 1, 0, 255);
                    float c = map(i, 0, ab.size(), 0, 255);
                    stroke(c, 255, 255);
                    float f = lerpedBuffer[i] * halfH * 4.0f;
                    line(i, halfH + f, i, halfH - f);                    
                }
                break;
            }


            case 1:
            {
                background(0);
                for(int i = 0 ; i < ab.size() ; i ++)
                {
                    //float c = map(ab.get(i), -1, 1, 0, 255);
                    float c = map(i, 0, ab.size(), 0, 255);
                    stroke(c, 255, 255);
                    float f = lerpedBuffer[i] * halfH * 4.0f;
                    line(i, halfH + f, halfH - f, i);                    
                }
                break;
            }


            case 2:
            {
                float c = map(smoothedAmplitude, 0, 0.5f, 0, 255);
                background(0, 0, 0, 10);
                stroke(c, 255, 255);	
                float radius = map(smoothedAmplitude, 0, 0.1f, 50, 300);		
                int points = (int)map(mouseX, 0, 255, 3, 10);
                int sides = points * 2;
                float px = cx;
                float py = cy - radius; 
                for(int i = 0 ; i <= sides ; i ++)
                {
                    float r = (i % 2 == 0) ? radius : radius / 2; 
                    // float r = radius;
                    float theta = map(i, 0, sides, 0, TWO_PI);
                    float x = cx + sin(theta) * r;
                    float y = cy - cos(theta) * r;
                    
                    //circle(x, y, 20);
                    line(px, py, x, y);
                    px = x;
                    py = y;
                }
                break;
            }
            

            case 3:
            {
                background(0);
                strokeWeight(2);
                noFill();
                float r = map(smoothedAmplitude, 0, 0.5f, 100, 2000);
                float c = map(smoothedAmplitude, 0, 0.5f, 0, 255);
                stroke(c, 255, 255);
                circle(cx, cy, r);
                break;
            }


            case 4:
            {
                background(0);
                strokeWeight(2);
                for(int i = 0 ; i < ab.size() ; i +=10)
                {
                    //float c = map(ab.get(i), -1, 1, 0, 255);
                    float cc = map(i, 0, ab.size(), 0, 255);
                    stroke(cc, 255, 255);
                    float f = lerpedBuffer[i] * halfH * 4.0f;
                    line(i, halfH + f, i, halfH - f);
                    fill(cc);
                    circle(i, halfH + f, 5);                    
                    circle(i, halfH - f, 5);                    
                }
                break;
            }
                
            case 5:
            {
                background(0);
                strokeWeight(2);
                float angle2 = 0; // Initialize rotation angle
                for(int i = 0 ; i < ab.size() ; i += 8)
                {
                    float cc = map(i, 0, ab.size(), 0, 255); // map the color of the circle to the index of the buffer
                    stroke(cc, 255, 255); 
                    float f = lerpedBuffer[i] * halfH * 2.0f; // calculate the height of the line based on the buffer value
                    
                    pushMatrix(); // Save the current transformation matrix
                    translate(width/15, height/15); // Translate to the center of the canvas
                    rotate(angle2); // Apply rotation
                    
                    line(halfH, i, halfH + f, i); // draw a line from the center to the buffer value on the x-axis
                    line(halfH, i, halfH - f, i); // draw a line from the center to the buffer value on the x-axis
                    fill(cc); 
                    circle(halfH + f, i, 10); // draw a circle at the end of the line on the x-axis
                    circle(halfH - f, i, 10); // draw a circle at the end of the line on the x-axis
                    stroke(255);
                    noFill();
                    
                    popMatrix(); // Restore the previous transformation matrix
                }

                angle2 += 0.01; // Increment rotation angle
                break;
            }
            
            case 6:
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

                for (int i = 0; i < numCubes; i++)
                {
                    pushMatrix();
                    float angle = map(i, 0, numCubes, 0, TWO_PI);
                    float x = spacing * cos(angle);
                    float y = spacing * sin(angle);
                    translate(x, y);
                    rotateZ(frameCount * 0.01f);
                    shape(cube);
                    box(200);
                    popMatrix();
                }
                break;
            }
            

            case 7: 
            {
                background(0);
                strokeWeight(2);
                translate(width / 2, height / 2);
                rotateX(frameCount * 0.01f);
                rotateY(frameCount * 0.01f);
                noFill();

                float colour = map(smoothedAmplitude, 0, 0.5f, 0, 255);
                //stroke(colour, 255, 255);
                
                //float amplitude = map(smoothedAmplitude, 0, 0.5f, 50, 100);
                float frequency = map(smoothedAmplitude, 0, 0.5f, 3, 150);
                float radius = map(smoothedAmplitude, 0, 0.5f, 50, 1000);
                for (int i = 0; i < frequency; i++)
                {
                    float angle = map(i, 0, frequency, 0, TWO_PI); 
                    float x = cos(angle) * radius; 
                    float y = sin(angle) * radius;
                    float z = lerpedBuffer[i] * smoothedAmplitude * 4.0f;
                    
                    stroke(80, 255, 255);
                    pushMatrix();
                    translate(x, y, z);
                    sphere(20); // the size of the sphere
                    
                    popMatrix();
                    
                    // Adjusted position to create the impression of crossing
                    angle += angleOffset;
                    float crossingX = cos(angle) * radius; 
                    float crossingY = sin(angle) * radius;
                    
                    pushMatrix();
                    translate(0, crossingX, crossingY);
                    sphere(20);
                    popMatrix();

                    
                    pushMatrix();
                    translate(crossingX, 0, crossingY);  
                    sphere(20);
                    popMatrix();
                    
                    stroke(colour, 255, 255);

                }
                
                angleOffset += 0.1;
                noFill();
                PShape cubeShape = createShape(BOX, 100);
               
                for(int i = 0; i < ab.size(); i += 12)
                {
                    cc = map(i, 0, ab.size(), 0, 255);
                    float f = lerpedBuffer[i] * halfH * 4.0f;
                    
                    shape(cubeShape, i, f);
                    rotateZ(frameCount * 0.01f);
                    cubeShape.scale(2);
                }
                break;
            }
        
        
            case 8: 
            {
                background(0);
                translate(width / 2, height / 2);
                frameRate(60);
                float rotationAngle = frameCount * 0.02f; // Adjust rotation speed
                
                rotate(rotationAngle); // Apply rotation
                
                int segments = 250; // Number of segments in the spiral
                float segmentAngle = TWO_PI / segments;
                float maxRadius = min(width, height) * 0.4f;
            
              
            
                for (int i = 0; i < segments; i+= 1)
                {
                    float angle = i * segmentAngle;
                    
            
                    float radius = maxRadius + smoothedAmplitude * 500; // Adjust multiplier for size

                    
                
                    float x1 = cos(angle) * radius;
                    float y1 = sin(angle) * radius;
                    float nextAngle = angle + segmentAngle;

                
                    float x2 = cos(nextAngle) * (radius - smoothedAmplitude * 0.5f); // Adjust segment length
                    float y2 = sin(nextAngle) * (radius - smoothedAmplitude * 0.5f); // Adjust segment length

                    line(x1, y1, x2, y2);
                
                    for (int j = 0; j < ab.size(); j++) 
                    {
                        float cc = map(i, 0, ab.size(), 0, 255);
                        stroke(cc, 255, 255);
                        float f = lerpedBuffer[i] * halfH * 6.0f;
                        line(x1, y1 + f, x2, y2 - f);
                    }
                }

                
                break;
            }
        
                
                
        
            case 9:
            {
                background(0);
                translate(width/2, height/2, 0); // Move to the center of the screen
                rotateX(frameCount * 0.001f);
                rotateY(frameCount * 0.001f);
                
                //screenZ(mouseX * 0.01f, 0, 0); // Rotate around the x-axis based on the mouse position
                // rotateY(mouseY * 0.01f); // Rotate around the y-axis based on the mouse position
                // rotateZ(mouseX * 0.01f); // Rotate around the z-axis based on the mouse wheel

                frameRate(60);
            
                //Draw second ring
                for (int i = 0; i < numSpheres; i++)
                {
                    
                    float angle = map(i, 0, numSpheres, 0, TWO_PI) + angleOffset;
                    float x = radius2 * cos(angle);
                    float y = radius2 * sin(angle);
                    noFill();
                    PShape cubeShape = createShape(BOX, 100);
                    
                    for(int k = 0; k < ab.size(); k += 50)
                    {
                        float cc = map(k, 0, ab.size(), 0, 255);
                        
                        float f = lerpedBuffer[k] * halfH * 4.0f;
                        shape(cubeShape, x, f);
                        
                        stroke(cc, 255, 255);
                    }
                
                
                    for (int j = 0; j < ab.size(); j += 30)
                    {
                        float cc = map(j, 0, ab.size(), 0, 255);
                        stroke(cc, 255, 255);
                        float f = lerpedBuffer[j] * halfH * 6.0f; // calculate the height of the line based on the buffer value
                        line(x, y + f, x, y - f);
                        fill(cc, 255, 255);
                        translate(0, x, y);
                        circle(x, y + f, 20);                    
                        circle(x, y - f, 20);
                        
                    }
                }

                // Update angle offset to make the rings cross each other
                angleOffset += 0.009f;
                break; 
            }


        
        }

    }
}



