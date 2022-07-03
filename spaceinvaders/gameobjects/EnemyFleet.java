package com.javarush.games.spaceinvaders.gameobjects;

import com.javarush.games.spaceinvaders.*;
import com.javarush.engine.cell.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnemyFleet {
    private static final int ROWS_COUNT = 3;
    private static final int COLUMNS_COUNT = 10;
    private static final int STEP = ShapeMatrix.ENEMY.length + 1;
    private List<EnemyShip> ships;
    private Direction direction = Direction.RIGHT;

    public EnemyFleet(){
        createShips();
    }

    private void createShips(){
        ships = new ArrayList<EnemyShip>();

        for (int x = 0; x < COLUMNS_COUNT; x++) {
            for (int y = 0; y < ROWS_COUNT; y++) {
                ships.add(new EnemyShip( x * STEP, y * STEP + 12));
            }
        }

        ships.add(new Boss(STEP * COLUMNS_COUNT / 2 - ShapeMatrix.BOSS_ANIMATION_FIRST.length / 2 - 1, 5));
    }

    public void draw(Game game){
        for (EnemyShip ship: ships) {
            ship.draw(game);
        }
    }

    private double getLeftBorder(){
        double minx = Double.MAX_VALUE;
        for (EnemyShip ship: ships) {
            if (ship.x < minx) {
                minx = ship.x;
            }
        }
        return minx;
    }

    private double getRightBorder(){
        double max = Double.MIN_VALUE;
        for (EnemyShip ship: ships) {
            if ((ship.x + ship.width) > max) {
                max = ship.x + ship.width;
            }
        }
        return max;
    }

    private double getSpeed(){
        return Math.min(2.0, 3.0 / ships.size());
    }

    public void move(){

        boolean isDirectionChanged = false;
        if (!ships.isEmpty()) {
            if ((direction == Direction.LEFT) && (getLeftBorder() < 0)) {
                direction = Direction.RIGHT;
                isDirectionChanged = true;
            }
            else if ((direction == Direction.RIGHT) && (getRightBorder() > SpaceInvadersGame.WIDTH)){
                direction = Direction.LEFT;
                isDirectionChanged = true;
            }

            if (isDirectionChanged) {
                for (EnemyShip ship: ships) {
                    ship.move(Direction.DOWN, getSpeed());
                }
            } else {
                for (EnemyShip ship: ships) {
                    ship.move(direction, getSpeed());
                }
            }
        }
    }

    public Bullet fire(Game game){
        Bullet result = null;
        
        if ( (ships.isEmpty()) ||
            (game.getRandomNumber(100 / SpaceInvadersGame.COMPLEXITY) > 0) ){
            result = null;
        }
        else {
            result = ships.get(game.getRandomNumber(ships.size())).fire();
        }

        return result;
    }

    public int verifyHit(List<Bullet> bullets){
        int totalscore = 0;
        if (bullets.isEmpty()) {
            totalscore = 0;
        }
        else {
            for (EnemyShip ship : ships) {
                for (Bullet bullet : bullets) {
                    if (ship.isCollision(bullet) && ship.isAlive && bullet.isAlive) {
                        totalscore = totalscore + ship.score;
                        ship.kill();
                        bullet.kill();
                    }
                }
            }
        }
        return totalscore;
    }

    public void deleteHiddenShips(){
        Iterator i = ships.iterator();
        while (i.hasNext()) {
            EnemyShip ship = (EnemyShip) i.next();
            if (ship.isVisible() == false) {
                i.remove();
            }
        }
    }

    public double getBottomBorder(){
        double max = Double.MIN_VALUE;
        for (EnemyShip ship: ships) {
            if (max < ship.y + ship.height) {
                max = ship.y + ship.height;
            }
        }

        return max;
    }

    public int getShipsCount(){
        return ships.size();
    }

}
