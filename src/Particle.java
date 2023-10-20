import processing.core.PApplet;
import processing.core.PVector;

class Particle {
    PApplet p;
    PVector position;  // 位置 Position
    PVector velocity;  // 速度 Velocity
    PVector acceleration; // 加速度 Acceleration
    float lifespan;    // 生命周期 Lifespan
    int col;           // 颜色 Color
    float size;

    Particle(PApplet p,PVector pos, int col, float particleSize) {//粒子的大小 Particle size
        this.p = p;
        acceleration = new PVector(0, 0.05f);
        velocity = new PVector(p.random(-0.001f, 0.001f), p.random(-7.4f, -7.6f));
        position = pos.copy();
        lifespan = 100;
        this.col = col;
        this.size = particleSize/3;
    }

    // 更新粒子状态 Update particle state
    void update() {
        velocity.add(acceleration);
        position.add(velocity);
        if (velocity.y < 0) {
            col = p.lerpColor(col, p.color(255, 255, 255, 0), 0.001f);
        }
        lifespan -= 4;
    }

    // 展示粒子 Display particle
    void display() {
        float gradientSize = 20; //椭圆形光照效果的大小 Size of the ellipse gradient
        for (int i = 0; i < gradientSize; i++) {
            float alpha = p.map(i * i, 0, gradientSize * gradientSize, 80, 0);// Gradually reduce alpha as we move outward
            p.fill(255, 255, 255, alpha);
            p.ellipse(position.x, position.y, i, i);
        }

        // 如果爆炸了，设置颜色为红色，轮廓和大小减半
        if (lifespan <= 128) {
            p.stroke(255,0,0, lifespan);
            p.fill(255,0,0, lifespan / 2);
            p.strokeWeight(1); // 轮廓减半
            p.ellipse(position.x, position.y, size / 4, size / 4);  // 大小/4
        } else {
            p.stroke(col, lifespan);
            p.strokeWeight(2);
            p.fill(col, lifespan / 2);
            p.ellipse(position.x, position.y, size, size);
        }
    }

    // 检查粒子是否消失 Check if particle has faded out
    boolean isDead() {
        return lifespan <= 0;
    }
}