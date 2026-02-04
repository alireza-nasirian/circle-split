package art;

import processing.core.PApplet;

public class RecursiveGenerative extends PApplet {

    // Change these to tune complexity
    int maxDepth = 9;
    float minSize = 35; // stop recursion when region is too small

    long seed;

    public static void main(String[] args) {
        PApplet.main(RecursiveGenerative.class);
    }

    public void settings() {
        size(1200, 900); // adjust as needed
        pixelDensity(2);
        smooth(8);
    }

    public void setup() {
        // Random each run:
        seed = (long) random(Integer.MAX_VALUE);

        randomSeed(seed);
        noiseSeed(seed);

        noLoop();
    }

    public void draw() {
        background(248);

        // subtle paper grain
        drawGrain(0.035f, 18);

        // pick a palette randomly each run
        int[][] palettes = {
                {0xFF0B1320, 0xFF1C2541, 0xFF3A506B, 0xFF5BC0BE, 0xFFFDE74C},
                {0xFF0D1B2A, 0xFF1B263B, 0xFF415A77, 0xFF778DA9, 0xFFE0E1DD},
                {0xFF2B2D42, 0xFF8D99AE, 0xFFEDF2F4, 0xFFEF233C, 0xFFD90429},
                {0xFF22223B, 0xFF4A4E69, 0xFF9A8C98, 0xFFC9ADA7, 0xFFF2E9E4}
        };
        int[] pal = palettes[(int) random(palettes.length)];

        // frame margin
        float m = 60;
        stroke(20, 28);
        noFill();
        rect(m, m, width - 2*m, height - 2*m, 14);

        // recursive composition
        recurseRegion(m, m, width - 2*m, height - 2*m, 0, pal);

        // tiny signature-ish seed for reproducibility
        fill(20, 120);
        noStroke();
        textSize(12);
        text("seed " + seed, m, height - m/2);
    }

    void recurseRegion(float x, float y, float w, float h, int depth, int[] pal) {
        if (depth >= maxDepth || min(w, h) < minSize) {
            drawLeaf(x, y, w, h, pal, depth);
            return;
        }

        // probability of stopping early (creates variety)
        float stopChance = map(depth, 0, maxDepth, 0.05f, 0.40f);
        if (random(1) < stopChance && min(w, h) < 220) {
            drawLeaf(x, y, w, h, pal, depth);
            return;
        }

        // choose split orientation based on aspect + randomness
        boolean splitVertical;
        float aspect = w / max(1, h);
        if (aspect > 1.2) splitVertical = true;
        else if (aspect < 0.85) splitVertical = false;
        else splitVertical = random(1) < 0.5;

        // split ratio with randomness (avoid too tiny slices)
        float r = random(0.35f, 0.65f);

        if (splitVertical) {
            float w1 = w * r;
            float w2 = w - w1;
            recurseRegion(x, y, w1, h, depth + 1, pal);
            recurseRegion(x + w1, y, w2, h, depth + 1, pal);
        } else {
            float h1 = h * r;
            float h2 = h - h1;
            recurseRegion(x, y, w, h1, depth + 1, pal);
            recurseRegion(x, y + h1, w, h2, depth + 1, pal);
        }
    }

    void drawLeaf(float x, float y, float w, float h, int[] pal, int depth) {
        pushMatrix();  // everything inside drawLeaf uses local coordinates - finally we should call popMatrix()
        translate(x, y);

        // light background tint in region
        noStroke();
        int bg = pal[(int) random(pal.length)];  // this makes the final image feel “layered” and helps separate regions.
        fill(red(bg), green(bg), blue(bg), random(10, 26));  // alpha = transparency (opacity)
        rect(0, 0, w, h);  // draws a rectangle to the screen

        // main shape: circle / arc / blob-ish ring
        float cx = random(w * 0.25f, w * 0.75f);  // picks a random “center” and a size for the main motif
        float cy = random(h * 0.25f, h * 0.75f);
        float d = random(min(w, h) * 0.35f, min(w, h) * 0.95f);

        // stroke style varies with depth
        float sw = map(depth, 0, maxDepth, 2.2f, 0.7f);  // depth controls style: deeper leaves get lighter, finer detail.
        strokeWeight(sw);

        int c = pal[(int) random(pal.length)];
        stroke(red(c), green(c), blue(c), random(120, 220));
        noFill();

        // choose a motif randomly
        float pick = random(1);
        if (pick < 0.45) {
            // circle ring with jittered points
            jitterCircle(cx, cy, d, 160 + (int) random(120));
        } else if (pick < 0.75) {
            // arc stack
            int layers = 4 + (int) random(8);
            for (int i = 0; i < layers; i++) {
                float dd = d * (0.35f + i * (0.65f / layers));
                float a0 = random(TWO_PI);
                float a1 = a0 + random(PI * 0.6f, PI * 1.6f);
                arc(cx, cy, dd, dd, a0, a1);
            }
        } else {
            // “blob” via noisy radius
            noisyBlob(cx, cy, d * 0.5f, 220);
        }

        // optional hatch lines
        if (random(1) < 0.35) {
            stroke(10, 22);
            float step = random(6, 14);
            float angle = random(-0.6f, 0.6f);
            hatch(w, h, step, angle);
        }

        // region border
        stroke(20, 30);
        noFill();
        rect(0, 0, w, h);

        popMatrix();
    }

    void jitterCircle(float cx, float cy, float d, int pts) {
        beginShape();
        for (int i = 0; i < pts; i++) {
            float t = map(i, 0, pts, 0, TWO_PI);
            float rr = d * 0.5f + random(-d * 0.03f, d * 0.03f);
            float x = cx + cos(t) * rr;
            float y = cy + sin(t) * rr;
            vertex(x, y);
        }
        endShape(CLOSE);
    }

    void noisyBlob(float cx, float cy, float r, int pts) {
        beginShape();
        float k = random(0.7f, 1.6f);
        float off = random(1000);
        for (int i = 0; i < pts; i++) {
            float t = map(i, 0, pts, 0, TWO_PI);
            float n = noise(cos(t) * k + off, sin(t) * k + off);
            float rr = r * (0.65f + 0.85f * n);
            vertex(cx + cos(t) * rr, cy + sin(t) * rr);
        }
        endShape(CLOSE);
    }

    void hatch(float w, float h, float step, float angle) {
        pushMatrix();
        translate(w/2f, h/2f);
        rotate(angle);
        translate(-w/2f, -h/2f);

        for (float y = -h; y < h*2; y += step) {
            line(-w, y, w*2, y);
        }
        popMatrix();
    }

    void drawGrain(float noiseScale, int alpha) {
        loadPixels(); // Loads the pixel data for the display window into the pixels[] array
        for (int i = 0; i < pixels.length; i++) {
            int x = i % width;
            int y = i / width;
            float n = noise(x * noiseScale, y * noiseScale);
            int g = (int) map(n, 0, 1, -alpha, alpha);
            int c = pixels[i];
            int r = constrain((int) red(c) + g, 0, 255);
            int gg = constrain((int) green(c) + g, 0, 255);
            int b = constrain((int) blue(c) + g, 0, 255);
            pixels[i] = color(r, gg, b);
        }
        updatePixels();
    }
}
