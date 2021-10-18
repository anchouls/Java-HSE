package ru.hse.java.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThreadPoolImpl implements ThreadPool {

    private final List<Thread> threads;
    private final Queue<LightFutureImpl<?>> tasks = new ArrayDeque<>();
    private int numberOfThreads;

    public ThreadPoolImpl(int threads) {
        this.threads = Stream.generate(() -> new Thread(new Run())).limit(threads).collect(Collectors.toList());
        this.threads.forEach(Thread::start);
        numberOfThreads = threads;
    }

    private class Run implements Runnable {

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    LightFutureImpl<?> task;
                    synchronized (tasks) {
                        while (tasks.isEmpty()) {
                            tasks.wait();
                        }
                        task = tasks.poll();
                    }
                    task.run();
                }
            } catch (final InterruptedException ignored) {
            }
        }
    }

    @Override
    public @NotNull <R> LightFuture<R> submit(Supplier<@NotNull R> supplier) {
        LightFutureImpl<R> task = new LightFutureImpl<>(supplier, this);
        synchronized (tasks) {
            tasks.add(task);
            tasks.notifyAll();
        }
        return task;
    }

    @Override
    public void shutdown() {
        threads.forEach(Thread::interrupt);
        for (Thread thread : threads) {
            try {
                thread.join(500);
                break;
            } catch (final InterruptedException ignored) { }
        }
        numberOfThreads = 0;
    }

    @Override
    public int getNumberOfThreads() {
        return numberOfThreads;
    }
}
