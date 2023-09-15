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

public class FinalProjRhythmGame extends PApplet {
    int currentSensorValueIndex = 0; // 当前传感器数据的索引用于无限循环URL数据 Current sensor data index used for infinite looping of URL data
    List<Float> sensorValues; // List存储传感器的数据值 Store sensor data values
    ArrayList<Firework> fireworks = new ArrayList<Firework>(); // List存储烟花对象 Store Firework objects
    String apiUrl = "https://eif-research.feit.uts.edu.au/api/csv/?rFromDate=2020-08-15T19%3A06%3A09&rToDate=2020-08-17T19%3A06%3A09&rFamily=wasp&rSensor=ES_B_06_418_7BED&rSubSensor=HUMA";
    float moonX;

    public static void main(String[] args) {
        PApplet.main("FinalProjRhythmGame");
    }

    public void settings() {
        fullScreen(); // 设置画布为全屏 Set canvas to full screen
    }

    // 从API获取数据 Fetch data from the API
    private String httpGet(String apiUrl) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(apiUrl);
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line).append("\n");
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    // 从CSV数据中提取传感器的数据值 Extract sensor values from CSV data
    private List<Float> extractDataValues(String csvData) {
        List<Float> values = new ArrayList<>();
        String[] lines = csvData.split("\n");
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length > 1) {
                try {
                    values.add(Float.parseFloat(parts[1]));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return values;
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
        moonX -= 1;

        // 检查月亮是否已经离开了屏幕 Check if the moon has left the screen
        if (moonX < -50) {  // 月亮完全离开屏幕 Moon is completely off the screen
            moonX = width + 50;  // 重新设置月亮的位置 Reset the moon's position
        }
    }
    public void setup() {
        frameRate(240);  // 将帧数上限设为80
        setupStars();
        sensorValues = extractDataValues(httpGet(apiUrl));
        print(sensorValues);
    }

    public void draw() {
        // 绘制星空 Draw the night sky
        drawNightSky();

        // 使用传感器数据生成烟花 Use sensor data to generate fireworks
        if (frameCount % 30 == 0) {
            float currentSensorValue = sensorValues.get(currentSensorValueIndex);
            //这里我用map（）方法，将传感器的数值映射到一个新的范围，然后才能随机生成烟花
            //Here I use the map() to map the sensor value to a new range, and then I can randomly generate fireworks
            //currentSensorValue like this -> 61.18626, 61.09955, 61.168922, 61.151573, 61.082203, 61.09954, 61.116882...
            //区间映射，将50-70的数值映射到0.05-0.5的数值区间 Range mapping, map the value of 50-70 to the value range of 0.05-0.5
            if (random(1) < map(currentSensorValue, 50.0f, 70.0f, 0.05f, 0.5f)) {
                fireworks.add(new Firework(this));
            }
            currentSensorValueIndex++;  // 更新传感器数据索引 Update sensor data index

            // 无限循环传感器数据索引 Infinite looping of sensor data index
            if (currentSensorValueIndex >= sensorValues.size()) {
                currentSensorValueIndex = 0;
            }
        }

        // 更新并展示烟花 Update and display fireworks
        for (int i = fireworks.size() - 1; i >= 0; i--) {
            fireworks.get(i).update();
            fireworks.get(i).display();
            if (fireworks.get(i).exploded && fireworks.get(i).particles.size() == 0) {
                fireworks.remove(i);
            }
        }
    }

    // 计算传感器数据的平均值 Calculate average of sensor data values
    private float averageSensorValue() {
        float sum = 0;
        for (Float value : sensorValues) {
            sum += value;
        }
        return sum / sensorValues.size();
    }
}

