package com.minesweeper;

import java.util.*;
import java.io.*;

/**
 * Класс, реализующий игровую логику 2048.
 * Содержит поле 4x4, текущий счёт, лучший рекорд и методы управления.
 */
public class GameBoard {
    private int[][] grid;
    private int score;
    private int bestScore;
    private boolean gameOver;
    private boolean winReached;
    private static final int SIZE = 4;
    private static final String STATS_FILE = "game2048_stats.properties";
    
    public GameBoard() {
        grid = new int[SIZE][SIZE];
        score = 0;
        gameOver = false;
        winReached = false;
        loadBestScore();
        resetGame();
    }
    
    /**
     * Сброс игры в начальное состояние.
     */
    public void resetGame() {
        // Очистка поля
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = 0;
            }
        }
        score = 0;
        gameOver = false;
        winReached = false;
        
        // Добавление двух начальных плиток
        addRandomTile();
        addRandomTile();
    }
    
    /**
     * Добавление случайной плитки (2 с вероятностью 90%, 4 с вероятностью 10%).
     */
    public void addRandomTile() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (grid[i][j] == 0) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }
        
        if (!emptyCells.isEmpty()) {
            Random rand = new Random();
            int[] randomCell = emptyCells.get(rand.nextInt(emptyCells.size()));
            int value = rand.nextDouble() < 0.9 ? 2 : 4;
            grid[randomCell[0]][randomCell[1]] = value;
        }
    }
    
    /**
     * Сдвиг всех плиток влево с объединением.
     * @return true, если поле изменилось
     */
    public boolean moveLeft() {
        boolean moved = false;
        
        for (int row = 0; row < SIZE; row++) {
            int[] newRow = new int[SIZE];
            int index = 0;
            
            // Сбор ненулевых элементов
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] != 0) {
                    newRow[index++] = grid[row][col];
                }
            }
            
            // Объединение равных соседних элементов
            for (int i = 0; i < SIZE - 1; i++) {
                if (newRow[i] != 0 && newRow[i] == newRow[i + 1]) {
                    newRow[i] *= 2;
                    score += newRow[i];
                    
                    // Проверка достижения цели
                    if (newRow[i] == 2048 && !winReached) {
                        winReached = true;
                    }
                    
                    newRow[i + 1] = 0;
                }
            }
            
            // Повторный сбор после объединения
            int[] finalRow = new int[SIZE];
            index = 0;
            for (int i = 0; i < SIZE; i++) {
                if (newRow[i] != 0) {
                    finalRow[index++] = newRow[i];
                }
            }
            
            // Проверка изменения строки
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] != finalRow[col]) {
                    moved = true;
                    grid[row][col] = finalRow[col];
                }
            }
        }
        
        // Обновление лучшего рекорда
        if (score > bestScore) {
            bestScore = score;
            saveBestScore();
        }
        
        return moved;
    }
    
    /**
     * Сдвиг вправо (через отражение и сдвиг влево).
     */
    public boolean moveRight() {
        reflectHorizontally();
        boolean moved = moveLeft();
        reflectHorizontally();
        return moved;
    }
    
    /**
     * Сдвиг вверх (через транспонирование и сдвиг влево).
     */
    public boolean moveUp() {
        transpose();
        boolean moved = moveLeft();
        transpose();
        return moved;
    }
    
    /**
     * Сдвиг вниз (через транспонирование, отражение и сдвиг влево).
     */
    public boolean moveDown() {
        transpose();
        reflectHorizontally();
        boolean moved = moveLeft();
        reflectHorizontally();
        transpose();
        return moved;
    }
    
    /**
     * Отражение матрицы по горизонтали.
     */
    private void reflectHorizontally() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE / 2; j++) {
                int temp = grid[i][j];
                grid[i][j] = grid[i][SIZE - 1 - j];
                grid[i][SIZE - 1 - j] = temp;
            }
        }
    }
    
    /**
     * Транспонирование матрицы.
     */
    private void transpose() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = i + 1; j < SIZE; j++) {
                int temp = grid[i][j];
                grid[i][j] = grid[j][i];
                grid[j][i] = temp;
            }
        }
    }
    
    /**
     * Проверка возможности выполнения хода.
     * @return true, если есть хотя бы один возможный ход
     */
    public boolean isMovePossible() {
        // Проверка пустых клеток
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (grid[i][j] == 0) return true;
            }
        }
        
        // Проверка возможности объединения по горизонтали
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE - 1; j++) {
                if (grid[i][j] == grid[i][j + 1]) return true;
            }
        }
        
        // Проверка возможности объединения по вертикали
        for (int i = 0; i < SIZE - 1; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (grid[i][j] == grid[i + 1][j]) return true;
            }
        }
        
        return false;
    }
    
    /**
     * Обновление состояния игры после хода.
     */
    public void updateGameState() {
        if (!isMovePossible()) {
            gameOver = true;
        }
    }
    
    /**
     * Загрузка лучшего рекорда из файла.
     */
    private void loadBestScore() {
        Properties props = new Properties();
        File file = new File(System.getProperty("user.home"), STATS_FILE);
        
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
                String bestScoreStr = props.getProperty("bestScore", "0");
                bestScore = Integer.parseInt(bestScoreStr);
            } catch (IOException | NumberFormatException e) {
                bestScore = 0;
            }
        } else {
            bestScore = 0;
        }
    }
    
    /**
     * Сохранение лучшего рекорда в файл.
     */
    private void saveBestScore() {
        Properties props = new Properties();
        props.setProperty("bestScore", String.valueOf(bestScore));
        
        File file = new File(System.getProperty("user.home"), STATS_FILE);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            props.store(fos, "2048 Game Best Score");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Сброс лучшего рекорда.
     */
    public void resetBestScore() {
        bestScore = 0;
        saveBestScore();
    }
    
    // Геттеры
    public int[][] getGrid() { return grid; }
    public int getScore() { return score; }
    public int getBestScore() { return bestScore; }
    public boolean isGameOver() { return gameOver; }
    public boolean isWinReached() { return winReached; }
}
