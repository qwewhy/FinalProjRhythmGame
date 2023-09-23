//Hongyuan Wang 25124924
//SDK corretto-11 Amazon Corretto version 11.0.19 in IDEA
//processing 3.5.4 ---> core_3.5.4.jar
import java.util.ArrayList;
import processing.core.PApplet;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import java.io.FileInputStream;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import ddf.minim.*;
import ddf.minim.analysis.*;

public class FinalProjRhythmGame extends PApplet {
    ArrayList<Firework> fireworks = new ArrayList<Firework>(); // List存储烟花对象 Store Firework objects
    float moonX;
    ArrayList<Integer> timeSeries = new ArrayList<Integer>();//曲包的时间序列  time series of the music package
    ArrayList<Ripple> ripples = new ArrayList<Ripple>();//波纹效果 ripple effect
    ArrayList<TimedEvent> timedEvents = new ArrayList<>();
    ArrayList<Star> stars = new ArrayList<Star>(); // List存储星星对象 Store Star objects
    int startTime;
    int score = 0;
    int totalFireworks = 0;
    boolean musicStarted = false;
    String package_name = "Komorebi"; // 严格输入曲名 Strictly enter the song name
    String music_folder = "music_package";
    String music_name;
    String music_txt;
    String music_png;
    String music_delay;
    int music_delay_num = 4000;
    int comboCount = 0;

