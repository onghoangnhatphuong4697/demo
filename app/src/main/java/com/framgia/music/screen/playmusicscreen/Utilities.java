package com.framgia.music.screen.playmusicscreen;

/**
 * Created by Admin on 3/14/2018.
 */

public class Utilities {

    private static final int TIME_1000 = 1000;
    private static final int TIME_60 = 60;
    private static final int TIME_10 = 10;
    private static final int TIME_100 = 100;
    private static final String TWO_DOT = ":";
    private static final String ZERO = "0";

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        int hours = (int) (milliseconds / (TIME_1000 * TIME_60 * TIME_60));
        int minutes =
                (int) (milliseconds % (TIME_1000 * TIME_60 * TIME_60)) / (TIME_1000 * TIME_60);
        int seconds =
                (int) ((milliseconds % (TIME_1000 * TIME_60 * TIME_60)) % (TIME_1000 * TIME_60)
                        / TIME_1000);

        if (hours > 0) {
            finalTimerString = hours + TWO_DOT;
        }

        if (seconds < TIME_10) {
            secondsString = ZERO + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + TWO_DOT + secondsString;

        return finalTimerString;
    }

    public int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage;

        long currentSeconds = (int) (currentDuration / TIME_1000);
        long totalSeconds = (int) (totalDuration / TIME_1000);

        percentage = (((double) currentSeconds) / totalSeconds) * TIME_100;

        return percentage.intValue();
    }

    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (totalDuration / TIME_1000);
        currentDuration = (int) ((((double) progress) / TIME_100) * totalDuration);

        return currentDuration * TIME_1000;
    }
}
