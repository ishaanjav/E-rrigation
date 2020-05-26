package app.ij.errigation;

public class OverflowInfo {

    public int circlePos, point1, point2;
    public double lineStartx, lineStarty, lineEndx, lineEndy;
    public double circleX, circleY, radius;
    public double x1, x2, y1, y2;

    public OverflowInfo(int circlePos, int point1, int point2, double lineStartx, double lineStarty, double lineEndx, double lineEndy, double circleX, double circleY, double radius, double x1, double x2, double y1, double y2) {
        this.circlePos = circlePos;
        this.point1 = point1;
        this.point2 = point2;
        this.lineStartx = lineStartx;
        this.lineStarty = lineStarty;
        this.lineEndx = lineEndx;
        this.lineEndy = lineEndy;
        this.circleX = circleX;
        this.circleY = circleY;
        this.radius = radius;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public int getCirclePos() {
        return circlePos;
    }

    public void setCirclePos(int circlePos) {
        this.circlePos = circlePos;
    }

    public int getPoint1() {
        return point1;
    }

    public void setPoint1(int point1) {
        this.point1 = point1;
    }

    public int getPoint2() {
        return point2;
    }

    public void setPoint2(int point2) {
        this.point2 = point2;
    }

    public double getLineStartx() {
        return lineStartx;
    }

    public void setLineStartx(double lineStartx) {
        this.lineStartx = lineStartx;
    }

    public double getLineStarty() {
        return lineStarty;
    }

    public void setLineStarty(double lineStarty) {
        this.lineStarty = lineStarty;
    }

    public double getLineEndx() {
        return lineEndx;
    }

    public void setLineEndx(double lineEndx) {
        this.lineEndx = lineEndx;
    }

    public double getLineEndy() {
        return lineEndy;
    }

    public void setLineEndy(double lineEndy) {
        this.lineEndy = lineEndy;
    }

    public double getCircleX() {
        return circleX;
    }

    public void setCircleX(double circleX) {
        this.circleX = circleX;
    }

    public double getCircleY() {
        return circleY;
    }

    public void setCircleY(double circleY) {
        this.circleY = circleY;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getX1() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public double getX2() {
        return x2;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public double getY1() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public double getY2() {
        return y2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }
}
