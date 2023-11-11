import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

class Firework {
    PApplet p;
    Particle firework;
//    ArrayList<Particle> particles = new ArrayList<>();
    boolean exploded = false;
    int col;
    int x;

    Firework(PApplet p, int x) {
        this.p = p;
        this.x = x;
        PVector position = new PVector(x, 0); // 从顶部开始 Start at the top
        col = p.color(p.random(100, 255), p.random(100, 255), p.random(100, 255));
        firework = new Particle(p, position, col, 8.0f);
        firework.velocity = new PVector(p.random(-0.01f, 0.01f), p.random(0.59f, 0.61f)); // 向下的速度 Downward velocity
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return (int)firework.position.y;
    }

    void update() {
        if (!exploded) {
            firework.update();
            //爆炸的判断条件
            if (firework.position.y >= p.height / 10*9) {
                exploded = true;
                explode();
            }
        }

//        for (int i = particles.size() - 1; i >= 0; i--) {
//            particles.get(i).update();
//            if (particles.get(i).isDead()) {
//                particles.remove(i);
//            }
//        }
    }

    void display() {
        if (!exploded) {
            firework.display();
        }
//        for (Particle particle : particles) {
//            particle.display();
//        }
    }

    void explode() {//爆炸烟花效果（暂时删除） Firework explosion effect (temporarily deleted)

    }

    boolean hit(float mouseX, float mouseY) {
        if (mouseY >= firework.position.y && mouseY <= firework.position.y + p.height/10) {
            return true;
        } else {
            return false;
        }
    }
}
