import processing.core.PApplet;
import processing.core.PVector;
import java.util.ArrayList;

public class Star {
    PApplet p;
    float x, y;            // 星星的位置 Position of the star
    float originalX, originalY;       // 星星的初始位置 Original position of the star
    float brightness;      // 星星的当前亮度 Brightness of the star
    float speed;           // 用于呼吸模式的速度 Speed used for breathing mode
    int mode;              // 星星的亮度模式 Brightness mode of the star
    boolean avoiding;      // 是否正在躲避 Whether the star is avoiding
    float avoidSpeed = 2;  // 躲避速度 Avoidance speed
    float targetX, targetY;         // 躲避目标位置 Avoidance target position
    float mouseAvoidDistance = 50; // 鼠标躲避的距离阈值 Distance threshold for mouse avoidance
    float orbitStartTime;  // 记录开始围绕光环移动的时间  Record the time when the orbit starts to move
    boolean orbiting;  // 标记是否正在围绕光环移动  Mark whether it is moving around the halo
    PVector orbitCenter;  // 光环的中心位置    Center position of the halo
    float orbitDuration = 4000;  // 围绕光环移动一圈的持续时间   Duration of moving around the halo
    float orbitRadius = 150; // 定义星星旋转的光环半径 Define the halo radius of the star rotation
    float stopOrbitTime; // 定义停止旋转的时间   Define the time to stop rotating


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
        // 在方法内部重新计算鼠标距离    Recalculate the mouse distance inside the method
        float mouseDistanceX = PApplet.abs(p.mouseX - x);
        float mouseDistanceY = PApplet.abs(p.mouseY - y);

        // 检查是否需要躲避烟花   Check if you need to avoid fireworks
        for (Firework fw : fireworks) {
            float distanceX = PApplet.abs(fw.getX() - x);
            float distanceY = PApplet.abs(fw.getY() - y);
            float fireworkAvoidDistance = 50; // 烟花躲避距离阈值   Firework avoidance distance threshold
            if (distanceX < fireworkAvoidDistance && distanceY < fireworkAvoidDistance && !avoiding) {
                avoiding = true;
                // 计算躲避目标位置     Calculate the avoidance target position
                targetX = x + (x - fw.getX());
                targetY = y + (y - fw.getY());
            }
        }

        // 检查鼠标位置并处理躲避逻辑    Check the mouse position and process the avoidance logic
        if (mouseDistanceX < mouseAvoidDistance && mouseDistanceY < mouseAvoidDistance && !avoiding) {
            avoiding = true;
            // 计算躲避目标位置    Calculate the avoidance target position
            targetX = x + (x - p.mouseX);
            targetY = y + (y - p.mouseY);
        }

        // 执行躲避动作
        if (avoiding) {
            // 计算水平和垂直方向的移动量    Calculate the amount of horizontal and vertical movement
            float moveAmountX = (targetX - x) * 0.05f;
            float moveAmountY = (targetY - y) * 0.05f;
            x += moveAmountX;
            y += moveAmountY;
            // 如果星星足够接近目标位置，则停止躲避   If the star is close enough to the target position, stop avoiding
            if (PApplet.abs(targetX - x) < 1 && PApplet.abs(targetY - y) < 1) {
                avoiding = false;
            }
        } else {
            // 光滑返回初始位置   Smoothly return to the initial position
            x += (originalX - x) * 0.05f;
            y += (originalY - y) * 0.05f;
        }
        updateOrbit();

        // 更新亮度模式
        switch (mode) {
            case 0: // 恒定模式 Constant mode
                break;
            case 1: // 不规律闪烁模式  Irregular flicker mode
                brightness = p.random(100, 255);
                break;
            case 2: // 呼吸模式 Breathing mode
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
            orbiting = true; // 设置星星为围绕状态   Set the star to orbit state
            orbitCenter = center; // 设置光环中心     Set the halo center
            orbitStartTime = p.millis(); // 记录开始围绕的时间   Record the time when the orbit starts to move
            stopOrbitTime = orbitStartTime + orbitDuration; // 设置停止围绕的时间    Set the time to stop orbiting
        }
    }

    void updateOrbit() {
        if (orbiting) {
            // 如果当前时间超过了停止围绕的时间，则停止围绕   If the current time exceeds the time to stop orbiting, stop orbiting
            if (p.millis() > stopOrbitTime) {
                stopOrbiting();
            } else {
                // ... [其余的围绕逻辑保持不变]
            }
        }
    }

    void stopOrbiting() {
        orbiting = false; // 停止围绕状态  Stop orbiting state
        x = originalX; // 返回到原始x位置  Return to original x position
        y = originalY; // 返回到原始y位置  Return to original y position
    }
}
