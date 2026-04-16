package com.minesweeper;

import javax.swing.*;
import java.awt.*;

/**
 * Главное окно приложения. Содержит игровую панель и панель управления.
 */
public class MainWindow extends JFrame {
    private GameBoard gameBoard;
    private GamePanel gamePanel;
    private JLabel scoreLabel;
    
    public MainWindow() {
        setTitle("2048");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        
        gameBoard = new GameBoard();
        gamePanel = new GamePanel(gameBoard);
        
        // Создание панели управления
        JPanel controlPanel = createControlPanel();
        
        // Размещение компонентов
        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        
        // Обработка изменения размера окна
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                gamePanel.refresh();
            }
        });
    }
    
    /**
     * Создание панели управления с кнопками.
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(new Color(187, 173, 160));
        
        JButton newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 14));
        newGameButton.addActionListener(e -> {
            gameBoard.resetGame();
            gamePanel.refresh();
            gamePanel.requestFocusInWindow();
        });
        
        JButton resetRecordButton = new JButton("Reset Best Score");
        resetRecordButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetRecordButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to reset the best score?",
                    "Confirm Reset",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                gameBoard.resetBestScore();
                gamePanel.refresh();
            }
        });
        
        panel.add(newGameButton);
        panel.add(resetRecordButton);
        
        return panel;
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            gamePanel.requestFocusInWindow();
        }
    }
}
