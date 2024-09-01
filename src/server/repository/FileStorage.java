package server.repository;

import java.io.FileReader;
import java.io.FileWriter;

public class FileStorage implements Repository{
    public static final String LOG_PATH = "src/server/log.txt"; // путь записи логов

    /**
     * Метод сохранения текста сообщения в лог.
     * @param text передаваемое клиентом сообщение
     */
    @Override
    public void saveInLog(String text) {
        try (FileWriter writer = new FileWriter(LOG_PATH, true)) {
            writer.write(text);
            writer.write("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод чтения лога.
     * @return текст прочитанного лог-файла
     */
    @Override
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
}
