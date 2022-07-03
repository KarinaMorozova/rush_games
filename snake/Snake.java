package com.javarush.games.snake;

import com.javarush.engine.cell.*;
import java.util.ArrayList;
import java.util.List;

public class Snake extends Game {
    private static final String HEAD_SIGN = "\uD83D\uDC7E";
    private static final String BODY_SIGN = "\u26AB";
    public boolean isAlive = true;
    private Direction direction = Direction.LEFT;

    private List<GameObject> snakeParts = new ArrayList<GameObject>();

    public Snake(int x, int y) {
        snakeParts.add(new GameObject(x, y));
        snakeParts.add(new GameObject(x + 1, y));
        snakeParts.add(new GameObject(x + 2, y));
    }

    public void setDirection(Direction direction) {
        if ((snakeParts.get(0).x == snakeParts.get(1).x) && ( (this.direction == Direction.LEFT) || (this.direction == Direction.RIGHT) ) ||
            (snakeParts.get(0).y == snakeParts.get(1).y) && ( (this.direction == Direction.UP) || (this.direction == Direction.DOWN) )) {
            return;
        }
        else {
            if (((this.direction == Direction.DOWN) && (direction != Direction.UP)) ||
                ((this.direction == Direction.UP) && (direction != Direction.DOWN)) ||
                ((this.direction == Direction.LEFT) && (direction != Direction.RIGHT)) ||
                ((this.direction == Direction.RIGHT) && (direction != Direction.LEFT)))
                this.direction = direction;
        }
    }

    public void draw(Game game) {
        Color snakeColor;
        if (this.isAlive)
            snakeColor = Color.BLACK;
        else
            snakeColor = Color.RED;

        for (GameObject part : snakeParts) {
            if (snakeParts.indexOf(part) == 0) {
                game.setCellValueEx(part.x, part.y, Color.NONE, HEAD_SIGN, snakeColor, 75);
            } else {
                game.setCellValueEx(part.x, part.y, Color.NONE, BODY_SIGN, snakeColor, 75);
            }
        }
    }

    public void move(Apple apple){
        GameObject newHead = createNewHead();

        if (newHead.x >= SnakeGame.WIDTH || (newHead.x < 0)
                || newHead.y >= SnakeGame.HEIGHT || newHead.y < 0) {
            isAlive = false;
        }
        else {
            if (checkCollision(newHead))
                isAlive = false;
            else {
                snakeParts.add(0, newHead);
                if ((apple.x == snakeParts.get(0).x) && (apple.y == snakeParts.get(0).y)) {
                    apple.isAlive = false;
                } else {
                    removeTail();
                }
            }
        }
    }

    public GameObject createNewHead(){
        GameObject head = snakeParts.get(0);
        GameObject newHead = new GameObject(head.x, head.y);

        switch (direction) {
            case LEFT :
                --newHead.x;
                break;
            case DOWN :
                ++newHead.y;
                break;
            case RIGHT :
                ++newHead.x;
                break;
            case UP:
                --newHead.y;
                break;
        }

        return newHead;
    }

    public void removeTail() {
        snakeParts.remove(snakeParts.size() - 1);
    }

    public boolean checkCollision(GameObject somepart) {
        boolean isCollision = false;
        for (GameObject part : snakeParts) {
            if ((somepart.x == part.x) && (somepart.y == part.y)) {
                isCollision = true;
                break;
            }
        }

        return isCollision;
    }

    public int getLength(){
        return snakeParts.size();
    }
}
