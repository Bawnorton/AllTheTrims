package com.bawnorton.allthetrims.client.compat.recipebrowser;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class CyclingStack<T> implements Closeable {
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    protected final List<T> entries;
    protected int index = 0;

    public CyclingStack(List<T> entries, int duration) {
        this.entries = Collections.synchronizedList(entries);
        service.scheduleAtFixedRate(this::cycle, duration, duration, TimeUnit.MILLISECONDS);
    }

    public void cycle() {
        index++;
        if (index >= entries.size()) {
            index = 0;
        }
    }

    @Override
    public void close() {
        service.shutdown();
    }
}