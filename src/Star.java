import processing.core.PApplet;
import processing.core.PVector;
import java.util.ArrayList;

public class Star {
    PApplet p;
    float x, y;            // 星星的位置
    float originalX, originalY;       // 星星的初始位置
    float brightness;      // 星星的当前亮度
    float speed;           // 用于呼吸模式的速度
    int mode;              // 星星的亮度模式
    boolean avoiding;      // 是否正在躲避
    float avoidSpeed = 2;  // 躲避速度
    float targetX, targetY;         // 躲避目标位置
    float mouseAvoidDistance = 50; // 鼠标躲避的距离阈值
    float orbitStartTime;  // 记录开始围绕光环移动的时间
    boolean orbiting;  // 标记是否正在围绕光环移动
    PVector orbitCenter;  // 光环的中心位置
    float orbitDuration = 4000;  // 围绕光环移动一圈的持续时间

    float orbitRadius = 150; // 定义星星旋转的光环半径
    float stopOrbitTime; // 定义停止旋转的时间


    Star(PApplet p) {
        this.p = p;
        originalX = x = p.random(p.width);
        originalY = y = p.random(p.height * 0.75f);
        brightness = p.random(100, 255);
        speed = p.random(0.5f, 3.0f);
        mode = (int) p.random(3);
        avoiding = false;
    }

    void update(ArrayList<Firework> fireworks) {
        // 在方法内部重新计算鼠标距离
        float mouseDistanceX = PApplet.abs(p.mouseX - x);
        float mouseDistanceY = PApplet.abs(p.mouseY - y);

        // 检查是否需要躲避烟花
        for (Firework fw : fireworks) {
            float distanceX = PApplet.abs(fw.getX() - x);
            float distanceY = PApplet.abs(fw.getY() - y);
            float fireworkAvoidDistance = 50; // 烟花躲避距离阈值
            if (distanceX < fireworkAvoidDistance && distanceY < fireworkAvoidDistance && !avoiding) {
                avoiding = true;
                // 计算躲避目标位置
                targetX = x + (x - fw.getX());
                targetY = y + (y - fw.getY());
            }
        }

        // 检查鼠标位置并处理躲避逻辑
        if (mouseDistanceX < mouseAvoidDistance && mouseDistanceY < mouseAvoidDistance && !avoiding) {
            avoiding = true;
            // 计算躲避目标位置
            targetX = x + (x - p.mouseX);
            targetY = y + (y - p.mouseY);
        }

        // 执行躲避动作
        if (avoiding) {
            // 计算水平和垂直方向的移动量
            float moveAmountX = (targetX - x) * 0.05f;
            float moveAmountY = (targetY - y) * 0.05f;
            x += moveAmountX;
            y += moveAmountY;
            // 如果星星足够接近目标位置，则停止躲避
            if (PApplet.abs(targetX - x) < 1 && PApplet.abs(targetY - y) < 1) {
                avoiding = false;
            }
        } else {
            // 光滑返回初始位置
            x += (originalX - x) * 0.05f;
            y += (originalY - y) * 0.05f;
        }
        updateOrbit();

        // 更新亮度模式
        switch (mode) {
            case 0: // 恒定模式
                // 在恒定模式下不需要做任何事情
                break;
            case 1: // 不规律闪烁模式
                brightness = p.random(100, 255);
                break;
            case 2: // 呼吸模式
                brightness = 155 + 100 * PApplet.sin(p.frameCount * speed * 0.05f);
                break;
        }
    }

    void display() {
        p.stroke(255, brightness);
        p.strokeWeight(p.random(1, 3));
        p.point(x, y);
    }

    void startOrbiting(PVector center) {
        float distanceToCenter = PVector.dist(new PVector(x, y), center);
        if (distanceToCenter < orbitRadius) {
            orbiting = true; // 设置星星为围绕状态
            orbitCenter = center; // 设置光环中心
            orbitStartTime = p.millis(); // 记录开始围绕的时间
            stopOrbitTime = orbitStartTime + orbitDuration; // 设置停止围绕的时间
        }
    }

    void updateOrbit() {
        if (orbiting) {
            // 如果当前时间超过了停止围绕的时间，则停止围绕
            if (p.millis() > stopOrbitTime) {
                stopOrbiting();
            } else {
                // ... [其余的围绕逻辑保持不变]
            }
        }
    }

    void stopOrbiting() {
        orbiting = false; // 停止围绕状态
        x = originalX; // 返回到原始x位置
        y = originalY; // 返回到原始y位置
    }
}
