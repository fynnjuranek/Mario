package util;

public class Time {
    public static float timeStarted = System.nanoTime();

    public static float getTime() { // time elapsed since the application is started
        return (float) ((System.nanoTime() - timeStarted) * 1E-9); // to convert nanoseconds into seconds
    }
}
