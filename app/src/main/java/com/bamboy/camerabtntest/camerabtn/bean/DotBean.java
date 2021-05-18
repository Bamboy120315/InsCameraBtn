package com.bamboy.camerabtntest.camerabtn.bean;

public class DotBean {
    /**
     * 坐标X
     */
    private float x;
    /**
     * 坐标Y
     */
    private float y;
    /**
     * 在圆上的角度
     */
    private float rotation;
    /**
     * 点的直径
     */
    private float diam;
    /**
     * 距离圆心最远的点的X坐标
     */
    private float farX;
    /**
     * 距离圆心最远的点的Y坐标
     */
    private float farY;
    /**
     * 距离圆心最近的点的X坐标
     */
    private float nearX;
    /**
     * 距离圆心最近的点的Y坐标
     */
    private float nearY;

    public DotBean() {

    }

    public DotBean(float x, float y, float rotation, float diam) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.diam = diam;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getDiam() {
        return diam;
    }

    public void setDiam(float diam) {
        this.diam = diam;
    }

    /**
     * 距离圆心最远的点的X坐标
     */
    public float getFarX() {
        return farX;
    }

    /**
     * 距离圆心最远的点的X坐标
     */
    public void setFarX(float farX) {
        this.farX = farX;
    }

    /**
     * 距离圆心最远的点的Y坐标
     */
    public float getFarY() {
        return farY;
    }

    /**
     * 距离圆心最远的点的Y坐标
     */
    public void setFarY(float farY) {
        this.farY = farY;
    }

    /**
     * 距离圆心最近的点的X坐标
     */
    public float getNearX() {
        return nearX;
    }

    /**
     * 距离圆心最近的点的X坐标
     */
    public void setNearX(float nearX) {
        this.nearX = nearX;
    }

    /**
     * 距离圆心最近的点的Y坐标
     */
    public float getNearY() {
        return nearY;
    }

    /**
     * 距离圆心最近的点的Y坐标
     */
    public void setNearY(float nearY) {
        this.nearY = nearY;
    }
}
