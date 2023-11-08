import processing.core.PApplet;
import processing.core.PVector;

// 继承Firework类 Inherits from the Firework class
class SuperFirewoekBonus extends Firework {
    static final int BONUS_COLOR = 0xFFFFD700; // 亮金色 Bright gold color in hexadecimal
    static final float VELOCITY_FACTOR = 0.5f; // 下落速度为原来的一半 Half the downward velocity

    SuperFirewoekBonus(PApplet p, int x) {
        super(p, x);
        col = BONUS_COLOR;
        firework.velocity = new PVector(p.random(-0.01f, 0.01f), p.random(1.99f * VELOCITY_FACTOR, 2.01f * VELOCITY_FACTOR)); // 下落速度减半 Half the speed
    }

    @Override
    void explode() {
        // 这里可以添加特殊的爆炸效果 You can add a special explosion effect here
        // For now, we will just call the parent explode method.
        super.explode();
    }

    @Override
    boolean hit(float mouseX, float mouseY) {
        // 这里可以添加特殊的击中检测逻辑 You can add a special hit detection logic here
        // For now, we will just call the parent hit method.
        return super.hit(mouseX, mouseY);
    }
}
