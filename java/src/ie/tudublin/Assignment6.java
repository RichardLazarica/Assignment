package ie.tudublin;

import ddf.minim.AudioBuffer;
import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;
import processing.core.PShape;

public class Assignment6 extends PApplet {

    Minim minim;
    AudioPlayer ap;
    AudioBuffer ab;
    FFT fft;

    int mode = 0;

    float[] lerpedBuffer;
    float smoothedAmplitude = 0;

    PShape cube;

    public void settings() {
        size(1024, 1000, P3D);
    }

    public void setup() {
        minim = new Minim(this);
        ap = minim.loadFile("InfectedMushroom.mp3", 1024);
        ap.play();
        ab = ap.mix;

        fft = new FFT(width, 44100);
        colorMode(HSB);

        lerpedBuffer = new float[width];

        cube = createShape(BOX, 50);
        cube.setStroke(color(255));
        cube.setFill(color(255, 0, 0));

        frameRate(60);
    }

    public void draw() {
        background(0);
    
        float average = 0;
        float sum = 0;
        for (int i = 0; i < ab.size(); i++) {
            sum += abs(ab.get(i));
            lerpedBuffer[i] = lerp(lerpedBuffer[i], ab.get(i), 0.05f);
        }
        average = sum / (float) ab.size();
        smoothedAmplitude = lerp(smoothedAmplitude, average, 0.1f);
    
        switch (mode) {
            case 0:
                drawWaveform();
                break;
            case 1:
                drawWaveformRotated();
                break;
            case 2:
                drawPulsatingSphere();
                break;
            case 3:
                drawCircularSpectrum();
                break;
            case 4:
                drawBars();
                break;
            case 5:
                drawRotatingBars();
                break;
            case 6:
                drawRotatingCube();
                break;
            case 7:
                drawSphereField();
                break;
            case 8:
                drawSpiral();
                break;
            case 9:
                // Add your own visualization here
                drawStarfield();
                break;
        }
    }
    
    public void drawCircularSpectrum() {
        float cx = width / 2;
        float cy = height / 2;
        float colour = map(smoothedAmplitude, 0, 0.5f, 0, 255);
        stroke(colour, 255, 255);
        float amplitude = map(smoothedAmplitude, 0, 0.5f, 50, 300);
        float frequency = map(smoothedAmplitude, 0, 0.5f, 3, 10);
        float radius = map(smoothedAmplitude, 0, 0.5f, 50, 300);
        for (int i = 0; i < frequency; i++) {
            float angle = map(i, 0, frequency, 0, TWO_PI);
            float x = cos(angle) * radius;
            float y = sin(angle) * radius;
            float z = lerpedBuffer[i] * amplitude;
            pushMatrix();
            translate(cx + x, cy + y, z);
            sphere(10);
            popMatrix();
        }
    }
    
    public void drawBars() {
        float halfH = height / 2;
        for (int i = 0; i < ab.size(); i += 10) {
            float cc = map(i, 0, ab.size(), 0, 255);
            stroke(cc, 255, 255);
            float f = lerpedBuffer[i] * halfH * 4.0f;
            line(i, halfH + f, i, halfH - f);
            fill(cc);
            circle(i, halfH + f, 5);
            circle(i, halfH - f, 5);
        }
    }
    
    public void drawRotatingBars() {
        float halfH = height / 2;
        for (int i = 0; i < ab.size(); i += 10) {
            float cc = map(i, 0, ab.size(), 0, 255);
            stroke(cc, 255, 255);
            float f = lerpedBuffer[i] * halfH * 4.0f;
            line(i, halfH + f, halfH - f, i);
            fill(cc);
            circle(halfH + f, i, 5);
            circle(halfH - f, i, 5);
        }
    }
    
    public void drawRotatingCube() {
        float colour = map(smoothedAmplitude, 0, 0.5f, 0, 255);
        background(0);
        strokeWeight(2);
        translate(width / 2, height / 2);
        rotateX(frameCount * 0.01f);
        rotateY(frameCount * 0.01f);
        noFill();
        stroke(colour, 255, 255);
        shape(cube);
        box(200);
    }
    
    public void drawSphereField() {
        fft.window(FFT.HAMMING);
        fft.forward(ab);
        float amplitude = map(smoothedAmplitude, 0, 0.5f, 50, 300);
        float frequency = map(smoothedAmplitude, 0, 0.5f, 3, 10);
        float radius = map(smoothedAmplitude, 0, 0.5f, 50, 300);
        for (int i = 0; i < frequency; i++) {
            float angle = map(i, 0, frequency, 0, TWO_PI);
            float x = cos(angle) * radius;
            float y = sin(angle) * radius;
            float z = lerpedBuffer[i] * amplitude;
            pushMatrix();
            translate(x, y, z);
            sphere(10);
            popMatrix();
        }
    }
    
    public void drawSpiral() {
        float cx = width / 2;
        float cy = height / 2;
        float segmentAngle = TWO_PI / 300;
        float maxRadius = min(width, height) * 0.4f;
        float timeScale = 0.01f;
        float hueOffset = 0;
        for (int i = 0; i < 300; i++) {
            float angle = i * segmentAngle;
            float timeOffset = angle * timeScale;
            float lerpedAmplitude = lerp(smoothedAmplitude, ab.get(i), 0.05f);
            float radius = maxRadius + lerpedAmplitude * 500;
            float hue = map(angle + hueOffset, 0, TWO_PI, 0, 255);
            float brightness = map(lerpedAmplitude, 0, 1, 50, 255);
            stroke(hue, 255, brightness);
            float x1 = cos(angle) * radius;
            float y1 = sin(angle) * radius;
            float nextAngle = angle + segmentAngle;
            float x2 = cos(nextAngle) * (radius - lerpedAmplitude * 0.5f);
            float y2 = sin(nextAngle) * (radius - lerpedAmplitude * 0.5f);
            line(cx + x1, cy + y1, cx + x2, cy + y2);
        }
        hueOffset += 0.1;
    }

