import processing.core.PApplet;
import processing.core.PVector;
public class Ripple {
    PApplet P;
    PVector position;  // 波纹的位置
    float radius;      // 波纹的半径
    float maxRadius;   // 波纹的最大半径
    float expansionSpeed;  // 波纹的扩展速度
    int alpha;         // 透明度

    Ripple(PApplet p, float x, float y) {
        position = new PVector(x, y);
        radius = 0;
        maxRadius = 60;  // 可以根据需要调整 Adjust as needed
        expansionSpeed = 3;  // 可以根据需要调整 Adjust as needed
        alpha = 255;
    }

    void update() {
        radius += expansionSpeed;
        if (radius >= maxRadius) {
            alpha -= 10;  // 使波纹渐渐消失 Make the ripple fade away
        }
    }

    void display(PApplet p) {
        p.noFill();
        p.stroke(80, alpha);
        p.ellipse(position.x, position.y, radius * 2, radius * 2);
    }

    boolean isDone() {
        return alpha <= 0;
    }
}

