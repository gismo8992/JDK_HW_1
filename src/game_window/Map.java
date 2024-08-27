package game_window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Map extends JPanel {
    private static final Random RANDOM = new Random(); // генератор псевдослучайных чисел
    private final int HUMAN_DOT = 1; // символ обозначения на поле игрока
    private final int AI_DOT = 2; // символ обозначения на поле компьютера
    private final int EMPTY_DOT = 0; // символ обозначения на поле пустой ячейки
    private static final int DOT_PADDING = 5;
    private int gameStateType;
    private static final int STATE_GAME = 0;
    private static final int STATE_WIN_HUMAN = 1;
    private static final int STATE_WIN_AI = 2;
    private static final int STATE_DRAW = 3;
    private static final String MSG_WIN_HUMAN = "Победил игрок!";
    private static final String MSG_WIN_AI = "Победил компьютер!";
    private static final String MSG_DRAW = "Ничья!";
    private int panelWidth, panelHeight, cellWidth, cellHeight;
    private int fieldSizeY;
    private int fieldSizeX;
    private int winLen;
    private int mode;
    private int[][] field;
    private boolean gameWork;

    public Map() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (gameWork) {
                    update(e); /* передаем ивент (содержит координаты нажатия) по нажатию кнопки мыши в метод update
                    игра запустилась и ожидается нажатие мыши, которое передается в метод update
                    */
                }
            }
        });
    }

    /**
     * Метод начала новой игры
     *
     * @param mode
     * @param fSzX
     * @param fSzy
     * @param wLen
     */
    void startNewGame(int mode, int fSzX, int fSzy, int wLen) {
        System.out.printf("Mode: %d;\nSize: x=%d, y=%d;\nWin Length: %d", mode, fSzX, fSzy, wLen);
        this.mode = mode;
        this.fieldSizeX = fSzX;
        this.fieldSizeY = fSzy;
        this.winLen = wLen;
        initMap();
        gameWork = true;
        gameStateType = STATE_GAME;
        repaint();
    }

    /**
     * Метод фреймворка для рисования панели, в нем вызывается пользовательский метод.
     *
     * @param g the <code>Graphics</code> object to protect
     */
    protected void paintComponent(Graphics g) { // метод фреймворка для рисования панели, в нем вызывается пользовательский метод
        super.paintComponent(g);
        render(g);
    }

    /**
     * Пользовательский метод рисования с бизнес-логикой.
     * Определяет размеры поля и размеры ячеек.
     * Закрашивает поле, рисует линии разметки.
     * Проверяет поле (двумерный массив) на наличие символа юзера или компьютера, в зависимости от символа массива,
     * объект g рисует нужный элемент в ячейке.
     *
     * @param g объект, умеющий рисовать.
     */
    private void render(Graphics g) {
        panelWidth = getWidth();
        panelHeight = getHeight();
        cellHeight = panelHeight / fieldSizeX;
        cellWidth = panelWidth / fieldSizeY;

        g.setColor(Color.BLACK);
        for (int h = 0; h < fieldSizeX; h++) { // рисует линии //-но оси абсцисс (x)
            int y = h * cellHeight;
            g.drawLine(0, y, panelWidth, y);
        }
        for (int w = 0; w < fieldSizeY; w++) { // рисует линии //-но оси ординат (y)
            int x = w * cellWidth;
            g.drawLine(x, 0, x, panelHeight);
        }
        for (int y = 0; y < fieldSizeY; y++) {
            for (int x = 0; x < fieldSizeX; x++) {
                if (field[y][x] == EMPTY_DOT) {
                    continue;
                }
                if (field[y][x] == HUMAN_DOT) {
                    g.drawLine(x * cellWidth + DOT_PADDING, y * cellHeight + DOT_PADDING,
                            (x + 1) * cellWidth - DOT_PADDING, (y + 1) * cellHeight - DOT_PADDING);
                    g.drawLine(x * cellWidth + DOT_PADDING, (y + 1) * cellHeight - DOT_PADDING,
                            (x + 1) * cellWidth - DOT_PADDING, y * cellHeight + DOT_PADDING);
                } else if (field[y][x] == AI_DOT) {
                    g.drawOval(x * cellWidth + DOT_PADDING, y * cellHeight + DOT_PADDING,
                            cellWidth - DOT_PADDING * 2, cellHeight - DOT_PADDING * 2);
                } else {
                    throw new RuntimeException("unchecked value " + field[y][x] +
                            " in cell: x=" + x + " y=" + y);
                }
            }
        }
        if (gameStateType != STATE_GAME) {
            showMessage(g);
        }
    }

    /**
     * Метод обновления поля.
     * Получает координаты клика и, исходя из размера ячейки, рассчитывает номер ячейки (счет слева направо, сверху вниз).
     * Дальше проверяет клик на попадание в поле и пустоту ячейки (если условия не выполняются - игнор клика).
     * Если условия выполнены - массив по координатам заполняется символом HUMAN_DOT.
     *
     * @param e объект, обрабатывающий клики мыши для получения координат клика
     */
    public void update(MouseEvent e) {
        int cellX = e.getX() / cellWidth;
        int cellY = e.getY() / cellHeight;
        if (!isValidCell(cellX, cellY) || !isEmptyCell(cellX, cellY))
            return; // выход из метода при невыполнении условий
        //System.out.printf("x=%d, y=%d\n", cellX, cellY);
        field[cellY][cellX] = HUMAN_DOT; // если все ок, просто ходим
        if (checkEndGame(HUMAN_DOT, STATE_WIN_HUMAN)) {
            return;
        }
        aiTurn();
        repaint();
        checkEndGame(AI_DOT, STATE_WIN_AI);
    }

    /**
     * Метод инициализации поля (карты)
     */
    private void initMap() {
        field = new int[fieldSizeY][fieldSizeX]; // создаем массив и заполняем его пустотой
        for (int y = 0; y < fieldSizeY; y++) {
            for (int x = 0; x < fieldSizeX; x++) {
                if (field[y][x] == EMPTY_DOT) continue;
                else if (field[y][x] == HUMAN_DOT) {
                } else if (field[y][x] == AI_DOT) {
                } else {
                    throw new RuntimeException("Unexpected value " + field[y][x] + " in cell: x=" + x + " y=" + y);
                }

            }
        }
    }

    /**
     * Метод для проверки, попал ли игрок в ячейку поля.
     *
     * @param x координата по оси абсцисс
     * @param y координата по оси ординат
     * @return истина, если игрок попал в ячейку поля
     */
    private boolean isValidCell(int x, int y) {
        return x >= 0 && x < fieldSizeX && y >= 0 && y < fieldSizeY;
    }

    /**
     * Метод проверки пустоты ячейки.
     *
     * @param x координата по оси абсцисс
     * @param y координата по оси ординат
     * @return истина, если ячейка поля пустая
     */
    private boolean isEmptyCell(int x, int y) {
        return field[y][x] == EMPTY_DOT;
    }

    /**
     * Метод инициализации хода компьютера. Задает рандомные значения для координат в пределах размера поля до тех пор,
     * пока не попадет в пустую ячейку. При попадании в пустую ячейку ставит символ AI_DOT в ней.
     */
    private void aiTurn() {
        int x, y;
        do {
            x = RANDOM.nextInt(fieldSizeX);
            y = RANDOM.nextInt(fieldSizeY);
        } while (!isEmptyCell(x, y)); // если попал в заполненную, продолжай генерировать цикл do
        field[y][x] = AI_DOT;
    }

    /**
     * Метод проверки выигрыша
     *
     * @param dot проверяемый символ для выигрыша
     * @return истина, если символ выиграл
     */
    private boolean checkWin(int dot) { // проверка выигрыша символа
        for (int i = 0; i < fieldSizeX; i++) {
            for (int j = 0; j < fieldSizeY; j++) {
                if (checkLine(i, j, 1, 0, winLen, dot)) return true;
                if (checkLine(i, j, 1, 1, winLen, dot)) return true;
                if (checkLine(i, j, 0, 1, winLen, dot)) return true;
                if (checkLine(i, j, 1, -1, winLen, dot)) return true;
            }
        }
        return false;
    }

    /**
     *
     * @param x координата ячейки по которой щелкнули
     * @param y координата ячейки по которой щелкнули
     * @param vx показывает в какую сторону двигаться для проверки по оси абсцисс
     * @param vy показывает в какую сторону двигаться для проверки по оси ординат
     * @param len проверяемая длина для победы
     * @param dot число показывает, чьи ходы проверяем
     * @return
     */
    private boolean checkLine(int x, int y, int vx, int vy, int len, int dot) { // принимает координаты ячейки
        int far_x = x + (len - 1) * vx; // находим координаты следующей ячейки
        int far_y = y + (len - 1) * vy;
        if (!isValidCell(far_x, far_y)) { // если ячейка существует, идем далее
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (field[y + i * vy][x + i * vx] != dot) {
                return false;
            }
        }
        return true;
    }

    /**
     * Метод проверки состояния ничьей.
     *
     * @return истина, если пустые ячейки отсутствуют.
     */
    private boolean isMapFull() {
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                if (field[i][j] == EMPTY_DOT) return false;
            }
        }
        return true;
    }

    /**
     * Метод проверки завершения игры. Проверяет наличие выигрыша или заполненности поля (ничьей).
     *
     * @param dot          символ для проверки
     * @param gameOverType значение для победы текущего игрока/пользователя
     * @return истина, если игра завершена (победа или ничья)
     */
    private boolean checkEndGame(int dot, int gameOverType) {
        if (checkWin(dot)) {
            this.gameStateType = gameOverType;
            repaint();
            return true;
        } else if (isMapFull()) {
            this.gameStateType = STATE_DRAW;
            repaint();
            return true;
        }
        return false;
    }

    /**
     * Метод показа сообщений
     *
     * @param g объект рисования
     */
    private void showMessage(Graphics g) { // Graphics это как бы холст, на котором мы рисуем и настраиваем кисти
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, getHeight() / 2, getWidth(), 70);
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Times new roman", Font.BOLD, 48));
        switch (gameStateType) {
            case STATE_DRAW -> g.drawString(MSG_DRAW, 180, getHeight() / 2 + 60);
            case STATE_WIN_HUMAN -> g.drawString(MSG_WIN_HUMAN, 20, getHeight() / 2 + 60);
            case STATE_WIN_AI -> g.drawString(MSG_WIN_AI, 70, getHeight() / 2 + 60);
            default -> throw new RuntimeException("Unchecked gameOverState: " + gameStateType);
        }
        gameWork = false;
    }
}
