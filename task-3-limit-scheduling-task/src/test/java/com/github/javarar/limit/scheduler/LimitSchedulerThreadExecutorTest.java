package com.github.javarar.limit.scheduler;

import lombok.val;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LimitSchedulerThreadExecutorTest {

    @Test
    void submitTask() {
        val executor = new LimitSchedulerThreadExecutor(5, 3, 1, SECONDS);
        val runnable = (Runnable) () -> {
            throw new RuntimeException("Task failed");
        };
        val task = new RetryableTask("Task-0", runnable, executor);

        executor.submitTask(task);

        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertEquals(3, task.getAttempts().get());
        executor.shutdown();
    }
}