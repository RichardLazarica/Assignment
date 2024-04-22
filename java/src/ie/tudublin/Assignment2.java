package ie.tudublin;

import ddf.minim.*;
import ddf.minim.analysis.*;
import processing.core.PApplet;
import processing.core.PVector;

public class Assignment2 extends PApplet {
    Minim minim;
    AudioPlayer player;
    FFT fft;

    int numParticles = 10;
    Particle[] particles;

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        minim = new Minim(this);
        player = minim.loadFile("InfectedMushroom.mp3", 1024);
        player.play();
        fft = new FFT(player.bufferSize(), player.sampleRate());
        fft.logAverages(60, 7);
        particles = new Particle[numParticles];
        for (int i = 0; i < numParticles; i++) {
            particles[i] = new Particle(random(width), random(height));
        }
    }

    public void draw() {
        background(0);

        fft.forward(player.mix);

        for (int i = 0; i < particles.length; i++) {
            particles[i].update();
            particles[i].display(fft);
        }
    }

    class Particle {
        PVector position;
        PVector velocity;
        float size;
        int color;

        Particle(float x, float y) {
            position = new PVector(x, y);
            velocity = PVector.random2D().mult(random(1, 3));
            size = random(5, 20);
            color = color(random(255), random(255), random(255));
        }

        void update() {
            position.add(velocity);
            if (position.x < 0 || position.x > width || position.y < 0 || position.y > height) {
                position.x = random(width);
                position.y = random(height);
            }
        }

        void display(FFT fft) {
            int freqIndex = (int) random(fft.avgSize());
            float freq = fft.getBand(freqIndex);
            size = map(freq, 0, 255, 20, 100);
            fill(color, 150);
            noStroke();
            ellipse(position.x, position.y, size, size);
        }
    }
}
