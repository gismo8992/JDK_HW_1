package server.repository;

public interface Repository {
    void saveInLog(String text);
    String readLog();
}