    public void keyPressed() {
        if (key >= '0' && key <= '9') {
            mode = key - '0';
        }
    }

    public void drawWaveform() {
        float halfH = height / 2;
        for (int i = 0; i < ab.size(); i++) {
            float c = map(i, 0, ab.size(), 0, 255);
            stroke(c, 255, 255);
            float f = lerpedBuffer[i] * halfH * 4.0f;
            line(i, halfH + f, i, halfH - f);
        }
    }

    public void drawWaveformRotated() {
        float halfH = height / 2;
        for (int i = 0; i < ab.size(); i++) {
            float c = map(i, 0, ab.size(), 0, 255);
            stroke(c, 255, 255);
            float f = lerpedBuffer[i] * halfH * 4.0f;
            line(i, halfH + f, halfH - f, i);
        }
    }

    public void drawPulsatingSphere() {
        float cx = width / 2;
        float cy = height / 2;
        float colour = map(smoothedAmplitude, 0, 0.5f, 0, 255);
        stroke(colour, 255, 255);
        float radius = map(smoothedAmplitude, 0, 0.1f, 50, 300);
        int points = (int) map(mouseX, 0, 255, 3, 10);
        int sides = points * 2;
        float px = cx;
        float py = cy - radius;
        for (int i = 0; i <= sides; i++) {
            float r = (i % 2 == 0) ? radius : radius / 2;
            float theta = map(i, 0, sides, 0, TWO_PI);
            float x = cx + sin(theta) * r;
            float y = cy - cos(theta) * r;
            line(px, py, x, y);
            px = x;
            py = y;
        }
    }

    public void drawExplodingCircles() {
        strokeWeight(3);
        float cx = width / 2;
        float cy = height / 2;
        float maxRadius = min(width, height) * 0.5f;
        float minRadius = 100;
        float explosionFactor = map(smoothedAmplitude, 0, 0.5f, 10, 20);
        float hueOffset = 0;
        for (int i = 0; i < 100; i++) {
            float angle = random(TWO_PI);
            float lerpedAmplitude = lerp(smoothedAmplitude, ab.get(i % ab.size()), 0.005f);
            float radius = lerpedAmplitude * maxRadius * explosionFactor;
            radius = constrain(radius, minRadius, maxRadius);
            float hue = map(radius, minRadius, maxRadius, 0, 255);
            float brightness = map(lerpedAmplitude, 0, 1, 50, 255);
            float x = cx + cos(angle) * radius;
            float y = cy + sin(angle) * radius;
            stroke(hue + hueOffset, 255, brightness);
            fill(hue + hueOffset, 255, brightness);
            ellipse(x, y, lerpedAmplitude * 20, lerpedAmplitude * 20);
        }
        hueOffset += 1; // Color offset for dynamic color change
    }

    public void drawRippleRings() {
        float cx = width / 2;
        float cy = height / 2;
        float maxRadius = min(width, height) * 0.4f;
        float minRadius = 10;
        float ringSpacing = 10;
        float numRings = 20;
        float pulseFactor = map(smoothedAmplitude, 0, 0.5f, 1, 5);
        float hueOffset = 0;
        for (int i = 0; i < numRings; i++) {
            float lerpedAmplitude = lerp(smoothedAmplitude, ab.get(i % ab.size()), 0.05f);
            float radius = (i * ringSpacing + lerpedAmplitude * 100) * pulseFactor;
            radius = constrain(radius, minRadius, maxRadius);
            float hue = map(radius, minRadius, maxRadius, 0, 255);
            float brightness = map(lerpedAmplitude, 0, 1, 50, 255);
            noFill();
            stroke(hue + hueOffset, 255, brightness);
            ellipse(cx, cy, radius * 2, radius * 2);
        }
        hueOffset += 1; // Color offset for dynamic color change
    }

    public void drawStarfield() {
        float cx = width / 2;
        float cy = height / 2;
        float maxRadius = min(width, height) * 0.4f;
        float minRadius = 10;
        float ringSpacing = 10;
        float numRings = 20;
        float pulseFactor = map(smoothedAmplitude, 0, 0.5f, 1, 5);
        float hueOffset = 0;
        for (int i = 0; i < numRings; i++) {
            float lerpedAmplitude = lerp(smoothedAmplitude, ab.get(i % ab.size()), 0.05f);
            float radius = (i * ringSpacing + lerpedAmplitude * 100) * pulseFactor;
            radius = constrain(radius, minRadius, maxRadius);
            float hue = map(radius, minRadius, maxRadius, 0, 255);
            float brightness = map(lerpedAmplitude, 0, 1, 50, 255);
            noFill();
            stroke(hue + hueOffset, 255, brightness);
            ellipse(cx, cy, radius * 2, radius * 2);
        }
        hueOffset += 1; // Color offset for dynamic color change
    }

    // Implement other visualization methods here
}

