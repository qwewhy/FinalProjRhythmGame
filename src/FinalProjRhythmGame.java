//Hongyuan Wang 25124924
//SDK corretto-11 Amazon Corretto version 11.0.19 in IDEA
//processing 3.5.4 ---> core_3.5.4.jar
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PVector;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import java.io.FileInputStream;

public class FinalProjRhythmGame extends PApplet {
    int currentSensorValueIndex = 0; // 当前传感器数据的索引用于无限循环URL数据 Current sensor data index used for infinite looping of URL data
    List<Float> sensorValues; // List存储传感器的数据值 Store sensor data values
    ArrayList<Firework> fireworks = new ArrayList<Firework>(); // List存储烟花对象 Store Firework objects
    float moonX;
    ArrayList<Integer> timeSeries = new ArrayList<Integer>();//曲包的时间序列  time series of the music package
    int startTime;
    boolean musicStarted = false;

    public class TimedEvent {
        public int x; // x 坐标
        public int time; // 时间（毫秒）

        public TimedEvent(int x, int time) {
            this.x = x;
            this.time = time;
        }
    }
    ArrayList<TimedEvent> timedEvents = new ArrayList<>();

    public static void main(String[] args) {
        PApplet.main("FinalProjRhythmGame");
    }

    public void settings() {
        String[] lines = loadStrings("music_package/BadApple/BadAppleHard.txt");
        for (String line : lines) {
            String[] parts = line.split(","); // 假设 x 和 time 由逗号分隔
            int x = Integer.parseInt(parts[0].trim());
            int time = Integer.parseInt(parts[1].trim());
            timedEvents.add(new TimedEvent(x, time));
        }

        fullScreen(); // 设置画布为全屏
    }
    ArrayList<Star> stars = new ArrayList<Star>(); // List存储星星对象 Store Star objects

    public void setupStars() { // 初始化星星 Initialize stars
        int numStars = 100;
        for (int i = 0; i < numStars; i++) {
            stars.add(new Star(this));
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

        if(frameCount%5==0) {moonX -= 1;}

        // 检查月亮是否已经离开了屏幕 Check if the moon has left the screen
        if (moonX < -50) {  // 月亮完全离开屏幕 Moon is completely off the screen
            moonX = width + 50;  // 重新设置月亮的位置 Reset the moon's position
        }
    }
    public void setup() {
        frameRate(240);  // 将帧数上限设为240 Set the frame rate limit to 240
        setupStars();
        startTime = millis();
    }

    public void draw() {
        drawNightSky();
        int currentTime = millis();

        for (int i = 0; i < timedEvents.size(); i++) {
            TimedEvent event = timedEvents.get(i);

            // 缩放 x 坐标
            int scaledX = (int) map(event.x, 0, 500, 0, width); // 原始 x 坐标的最大值是 500，width 是屏幕宽度

            if (currentTime >= event.time && currentTime <= event.time + 10) { // 50毫秒的容错范围
                fireworks.add(new Firework(this, scaledX));  // 使用缩放后的 x 坐标
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
        if (!musicStarted && millis() - startTime >= 2000) { // 2秒后开始播放
            musicStarted = true; // 设置标志，防止多次播放

            try {
                FileInputStream fileInputStream = new FileInputStream("music_package/BadApple/BadApple.mp3");
                AdvancedPlayer player = new AdvancedPlayer(fileInputStream);

                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        System.out.println("Playback finished");
                    }
                });

                new Thread(() -> {  // 在新的线程中播放音乐，避免阻塞UI线程
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
    }

}

