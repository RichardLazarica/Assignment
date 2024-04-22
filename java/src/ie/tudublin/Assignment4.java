package ie.tudublin;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;

public class Assignment4 extends PApplet {
    Minim minim;
    AudioPlayer player;
    FFT fft;

    int cols = 800;
    int rows = 600;
    float minReal = -2.0f;
    float maxReal = 1.0f;
    float minImaginary = -1.5f;
    float maxImaginary = 1.5f;
    float maxIter = 100;
    float zoom = 1.0f;
    float zoomSpeed = 0.005f; // Adjust the speed of zooming
    float offsetX = 0.0f;
    float offsetY = 0.0f;

    public void settings() {
        size(cols, rows);
    }

    public void setup() {
        minim = new Minim(this);
        player = minim.loadFile("InfectedMushroom.mp3", 1024);
        player.play();
        fft = new FFT(player.bufferSize(), player.sampleRate());
    }

    public void draw() {
        loadPixels();
        float realWidth = (maxReal - minReal) / zoom;
        float imaginaryHeight = (maxImaginary - minImaginary) / zoom;

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                float real = map(x, 0, cols, minReal + offsetX, minReal + realWidth + offsetX); // 
                float imaginary = map(y, 0, rows, minImaginary + offsetY, minImaginary + imaginaryHeight + offsetY);

                float zReal = 0;
                float zImaginary = 0;

                int iter = 0;
                while (iter < maxIter && zReal * zReal + zImaginary * zImaginary < 4) { 
                    float temp = zReal * zReal - zImaginary * zImaginary + real; 
                    zImaginary = 2 * zReal * zImaginary + imaginary;
                    zReal = temp;
                    iter++;
                }

                float brightness = map(iter, 0, maxIter, 0, 1);
                int pixelColor = color(brightness * 255);

                pixels[x + y * cols] = pixelColor;
            }
        }
        updatePixels();

        fft.forward(player.mix);
        // Your visualization code based on FFT can go here

        // Increase zoom level
        zoom += zoomSpeed;

        // Reset zoom level when it gets too high
        if (zoom > 100) {
            zoom = 1.0f;
        }
    }
}
