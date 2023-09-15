import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

class Firework {
    PApplet p;
    Particle firework;
    ArrayList<Particle> particles = new ArrayList<>();
    boolean exploded = false;
    int col;

    Firework(PApplet p) {
        this.p = p;
        PVector position = new PVector(p.random(p.width), 0); // 从顶部开始 Start at the top
        col = p.color(p.random(100, 255), p.random(100, 255), p.random(100, 255));
        firework = new Particle(p, position, col, 8.0f);
        firework.velocity = new PVector(p.random(-1, 1), p.random(1, 3)); // 向下的速度 Downward velocity
    }


    void update() {
        if (!exploded) {
            firework.update();
            if (firework.position.y >= p.height / 2) {
                exploded = true;
                explode();
            }
        }

        for (int i = particles.size() - 1; i >= 0; i--) {
            particles.get(i).update();
            if (particles.get(i).isDead()) {
                particles.remove(i);
            }
        }
    }

    void display() {
        if (!exploded) {
            firework.display();
        }
        for (Particle particle : particles) {
            particle.display();
        }
    }

    void explode() {
        for (int i = 0; i < 10; i++) {
            Particle p = new Particle(this.p, firework.position, col, 0.5f);  // Provide a float value here
            p.velocity = new PVector(this.p.random(-5, 5), this.p.random(-5, 5));
            particles.add(p);
        }
    }
}
