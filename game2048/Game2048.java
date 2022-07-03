package com.javarush.games.game2048;

import com.javarush.engine.cell.*;

public class Game2048 extends Game {
    private static final int SIDE = 4;
    private int[][] gameField = new int[SIDE][SIDE];
    private boolean isGameStopped = false;
    private int score;

    public void initialize(){
        setScreenSize(SIDE, SIDE);
        createGame();
        drawScene();
    }

    private void createGame(){
        gameField = new int[SIDE][SIDE];
        setScore(score = 0);
        createNewNumber();
        createNewNumber();
    }

    private void drawScene(){
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                setCellColoredNumber(i, j, gameField[j][i]);
            }
        }

    }

    private void createNewNumber(){
        if (getMaxTileValue() == 2048) {
            win(); }

        int x = -1, y = -1;
        do {
            x = getRandomNumber(SIDE);
            y = getRandomNumber(SIDE);
        } while (gameField[y][x] != 0);

        if (getRandomNumber(10) == 9) {
            gameField[y][x] = 4;
        }
        else {
            gameField[y][x] = 2;
        }
    }

    private Color getColorByValue(int value){
        Color result = null;
        switch (value) {
            case 0 : result = Color.WHITE; break;
            case 2 : result = Color.LAVENDER; break;
            case 4 : result = Color.VIOLET; break;
            case 8 : result = Color.LIGHTBLUE; break;
            case 16 : result = Color.AQUA; break;
            case 32 : result = Color.FORESTGREEN; break;
            case 64 : result = Color.GREEN; break;
            case 128 : result = Color.LIGHTSALMON; break;
            case 256 : result = Color.CORAL; break;
            case 512 : result = Color.LIGHTPINK; break;
            case 1024 : result = Color.FUCHSIA; break;
            case 2048 : result = Color.MAGENTA; break;
        }
        return result;
    }

    private  void setCellColoredNumber(int x, int y, int value){
        setCellValueEx(x, y, getColorByValue(value), (value == 0) ? "" : String.valueOf(value));
    }

    private boolean compressRow(int[] row){
        int[] oldrow = new int[SIDE];
        for (int i = 0; i < row.length; i++) {
            oldrow[i] = row[i];
        }
        for (int i = row.length - 1; i >= 0; i--) {
            for (int j = 0; j < i; j++) {
                if (row[j] == 0) {
                    int temp = row[j + 1];
                    row[j + 1] = row[j];
                    row[j] = temp;
                }
            }
        }

        boolean isMoved = false;
        for (int i = 0; i < row.length; i++) {
            if (oldrow[i] != row[i]) { isMoved = true; }
        }

        return isMoved;
    }

    private boolean mergeRow(int[] row){
        int[] oldrow = new int[SIDE];
        for (int i = 0; i < row.length; i++) {
            oldrow[i] = row[i];
        }
        for (int i = 0; i < row.length - 1; i++) {
            if (row[i] == row[i + 1]) {
                row[i] = row[i] + row[i + 1];
                score = score + row[i];
                setScore(score);
                row[i + 1] = 0;
            }
        }

        boolean isMoved = false;
        for (int i = 0; i < row.length; i++) {
            if (oldrow[i] != row[i]) { isMoved = true; }
        }

        return isMoved;
    }

    public void onKeyPress(Key key){
        if (!isGameStopped) {
            if (canUserMove() == false) {
                gameOver();
            } else {
                switch (key) {
                    case LEFT:
                        moveLeft();
                        break;
                    case RIGHT:
                        moveRight();
                        break;
                    case UP:
                        moveUp();
                        break;
                    case DOWN:
                        moveDown();
                        break;
                }
                drawScene();
            }
        }
        else if (key == Key.SPACE) {
            isGameStopped = false;
            createGame();
            drawScene();
        }
    }

    private void moveLeft(){
        int oldField[][] = new int[SIDE][SIDE];
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                oldField[i][j] = gameField[i][j];
            }
        }

        for (int i = 0; i < SIDE; i++){
            compressRow(gameField[i]);
            mergeRow(gameField[i]);
            compressRow(gameField[i]);
        }

        boolean isMoved = false;
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (oldField[i][j] != gameField[i][j]) { isMoved = true; }
            }
        }

        if (isMoved) createNewNumber();
    }

    private void moveRight(){
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
    }

    private void moveUp(){
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
    }

    private void moveDown(){
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    private void rotateClockwise(){
        int[][] result = new int[SIDE][SIDE];
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                result[i][j] = gameField[SIDE - 1 - j][i];
            }
        }

        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                gameField[i][j] = result[i][j];
            }
        }

    }

    private int getMaxTileValue() {
        int max = Integer.MIN_VALUE;

        for (int[] ints : gameField) { // цикл по строкам
            for (int anInt : ints) { // цикл по элементам строк
                max = Math.max(anInt, max);
            }
        }

        return max;
    }

    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.ANTIQUEWHITE, "WIN!", Color.CORNFLOWERBLUE, 100);
    }

    private boolean canUserMove(){
        boolean result = false;
        for (int[] ints : gameField) {
            for (int anInt : ints) {
                if (anInt == 0) {
                    result = true;
                }
            }
        }

        for (int i = 0; i < SIDE - 1; i++) {
            for (int j = 0; j < SIDE - 1; j++) {
                if (gameField[i][j] == gameField[i][j + 1]) {
                    result = true;
                }
                else if (gameField[i][j] == gameField[i + 1][j]) {
                    result = true;
                }
            }
            if (gameField[i][SIDE - 1] == gameField[i + 1][SIDE - 1]) {
                result = true;
            }
            else if (gameField[SIDE - 1][i] == gameField[SIDE - 1][i + 1]) {
                result = true;
            }
        }

        return result;
    }

    private void gameOver(){
        isGameStopped = true;
        showMessageDialog(Color.ANTIQUEWHITE, "GAME OVER!", Color.CORNFLOWERBLUE, 100);
    }
}

