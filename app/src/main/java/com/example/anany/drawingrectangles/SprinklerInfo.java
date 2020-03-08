package com.example.anany.drawingrectangles;

public class SprinklerInfo {
    double radius;
    int quantity;

    public SprinklerInfo(double radius, int quantity) {
        this.radius = radius;
        this.quantity = quantity;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getFrequency() {
        return quantity;
    }

    public void setFrequency(int quantity) {
        this.quantity = quantity;
    }
}