    public void playWav(String filename) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filename).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch(UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            System.out.println("Error playing .wav file.");
            ex.printStackTrace();
        }
    }

    public class TimedEvent {
        public int x; // x 坐标 x coordinate
        public int time; // 时间（毫秒） Time (milliseconds)

        public TimedEvent(int x, int time) {
            this.x = x;
            this.time = time;
        }
    }

    public static void main(String[] args) {
        PApplet.main("FinalProjRhythmGame");
    }

    public void settings() {
        // 使用基础的包名来构建其它文件名 Build other file names using the base package name
        music_name = music_folder + "/" + package_name + "/" + package_name + ".mp3";
        music_txt = music_folder + "/" + package_name + "/" + package_name + ".txt";
        music_png = music_folder + "/" + package_name + "/" + package_name + ".jpg";
        music_delay = music_folder + "/" + package_name + "/" + package_name+"_delay" + ".txt"; //曲子播放延迟时间数据存储路径 Path to store song playback delay time data
        // 初始化为默认值 Initialize to default value
        music_delay_num = 4000;

        // 检查文件是否存在 Check if the file exists
        File delayFile = new File(music_delay);
        if (delayFile.exists() && delayFile.isFile()) {
            String[] lines = loadStrings(music_delay); //读取延迟时间数据 Read delay time data
            if (lines != null && lines.length > 0) {
                music_delay_num = Integer.parseInt(lines[0].trim());
            }
        }

        String[] lines2 = loadStrings(music_txt);

        for (String line : lines2) {
            String[] parts = line.split(","); // x 和 time 由逗号分隔 x and time are separated by commas
            int x = Integer.parseInt(parts[0].trim());
            int time = Integer.parseInt(parts[1].trim());
            timedEvents.add(new TimedEvent(x, time));
        }

        fullScreen(); // 设置画布为全屏 Set the canvas to full screen
    }


    public void setupStars() { // 初始化星星 Initialize stars
        int numStars = 100;
        for (int i = 0; i < numStars; i++) {
            stars.add(new Star(this));
        }
    }
    void dashedLine(float x1, float y1, float x2, float y2, float dashLength, float gapLength) {
        float distance = dist(x1, y1, x2, y2);
        float dashAndGap = dashLength + gapLength;
        float numberOfDashes = distance / dashAndGap;
        for (float i = 0; i < numberOfDashes; i++) {
            float startX = lerp(x1, x2, i / numberOfDashes);
            float startY = lerp(y1, y2, i / numberOfDashes);
            float endX = lerp(x1, x2, (i * dashAndGap + dashLength) / distance);
            float endY = lerp(y1, y2, (i * dashAndGap + dashLength) / distance);
            line(startX, startY, endX, endY);
        }
    }

    public void drawNightSky() {
        background(0, 0, 0);
        fill(255, 255, 200);
        noStroke();
        ellipse(moonX, height * 0.25f, 100, 100);  // Draw the moon

        for (Star s : stars) {
            s.update();
            s.display();
        }

        if(frameCount%20==0) {moonX -= 1;}// 月亮向左移动 Moon moves left,20帧一次

        // 检查月亮是否已经离开了屏幕 Check if the moon has left the screen
        if (moonX < -50) {  // 月亮完全离开屏幕 Moon is completely off the screen
            moonX = width + 50;  // 重新设置月亮的位置 Reset the moon's position
        }
        float lowerBound = height * 0.7f; // Y/10*7
        float upperBound = height * 0.9f; // Y/10*9
        if (fireworks.size() >= 1 && fireworks.size()<5)
        {
            stroke(50);  // 设置线条颜色为灰色 Set line color to gray
        }
        else if (fireworks.size() >= 5 && fireworks.size()<10)
        {
            stroke(100);  // 设置线条颜色为灰色 Set line color to gray
        }
        else if (fireworks.size()>=10)
        {
            stroke(120,0,0);
        }
        else
        {
            stroke(10);  // 设置线条颜色为深灰色 Set line color to white
        }
        strokeWeight(2); // 设置线条宽度 Set line width
        float dashLength = 10;  // 虚线的长度 Length of the dash
        float gapLength = 5;    // 间隙的长度 Length of the gap

        dashedLine(50, lowerBound, width - 50, lowerBound, dashLength, gapLength);
        dashedLine(50, upperBound, width - 50, upperBound, dashLength, gapLength);

    }
    public void setup() {
        frameRate(240);  // 将帧数上限设为240 Set the frame rate limit to 240
        setupStars();
        startTime = millis();
    }

    public void draw() {
        drawNightSky();
        text("Score: " + score + "/" + totalFireworks, 10, 30);
        if (comboCount ==1)
        {
            fill(60);
        }
        else if (comboCount >1&& comboCount<=5)
        {
            fill(80);
        }
        else if (comboCount >5&& comboCount<=10)
        {
            fill(100);
        }
        else if (comboCount >10&& comboCount<=20)
        {
            fill(120);
        }
        else if (comboCount >20&& comboCount<=40)
        {
            fill(130,130,0);
        }
        else if (comboCount >40)
        {
            fill(255,223,0);
        }
        else
        {
            fill(30,0,0);
        }
        text("Combo: " + comboCount, width / 2 - 60, 50);
        textSize(32);

        int currentTime = millis();

        for (int i = 0; i < timedEvents.size(); i++) {
            TimedEvent event = timedEvents.get(i);

            // 缩放 x 坐标
            int scaledX = (int) map(event.x, 0, 500, 200, width-200); // 原始 x 坐标的最大值是 500，width 是屏幕宽度 The maximum value of the original x coordinate is 500, width is the screen width

            if (currentTime >= event.time && currentTime <= event.time + 10) { // 50毫秒的容错范围 50ms fault tolerance range
                fireworks.add(new Firework(this, scaledX));  // 使用缩放后的 x 坐标 Use scaled x coordinate
                totalFireworks++;
                timedEvents.remove(i);
                break;
            }
        }

        for (int i = fireworks.size() - 1; i >= 0; i--) {
            Firework f = fireworks.get(i);
            f.update();
            f.display();

            if (f.exploded && f.particles.size() == 0) {
                fireworks.remove(i);
            }
        }
        if (!musicStarted && millis() - startTime >= music_delay_num) { // 根据.txt中的延迟时间来播放音乐 Play music according to the delay time in .txt
            musicStarted = true; // 设置标志，防止多次播放

            try {
                FileInputStream fileInputStream = new FileInputStream(music_name);
                AdvancedPlayer player = new AdvancedPlayer(fileInputStream);

                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        System.out.println("Playback finished");
                    }
                });

                new Thread(() -> {  // 在新的线程中播放音乐，避免阻塞UI线程 Play music in a new thread to avoid blocking the UI thread
                    try {
                        player.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (int i = ripples.size() - 1; i >= 0; i--) { // 更新并绘制波纹 Update and draw ripples
            Ripple r = ripples.get(i);
            r.update();
            r.display(this);

            if (r.isDone()) {
                ripples.remove(i);
            }
        }
    }

    public void mousePressed() {
        float lowerBound = height * 0.7f;
        float upperBound = height * 0.9f;

        if (mouseY >= lowerBound && mouseY <= upperBound) {
            boolean hitFlag = false;  // 标志是否有烟花被点击 Flag whether a firework is clicked

            for (Firework f : fireworks) {
                float hitRange = 10.0f;
                if (mouseY >= f.firework.position.y - hitRange && mouseY <= f.firework.position.y + height / 10 + hitRange) {
                    score++;
                    comboCount++;
                    ripples.add(new Ripple(this, mouseX, mouseY));  // 添加波纹 Add ripple
                    f.exploded = true; // Explode
                    playWav("CorrectHit.wav"); // 播放音效 Play sound effect
                    hitFlag = true;  // 设置标志为真，表示有烟花被点击 Set the flag to true to indicate that a firework is clicked
                    break;  // 如果已经检测到点击，可以跳出循环 If the click has been detected, you can break out of the loop
                }
            }

            // 只有当没有任何烟花被点击时，才重置comboCount Only when no fireworks are clicked, reset comboCount
            if (!hitFlag) {
                comboCount = 0;
            }
        }
    }

}

