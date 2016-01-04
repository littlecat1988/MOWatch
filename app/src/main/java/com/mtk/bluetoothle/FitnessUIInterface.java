package com.mtk.bluetoothle;

public interface FitnessUIInterface {

    public void onSleepNotify(long startTime, long endTime, int sleepMode);

    public void onPedometerNotify(int stepCount, int calories, int distance);

    public void onHRNotify(int bpm);
};