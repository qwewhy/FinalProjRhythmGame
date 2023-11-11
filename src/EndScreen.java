import java.util.ArrayList;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import processing.core.PApplet;
import processing.core.PVector;


public class EndScreen {
    private FinalProjRhythmGame mainClass;
    private ArrayList<Meteor> meteors;
    private PApplet parent;
    private ArrayList<Star> stars; // 添加星星的列表 add a list of stars
    private int score;
    private int totalFireworks;

    private List<Integer> readMappedTimes(String filename) {
        List<Integer> times = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                times.add(Integer.parseInt(line.trim()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return times;
    }


    public EndScreen(PApplet parent, FinalProjRhythmGame mainClass) {
        this.mainClass = mainClass;
        this.parent = parent;
        meteors = new ArrayList<Meteor>();
        stars = new ArrayList<Star>(); // 初始化星星列表 initialize the list of stars

        // 初始化星星
        for (int i = 0; i < 500; i++) {
            stars.add(new Star(parent));
        }
        this.parent = parent;
        meteors = new ArrayList<Meteor>();

        adjustMeteorGeneration();
    }

    private void adjustMeteorGeneration() {
        int meteorCount = mapIntervalToMeteorCount();
        for (int j = 0; j < meteorCount; j++) {
            float initialX = parent.random(-50, parent.width / 2);
            float initialY = parent.random(-50, parent.height / 2);
            meteors.add(new Meteor(initialX, initialY, parent.random(2, 5), parent.random(1, 3), generateColor()));
        }
    }



    private int mapIntervalToMeteorCount() {
        float scorePercentage = (float) mainClass.score / mainClass.totalFireworks;
        if (scorePercentage < 0.5) {
            return 1; // 得分低于50%，生成较少流星 generate fewer meteors
        } else if (scorePercentage < 0.8) {
            return 2; // 得分在50%到80%之间，生成一些流星    generate some meteors
        } else {
            return 3; // 得分高于80%，生成较多流星 generate more meteors
        }
    }




    public void display(int score, int totalFireworks) {
        parent.background(0);

        // 设置字体大小为屏幕宽度的1/50 set the font size to 1/50 of the screen width
        parent.textSize(parent.width / 50.0f);

        // 计算文本位置，使其位于左上角靠中间一点  calculate the text position so that it is in the upper left corner and slightly in the middle
        float textX = parent.width / 10.0f; // 屏幕宽度的1/10作为X坐标   1/10 of the screen width as the X coordinate
        float textY = parent.height / 10.0f; // 屏幕高度的1/10作为Y坐标  1/10 of the screen height as the Y coordinate

        // 显示分数
        parent.text("Score: " + score + "/" + totalFireworks, textX, textY);

        // 更新和显示每颗星星    update and display each star
        for (Star star : stars) {
            star.update(new ArrayList<Firework>()); // 假设没有烟花   assume there are no fireworks
            star.display();
        }

        // 更新和显示每颗流星    update and display each meteor
        for (Meteor m : meteors) {
            m.update();
            m.display();
            System.out.println("Displaying meteor at: " + m.x + ", " + m.y); // 添加日志    add log
        }
        // 绘制返回按钮   draw the back button
        drawBackButton();
    }


    private int generateColor() {
        float r = parent.random(100, 255);
        float g = r + parent.random(-20, 20);
        float b = r + parent.random(-20, 20);
        return parent.color(r, g, b);
    }

    class Meteor {
        float x, y;
        float speedX, speedY;
        int c;
        LinkedList<PVector> tail;

        Meteor(float x, float y, float speedX, float speedY, int c) {
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
            this.c = c;
            tail = new LinkedList<PVector>();
        }

        void update() {
            x += speedX;
            y += speedY;

            if (parent.dist(parent.mouseX, parent.mouseY, x, y) < 50) {
                x += parent.mouseX < x ? 1 : -1;
            }

            tail.add(new PVector(x, y));
            if (tail.size() > 20) {
                tail.removeFirst();
            }

            if (x > parent.width || y > parent.height) {
                reset();
            }
        }

        void reset() {
            x = parent.random(-50, -10);
            y = parent.random(-50, -10);
            c = generateColor();
            tail.clear();
        }

        void display() {
            parent.fill(c);
            parent.noStroke();
            parent.ellipse(x, y, 10, 10);

            for (int i = 0; i < tail.size() - 1; i++) {
                PVector current = tail.get(i);
                PVector next = tail.get(i + 1);
                float strokeWeight = PApplet.map(i, 0, tail.size(), 1, 10);
                int alpha = (int) PApplet.map(i, 0, tail.size(), 50, 255);
                parent.stroke(parent.red(c), parent.green(c), parent.blue(c), alpha);
                parent.strokeWeight(strokeWeight);
                parent.line(current.x, current.y, next.x, next.y);
            }
        }
    }

    private void drawBackButton() {
        float buttonWidth = 100;
        float buttonHeight = 50;
        float buttonX = 10; // 按钮距离左边界的距离   distance from the left edge of the button
        float buttonY = parent.height - buttonHeight - 10; // 按钮距离底边界的距离    distance from the bottom edge of the button

        parent.fill(200);
        parent.rect(buttonX, buttonY, buttonWidth, buttonHeight);
        parent.fill(0);
        parent.textSize(16);
        parent.text("Back", buttonX + buttonWidth / 2 - 20, buttonY + buttonHeight / 2 + 5);
    }

    interface ButtonClickListener {
        void onBackButtonClick();
    }

    private ButtonClickListener buttonClickListener;

    public void setButtonClickListener(ButtonClickListener listener) {
        this.buttonClickListener = listener;
    }

    public void mouseClicked() {
        float buttonWidth = 100;
        float buttonHeight = 50;
        float buttonX = 10;
        float buttonY = parent.height - buttonHeight - 10;

        if (parent.mouseX >= buttonX && parent.mouseX <= buttonX + buttonWidth &&
                parent.mouseY >= buttonY && parent.mouseY <= buttonY + buttonHeight) {
            parent.exit(); // 调用 exit 方法来结束程序   call the exit method to end the program
        }
    }
}
