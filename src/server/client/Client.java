package server.client;

import server.server.Server;

public class Client { // класс отображает логику работы клиента
    private boolean connected; // флаг подключения
    private Server server;
    private String name; // имя пользователя
    private ClientVIew view;
    public Client(ClientVIew vIew, Server server) {
        this.view = vIew;
        this.server = server;

    }
    /**
     * Метод попытки подключения к серверу. Вызывается из GUI
     * @param name имя пользователя, которым будем подписывать исходящие сообщения
     * @return ответ от сервера. true, если прошли авторизацию
     */
    public boolean connectToServer(String name) { // вызывается, когда в gui будет нажата кнопка login
        this.name = name;
        if (server.connectUser(this)){
            printText("Вы успешно подключились!\n");
            connected = true;
            String log = server.getLog();
            if (log != null){
                printText(log);
            }
            return true;
        } else {
            printText("Подключение не удалось");
            return false;
        }
    }

    /**
     * Метод отключения от сервера инициализированное сервером
     */
    public void disconnectFromServer() {
        if (connected) {
            connected = false;
            view.disconnectedFromServer();
            server.disconnectUser(this);
            printText("Вы были отключены от сервера!");
        }
    }
    /**
     * Метод отключения от сервера инициализированное клиентом (например закрыто GUI)
     */
    public void disconnectServer() {
        server.disconnectUser(this);
    }

    private void printText(String text) {
        view.showMessage(text);
    }

    /**
     * Метод для передачи сообщения на сервер
     * @param message текст передаваемого сообщения
     */
    public void sendMessage(String message) {
        if(connected) {
            if(!message.isEmpty()) {
                server.message(name + ": " + message);
            }
        }
        else {
            printText("Нет подключения к серверу.");
        }
    }

    /**
     * Метод, с помощью которого сервер передает клиенту сообщения
     * @param text текст переданный от сервера
     */
    public void answerFromServer(String text) {
        showOnWindow(text);
    }

    public String getName() {
        return name;
    }
    /**
     * Метод вывода сообщения на GUI
     * @param text текст, который требуется вывести на экран
     */
    private void showOnWindow(String text) {
        view.showMessage(text + "\n");
    }
}
