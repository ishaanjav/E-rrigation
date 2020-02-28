package com.example.anany.drawingrectangles;

public class Coordinates {

    int textId, x, y;
    boolean sprinkler;

    public Coordinates(int textId, int x, int y, boolean b) {
        this.textId = textId;
        this.x = x;
        this.y = y;
        sprinkler = b;
    }

    public boolean compareTo(Coordinates comparing) {
        if (comparing.y > y) return true;
        if (comparing.x < x && y < comparing.y + 260) return true;
        return false;
    }

    public boolean isSprinkler() {
        return sprinkler;
    }

    public int getTextId() {
        return textId;
    }

    public void setTextId(int textId) {
        this.textId = textId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
