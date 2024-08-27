import server.client.ClientGUI;
import server.server.ServerWindow;

public class Main {
    public static void main(String[] args) {
        //GameWindow gameWindow = new GameWindow();
        ServerWindow serverWindow = new ServerWindow();
        ClientGUI clientGUI_1 = new ClientGUI(serverWindow);
        ClientGUI clientGUI_2 = new ClientGUI(serverWindow);


    }
}