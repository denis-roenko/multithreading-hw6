package com.github.javarar.limit.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@AllArgsConstructor
public class RetryableTask implements Runnable {
    private final String id;
    private final Runnable task;
    private final LimitSchedulerThreadExecutor executor;

    @Getter
    private final AtomicInteger attempts = new AtomicInteger();

    @Override
    public void run() {
        try {
            task.run();
        } catch (Exception e) {
            if (attempts.get() < executor.getMaxAttempts()) {
                log.info("Попытка {} выполнения задачи {} провалилась. Повтор через {} {}.",
                        attempts.getAndIncrement() + 1, id, executor.getDelay(), executor.getTimeUnit().name().toLowerCase());
                executor.submitTask(this);
            } else {
                log.info("Достигнуто максимальное число потыток выполнения задачи {}. Задача отклоняется.", id);
            }
        }
    }
}
