package com.github.vitaliikapliuk.ask.core;

import com.github.vitaliikapliuk.ask.Server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeFrame implements Runnable {

    private final ScheduledExecutorService ses;
    private final Map<String, AtomicInteger> isoTimeFrame;
    private static final int MAX_TIME_FRAME = Server.getConfig().timeFrameMaxIsoCallsPerSecond;

    private TimeFrame() {
        ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS); // execute every second

        isoTimeFrame = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        isoTimeFrame.forEach((k, v) -> v.set(0));
    }

    // create iso with 0 calls if absent, also check free calls
    public static synchronized boolean isAvailable(String iso) {
        getInstance().isoTimeFrame.putIfAbsent(iso, new AtomicInteger(0));
        return getInstance().isoTimeFrame.get(iso).getAndIncrement() < MAX_TIME_FRAME;
    }

    private static TimeFrame getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        private static final TimeFrame instance = new TimeFrame();
    }
}
