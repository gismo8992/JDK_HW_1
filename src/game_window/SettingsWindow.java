package game_window;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsWindow extends JFrame {
    private static final int WINDOW_HEIGHT = 230;
    private static final int WINDOW_WIDTH = 350;
    private static final String SELECTED_FILED_SIZE = "Selected filed size: ";
    private static final String SELECTED_WIN_COUNT = "Selected win count: ";

    JButton btnStart = new JButton("Start new game");
    JSlider winCountSlider = new JSlider(3, 10);
    JSlider fieldSizeSlider = new JSlider(3, 10
    );
    JRadioButton jRadioButton1 = new JRadioButton("Human vs AI");
    JRadioButton jRadioButton2 = new JRadioButton("Human vs Human");
    GameWindow gameWindow;


    SettingsWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        setLocationRelativeTo(gameWindow);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        // создание общей панели для настроек
        JPanel settingsGame = new JPanel(new GridLayout(3, 1));

        // выбора типа игры
        JPanel typeGameSelection = new JPanel(new GridLayout(3, 1));
        typeGameSelection.add(new Label("Select type game"));
        typeGameSelection.add(new Label("Selected type game: "));
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(jRadioButton1);
        buttonGroup.add(jRadioButton2);
        typeGameSelection.add(jRadioButton1);
        typeGameSelection.add(jRadioButton2);

        // выбор количества повторений для победы
        JPanel winCountSelection = new JPanel(new GridLayout(3, 1));
        winCountSelection.add(new Label("Select win count"));
        JLabel currentWinCount = new JLabel(SELECTED_WIN_COUNT);
        winCountSelection.add(currentWinCount);
        winCountSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int  sizeWin = winCountSlider.getValue();
                currentWinCount.setText(SELECTED_WIN_COUNT + sizeWin);
            }
        });
        winCountSelection.add(winCountSlider);

        // выбор размеров поля
        JPanel fieldSizeSelection = new JPanel(new GridLayout(3, 1));
        fieldSizeSelection.add(new Label("Select filed size"));
        JLabel currentFieldSize = new JLabel(SELECTED_FILED_SIZE);
        fieldSizeSelection.add(currentFieldSize);

        fieldSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int sizeField = fieldSizeSlider.getValue();
                currentFieldSize.setText(SELECTED_FILED_SIZE + sizeField);
                winCountSlider.setMaximum(sizeField);
            }
        });
        fieldSizeSelection.add(fieldSizeSlider);
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startNewGame();
            }
        });
        add(btnStart, BorderLayout.SOUTH);

        settingsGame.add(typeGameSelection);
        settingsGame.add(fieldSizeSelection);
        settingsGame.add(winCountSelection);
        add(settingsGame);


    }
    private void startNewGame() {
        int mode = 0;
        if(jRadioButton1.isSelected()) {
            mode = 1;
        } else if (jRadioButton2.isSelected()) {
            mode = 2;
        }
        int fieldSize = fieldSizeSlider.getValue();
        int winCount = winCountSlider.getValue();
        gameWindow.startNewGame(mode, fieldSize, fieldSize, winCount);
        setVisible(false);
    }
}
