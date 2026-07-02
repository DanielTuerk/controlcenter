package net.wbz.moba.controlcenter.api;

public class BaseTestData {

    public static final Train TRAIN1 = new Train(10001, 5);
    public static final Train TRAIN2 = new Train(10002, 6);

    public record Train(int id, int address) {
    }
}
