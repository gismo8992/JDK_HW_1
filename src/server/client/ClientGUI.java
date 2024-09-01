package server.client;
import server.server.ServerWindow;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientGUI extends JFrame implements ClientVIew {
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
    private Client client; // мозг программы (будет выполняться логика клиента)

    public ClientGUI(ServerWindow server) {
        client = new Client(this, server.getConnection());
        // сразу при создании пользователя сохраняем сервер в поле
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(server.getX() - 500, server.getY());
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
                    sendMessage(); // то отправляем сообщение
                }
            }
        });
        btnSend = new JButton("Send");
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        panelBottom.add(tfMessage, BorderLayout.CENTER);
        panelBottom.add(btnSend, BorderLayout.EAST);
        return panelBottom;
    }

    /**
     * Метод для отправки сообщения. Используется при нажатии на кнопку send
     */
    private void sendMessage(){
        client.sendMessage(tfMessage.getText());
        tfMessage.setText("");
    }

    /**
     * Метод отключение пользователя от сервера при закрытии окна пользователя.
     *
     * @param e the window event
     */
    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) { // если текущее событие - закрытие окна
            disconnectServer(); // то отключаемся от сервера
        }
        super.processWindowEvent(e);
    }

    /**
     * Метод вывода текста на экран GUI
     * @param message текст, который требуется отобразить на экране
     */
    @Override
    public void showMessage(String message) {
        log.append(message + "\n");
    }

    /**
     * Метод, описывающий отключение клиента от сервера со стороны сервера
     */
    @Override
    public void disconnectedFromServer(){
        hideHeaderPanel(true);
    }

    /**
     * Метод, описывающий отключение клиента от сервера со стороны клиента
     */
    public void disconnectServer(){
        client.disconnectFromServer();
    }

    private void connectToServer() { // срабатываем при нажатии на кнопку login
        if (client.connectToServer(tfLogin.getText())) {
            hideHeaderPanel(false);
        }
    }

    /**
     * Метод изменения видимости верхней панели экрана, на которой виджеты для авторизации (например кнопка логин)
     * @param visible true, если надо сделать панель видимой
     */
    private void hideHeaderPanel(boolean visible) {
        panelTop.setVisible(visible);
    }

    /**
     * Метод, срабатывающий при нажатии кнопки авторизации
     */
    public void login(){
        if (client.connectToServer(tfLogin.getText())){
            panelTop.setVisible(false);
        }
    }
}
