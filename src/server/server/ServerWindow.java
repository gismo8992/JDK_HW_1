package server.server;

import server.repository.FileStorage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerWindow extends JFrame implements ServerView {
    private static final int POS_X = 500;
    private static final int POS_Y = 550;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;
    private JButton btnStart;
    private JButton btnStop;
    private JTextArea log;
    private Server server;

    public ServerWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(POS_X, POS_Y, WIDTH, HEIGHT);
        setResizable(false);
        setTitle("Chat server");
        setAlwaysOnTop(true);
        createPanel();
        setVisible(true);
        server = new Server(this, new FileStorage());
    }

    /**
     * Метод создает панель из поля для отображения текста и кнопок
     */
    private void createPanel() {
        log = new JTextArea(); // область для отображения текста
        add(log);
        add(createButtons(), BorderLayout.SOUTH);
    }

    /**
     * Метод создает панель с кнопками и определяет бизнес-логику при нажатии кнопок
     * @return панель с кнопками запуска и остановки сервера
     */
    private Component createButtons() {
        btnStart = new JButton("Start");
        btnStop = new JButton("Stop");
        JPanel jPanel = new JPanel(new GridLayout(1, 2));
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.stop();
            }
        });
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.start();
            }
        });
        jPanel.add(btnStart);
        jPanel.add(btnStop);
        return jPanel;
    }

    public Server getConnection() {
        return server;
    }
    public void showMessage(String message) {
        log.append(message);
    }

}
