package ru.hse.java.threadpool;

import org.jetbrains.annotations.NotNull;
import ru.hse.java.threadpool.exceptions.LightExecutionException;

import java.util.function.Function;
import java.util.function.Supplier;

public class LightFutureImpl<R> implements LightFuture<R> {

    private final Supplier<R> supplier;
    private final ThreadPool parent;
    private volatile boolean isReady;
    private R result;
    private LightExecutionException exception;

    public LightFutureImpl(Supplier<R> supplier, ThreadPoolImpl parent) {
        this.supplier = supplier;
        this.parent = parent;
    }

    @Override
    public boolean isReady() {
        return isReady;
    }

    @NotNull
    @Override
    public R get() throws LightExecutionException {
        while (!isReady && !Thread.currentThread().isInterrupted()) {
            synchronized (this) {
                while (!isReady) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
        if (result == null) {
            throw new LightExecutionException("thread was interrupted");
        }
        return result;
    }

    @Override
    public <R1> @NotNull LightFuture<R1> thenApply(Function<R, @NotNull R1> function) {
        return parent.submit(() -> {
            try {
                return function.apply(get());
            } catch (LightExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public synchronized void run() {
        try {
            result = supplier.get();
        } catch (RuntimeException e) {
            if (e.getCause() != null && e.getCause().getClass() == LightExecutionException.class) {
                exception = new LightExecutionException(e.getCause().getCause());
            } else {
                exception = new LightExecutionException(e);
            }
        }
        isReady = true;
        notifyAll();
    }
}
