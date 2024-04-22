package ie.tudublin;
import ddf.minim.*;
import ddf.minim.analysis.*;
import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PShape;
import processing.core.PImage;
import processing.core.PConstants;
import processing.*;


public class Assignment3 extends PApplet {
    Minim minim;
    AudioPlayer player;
    FFT fft;

    int cols, rows; // Number of columns and rows in the terrain
    float[][] terrain; // 2D array to store the terrain heights
    float flying = 0; // Variable to control the terrain generation speed and direction
    float terrainWidth = 600;
    float terrainHeight = 400;

    PShape mushroom; // Shape for mushroom model

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

        // Load mushroom model
        //mushroom = loadShape("10192_MushroomShitake_v1-L3.obj");
    }

    public void draw() {
        background(0);
        fft.forward(player.mix);

        // Update terrain using Perlin noise
        float yoff = flying; // Start yoff at 0 to generate terrain from the top row of the 2D array
        for (int y = 0; y < rows; y++) { // Loop through each row of the 2D array to generate terrain heights
            float xoff = 0; // Start xoff at 0 to generate terrain from the left column of the 2D array
            for (int x = 0; x < cols; x++) { // Loop through each column of the 2D array to generate terrain heights
                terrain[x][y] = map(noise(xoff, yoff), 0, 1, -100, 100); // Generate terrain height using Perlin noise
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
        
        // Draw mushrooms
        drawMushrooms();
    }

    void drawTerrain() {
        stroke(255);
        noFill();

        for (int y = 0; y < rows - 1; y++) { // Loop through each row of the 2D array to draw the triangles
            beginShape(TRIANGLE_STRIP); // Begin drawing a triangle strip to connect the vertices
            for (int x = 0; x < cols; x++) { // Loop through each column of the 2D array to draw the vertices
                vertex(x * (terrainWidth / (cols - 1)), y * (terrainHeight / (rows - 1)), terrain[x][y]); // Draw the current vertex
                vertex(x * (terrainWidth / (cols - 1)), (y + 1) * (terrainHeight / (rows - 1)), terrain[x][y + 1]); // Draw the vertex below the current vertex
            }
            endShape(); // End drawing the triangle strip
        }
    }
    
    // void drawMushrooms() {
    //     for (int y = 0; y < rows; y += 10) { // Draw mushrooms on every 10th row
    //         for (int x = 0; x < cols; x += 10) { // Draw mushrooms on every 10th column
    //             float xpos = x * (terrainWidth / (cols - 1)); // Calculate x position of the mushroom
    //             float ypos = y * (terrainHeight / (rows - 1)); // Calculate y position of the mushroom
    //             float zpos = terrain[x][y]; // Get the terrain height at the mushroom position
    //             pushMatrix(); // Save current transformation matrix
    //             translate(xpos, ypos, zpos); // Move to mushroom position
    //             scale(2); // Increase the size of the mushroom (change the scale factor as needed)
    //             shape(mushroom); // Draw mushroom
    //             popMatrix(); // Restore previous transformation matrix
    //         }
    //     }
    // }
    void drawMushrooms() {
        for (int y = 0; y < rows; y += 10) { // Draw mushrooms on every 10th row
            for (int x = 0; x < cols; x += 10) { // Draw mushrooms on every 10th column
                float xpos = x * (terrainWidth / (cols - 1)); // Calculate x position of the mushroom
                float ypos = y * (terrainHeight / (rows - 1)); // Calculate y position of the mushroom
                float zpos = terrain[x][y]; // Get the terrain height at the mushroom position
                float mushroomHeight = map(noise(x * 0.1f, y * 0.1f), 0, 1, 5, 50); // Adjust mushroom height based on Perlin noise
                drawMushroom(xpos, ypos, zpos + mushroomHeight); // Draw mushroom at adjusted position
            }
        }
    }
    
    void drawMushroom(float x, float y, float z) {
        float capSize = 20; // Size of the mushroom cap
        float stemHeight = 40; // Height of the mushroom stem
        
        // Draw mushroom cap
        pushMatrix();
        translate(x, y, z);
        fill(255, 0, 0); // Red color for the mushroom cap
        noStroke();
        sphere(capSize);
        popMatrix();
        
        // Draw mushroom stem using stacked ellipses
        pushMatrix();
        translate(x, y, z + capSize / 2); // Move stem to the bottom of the cap
        fill(255, 255, 0); // Yellow color for the mushroom stem
        noStroke();
        for (int i = 0; i < stemHeight; i += 5) {
            ellipse(0, 0, capSize / 2, capSize / 4); // Draw ellipse as a segment of the stem
            translate(0, 5); // Move up to the next segment
        }
        popMatrix();
    }
}