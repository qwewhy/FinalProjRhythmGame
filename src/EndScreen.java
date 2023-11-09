import processing.core.PApplet;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.*;
import java.io.File;



public class EndScreen {

    FinalProjRhythmGame mainApp;
    ArrayList<Integer> missedNotes;
    float maxTime; // 假设游戏的最长时间，用于归一化
    // 添加星星列表
    ArrayList<Star> stars;
    Clip clip;
    AudioInputStream audioInputStream;

    public EndScreen(FinalProjRhythmGame app) {
        this.mainApp = app;
        this.missedNotes = new ArrayList<>();
        this.maxTime = 0;
        this.stars = new ArrayList<>();
        // Initialize other necessary fields if any
    }

    public void initialize() {
        loadMissedNotes();
        setupStars();
    }

    private void loadMissedNotes() {
        // Load the missed notes from MappedTimes.txt
        String filePath = mainApp.sketchPath() + "/MappedTimes.txt";
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while ((line = reader.readLine()) != null) {
                int time = Integer.parseInt(line.trim());
                missedNotes.add(time);
                if (time > maxTime) maxTime = time; // Update max time
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setupStars() { // Initialize stars
        int numStars = 500;
        for (int i = 0; i < numStars; i++) {
            stars.add(new Star(mainApp));
        }
    }

    public void display() {
        mainApp.background(0);  // 设定背景为纯黑色

        // 更新和显示每颗星星
        for (Star star : stars) {
            star.update(new ArrayList<Firework>()); // 假设更新方法需要烟花列表，这里提供一个空列表
            star.display();
        }

        // 设置一些绘图参数
        int startX = 50; // 起始X坐标
        int endX = mainApp.width - 50; // 结束X坐标
        float startY = mainApp.height - 100; // 起始Y坐标
        float spacing = (endX - startX) / (float) missedNotes.size(); // 计算间隔

        // 添加一些文本信息
        mainApp.fill(255);
        mainApp.textAlign(PApplet.CENTER, PApplet.BOTTOM);
        mainApp.text("Missed Notes", mainApp.width / 2, startY - 50);

        // 返回菜单的按钮
        mainApp.fill(100);
        mainApp.rect(50, mainApp.height - 50, 100, 30);
        mainApp.fill(255);
        mainApp.text("Menu", 100, mainApp.height - 30);
        float timelineY = mainApp.height * 0.8f; // 数轴的Y位置
        mainApp.stroke(255); // 设置为白色
        mainApp.strokeWeight(2); // 设置线条粗细
        mainApp.line(startX, timelineY, endX, timelineY); // 绘制时间轴

        mainApp.fill(255, 0, 0); // 设置为红色
        // 绘制未点击成功的音符时间点
        for (int i = 0; i < missedNotes.size(); i++) {
            float x = startX + i * spacing;
            mainApp.ellipse(x, timelineY, 10, 10);
        }
    }

    public void mousePressed() {
        System.out.println("Mouse clicked at: (" + mainApp.mouseX + ", " + mainApp.mouseY + ")");

        // 检查是否点击了返回菜单的按钮
        if (mainApp.mouseX >= 50 && mainApp.mouseX <= 150 && mainApp.mouseY >= mainApp.height - 50 && mainApp.mouseY <= mainApp.height - 20) {
            mainApp.currentState = FinalProjRhythmGame.GameState.MENU;
        } else {
            float timelineY = mainApp.height * 0.8f;
            int startX = 50;
            float spacing = (mainApp.width - 100) / (float) missedNotes.size();
            // 检测点击是否在任一missed note上
            for (int i = 0; i < missedNotes.size(); i++) {
                float x = startX + i * spacing;
                if (PApplet.dist(mainApp.mouseX, mainApp.mouseY, x, timelineY) < 5) {
                    System.out.println("Attempting to play note at: " + missedNotes.get(i) + " ms");
                    playSegment(missedNotes.get(i));
                    break;
                }
            }
        }
    }

    private void playSegment(int noteTime) {
        System.out.println("Playing segment around: " + noteTime + " ms");
        if (clip != null) {
            clip.stop(); // Stop the clip if it is already playing
            clip.close(); // Close the clip to release the resources
        }
        try {
            // Assuming mainApp.music_name contains the path to the music file
            audioInputStream = AudioSystem.getAudioInputStream(new File(mainApp.music_name));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            // Calculate the start and end points for the segment to be played
            int start = Math.max(0, noteTime - 5000); // Start 5 seconds before the missed note time
            int end = noteTime + 5000; // End 5 seconds after the missed note time
            System.out.println("Segment start: " + start + " ms, end: " + end + " ms");

            // Set the position of the clip to the start point
            clip.setMicrosecondPosition(start * 1000); // Convert milliseconds to microseconds
            clip.start(); // Start playing the clip

            // Start a new thread to stop the clip after the segment duration has passed
            new Thread(() -> {
                try {
                    Thread.sleep(end - start); // Wait for the duration of the segment
                    clip.stop(); // Stop the clip
                    clip.close(); // Close the clip to release the resources
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt(); // Re-interrupt the thread if it was interrupted during sleep
                }
            }).start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing audio clip.");
            e.printStackTrace();
        }
    }

}
