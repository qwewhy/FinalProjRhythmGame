import processing.core.PApplet;

public class Star {
    PApplet p;
    float x, y;            // 星星的位置 Position of the star
    float brightness;      // 星星的当前亮度 Current brightness of the star
    float speed;           // 用于呼吸模式的速度 Speed used for breathing mode
    int mode;              // 星星的亮度模式 Brightness mode of the star

    Star(PApplet p) { // 初始化星星 Initialize star
        this.p = p;
        x = p.random(p.width);
        y = p.random(p.height * 0.75f);
        brightness = p.random(100, 255);
        speed = p.random(0.5f, 3.0f);
        mode = (int) p.random(3);  // 随机选择一种模式 Randomly select a mode
    }

    void update() { // 更新星星状态 Update star state
        switch (mode) {
            case 0:  // 恒定模式 Constant mode
                break;
            case 1:  // 不规律闪烁模式 Irregular flicker mode
                brightness = p.random(100, 255);
                break;
            case 2:  // 呼吸模式 Breathing mode
                brightness = 155 + 100 * p.sin(p.frameCount * speed * 0.05f);
                break;
        }
    }

    void display() { // 绘制星星 Draw the star
        p.stroke(255, brightness);
        p.strokeWeight(p.random(1, 3));
        p.point(x, y);
    }
}