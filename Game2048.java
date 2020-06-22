package com.codegym.games.game2048;

import com.codegym.engine.cell.*;
import java.util.Arrays;

public class Game2048 extends Game {
    private static final int SIDE = 4;
    private int[][] gameField = new int[SIDE][SIDE];
    private boolean isGameStopped = false;
    private int score = 0;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);

        createGame();
        drawScene();

    }

    private void createGame() {
        gameField = new int[SIDE][SIDE];
        createNewNumber();
        createNewNumber();
    }

    private void drawScene() {
        for (int x = 0; x <= 3; x++) {
            for (int y = 0; y <= 3; y++) {

                setCellColoredNumber(y, x, gameField[x][y]);
            }
        }
    }

    private void restart() {
        isGameStopped = false;
        score = 0;
        setScore(0);
        createGame();
        drawScene();
    }

    // Create random number-cell to add no 2 or 4//
    private void createNewNumber() {
        int x;
        int y;

        do {
            x = getRandomNumber(SIDE);
            y = getRandomNumber(SIDE);
        } while (gameField[x][y] != 0);

        if (getRandomNumber(10) == 9) {
            gameField[x][y] = 4;
        } else {
            gameField[x][y] = 2;
        }

        if (getMaxTileValue() == 2048) {
            win();
        }
    }

    // setting color according to numbers//
    private void setCellColoredNumber(int y, int x, int value) {
        if (value != 0) {
            setCellValueEx(y, x, getColorByValue(value), Integer.toString(value));
        } else {
            setCellValueEx(y, x, getColorByValue(value), "");
        }
    }

    // return a cell color based on value
    private Color getColorByValue(int value) {

        switch (value) {
            case 2:
                return Color.GREEN;
            case 4:
                return Color.RED;
            case 8:
                return Color.BLUE;
            case 16:
                return Color.CYAN;
            case 32:
                return Color.GRAY;
            case 64:
                return Color.MAGENTA;
            case 128:
                return Color.ORANGE;
            case 256:
                return Color.PINK;
            case 512:
                return Color.BROWN;
            case 1024:
                return Color.PURPLE;
            case 2048:
                return Color.YELLOW;
            default:
                return Color.WHITE;
        }
    }

    // for compressing rows//
    private boolean compressRow(int[] row) {
        int temp = 0;
        int[] rowtemp = row.clone();
        boolean isChanged = false;
        for (int i = 0; i < row.length; i++) {
            for (int j = 0; j < row.length - i - 1; j++) {
                if (row[j] == 0) {
                    temp = row[j];
                    row[j] = row[j + 1];
                    row[j + 1] = temp;
                }
            }
        }
        if (!Arrays.equals(row, rowtemp))
            isChanged = true;
        // row = rowtemp;
        return isChanged;
    }

    // for merging rows with same numbers//
    private boolean mergeRow(int[] row) {
        boolean isMerged = false;
        for (int i = 0; i < 3; i++) {
            if (row[i] == row[i + 1] && row[i] != 0) {
                row[i] = 2 * row[i];
                row[i + 1] = 0;
                isMerged = true;
                score += row[i];
                setScore(score);
            }
        }
        return isMerged;
    }

    // for using keys to play game//
    public void onKeyPress(Key key) {
        if (isGameStopped) { // Checks if the game is stopped before the game over conditions
            if (key == Key.SPACE)
                restart();
            return;
        }
        if (!canUserMove()) {
            gameOver();
            if (key == Key.SPACE) {
                restart();
            }
            return;
        }
        switch (key) { // this block should not be reachable if the game is stopped
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
            default:
                return;
        }
        drawScene();
    }

    // To moves tile to left//
    private void moveLeft() {

        boolean compress; // variable to get return from compressRow
        boolean merge; // variable to get return from mergeRow
        boolean compress1; // variable to get return from compressRow
        int move = 0; // to check if compressRow or mergeRow occurs
        for (int i = 0; i < SIDE; i++) {
            compress = compressRow(gameField[i]);
            merge = mergeRow(gameField[i]);
            compress1 = compressRow(gameField[i]);
            if (compress || merge || compress1)
                move++;
        }
        if (move != 0) {
            createNewNumber();
        }
    }

    // To rotate tiles clockwise//
    private void rotateClockwise() {
        // Traverse each cycle
        for (int i = 0; i < SIDE / 2; i++) {
            for (int j = i; j < SIDE - i - 1; j++) {
                // Swap elements of each cycle in clockwise direction
                int temp = gameField[i][j];
                gameField[i][j] = gameField[SIDE - 1 - j][i];
                gameField[SIDE - 1 - j][i] = gameField[SIDE - 1 - i][SIDE - 1 - j];
                gameField[SIDE - 1 - i][SIDE - 1 - j] = gameField[j][SIDE - 1 - i];
                gameField[j][SIDE - 1 - i] = temp;
            }
        }
    }

    private void moveUp() {
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
    }

    private void moveRight() {
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
    }

    private void moveDown() {
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    private int getMaxTileValue() {
        int max = 0;
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (gameField[i][j] > max)
                    max = gameField[i][j];
            }
        }
        return max;
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.GREEN, "U WON THE Game!!", Color.BLUE, 20);
    }

    private boolean canUserMove() {

        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (gameField[i][j] == 0)
                    return true;
            }
        }

        for (int i = 0; i < SIDE - 1; i++)
            for (int j = 0; j < SIDE; j++)
                if (gameField[i][j] == gameField[i + 1][j])
                    return true;

        for (int i = 0; i < SIDE; i++)
            for (int j = 0; j < SIDE - 1; j++)
                if (gameField[i][j] == gameField[i][j + 1])
                    return true;

        return false;
    }

    // gameover notification//
    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.RED, "Game Over!", Color.BLUE, 20);
    }

}
