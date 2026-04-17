

import java.io.*;
import java.util.Properties;

/**
 * Класс для сохранения и загрузки статистики (лучшего рекорда).
 */
public class Statistics {
    private static final String STATS_FILE = "game2048_stats.properties";
    
    /**
     * Загрузка лучшего рекорда из файла.
     * @return лучший рекорд, или 0 если файл не найден или повреждён
     */
    public static int loadBestScore() {
        Properties props = new Properties();
        File file = new File(System.getProperty("user.home"), STATS_FILE);
        
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
                String bestScoreStr = props.getProperty("bestScore", "0");
                return Integer.parseInt(bestScoreStr);
            } catch (IOException | NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * Сохранение лучшего рекорда в файл.
     * @param bestScore значение лучшего рекорда
     */
    public static void saveBestScore(int bestScore) {
        Properties props = new Properties();
        props.setProperty("bestScore", String.valueOf(bestScore));
        
        File file = new File(System.getProperty("user.home"), STATS_FILE);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            props.store(fos, "2048 Game Best Score");
        } catch (IOException e) {
            System.err.println("Failed to save best score: " + e.getMessage());
        }
    }
    
    /**
     * Сброс лучшего рекорда.
     */
    public static void resetBestScore() {
        saveBestScore(0);
    }
}
