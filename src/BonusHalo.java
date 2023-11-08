import processing.core.PApplet;
import processing.core.PVector;
import java.util.ArrayList;

class BonusHalo {
    PVector position; // 光圈的位置 Position of the halo
    float alpha; // 光圈当前的透明度 Current transparency of the halo
    float brightness; // 光圈的亮度 Brightness of the halo
    boolean active; // 光圈是否活跃 Whether the halo is active
    int startTime; // 光圈开始时间 Halo start time
    PApplet parent; // 对 PApplet 的引用 Reference to PApplet
    float expandRate; // 圆环的扩大速率
    boolean expanding; // 圆环是否正在扩大
    float haloSize; // 圆环的当前尺寸
    ArrayList<Ripple> ripples;


    BonusHalo(PApplet p, float x, float y) {
        parent = p;
        position = new PVector(x, y);
        alpha = 255;
        brightness = 1;
        active = true;
        startTime = parent.millis();
        ripples = new ArrayList<Ripple>();
    }

    void update() {
        int elapsedTime = parent.millis() - startTime;
        if (elapsedTime > 4000) {
            active = false; // 持续6秒后消失 Disappear after 6 seconds
        } else {
            // 光圈从亮到暗的动画效果 Bright to dark animation effect
            brightness = 0.5f + 0.5f * PApplet.cos(PApplet.PI * elapsedTime / 2000.0f);
            alpha = PApplet.lerp(255, 0, brightness);
        }
        // 更新波纹对象
        for (int i = ripples.size() - 1; i >= 0; i--) {
            Ripple ripple = ripples.get(i);
            ripple.update(0); // 这里的comboCount传入0，因为此示例中没有使用
            if (ripple.isDone()) {
                ripples.remove(i);
            }
        }
    }

    void display() {
        // 先画波纹
        for (Ripple ripple : ripples) {
            ripple.display(parent);
        }

        // 然后画光环
        if (active) {
            parent.stroke(173, 255, 47, alpha);
            parent.strokeWeight(10);
            parent.noFill();
            parent.ellipse(position.x, position.y, 50, 50);
        }
    }

    boolean contains(float x, float y) {
        // 检查鼠标点击是否在光圈内 Check if mouse click is within the halo
        return position.dist(new PVector(x, y)) < 25;
    }

    void clicked() {
        if (active) {
            active = false;

            // 创建新的波纹对象
            ripples.add(new Ripple(parent, position.x, position.y, false));
        }
    }


}
