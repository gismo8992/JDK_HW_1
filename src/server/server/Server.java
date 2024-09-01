package server.server;

import server.client.Client;
import server.repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class Server {
    private boolean isServerWorking; // флаг работы сервера
    private List<Client> clientList;
    private Repository repository;
    private ServerView serverView;

    public Server(ServerView serverView, Repository repository) {
        clientList = new ArrayList<>();
        this.serverView = serverView;
        this.repository = repository;
    }

    public void start() {
        if (isServerWorking) {
            showOnWindow("Сервер уже был запущен");
        } else {
            isServerWorking = true;
            showOnWindow("Сервер запущен!");
        }
    }
    public void stop() {
        if (!isServerWorking) {
            showOnWindow("Сервер уже был остановлен");
        } else {
            isServerWorking = false;
            while (!clientList.isEmpty()) { // пока в списке есть подключенные клиенты
                disconnectUser(clientList.get(clientList.size() - 1)); // отключаем клиента из конца списка
            }
            showOnWindow("Сервер остановлен!"); // выводим сообщение, когда все клиенты отключены
        }
    }

    /**
     * Метод подключения юзера к серверу. Если сервер работает - подключает юзера. Если юзер уже подключен - сообщает об этом.
     * @param client объект юзер, который подключается к серверу
     * @return истина - юзер добавлен в список клиентов, ложь - если сервер не работает
     */
    public boolean connectUser(Client client) {
        if (!isServerWorking) {
            return false;
        } else if (clientList.contains(client)) {
            showOnWindow("Такой пользователь уже подключен.");
            return true;
        }
        clientList.add(client);
        showOnWindow(client.getName() + " подключился к беседе");
        return true;
    }

    /**
     * Метод отключения юзера от сервера.
     * Сначала юзер удаляется из списка юзеров на сервере. Далее вызывается метод юзера для отключения от сервера.
     * @param client объект юзер, который отключается от сервера
     */
    public void disconnectUser(Client client) {
        clientList.remove(client);
        if (client != null) { // если пользователь существует (т.к. отключение может быть инициализировано и сервером и клиентом)
            client.disconnectFromServer(); // отключается от сервера
            showOnWindow(client.getName() + " отключился от сервера.");
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
        showOnWindow(text);
        answerAll(text); // отправляет всем клиентам из списка такое же сообщение
        saveInLog(text); // сохраняем сообщение в лог
    }

    /**
     * Метод сохранения текста сообщения в лог
     * @param text текст сообщения
     */
    private void saveInLog(String text) {
        repository.saveInLog(text);
    }

    /**
     * Метод трансляции сообщения всем клиентам на сервере.
     * @param text передаваемое клиентом сообщение
     */
    private void answerAll(String text) {
        for (Client client : clientList) {
            client.answerFromServer(text);
        }
    }

    private void showOnWindow(String text){
        serverView.showMessage(text + "\n");
    }
    /**
     * Метод получения лога.
     * @return возвращает текст лога
     */
    public String getLog() {
        return repository.readLog();
    }
}
