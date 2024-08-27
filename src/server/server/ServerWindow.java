package server.server;

import server.client.ClientGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ServerWindow extends JFrame {
    private static final int POS_X = 500;
    private static final int POS_Y = 550;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;
    public static final String LOG_PATH = "src/server/log.txt"; // путь записи логов
    private JButton btnStart;
    private JButton btnStop;
    private JTextArea log;
    private List<ClientGUI> clientGUIList;
    private boolean isServerWorking; // флаг работы сервера


    public ServerWindow() {
        clientGUIList = new ArrayList<>();
        isServerWorking = false;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(POS_X, POS_Y, WIDTH, HEIGHT);
        setResizable(false);
        setTitle("Chat server");
        setAlwaysOnTop(true);

        createPanel();

        setVisible(true);
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
                if (!isServerWorking) {
                    appendLog("Сервер уже был остановлен");
                } else {
                    isServerWorking = false;
                    while (!clientGUIList.isEmpty()) { // пока в списке есть подключенные клиенты
                        disconnectUser(clientGUIList.get(clientGUIList.size() - 1)); // отключаем клиента из конца списка
                    }
                    appendLog("Сервер остановлен!"); // выводим сообщение, когда все клиенты отключены
                }
            }
        });
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isServerWorking) {
                    appendLog("Сервер уже был запущен");
                } else {
                    isServerWorking = true;
                    appendLog("Сервер запущен!");
                }
            }
        });
        jPanel.add(btnStart);
        jPanel.add(btnStop);
        return jPanel;
    }

    /**
     * Метод подключения юзера к серверу. Если сервер работает - подключает юзера. Если юзер уже подключен - сообщает об этом.
     * @param clientGUI объект юзер, который подключается к серверу
     * @return истина - юзер добавлен в список клиентов, ложь - если сервер не работает
     */
    public boolean connectUser(ClientGUI clientGUI) {
        if (!isServerWorking) {
            return false;
        } else if (clientGUIList.contains(clientGUI)) {
            appendLog("Такой пользователь уже подключен.");
            return true;
        }
        clientGUIList.add(clientGUI);
        return true;
    }

    /**
     * Метод отключения юзера от сервера.
     * Сначала юзер удаляется из списка юзеров на сервере. Далее вызывается метод юзера для отключения от сервера.
     * @param clientGUI объект юзер, который отключается от сервера
     */
    public void disconnectUser(ClientGUI clientGUI) {
        clientGUIList.remove(clientGUI);
        if (clientGUI != null) { // если пользователь существует (т.к. отключение может быть инициализировано и сервером и клиентом)
            clientGUI.disconnectFromServer(); // отключается от сервера
        }
    }

    /**
     * Метод получения лога.
     * @return возвращает текст лога
     */
    public String getLog() {
        return readLog();
    }

    /**
     * Метод чтения лога.
     * @return текст прочитанного лог-файла
     */
    public String readLog() {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader reader = new FileReader(LOG_PATH)) {
            int c;
            while ((c = reader.read()) != -1) { // пока файл не будет прочитан до конца
                stringBuilder.append((char) c); // кастить и добавлять символы лога в объект stringBuilder
            }
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length()); // удаляет -1 в конце файла
            return stringBuilder.toString(); // возвращает строку из символов в файле
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Метод, который вызывает клиент, когда пишет на сервер.
     * @param text передаваемое клиентом сообщение
     */
    public void message(String text) {
        if (!isServerWorking) {
            return;
        }
        text += "";
        appendLog(text);
        answerAll(text); // отправляет всем клиентам из списка такое же сообщение
        saveInLog(text); // сохраняем сообщение в лог
    }

    /**
     * Метод трансляции сообщения всем клиентам на сервере.
     * @param text передаваемое клиентом сообщение
     */
    private void answerAll(String text) {
        for (ClientGUI clientGUI : clientGUIList) {
            clientGUI.answer(text);
        }
    }

    /**
     * Метод сохранения текста сообщения в лог.
     * @param text передаваемое клиентом сообщение
     */
    private void saveInLog(String text) {
        try (FileWriter writer = new FileWriter(LOG_PATH, true)) {
            writer.write(text);
            writer.write("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод добавления текста сообщения в лог.
     * @param text передаваемое клиентом сообщение
     */
    private void appendLog(String text) {
        log.append(text + "\n");
    }
}
