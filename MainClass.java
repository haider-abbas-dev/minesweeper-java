package minesweeper.ui;

import javax.swing.*;

public class MainClass {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MinesweeperFrame frame = new MinesweeperFrame();
            frame.setVisible(true);
        });
    }
}