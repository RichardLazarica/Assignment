package ie.tudublin;

import com.jogamp.opengl.Threading.Mode;

import ddf.minim.AudioBuffer;
import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;
import processing.core.PShape;

public class Audio1 extends PApplet
{
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
        //size(1024, 1000, P3D);
        fullScreen(P3D, SPAN);
    }

    public void setup()
    {
        minim = new Minim(this);
        // Uncomment this to use the microphone
        // ai = minim.getLineIn(Minim.MONO, width, 44100, 16);
        // ab = ai.mix; 
        ap = minim.loadFile("heroplanet.mp3", 1024);
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
        fft = new FFT(ap.bufferSize(), ap.sampleRate());
    }

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
            float amplitude = fft.getAvg(ARGB);
                
            // Calculate the movement towards the screen based on amplitude
            float zMovement = map(amplitude, 0, 1, -200, 0);
            
            // Set the position of the cube
            float x = width / 2;
            float y = height / 2;
            float z = -200;  // Start position behind the screen
            translate(x, y, z + zMovement);  // Move the cube towards the screen
            
            // Draw the cube
            shape(cube);
            break;
        }
        


        
        // Other examples we made in the class
        /*
        stroke(255);
        fill(100, 255, 255);        
        
        circle(width / 2, halfH, lerpedA * 100);

        circle(100, y, 50);
        y += random(-10, 10);
        smoothedY = lerp(smoothedY, y, 0.1f);        
        circle(200, smoothedY, 50);
        */

    }        
}
