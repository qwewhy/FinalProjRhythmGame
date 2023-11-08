import java.nio.file.*;
import java.io.IOException;
import java.util.*;

public class TimesMapper {

    /**
     * @param missTimesPath 错误点击时间的文件路径，如 "MissFireWorkTimes.txt"
     * @param musicTimesPath 曲子时间序列文件路径，如 "HighScore.txt"
     * @return 一个包含转换后对应的曲子时间序列的列表
     */
    public static List<Integer> mapTimes(String missTimesPath, String musicTimesPath) {
        List<Integer> mappedTimes = new ArrayList<>();
        List<Integer> missTimes = readTimesFromFile(missTimesPath);
        List<Integer> musicTimes = readMusicTimesFromFile(musicTimesPath);

        for (int missTime : missTimes) {
            int closestTime = findClosestTime(missTime, musicTimes);
            mappedTimes.add(closestTime);
        }

        return mappedTimes;
    }

    private static List<Integer> readTimesFromFile(String path) {
        List<Integer> times = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            for (String line : lines) {
                times.add(Integer.parseInt(line.trim()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return times;
    }

    private static List<Integer> readMusicTimesFromFile(String path) {
        List<Integer> times = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            for (String line : lines) {
                String[] parts = line.split(",");
                times.add(Integer.parseInt(parts[1].trim()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return times;
    }

    private static int findClosestTime(int time, List<Integer> times) {
        int closest = times.get(0);
        int minDiff = Math.abs(time - closest);

        for (int t : times) {
            int diff = Math.abs(time - t);
            if (diff < minDiff) {
                minDiff = diff;
                closest = t;
            }
        }

        return closest;
    }

    private static void writeTimesToFile(List<Integer> times, String path) {
        try {
            List<String> lines = new ArrayList<>();
            for (Integer time : times) {
                lines.add(String.valueOf(time));
            }
            Files.write(Paths.get(path), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void FileWR(String musicPlayingNowTimesPath) {
        List<Integer> results = mapTimes("MissFireWorkTimes.txt", musicPlayingNowTimesPath);

        // 使用 HashSet 来去除重复
        Set<Integer> uniqueResults = new HashSet<>(results);

        // 为了保持顺序性（如果需要），将 Set 转回 List
        List<Integer> sortedUniqueResults = new ArrayList<>(uniqueResults);
        Collections.sort(sortedUniqueResults);

        writeTimesToFile(sortedUniqueResults, "MappedTimes.txt");
    }


    public static void main(String[] args) {
        List<Integer> results = mapTimes("MissFireWorkTimes.txt", "music_package/HighScore/HighScore.txt");
        writeTimesToFile(results, "MappedTimes.txt");//输出的数据是错误点击或未能点击的音符对应的时间序列
    }
}
