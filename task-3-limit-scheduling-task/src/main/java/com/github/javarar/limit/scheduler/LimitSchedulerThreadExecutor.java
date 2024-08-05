package com.github.javarar.limit.scheduler;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
public class LimitSchedulerThreadExecutor {
    @Getter
    private final ScheduledExecutorService executor;
    @Getter
    private final int maxAttempts;
    @Getter
    private final long delay;
    @Getter
    private final TimeUnit timeUnit;

    public LimitSchedulerThreadExecutor(int poolSize, int maxAttempts, long delay, TimeUnit timeUnit) {
        this.executor = Executors.newScheduledThreadPool(poolSize);
        this.maxAttempts = maxAttempts;
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    public void submitTask(Runnable task) {
        executor.schedule(task, delay, timeUnit);
    }

    public void shutdown() {
        executor.shutdown();
    }
}
