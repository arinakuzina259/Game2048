package com.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Панель для отрисовки игрового поля и обработки клавиш.
 */
public class GamePanel extends JPanel {
    private GameBoard gameBoard;
    private Map<Integer, Color> tileColors;
    private static final int SIZE = 4;
    
    public GamePanel(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        initColors();
        setFocusable(true);
        addKeyListener(new KeyHandler());
        setBackground(new Color(250, 248, 239));
    }
    
    /**
     * Инициализация цветов для разных значений плиток.
     */
    private void initColors() {
        tileColors = new HashMap<>();
        tileColors.put(0, new Color(205, 193, 180));
        tileColors.put(2, new Color(238, 228, 218));
        tileColors.put(4, new Color(237, 224, 200));
        tileColors.put(8, new Color(242, 177, 121));
        tileColors.put(16, new Color(245, 149, 99));
        tileColors.put(32, new Color(246, 124, 95));
        tileColors.put(64, new Color(246, 94, 59));
        tileColors.put(128, new Color(237, 207, 114));
        tileColors.put(256, new Color(237, 204, 97));
        tileColors.put(512, new Color(237, 200, 80));
        tileColors.put(1024, new Color(237, 197, 63));
        tileColors.put(2048, new Color(237, 194, 46));
    }
    
    /**
     * Получение цвета плитки по её значению.
     */
    private Color getTileColor(int value) {
        if (tileColors.containsKey(value)) {
            return tileColors.get(value);
        }
        // Для значений больше 2048
        return new Color(60, 58, 50);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int tileSize = Math.min(panelWidth, panelHeight) / SIZE - 10;
        int offsetX = (panelWidth - tileSize * SIZE) / 2;
        int offsetY = (panelHeight - tileSize * SIZE) / 2;
        
        int[][] grid = gameBoard.getGrid();
        
        // Отрисовка плиток
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int value = grid[row][col];
                int x = offsetX + col * tileSize;
                int y = offsetY + row * tileSize;
                
                // Закруглённый прямоугольник для плитки
                g2d.setColor(getTileColor(value));
                g2d.fillRoundRect(x, y, tileSize, tileSize, 15, 15);
                
                // Рамка
                g2d.setColor(new Color(187, 173, 160));
                g2d.drawRoundRect(x, y, tileSize, tileSize, 15, 15);
                
                // Отрисовка текста (значения)
                if (value != 0) {
                    String text = String.valueOf(value);
                    Font font = new Font("Arial", Font.BOLD, tileSize / 3);
                    g2d.setFont(font);
                    
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(text);
                    int textHeight = fm.getAscent();
                    
                    // Цвет текста в зависимости от значения
                    if (value <= 4) {
                        g2d.setColor(new Color(119, 110, 101));
                    } else {
                        g2d.setColor(Color.WHITE);
                    }
                    
                    int textX = x + (tileSize - textWidth) / 2;
                    int textY = y + (tileSize + textHeight) / 2 - 3;
                    g2d.drawString(text, textX, textY);
                }
            }
        }
        
        // Отрисовка сообщений о победе/поражении
        if (gameBoard.isGameOver()) {
            drawOverlay(g2d, "GAME OVER!");
        } else if (gameBoard.isWinReached()) {
            drawOverlay(g2d, "YOU WIN!");
        }
        
        // Отрисовка счёта
        drawScore(g2d);
    }
    
    /**
     * Отрисовка наложения (победа/поражение).
     */
    private void drawOverlay(Graphics2D g2d, String message) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g2d.setColor(Color.WHITE);
        Font font = new Font("Arial", Font.BOLD, getHeight() / 12);
        g2d.setFont(font);
        
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(message);
        int textHeight = fm.getAscent();
        
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() - textHeight) / 2;
        
        g2d.drawString(message, x, y);
    }
    
    /**
     * Отрисовка текущего счёта в верхней части панели.
     */
    private void drawScore(Graphics2D g2d) {
        int score = gameBoard.getScore();
        int bestScore = gameBoard.getBestScore();
        
        g2d.setColor(new Color(187, 173, 160));
        Font font = new Font("Arial", Font.BOLD, 20);
        g2d.setFont(font);
        
        String scoreText = "Score: " + score + "   Best: " + bestScore;
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(scoreText);
        
        g2d.drawString(scoreText, (getWidth() - textWidth) / 2, 30);
    }
    
    /**
     * Обработчик нажатий клавиш.
     */
    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (gameBoard.isGameOver()) {
                return;
            }
            
            boolean moved = false;
            
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    moved = gameBoard.moveLeft();
                    break;
                case KeyEvent.VK_RIGHT:
                    moved = gameBoard.moveRight();
                    break;
                case KeyEvent.VK_UP:
                    moved = gameBoard.moveUp();
                    break;
                case KeyEvent.VK_DOWN:
                    moved = gameBoard.moveDown();
                    break;
                default:
                    return;
            }
            
            if (moved) {
                gameBoard.addRandomTile();
                gameBoard.updateGameState();
                repaint();
            }
        }
    }
    
    /**
     * Обновление панели (перерисовка).
     */
    public void refresh() {
        repaint();
    }
}
