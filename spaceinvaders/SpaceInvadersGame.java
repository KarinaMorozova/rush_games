package com.javarush.games.spaceinvaders;

import com.javarush.engine.cell.*;
import com.javarush.games.spaceinvaders.gameobjects.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SpaceInvadersGame extends Game {
    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;
    private List<Star> stars;
    private EnemyFleet enemyFleet;
    public static final int COMPLEXITY = 5;
    private List<Bullet> enemyBullets;
    private PlayerShip playerShip;
    private boolean isGameStopped;
    private int animationsCount;
    private List<Bullet> playerBullets;
    private static final int PLAYER_BULLETS_MAX = 1;
    private int score;

    @Override
    public void initialize() {
        setScreenSize(WIDTH, HEIGHT);
        createGame();
    }

    private void createGame(){
        score = 0;
        isGameStopped = false;
        animationsCount = 0;
        createStars();
        enemyFleet = new EnemyFleet();
        enemyBullets = new ArrayList<Bullet>();
        playerShip = new PlayerShip();
        playerBullets = new ArrayList<Bullet>();
        drawScene();
        setTurnTimer(40);
    }

    private void drawScene(){
        drawField();
        playerShip.draw(this);
        enemyFleet.draw(this);
        for (Bullet enemybullet: enemyBullets) {
            enemybullet.draw(this);
        }
        for (Bullet playerbullet: playerBullets) {
            playerbullet.draw(this);
        }

    }

    private void drawField(){
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                setCellValueEx(x, y, Color.BLACK, "");
            }
        }
        for (Star star: stars) {
            star.draw(this);
        }
    }

    private void createStars(){
        stars = new ArrayList<Star>();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            stars.add(new Star(x, y));
        }
    }

    @Override
    public void onTurn(int step) {
        setScore(score);
        moveSpaceObjects();
        Bullet bullet = enemyFleet.fire(this);
        if (bullet != null) {
            enemyBullets.add(bullet);
        }
        check();
        drawScene();
    }

    private void moveSpaceObjects(){
        enemyFleet.move();
        for (Bullet enemybullet: enemyBullets) {
            enemybullet.move();
        }
        playerShip.move();
        for (Bullet playerbullet: playerBullets) {
            playerbullet.move();
        }

    }

    private void removeDeadBullets(){
        Iterator i = enemyBullets.iterator();
        while (i.hasNext()) {
            Bullet enemybullet = (Bullet) i.next();
            if ((enemybullet.isAlive == false) || (enemybullet.y >= (HEIGHT - 1))) {
                i.remove();
            }
        }

        i = playerBullets.iterator();
        while (i.hasNext()) {
            Bullet playerBullets = (Bullet) i.next();
            if ((playerBullets.isAlive == false) || ((playerBullets.y + playerBullets.height) < 0)) {
                i.remove();
            }
        }
    }

    private void check(){
        playerShip.verifyHit(enemyBullets);
        score = score + enemyFleet.verifyHit(playerBullets);
        enemyFleet.deleteHiddenShips();
        removeDeadBullets();
        if (!playerShip.isAlive) {
            stopGameWithDelay();
        }
        if (enemyFleet.getBottomBorder() >= playerShip.y) {
            playerShip.kill();
        }

        if (enemyFleet.getShipsCount() == 0) {
            playerShip.win();
            stopGameWithDelay();
        }

    }

    private void stopGame(boolean isWin){
        isGameStopped = true;
        stopTurnTimer();
        if (isWin) {
            showMessageDialog(Color.WHITE, "IT'S WIN!", Color.GREEN, 100);
        }
        else {
            showMessageDialog(Color.WHITE, "GAME OVER!", Color.RED, 100);
        }
    }

    private void stopGameWithDelay(){
        animationsCount++;
        if (animationsCount >= 10) {
            stopGame(playerShip.isAlive);
        }
    }

    @Override
    public void onKeyPress(Key key) {
        if (key == Key.SPACE) {
            Bullet bullet = playerShip.fire();
            if ((bullet != null) && (playerBullets.size() < PLAYER_BULLETS_MAX)) {
                playerBullets.add(bullet);
            }
        }
        else if ((key == Key.SPACE) && (isGameStopped == true)) {
            createGame();
        }
        else {
            switch (key) {
                case LEFT:
                    playerShip.setDirection(Direction.LEFT);
                    break;
                case RIGHT:
                    playerShip.setDirection(Direction.RIGHT);
                    break;
            }
        }
    }

    @Override
    public void onKeyReleased(Key key) {
        if (((key == Key.RIGHT) && (playerShip.getDirection() == Direction.RIGHT)) ||
                ((key == Key.LEFT) && (playerShip.getDirection() == Direction.LEFT))) {
            playerShip.setDirection(Direction.UP);
        }

    }

    @Override
    public void setCellValueEx(int x, int y, Color cellColor, String value) {
        if ((0 < x && x < WIDTH) || (0 < y && y < HEIGHT)) {
            super.setCellValueEx(x, y, cellColor, value);
        }
    }
}
