package server.client;

import server.server.ServerWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientGUI extends JFrame {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;
    private JTextArea log;
    private JPanel panelTop;
    private JTextField tfIPAddress;
    private JTextField tfPort;
    private JTextField tfLogin;
    private JPasswordField tfPassword;
    private JButton btnLogin;

    private JPanel panelBottom;
    private JTextField tfMessage;
    private JButton btnSend;
    private boolean connected; // флаг подключения
    private ServerWindow server;
    private String name; // имя пользователя

    public ClientGUI(ServerWindow server) {
        this.server = server; // сразу при создании пользователя сохраняем сервер в поле
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(server);
        setSize(WIDTH, HEIGHT);
        setTitle("ChatClient");
        createPanel();
        setVisible(true);
    }

    /**
     * Метод собирает панель клиента из 3-х составных панелей - верхней, средней, центральной.
     */
    private void createPanel() {
        add(createTopPanel(), BorderLayout.NORTH);
        add(createLog(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    /**
     * Метод собирает верхнюю панель, включающую ip-адрес, порт, пароль, логин, кнопку логина
     *
     * @return собранная верхняя панель
     */
    private Component createTopPanel() {
        panelTop = new JPanel(new GridLayout(2, 3));
        tfIPAddress = new JTextField("127.0.0.1");
        tfPort = new JTextField("8189");
        tfLogin = new JTextField("ivan ivanovich");
        tfPassword = new JPasswordField("123456");
        btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });
        panelTop.add(tfIPAddress);
        panelTop.add(tfPort);
        panelTop.add(tfLogin);
        panelTop.add(tfPassword);
        panelTop.add(btnLogin);

        return panelTop;
    }

    /**
     * Метод собирает центральную панель, содержащую не редактируемую область для отображения отправленного текста
     *
     * @return собранная центральная панель
     */
    private Component createLog() {
        log = new JTextArea();
        log.setEditable(false);
        return new JScrollPane(log);
    }

    /**
     * Метод собирает нижнюю панель, содержащую область для ввода текста и кнопку отправки сообщений.
     *
     * @return собранная нижняя панель
     */
    private Component createBottomPanel() {
        panelBottom = new JPanel(new BorderLayout());
        tfMessage = new JTextField();
        tfMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') { // если нажат enter (символ переноса),
                    message(); // то отправляем сообщение
                }
            }
        });
        btnSend = new JButton("Send");
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                message();
            }
        });
        panelBottom.add(tfMessage, BorderLayout.CENTER);
        panelBottom.add(btnSend, BorderLayout.EAST);
        return panelBottom;
    }

    /**
     * Метод подключения текущего пользователя к серверу.
     */
    private void connectToServer() {
        if (server.connectUser(this)) {
            appendLog("Вы успешно подключились!\n");
            panelTop.setVisible(false);
            connected = true;
            name = tfLogin.getText();
            String log = server.getLog();
            if (log != null) {
                appendLog(log);
            }
        } else {
            appendLog("Подключение не удалось");
        }
    }

    /**
     * Метод отключения текущего пользователя от сервера.
     */
    public void disconnectFromServer() {
        if (connected) { // если подключен
            panelTop.setVisible(true); // отображаем панель для ввода данных, чтобы можно было обратно подключиться
            connected = false; // отключаем
            server.disconnectUser(this); // вызываем метод отключения пользователя у сервера
            appendLog("Вы были отключены от сервера!");
        }
    }

    /**
     * Метод добавления текста сообщения в лог.
     * @param text
     */
    private void appendLog(String text) {
        log.append(text + "\n");
    }

    /** Метод, который использует сервер, когда хочет отправить сообщение клиенту.
     * @param text отправленный текст.
     */
    public void answer(String text) {
        appendLog(text); // отображаем текст в текстовом поле
    }

    /**
     * Метод вызывается по кнопке send или клавише enter и
     */
    public void message() {
        if (connected) {
            String text = tfMessage.getText(); // получает текст из текстового поля
            if (!text.equals("")) { // если текст не пустой
                server.message(name + ": " + text); // обращается к серверу и пишем, от кого это сообщение
                tfMessage.setText(""); // обнуляем текстовое поле
            }
        } else {
            appendLog("Нет подключения к серверу");
        }

    }

    /**
     * Метод отключение пользователя от сервера при закрытии окна пользователя.
     * @param e  the window event
     */
    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) { // если текущее событие - закрытие окна
            disconnectFromServer(); // то отключаемся от сервера
        }
        super.processWindowEvent(e);
    }
}
