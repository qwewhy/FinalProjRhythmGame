import processing.core.PApplet;
import processing.core.PVector;
public class Ripple {
    PApplet P;
    PVector position; // 波纹的位置
    float radius; // 波纹的半径
    float maxRadius; // 波纹的最大半径
    float expansionSpeed; // 波纹的扩展速度
    int alpha; // 透明度
    boolean isComboActive; // 是否处于连击状态

    // Ripple的构造函数，新增了是否处于连击状态的参数
    Ripple(PApplet p, float x, float y, boolean isCombo) {
        P = p;
        position = new PVector(x, y);
        radius = 0;
        maxRadius = 60; // 可以根据需要调整 Adjust as needed
        expansionSpeed = 3; // 可以根据需要调整 Adjust as needed
        alpha = 255;
        isComboActive = isCombo; // 设置连击状态
    }

    // 更新波纹状态的方法，新增了连击时变化的逻辑
    void update(int comboCount) {
        radius += expansionSpeed;
        if (radius >= maxRadius) {
            alpha -= 10; // 使波纹渐渐消失 Make the ripple fade away
        }

        // 如果连击数超过5，进行特殊处理
        if (comboCount > 5) {
            isComboActive = true;
            maxRadius = 120; // 将最大半径加倍
        } else {
            isComboActive = false;
            maxRadius = 60; // 重置最大半径
        }
    }

    // 绘制波纹的方法，新增了连击时变化的逻辑
    void display(PApplet p) {
        p.noFill();
        if (isComboActive) {
            p.stroke(255, 255, 0, alpha); // 设置为黄色
        } else {
            p.stroke(80, alpha); // 设置为默认颜色
        }
        p.ellipse(position.x, position.y, radius * 2, radius * 2);
    }

    // 判断波纹是否完成的方法
    boolean isDone() {
        return alpha <= 0;
    }
}


