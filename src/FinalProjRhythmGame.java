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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.Path;
import java.io.BufferedWriter;
import java.io.FileWriter;



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
    public String package_name = "BadApple";
    String music_folder = "music_package";
    String music_name;
    String music_txt;
    String music_png;
    String music_delay;
    String fireworks_delay;
    int music_delay_num = 4000;
    int fireworks_delay_num = 0;
    int comboCount = 0;
    OpenCV openCVController;
    public boolean openCVUesd = false; // 是否使用OpenCV Whether to use OpenCV，Ture则点头 = 点击鼠标
    int frameOpenCVCount = 0;
    enum GameState {
        MENU, PLAYING, END
    }
    GameState currentState = GameState.MENU;
    MenuScreen menuScreen;
    boolean musicFlag = true;


    public static void main(String[] args) {
        PApplet.main("FinalProjRhythmGame");
    }

    public void settings() {
        //清除txt中的所有内容,以便本次游戏重新记录错误数据 Clear all content in txt so that this game can re-record data
        //Helper function to clear or create file content
        try {
            clearOrCreateFile("MissFireWorkTimes.txt");
            clearOrCreateFile("MappedTimes.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        fullScreen(); // 设置画布为全屏 Set the canvas to full screen
    }
    public void setup() {
        frameRate(240);  // 将帧数上限设为240 Set the frame rate limit to 240
        setupStars();
        startTime = millis();
        openCVController = new OpenCV();
        menuScreen = new MenuScreen(this);
        menuScreen.initialize();
    }
    public void draw() {
        int referenceTime = startTime + music_delay_num;
        if (musicFlag == true)
        {
            MusicChange();
//            musicFlag = false;
        }
        switch(currentState) {
            case MENU:
                menuScreen.display();
                break;
            case PLAYING:
                GameMainController();
                break;
            case END:
                TimesMapper.FileWR(music_txt);

                break;
        }
    }
    public void MusicChange()
    {
        // 使用基础的包名来构建其它文件名 Build other file names using the base package name
        music_name = music_folder + "/" + package_name + "/" + package_name + ".mp3";
        music_txt = music_folder + "/" + package_name + "/" + package_name + ".txt";
        music_png = music_folder + "/" + package_name + "/" + package_name + ".jpg";
        music_delay = music_folder + "/" + package_name + "/" + package_name+"_delay" + ".txt"; //曲子播放延迟时间数据存储路径 Path to store song playback delay time data
        fireworks_delay = music_folder + "/" + package_name + "/" + package_name+"_Fireworksdelay" + ".txt";

        // 初始化为默认值 Initialize to default value
        music_delay_num = 4000;
        fireworks_delay_num = 0;
        // 检查文件是否存在 Check if the file exists
        File delayFile = new File(music_delay);
        if (delayFile.exists() && delayFile.isFile()) {
            String[] lines = loadStrings(music_delay); //读取延迟时间数据 Read delay time data
            if (lines != null && lines.length > 0) {
                music_delay_num = Integer.parseInt(lines[0].trim());
            }
        }

        File FireworksdelayFile = new File(fireworks_delay);
        if (FireworksdelayFile.exists() && FireworksdelayFile.isFile()) {
            String[] lines = loadStrings(fireworks_delay); //读取延迟时间数据 Read delay time data
            if (lines != null && lines.length > 0) {
                fireworks_delay_num = Integer.parseInt(lines[0].trim());
            }
        }

        String[] lines2 = loadStrings(music_txt);

        for (String line : lines2) {
            String[] parts = line.split(","); // x 和 time 由逗号分隔 x and time are separated by commas
            int x = Integer.parseInt(parts[0].trim());
            int time = Integer.parseInt(parts[1].trim());
            timedEvents.add(new TimedEvent(x, time));
        }
    }
    public void ComboJudge()
    {
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
    }
    public void mousePressed() {
        float lowerBound = height * 0.7f;
        float upperBound = height * 0.9f;

        if (currentState == GameState.MENU) {
            menuScreen.mouseClicked();
        }

        if (mouseY >= lowerBound && mouseY <= upperBound) {
            boolean hitFlag = false;  // 标志是否有烟花被点击 Flag whether a firework is clicked

            for (Firework f : fireworks) {
                float hitRange = 10.0f;
                if (mouseY >= f.firework.position.y - hitRange && mouseY <= f.firework.position.y + height / 10 + hitRange) {
                    score++;
                    comboCount++;
                    // 检查连击状态，如果 comboCount 大于 5，传递 true 给 Ripple 构造函数
                    boolean isCombo = comboCount > 5;
                    // 现在传递所有必需的参数给 Ripple 构造函数，包括 combo 状态
                    ripples.add(new Ripple(this, mouseX, mouseY, isCombo));
                    f.exploded = true;
                    playWav("CorrectHit.wav");
                    hitFlag = true;
                    break;
                }
            }

            // 当没有任何烟花被点击时，重置comboCount Only when no fireworks are clicked, reset comboCount
            if (!hitFlag) {
                comboCount = 0;
                String filename = "MissFireWorkTimes.txt";
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
                    long relativeTime = millis() - startTime; // 获取从程序开始运行的相对时间 Get relative time from program start
                    bw.write(Long.toString(relativeTime));
                    bw.newLine();  // 新的一行 New line
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            hitFlag = false;  // 重置标志 Reset flag
        }
    }

    public void GameMainController()
    {
        drawNightSky();
        ComboJudge();
        int currentTime = millis();

        for (int i = 0; i < timedEvents.size(); i++) {
            TimedEvent event = timedEvents.get(i);

            // 缩放 x 坐标
            int scaledX = (int) map(event.x, 0, 500, 200, width-200); // 原始 x 坐标的最大值是 500，width 是屏幕宽度 The maximum value of the original x coordinate is 500, width is the screen width

            if (currentTime >= event.time + fireworks_delay_num && currentTime <= event.time + fireworks_delay_num + 10) { // 容错范围 fault tolerance range
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

            if (f.exploded ) {
                fireworks.remove(i);
            }
        }
        if (!musicStarted && millis() - startTime >= music_delay_num) { // 根据.txt中的延迟时间来播放音乐 Play music according to the delay time in .txt
            musicStarted = true; // 设置标志，防止多次播放 Set the flag to prevent multiple plays

            try {
                FileInputStream fileInputStream = new FileInputStream(music_name);
                AdvancedPlayer player = new AdvancedPlayer(fileInputStream);

                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        System.out.println("Playback finished");
                        currentState = GameState.END;
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
            // 现在传递当前的 comboCount 给 update 方法
            r.update(comboCount);
            r.display(this);

            if (r.isDone()) {
                ripples.remove(i);
            }
        }
        frameOpenCVCount++;
        if (openCVUesd && frameOpenCVCount % 10 == 0) {
            openCVController.update();
        }
    }

    private void clearOrCreateFile(String filename) throws IOException {
        Path path = Paths.get(filename);
        if (Files.exists(path)) {
            // If file exists, truncate its content
            Files.write(path, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
        } else {
            // If file doesn't exist, create it
            Files.createFile(path);
        }
    }
    private Runnable openCVUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            openCVController.update();
        }
    };
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
    public static class TimedEvent {
        public int x; // x 坐标 x coordinate
        public int time; // 时间（毫秒） Time (milliseconds)

        public TimedEvent(int x, int time) {
            this.x = x;
            this.time = time;
        }
    }
    void dashedLine(float y1, float x2, float y2, float dashLength, float gapLength) {
        float distance = dist((float) 50, y1, x2, y2);
        float dashAndGap = dashLength + gapLength;
        float numberOfDashes = distance / dashAndGap;
        for (float i = 0; i < numberOfDashes; i++) {
            float startX = lerp((float) 50, x2, i / numberOfDashes);
            float startY = lerp(y1, y2, i / numberOfDashes);
            float endX = lerp((float) 50, x2, (i * dashAndGap + dashLength) / distance);
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
        if (!fireworks.isEmpty() && fireworks.size()<5)
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

        dashedLine(lowerBound, width - 50, lowerBound, dashLength, gapLength);
        dashedLine(upperBound, width - 50, upperBound, dashLength, gapLength);

    }
    public void setupStars() { // 初始化星星 Initialize stars
        int numStars = 100;
        for (int i = 0; i < numStars; i++) {
            stars.add(new Star(this));
        }
    }
}

