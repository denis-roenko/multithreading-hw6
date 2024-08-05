package com.github.javarar.rejected.task;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;

@Log4j2
public class DelayedOnRejectedThreadExecutor {

    private final int corePoolSize;
    private final int maximumPoolSize;
    private int delay;
    private TimeUnit timeUnit;
    private final BlockingQueue<Runnable> queue;
    private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

    public DelayedOnRejectedThreadExecutor(int corePoolSize, int maximumPoolSize, int delay, TimeUnit timeUnit) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.delay = delay;
        this.timeUnit = timeUnit;
        queue = new ArrayBlockingQueue<>(maximumPoolSize);
    }

    public final RejectedExecutionHandler rejectedExecutionHandler = (r, executor) -> {
        log.info("Задача не поместилась в очередь, откладываем выполнение {}", r.toString());
        scheduledExecutor.schedule(() -> {
            log.info("Запускаем отложенную задачу {}", r.toString());
            executor.execute(r);
        }, delay, timeUnit);
    };

    public ExecutorService delayRejectedThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                0L,
                SECONDS,
                queue,
                rejectedExecutionHandler
        );
    }
}
