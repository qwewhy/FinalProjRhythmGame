import processing.core.PApplet;
import java.util.ArrayList;
import java.util.Iterator;

// 假设存在 Firework, Ripple, TimedEvent 类
// Assuming that classes Firework, Ripple, and TimedEvent exist

public class LongNoteHandler {
    PApplet parent; // For handling Processing's functions
    ArrayList<Firework> fireworks;
    ArrayList<Ripple> ripples;
    float moonX;
    int comboCount;
    int score;
    final int X_DISTANCE_THRESHOLD = 50; // X轴间隔小于50的阈值

    public LongNoteHandler(PApplet p, ArrayList<Firework> fireworks, ArrayList<Ripple> ripples, float moonX, int comboCount, int score) {
        this.parent = p;
        this.fireworks = fireworks;
        this.ripples = ripples;
        this.moonX = moonX;
        this.comboCount = comboCount;
        this.score = score;
    }

    // 调用这个方法来处理和更新长音符逻辑
    // Call this method to process and update long notes logic
    public void update() {
        if (comboCount > 3) {
            ArrayList<Firework> closeFireworks = getCloseFireworks();

            if (!closeFireworks.isEmpty()) {
                createLongNoteRipple(closeFireworks);
            }
        }
    }

    // 获取X轴间隔小于50的烟花对象集合
    // Get a collection of firework objects with less than 50 intervals on the X-axis
    private ArrayList<Firework> getCloseFireworks() {
        ArrayList<Firework> closeFireworks = new ArrayList<>();
        Firework previousFirework = null;

        for (Firework fw : fireworks) {
            if (previousFirework != null && Math.abs(fw.getX() - previousFirework.getX()) < X_DISTANCE_THRESHOLD) {
                closeFireworks.add(fw);
            }
            previousFirework = fw;
        }

        return closeFireworks;
    }

    // 根据获取的烟花创建长音符的波纹效果
    // Create the ripple effect of long notes based on the fireworks obtained
    private void createLongNoteRipple(ArrayList<Firework> closeFireworks) {
        Firework startFirework = closeFireworks.get(0);
        Firework endFirework = closeFireworks.get(closeFireworks.size() - 1);

        Ripple longNoteRipple = new Ripple(parent, startFirework.getX(), endFirework.getX());
        ripples.add(longNoteRipple);

        for (Iterator<Firework> iterator = closeFireworks.iterator(); iterator.hasNext();) {
            Firework fw = iterator.next();
            if (parent.mouseX >= fw.getX() - 25 && parent.mouseX <= fw.getX() + 25) {
                // 假设 mouseInNote 方法用于检测鼠标光标是否在音符上
                if (mouseInNoteRange(fw)) {
                    iterator.remove(); // Remove firework if mouse is over it
                    score += 10; // Increment score for each firework in range
                }
            }
        }
    }

    // 检查鼠标光标是否在音符上
    // Check if the mouse cursor is on the note
    private boolean mouseInNoteRange(Firework fw) {
        // 具体实现留给读者，需要考虑游戏逻辑和坐标
        // Specific implementation left to the reader, needs to consider game logic and coordinates
        return true; // Placeholder return
    }
}