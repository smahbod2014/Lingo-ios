package com.sean.koda.lingo.internal;

import java.util.HashMap;

public class Timer {

    private static final HashMap<String, TimerUnit> timers = new HashMap<String, TimerUnit>();

    public static void start(String name, long amount) {
        timers.put(name, new TimerUnit(System.currentTimeMillis() + amount));
    }

    public static boolean done(String name) {
        if (timers.get(name) == null)
            return false;
        return System.currentTimeMillis() >= timers.get(name).expiry;
    }

    public static boolean running(String name) {
        if (timers.get(name) == null)
            return false;
        return System.currentTimeMillis() < timers.get(name).expiry;
    }

    public static boolean justFinished(String name) {
        if (done(name)) {
            if (!timers.get(name).checked) {
                timers.get(name).checked = true;
                return true;
            }
        }
        return false;
    }

    private static class TimerUnit {
        long expiry;
        boolean checked;

        TimerUnit(long expiry) {
            this.expiry = expiry;
            checked = false;
        }
    }
}
