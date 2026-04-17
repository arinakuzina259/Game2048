

import javax.swing.*;

/**
 * Главный класс приложения. Точка входа в игру 2048.
 */
public class Game2048Main {
    public static void main(String[] args) {
        // Установка Look and Feel для улучшения внешнего вида на разных платформах
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Запуск приложения в потоке обработки событий Swing
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow();
            mainWindow.setVisible(true);
        });
    }
}
