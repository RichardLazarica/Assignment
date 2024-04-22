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

    int numSpheres = 30; // Number of spheres in each ring
    float radius1 = 150; // Radius of the first ring
    float radius2 = 100; // Radius of the second ring
    float angleOffset = 0; // Angle offset for the second ring
    


    public void keyPressed() {
		if (key >= '0' && key <= '9') {
			mode = key - '0';
		}
		if (keyCode == ' ') {
            if (ap.isPlaying()) {
                ap.pause();
            } else {
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



    
    public void drawTree(float x, float y, float trunkHeight, float angle, int levels, AudioPlayer player) {
        float branchAngle = PI / 4;
        float branchLength = trunkHeight / (levels + 1);
        float leafSize = branchLength / 2;
    
        // Draw trunk
        stroke(139, 69, 19); // brown color
        strokeWeight(10);
        line(x, y, 0, x, y - trunkHeight, 0); // Draw trunk in 3D
    
        // Draw branches and leaves
        for (int i = 0; i < levels; i++) {
            float branchHeight = y - (i + 1) * branchLength;
            float branchWidth = branchLength * tan(branchAngle);
    
            // Draw branches
            stroke(139, 69, 19); // brown color
            strokeWeight(5);
            line(x, branchHeight, 0, x - branchWidth, branchHeight - branchLength, 0); // Draw branch in 3D
            line(x, branchHeight, 0, x + branchWidth, branchHeight - branchLength, 0); // Draw branch in 3D
    
            // Draw additional branches
            float newBranchWidth = branchWidth * 0.8f; // Adjust the width of the new branches
            float newBranchLength = branchLength * 0.8f; // Adjust the length of the new branches
    
            line(x - branchWidth, branchHeight - branchLength, 0, x - newBranchWidth, branchHeight - branchLength - newBranchLength, 0); // Draw branch in 3D
            line(x + branchWidth, branchHeight - branchLength, 0, x + newBranchWidth, branchHeight - branchLength - newBranchLength, 0); // Draw branch in 3D
    
            // Draw leaves
            stroke(144, 238, 144); // light green color
            fill(34, 139, 34);
            ellipse(x, branchHeight - branchLength, 0, leafSize); // Draw leaves in 3D
        }
    }

    
    // public void drawTree(float x, float y, float trunkHeight, float angle, int levels) {
    //     float branchAngle = PI / 4;
    //     float branchLength = trunkHeight / (levels + 1);
    //     float leafSize = branchLength / 2;

    //     // Draw trunk
    //     stroke(139, 69, 19); // brown color
    //     strokeWeight(10);
    //     line(x, y, x, y - trunkHeight);

    //     // Draw branches and leaves
    //     for (int i = 0; i < levels; i++) {
    //         float branchHeight = y - (i + 1) * branchLength;
    //         float branchWidth = branchLength * tan(branchAngle);

    //         // Draw branches
    //         stroke(139, 69, 19); // brown color
    //         strokeWeight(5);
    //         line(x, branchHeight, x - branchWidth, branchHeight - branchLength);
    //         line(x, branchHeight, x + branchWidth, branchHeight - branchLength);

    //         // Draw leaves
    //         stroke(34, 139, 34); // green color
    //         strokeWeight(1);
    //         fill(34, 139, 34); // green color
    //         ellipse(x - branchWidth, branchHeight - branchLength, leafSize, leafSize);
    //         ellipse(x + branchWidth, branchHeight - branchLength, leafSize, leafSize);
    //     }
    // }
    // public void drawTree(float x, float y, float trunkHeight, float angle, int levels, float amplitude) {
    //     float branchAngle = PI / 4;
    //     float branchLength = trunkHeight / (levels + 1);
    //     float leafSize = branchLength / 2;
    
    //     // Use amplitude to determine tree height
    //     float treeHeight = trunkHeight * amplitude;
    
    //     // Draw trunk
    //     stroke(139, 69, 19); // brown color
    //     strokeWeight(10);
    //     line(x, y, x, y - treeHeight);
    
    //     // Draw branches and leaves
    //     for (int i = 0; i < levels; i++) {
    //         float branchHeight = y - (i + 1) * branchLength;
    //         float branchWidth = branchLength * tan(branchAngle);
    
    //         // Draw branches
    //         stroke(139, 69, 19); // brown color
    //         strokeWeight(5);
    //         line(x, branchHeight, x - branchWidth, branchHeight - branchLength);
    //         line(x, branchHeight, x + branchWidth, branchHeight - branchLength);
    
    //         // Draw leaves
    //         stroke(34, 139, 34); // green color
    //         strokeWeight(1);
    //         fill(34, 139, 34); // green color
    //         ellipse(x - branchWidth, branchHeight - branchLength, leafSize, leafSize);
    //         ellipse(x + branchWidth, branchHeight - branchLength, leafSize, leafSize);
    //     }
    // }

    float off = 0;

    public void draw()
    {
        //background(0);
        float halfH = height / 2;
        float average = 0;
        float sum = 0;
        off += 1;
        // Calculate sum and average of the samples
        // Also lerp each element of buffer;
        for(int i = 0 ; i < ab.size() ; i ++)
        {
            sum += abs(ab.get(i));
            lerpedBuffer[i] = lerp(lerpedBuffer[i], ab.get(i), 0.05f);
        }
        average= sum / (float) ab.size();

        smoothedAmplitude = lerp(smoothedAmplitude, average, 0.1f);
        
        float cx = width / 2;
        float cy = height / 2;

        

        switch (mode) {
			case 0:
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
        case 1:
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
            }
            break;
        case 3:
            background(0);
            strokeWeight(2);
            noFill();
            float r = map(smoothedAmplitude, 0, 0.5f, 100, 2000);
            float c = map(smoothedAmplitude, 0, 0.5f, 0, 255);
            stroke(c, 255, 255);
            circle(cx, cy, r);
            break;
        case 4:
        
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
            
            case 5:
            background(0);
            strokeWeight(2);
            float angle2 = 0; // Initialize rotation angle
            for(int i = 0 ; i < ab.size() ; i += 8) {
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

            for (int i = 0; i < numCubes; i++) {
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
        }
        break;
        
        
        case 7: {
            fft.window(FFT.HAMMING);
            fft.forward(ab);
            background(0);
            strokeWeight(2);
            translate(width / 2, height / 2);
            rotateX(frameCount * 0.01f);
            rotateY(frameCount * 0.01f);
            noFill();
            
            colorMode(RGB);
            
            float amplitude = map(smoothedAmplitude, 0, 0.5f, 50, 100);
            float frequency = map(smoothedAmplitude, 0, 0.5f, 3, 150);
            float radius = map(smoothedAmplitude, 0, 0.5f, 50, 1000);
            for (int i = 0; i < frequency; i++) {
              float angle = map(i, 0, frequency, 0, TWO_PI); 
              float x = cos(angle) * radius; 
              float y = sin(angle) * radius;
              float z = lerpedBuffer[i] * amplitude * 4.0f;

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
              

            }
            angleOffset += 0.05;
            noFill();
            PShape cubeShape = createShape(BOX, 100);
            cubeShape.setStroke(color(3, 250, 58));
            //cubeShape.setFill(color(3, 160, 98));
            for(int i = 0; i < ab.size(); i += 10) {
                float f = lerpedBuffer[i] * halfH * 4.0f;
                shape(cubeShape, i, f);
                cubeShape.scale(2);
            }
            

            
            break;
          }
          
            

        // // Draw the sun
        // fill(255, 204, 0); // yellow color
        // noStroke(); // remove the outline
        // ellipse(width - 100, 100, 80, 80); // draw a circle at (width - 100, 100) with a diameter of 80
        // stroke(255); // restore the outline for the grass
        
        // // Draw clouds
        // fill(255); // white color
        // noStroke(); // remove the outline
        // ellipse(100, 100, 100, 50); // draw the main body of the cloud
        // ellipse(150, 100, 70, 70); // draw the right part of the cloud
        // ellipse(50, 100, 70, 70); // draw the left part of the cloud
        // stroke(255); // restore the outline for the grass
        
        // // Get the current amplitude of the audio
        // float amplitude1 = ap.mix.level() * 500;
        // // Draw grass shapes that react to the audio
        // for (int i = 0; i < width; i += 20) {
        //     // The height of each grass shape is influenced by the audio amplitude
        //     float grassHeight = height - amplitude1 - random(50);
        //     float wind = sin(frameCount * 0.01f) * 50;
        //     // Add a sin wave to the x position of each grass shape to create a swaying motion
        //     float sway = sin(frameCount * 0.05f + i * 0.1f) * (amplitude1 * 0.05f) + wind;
        //     // Make the grass grow taller based on the audio amplitude
        //     float grow = map(amplitude1, 0, 500, 0, height);
        //     line(i + sway, height, i + sway, grassHeight - grow);
        // }
        // break;
        
        // case 9:
        // {
        //     background(135, 206, 250); // sky blue color
        //     noStroke();
        //     fill(34, 139, 34); // green color
        //     rect(0, height / 2, width, height / 2); // draw the ground
        //     for (int i = 0; i < ab.size(); i += 10) {
        //         float cc = map(i, 0, ab.size(), 0, 255);
        //         stroke(cc, 255, 255);
        //         float treeHeight = lerpedBuffer[i] * height;
        //         drawTree(i, height, treeHeight, -PI / 2, 6);
        //     }
        //     break;
        // }
        // case 9:
 

        // // Calculate the tree height based on the amplitude
        // //float trunkHeight = smoothedAmplitude * halfH * 0.5f + halfH * 0.5f + 100 * sin(frameCount * 0.05f) + 100 * cos(frameCount * 0.05f) + 100 * sin(frameCount * 0.05f);  
        // float trunkHeight = halfH - smoothedAmplitude * height;
        
        // // Clear the screen
        // background(255);

        // // Draw the tree
        // drawTree(width / 2, height, trunkHeight, PI / 4, 6, ap); // draw the tree with 6 levels of branches and leaves 

        case 8: {
            background(0);
            translate(width / 2, height / 2);
            
            float rotationAngle = frameCount * 0.01f; // Adjust rotation speed
            
            rotate(rotationAngle); // Apply rotation
            
            int segments = 300; // Number of segments in the spiral
            float segmentAngle = TWO_PI / segments;
            float maxRadius = min(width, height) * 0.4f;
            float timeScale = 0.01f; // Controls the speed of animation
            float hueOffset = 0; // Initial hue offset
          
            for (int i = 0; i < segments; i++) {
              float angle = i * segmentAngle;
              float timeOffset = angle * timeScale; // Use angle to vary time offset
              float lerpedAmplitude = lerp(smoothedAmplitude, ab.get(i), 0.05f); // Smoothly transition amplitude
              float radius = maxRadius + lerpedAmplitude * 500; // Adjust multiplier for size
              float hue = map(angle + hueOffset, 0, TWO_PI, 0, 255);
              float brightness = map(lerpedAmplitude, 0, 1, 50, 255); // Adjust mapping for brightness
              stroke(hue, 255, brightness);
              float x1 = cos(angle) * radius;
              float y1 = sin(angle) * radius;
              float nextAngle = angle + segmentAngle;
              float x2 = cos(nextAngle) * (radius - lerpedAmplitude * 0.5f); // Adjust segment length
              float y2 = sin(nextAngle) * (radius - lerpedAmplitude * 0.5f); // Adjust segment length
              line(x1, y1, x2, y2);
              for (int j = 0; j < ab.size(); j++) {
                float cc = map(i, 0, ab.size(), 0, 255);
                stroke(cc, 255, 255);
                float f = lerpedBuffer[i] * halfH * 6.0f;
                line(x1, y1 + f, x2, y2 - f);
              }
            }
            
            hueOffset += 0.5; // Increment hue offset for color variation
            break;
          }
        
        
        case 9:
        {
            background(255);
            translate(width/2, height/2, 0); // Move to the center of the screen
            rotateX(frameCount * 0.01f);
            rotateY(frameCount * 0.01f);
            // Draw first ring
            for (int i = 0; i < numSpheres; i++) {
                float angle1 = map(i, 0, numSpheres, 0, TWO_PI);
                float x1 = radius1 * cos(angle1);
                float y1 = radius1 * sin(angle1);
                pushMatrix();
                translate(x1, y1, 0);
                sphere(20);
                popMatrix();
            }
            
            // Draw second ring
            for (int i = 0; i < numSpheres; i++) {
                float angle3 = map(i, 0, numSpheres, 0, TWO_PI) + angleOffset;
                float x2 = radius2 * cos(angle3);
                float y2 = radius2 * sin(angle3);
                pushMatrix();
                translate(0, x2, y2);
                sphere(20);
                popMatrix();
            }
            
            // Update angle offset to make the rings cross each other
            angleOffset += 0.05;
            }
        
}
}
}


