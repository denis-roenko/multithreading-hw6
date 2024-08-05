package com.github.javarar.rejected.task;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DelayedOnRejectedThreadExecutorTest {

    @Test
    void delayRejectedThreadPoolExecutor() {
        val executor = new DelayedOnRejectedThreadExecutor(1, 1, 2, SECONDS)
                .delayRejectedThreadPoolExecutor();
        val competedTasks = executeTasks(executor);
        executor.shutdown();

        val assertions = competedTasks.stream()
                .map(task -> (Executable) () -> assertTrue(task.isDone()))
                .toList();
        assertAll(assertions);
    }

    private List<Future<?>> executeTasks(ExecutorService executor) {
        val futures = new ArrayList<Future<?>>();
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(750);
                val future = executor.submit(new Task(String.format("Task-%d", i)));
                futures.add(future);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            for (Future<?> future : futures)
                future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return futures;
    }
}