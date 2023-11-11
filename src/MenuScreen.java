import processing.core.PImage;
import java.io.File;

public class MenuScreen {

    FinalProjRhythmGame mainApp;
    File[] songs;
    PImage[] songIcons;
    String[] songNames;
    int currentPage = 0;
    final int SONGS_PER_PAGE = 9;
    int textDisplayTime;

    public MenuScreen(FinalProjRhythmGame app) {
        this.mainApp = app;
    }

    public void initialize() {
        System.out.println("Sketch path: " + mainApp.sketchPath());
        loadSongs();
    }

    private String constructPath(String packageName, String extension) {
        return mainApp.music_folder + "/" + packageName + "/" + packageName + extension;
    }

    private void loadSongs() {
        File musicDir = new File(mainApp.sketchPath() + "/music_package");
        songs = musicDir.listFiles(File::isDirectory); // 获取所有歌曲目录 get all song directories

        songIcons = new PImage[songs.length];
        songNames = new String[songs.length];

        for (int i = 0; i < songs.length; i++) {
            songNames[i] = songs[i].getName();
            String basePath = mainApp.sketchPath() + "/music_package/" + songNames[i] + "/" + songNames[i];

            // 首先尝试加载.jpg图片
            if (new File(basePath + ".jpg").exists()) {
                songIcons[i] = mainApp.loadImage(basePath + ".jpg");
            }
            // 如果.jpg图片未加载，则尝试.png
            else if (new File(basePath + ".png").exists()) {
                songIcons[i] = mainApp.loadImage(basePath + ".png");
            }
        }
    }
    int selectedSongIndex = -1; // 新增变量，用于存储被选中的歌曲的索引 Add a new variable to store the index of the selected song
    public void display() {
        mainApp.background(0);  // 设定背景为纯黑色 set the background to black

        int rows = 3;
        int cols = 3;
        int iconSize = 200;
        int spacing = 30;  // 增加间距

        int startX = (mainApp.width - (cols * iconSize + (cols - 1) * spacing)) / 2;  // 居中对齐 Center alignment
        int startY = (mainApp.height - (rows * (iconSize + spacing) - spacing)) / 2;  // 居中对齐 Center alignment

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int index = currentPage * SONGS_PER_PAGE + i * cols + j;
                if (index < songNames.length) {
                    if (songIcons[index] != null) {
                        mainApp.image(songIcons[index], startX + j * (iconSize + spacing), startY + i * (iconSize + spacing), iconSize, iconSize);
                    }
                    mainApp.textAlign(mainApp.CENTER, mainApp.CENTER);
                    mainApp.text(songNames[index], startX + j * (iconSize + spacing) + iconSize / 2, startY + i * (iconSize + spacing) + iconSize + 10); // 为曲名提供额外的空间
                }
            }
        }
        // 高亮选中的歌曲图标 Highlight the selected song icon
        if (selectedSongIndex != -1) {
            int row = (selectedSongIndex % SONGS_PER_PAGE) / cols;
            int col = (selectedSongIndex % SONGS_PER_PAGE) % cols;
            mainApp.stroke(255, 255, 0);  // 黄色高亮
            mainApp.strokeWeight(5);
            mainApp.noFill();
            mainApp.rect(startX + col * (iconSize + spacing), startY + row * (iconSize + spacing), iconSize, iconSize);
            mainApp.noStroke();
        }

        // Next and Previous Page Buttons
        mainApp.fill(100);
        mainApp.rect(mainApp.width - 110, mainApp.height - 40, 100, 30);
        mainApp.fill(255);
        mainApp.text("Next", mainApp.width - 60, mainApp.height - 25);

        mainApp.fill(100);
        mainApp.rect(10, mainApp.height - 40, 100, 30);
        mainApp.fill(255);
        mainApp.text("Previous", 60, mainApp.height - 25);

        // Start Button
        mainApp.fill(50, 255, 50);
        mainApp.rect(mainApp.width / 2 - 50, mainApp.height - 100, 100, 50);
        mainApp.fill(255);
        mainApp.text("Start", mainApp.width / 2, mainApp.height - 75);

        // Exit Button
        mainApp.fill(255, 50, 50);
        mainApp.rect(mainApp.width / 2 - 50, mainApp.height - 40, 100, 30);
        mainApp.fill(255);
        mainApp.text("Exit", mainApp.width / 2, mainApp.height - 25);

        if (textDisplayTime != -1 && mainApp.millis() - textDisplayTime <= 3000) {
            mainApp.fill(255, 0, 0);  // set text color to red
            mainApp.textAlign(mainApp.CENTER, mainApp.CENTER);
            mainApp.textSize(32);  // set the font size to 32 pixels
            mainApp.text("Please select a song!", mainApp.width / 2, mainApp.height / 2);
            mainApp.textSize(12);  // reset the font size back to the default (or whatever size you were using previously)
        }

    }

    public void mouseClicked() {
        // Check if any song icon is clicked
        int rows = 3;
        int cols = 3;
        int iconSize = 200;
        int spacing = 30;

        int startX = (mainApp.width - (cols * iconSize + (cols - 1) * spacing)) / 2;
        int startY = (mainApp.height - (rows * (iconSize + spacing) - spacing)) / 2;

        boolean songClicked = false;

        for (int i = 0; i < rows && !songClicked; i++) {
            for (int j = 0; j < cols; j++) {
                int index = currentPage * SONGS_PER_PAGE + i * cols + j;
                if (index < songNames.length && songIcons[index] != null &&
                        mainApp.mouseX >= startX + j * (iconSize + spacing) && mainApp.mouseX <= startX + j * (iconSize + spacing) + iconSize &&
                        mainApp.mouseY >= startY + i * (iconSize + spacing) && mainApp.mouseY <= startY + i * (iconSize + spacing) + iconSize) {
                    mainApp.package_name = songNames[index];
                    selectedSongIndex = index;
                    songClicked = true;
                    break;
                }
            }
        }

        // Check if Next or Previous is clicked
        if (mainApp.mouseX >= mainApp.width - 110 && mainApp.mouseX <= mainApp.width - 10 && mainApp.mouseY >= mainApp.height - 40 && mainApp.mouseY <= mainApp.height - 10 && (currentPage + 1) * SONGS_PER_PAGE <= songNames.length) {
            currentPage++;
        } else if (mainApp.mouseX >= 10 && mainApp.mouseX <= 110 && mainApp.mouseY >= mainApp.height - 40 && mainApp.mouseY <= mainApp.height - 10 && currentPage > 0) {
            currentPage--;
        }

        // Check if Start or Exit is clicked
        if (mainApp.mouseX >= mainApp.width / 2 - 50 && mainApp.mouseX <= mainApp.width / 2 + 50) {
            if (mainApp.mouseY >= mainApp.height - 100 && mainApp.mouseY <= mainApp.height - 50) {
                if (selectedSongIndex != -1) {
                    mainApp.musicFlag = false;
                    mainApp.currentState = FinalProjRhythmGame.GameState.PLAYING;
                } else {
                    System.out.println("Please select a song!");
                    textDisplayTime = mainApp.millis(); // 记录当前时间
                }
            } else if (mainApp.mouseY >= mainApp.height - 40 && mainApp.mouseY <= mainApp.height - 10) {
                mainApp.exit();
            }
        }
    }
}

