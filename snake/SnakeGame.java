package com.javarush.games.snake;

import com.javarush.engine.cell.*;

public class SnakeGame extends Game {
    public static final int WIDTH = 15;
    public static final int HEIGHT = 15;
    private static final int GOAL = 28;

    private Snake snake;
    private int turnDelay;
    private Apple apple;
    private boolean isGameStopped;
    private int score;

    public void initialize(){
        setScreenSize(WIDTH, HEIGHT);
        createGame();
    }

    private void createGame(){
        isGameStopped = false;
        setScore(score = 0);
        snake = new Snake(WIDTH / 2, HEIGHT / 2);
        createNewApple();
        drawScene();
        
        turnDelay = 300;
        setTurnTimer(turnDelay);
    }

    private void drawScene(){
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                setCellValueEx(x, y, Color.DARKSEAGREEN, "");
            }
        }

        snake.draw(this);
        apple.draw(this);
    }

    private void createNewApple(){
        do {
            apple = new Apple(getRandomNumber(WIDTH), getRandomNumber(HEIGHT));
        } while (snake.checkCollision(apple));
    }

    private void gameOver(){
        stopTurnTimer();
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "GAME OVER!", Color.RED, 75);
    }

    private void win(){
        stopTurnTimer();
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "YOU WIN!", Color.RED, 75);
    }

    @Override
    public void onTurn(int a){
        snake.move(apple);

        if (!apple.isAlive) {
            setScore(score = score + 5);
            createNewApple();
            setTurnTimer(turnDelay = turnDelay - 10);
        }

        if ( snake.isAlive == false )
            gameOver();
        if ( snake.getLength() > GOAL )
            win();
        drawScene();
    }

    @Override
    public void onKeyPress(Key key){
        if (key == Key.LEFT)
            snake.setDirection(Direction.LEFT);
        if (key == Key.RIGHT)
            snake.setDirection(Direction.RIGHT);
        if (key == Key.DOWN)
            snake.setDirection(Direction.DOWN);
        if (key == Key.UP)
            snake.setDirection(Direction.UP);
        if ((key == Key.SPACE) && (isGameStopped))
            createGame();
    }
}
