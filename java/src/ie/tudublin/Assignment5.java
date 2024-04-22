package ie.tudublin;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;
import processing.core.PVector;



public class Assignment5 extends PApplet {
    Minim minim;
    AudioPlayer ap;
    FFT fft;

    int mode = 0;

    float smoothedAmplitude = 0;
    float lerpedFrequency;

   
    float lerpedTimeOffsetX = 0.0f;
    float lerpedTimeOffsetY = 0.0f;

    PVector sunPos;

    public void settings() {
        size(800, 600, P3D);
    }

    public void setup() {
        minim = new Minim(this);
        ap = minim.loadFile("InfectedMushroom.mp3", 1024);
        ap.play();

        fft = new FFT(ap.bufferSize(), ap.sampleRate());
        fft.linAverages(60);

        sunPos = new PVector(width * 0.75f, height * 0.25f);
        frameRate(10);
    }

    public void draw() {
        background(135, 206, 250); // Sky blue color

        // Calculate amplitude
        fft.forward(ap.mix);
        smoothedAmplitude = ap.mix.level();

        // Adjust time offsets based on music amplitude
        float lerpedSpeed = map(smoothedAmplitude, 0, 1, 0.001f, 0.00001f);
        lerpedTimeOffsetX = lerp(lerpedTimeOffsetX, lerpedSpeed, 0.1f);
        lerpedTimeOffsetY = lerp(lerpedTimeOffsetY, lerpedSpeed, 0.1f);
        // Draw Sun
        drawSun();

        // Draw Ground (Green Landscape)
        drawGround();

        // Draw Trees
        //drawTrees();
    }

    public void drawSun() {
        float sunSize = map(smoothedAmplitude, 0, 1, 50, 200);
        float sunColor = map(smoothedAmplitude, 0, 1, 255, 0);
        fill(255, sunColor, 0); // Sun color (yellow)
        noStroke();
        ellipse(sunPos.x, sunPos.y, sunSize, sunSize);
    }

 // Declare variables for controlling the speed of Perlin noise changes
 float timeOffsetX = 0.0f;
 float timeOffsetY = 0.0f;
 
 public void drawGround() {
    int layerCount = 5; // Number of layers
    float baseHeight = height * 0.6f; // Base height of the hills
    float layerHeightIncrement = (height - baseHeight) / layerCount; // Incremental height for each layer

    int hillCount = 10; // Number of hills in each layer
    float hillWidth = random(400, 600); // Random width for each hill (wider)
    float hillSpacing = width / hillCount; // Spacing between hills

    for (int layer = 0; layer < layerCount; layer++) {
        float layerHeight = baseHeight + layer * layerHeightIncrement; // Height of the current layer
        float layerColor = map(layer, 0, layerCount - 1, 34, 139); // Color gradient for each layer

        fill(34, layerColor, 34); // Green color with varying shades
        noStroke();

        for (int i = 0; i < hillCount; i++) {
            float hillHeight = random(200, 1000); // Random height for each hill
            float x = i * hillSpacing; // X position of the hill
            float noiseX = x / width * 5f; // Scale X position for Perlin noise
            float noiseY = layer * 5f; // Scale Y position for Perlin noise

            float noiseValue = map(noise(noiseX + timeOffsetX, noiseY + timeOffsetY), 0, 1, -1, 1); // Perlin noise value
            float y = layerHeight + noiseValue * 100.0f; // Y position of the hill with Perlin noise

            // Calculate color based on distance from the screen
            float distanceFromScreen = map(y, baseHeight, height, 0, 1);
            float greenColor = map(distanceFromScreen, 0, 1, 255, layerColor);

            fill(34, greenColor, 34); // Green color with varying shades

            // Draw the hill as a flat-topped shape
            beginShape();
            vertex(x, height);
            bezierVertex(x - 150, y - hillHeight / 40, x + hillWidth, y - hillHeight / 40, x + hillWidth, height); // Bezier curve for the hill 
            endShape();
            endShape(CLOSE);

            // Draw forest pine tree behind the hill
            drawPineTree(x + hillWidth * 0.5f, y - hillHeight / 40);
        }
    }

    // Increment time offsets for Perlin noise
    timeOffsetX += 0.5f; // Reduce the increment value
    timeOffsetY += 0.5f; // Reduce the increment value
}

  


public void drawPineTree(float x, float y) {
    float treeHeight = 100; // Initial height of the tree
    float treeWidth = 20; // Initial width of the tree

    fill(0, 100, 0); // Dark green color for the tree

    // Draw tree trunk
    rectMode(CENTER);
    rect(x, y, treeWidth / 2, treeHeight / 2);

    // Draw branches with triangles
    for (int i = 0; i < 5; i++) {
        float branchHeight = map(i, 0, 4, treeHeight * 0.2f, treeHeight * 0.8f); // Gradually increase branch height
        float branchWidth = map(i, 0, 4, treeWidth * 0.8f, treeWidth * 0.2f); // Gradually decrease branch width

        // Draw triangle for each branch level
        triangle(x - branchWidth / 2, y - i * 10, x + branchWidth / 2, y - i * 10, x, y - branchHeight);
    }
}

}
