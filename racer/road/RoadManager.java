package com.javarush.games.racer.road;

import com.javarush.engine.cell.Game;
import com.javarush.games.racer.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RoadManager {
    public static final int LEFT_BORDER = RacerGame.ROADSIDE_WIDTH;
    public static final int RIGHT_BORDER = RacerGame.WIDTH - LEFT_BORDER;
    private static final int FIRST_LANE_POSITION = 16;
    private static final int FOURTH_LANE_POSITION = 44;
    private static final int PLAYER_CAR_DISTANCE = 12;
    private List<RoadObject> items = new ArrayList();
    private int passedCarsCount = 0;

    private RoadObject createRoadObject(RoadObjectType type, int x, int y){
        RoadObject result = null;
        if (type == RoadObjectType.THORN) {
            result = new Thorn(x, y);
        }
        else if (type == RoadObjectType.DRUNK_CAR) {
            result = new MovingCar(x, y);
        }
        else {
            result = new Car(type, x, y);
        }
        return result;
    }

    private void addRoadObject(RoadObjectType type, Game game){
        int x = game.getRandomNumber(FIRST_LANE_POSITION, FOURTH_LANE_POSITION);
        int y = -1 * RoadObject.getHeight(type);

        RoadObject ro = createRoadObject(type, x, y);
        if ((ro != null) && isRoadSpaceFree(ro)) {
            items.add(ro);
        }
    }

    public void draw(Game game){
        for (RoadObject item: items) {
            item.draw(game);
        }
    }

    public void move(int boost){
        for (RoadObject item: items) {
            item.move(boost + item.speed, items);
        }
        deletePassedItems();
    }

    private boolean isThornExists(){
        boolean result = false;
        for (RoadObject item: items) {
            if (item.type == RoadObjectType.THORN) {
                return true;
            }
        }
        return result;
    }

    private void generateThorn(Game game){
        if ((game.getRandomNumber(100) < 10) && !isThornExists()) {
            addRoadObject(RoadObjectType.THORN, game);
        }
    }

    public void generateNewRoadObjects(Game game){
        generateThorn(game);
        generateRegularCar(game);
        generateMovingCar(game);
    }

    private void deletePassedItems(){
        Iterator iterator = items.iterator();
        while(iterator.hasNext()) {
            RoadObject item = (RoadObject)  iterator.next();
            if(item.y >= RacerGame.HEIGHT) {
                if (item.type != RoadObjectType.THORN) {
                    passedCarsCount++;
                }
                iterator.remove();
            }
        }
    }

    public boolean checkCrush(PlayerCar playerCar){
        boolean result = false;

        for (RoadObject item: items) {
            if (item.isCollision(playerCar)) {
                result = true;
                break;
            }
        }

        return result;
    };

    private  void generateRegularCar(Game game){
        int carTypeNumber = game.getRandomNumber(4);

        if (game.getRandomNumber(100) < 30) {
            addRoadObject(RoadObjectType.values()[carTypeNumber], game);
        }
    }

    private boolean isRoadSpaceFree(RoadObject object){
        boolean result = true;
        for (RoadObject item: items) {
            if (item.isCollisionWithDistance(object, PLAYER_CAR_DISTANCE)) {
                result = false;
            }
        }
        return result;
    }

    private boolean isMovingCarExists(){
        boolean result = false;
        for (RoadObject item: items) {
            if (item.type == RoadObjectType.DRUNK_CAR) {
                result = true;
            }
        }
        return result;
    }

    private void generateMovingCar(Game game){
        if ((game.getRandomNumber(100) < 10) && (isMovingCarExists() == false)) {
            addRoadObject(RoadObjectType.DRUNK_CAR, game);
        }
    }

    public int getPassedCarsCount(){
        return passedCarsCount;
    }
}
