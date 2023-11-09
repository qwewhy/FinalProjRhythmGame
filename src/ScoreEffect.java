import processing.core.PApplet;
import processing.core.PVector;
import java.util.ArrayList;
class ScoreEffect {
    PVector position; // 分数效果的位置
    float alpha; // 透明度
    int startTime; // 开始时间
    PApplet parent; // 对 PApplet 的引用
    String scoreText; // 要显示的文本

    ScoreEffect(PApplet p, float x, float y, String text) {
        parent = p;
        position = new PVector(x, y);
        alpha = 255;
        startTime = parent.millis();
        scoreText = text;
    }

    void update() {
        // 计算已过时间
        int elapsedTime = parent.millis() - startTime;
        // 一秒后开始消失
        if (elapsedTime > 1000) {
            alpha = PApplet.map(elapsedTime, 1000, 2000, 255, 0);
            position.y -= 1; // 文本向上移动
        }
        // 两秒后完全消失
        if (elapsedTime > 2000) {
            alpha = 0;
        }
    }

    void display() {
        if (alpha > 0) {
            parent.fill(0, alpha);
            parent.text(scoreText, position.x, position.y);
        }
    }

    boolean isDone() {
        return alpha <= 0;
    }
}
