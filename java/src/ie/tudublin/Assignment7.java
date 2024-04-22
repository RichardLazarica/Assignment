package ie.tudublin;

import ddf.minim.*;
import ddf.minim.analysis.*;
import processing.core.PApplet;

public class Assignment7 extends PApplet {
    Minim minim;
    AudioPlayer player;
    FFT fft;

    int cols, rows; // Number of columns and rows in the terrain
    float[][] terrain; // 2D array to store the terrain heights
    float flying = 0; // Variable to control the terrain generation speed and direction
    float terrainWidth = 600;
    float terrainHeight = 400;
    float[][] terrainOffset;

    public void settings() {
        size(800, 600, P3D);
    }

    public void setup() {
        minim = new Minim(this);
        player = minim.loadFile("InfectedMushroom.mp3", 1024);
        player.play();
        fft = new FFT(player.bufferSize(), player.sampleRate());

        cols = 60;
        rows = 60;
        terrain = new float[cols][rows];
        terrainOffset = new float[cols][rows];
    }

    public void draw() {
        background(0);
        fft.forward(player.mix);

        // Update terrain using Perlin noise
        float yoff = flying; // Start yoff at 0 to generate terrain from the top row of the 2D array
        for (int y = 0; y < rows; y++) { // Loop through each row of the 2D array to generate terrain heights
            float xoff = 0; // Start xoff at 0 to generate terrain from the left column of the 2D array
            for (int x = 0; x < cols; x++) { // Loop through each column of the 2D array to generate terrain heights
                terrainOffset[x][y] = map(fft.getBand(x + y), 0, 255, -100, 100); // Use FFT data to modify terrain offset
                float heightFactor = map(fft.getBand(x + y), 0, 255, -100, 100); // Use FFT data to modify terrain height
                terrain[x][y] = map(noise(xoff, yoff), 0, 1, -100, 100) + heightFactor + terrainOffset[x][y]; // Generate terrain height using Perlin noise and add FFT-based offset
                xoff += 0.1; // Increment xoff to move to the next column
            }
            yoff += 0.1; // Increment yoff to move to the next row
        }
        flying -= 0.01; // Decrement flying to move the terrain in the negative z direction

        // Change perspective
        translate(width / 2, height / 2); // Move origin to the center of the screen
        rotateX(PI / 2.5f); // Rotate around the x-axis to change perspective
        translate(-terrainWidth / 2, -terrainHeight / 10); // Move origin to the top-left corner of the terrain

        // Draw terrain
        drawTerrain();
    }

    void drawTerrain() {
        for (int y = 0; y < rows - 1; y++) { // Loop through each row of the 2D array to draw the triangles
            beginShape(TRIANGLE_STRIP); // Begin drawing a triangle strip to connect the vertices
            for (int x = 0; x < cols - 1; x++) { // Loop through each column of the 2D array to draw the vertices
                float h = terrain[x][y];
                float h1 = terrain[x][y + 1];
                float h2 = terrain[x + 1][y];
                float h3 = terrain[x + 1][y + 1];
                float amp = map(fft.getBand(x + y), 0, 255, -50, 50); // Use FFT data to modify amplitude
                h += amp;
                h1 += amp;
                h2 += amp;
                h3 += amp;
                fill(map(h, -100, 100, 0, 255), map(h, -100, 100, 255, 0), 255);
                vertex(x * (terrainWidth / (cols - 1)), y * (terrainHeight / (rows - 1)), h); // Draw the current vertex
                vertex(x * (terrainWidth / (cols - 1)), (y + 1) * (terrainHeight / (rows - 1)), h1); // Draw the vertex below the current vertex
                vertex((x + 1) * (terrainWidth / (cols - 1)), y * (terrainHeight / (rows - 1)), h2); // Draw the vertex to the right of the current vertex
                vertex((x + 1) * (terrainWidth / (cols - 1)), (y + 1) * (terrainHeight / (rows - 1)), h3); // Draw the vertex to the bottom-right of the current vertex
            }
            endShape(); // End drawing the triangle strip
        }
    }
}
