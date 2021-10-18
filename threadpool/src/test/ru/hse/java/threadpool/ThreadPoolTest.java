package ru.hse.java.threadpool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.java.threadpool.exceptions.LightExecutionException;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class ThreadPoolTest {

    int processors = Runtime.getRuntime().availableProcessors();

    @Test
    void testWithOneThread() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(1);
        LightFuture<Integer> task = pool.submit(() -> 1);
        Assertions.assertEquals(1, task.get());
    }

    @Test
    void testWithManyThread() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(processors);
        LightFuture<Integer> task = pool.submit(() -> 1);
        Assertions.assertEquals(1, task.get());
        Assertions.assertEquals(1, task.get());
    }

    @Test
    void testGetException() {
        ThreadPool pool = ThreadPool.create(processors);
        List<LightFuture<String>> tasks = new ArrayList<>();
        for (int i = 0; i < processors * 2; i++) {
            LightFuture<String> task = pool.submit(() -> {
                throw new IllegalStateException();
            });
            tasks.add(task);
        }
        for (LightFuture<String> t : tasks) {
            Assertions.assertThrows(LightExecutionException.class, t::get);
        }
    }

    @Test
    void testGetNumberOfThreads() {
        ThreadPool pool = ThreadPool.create(processors);
        Assertions.assertEquals(processors, pool.getNumberOfThreads());
    }

    @Test
    void testShutdown() {
        ThreadPool pool = ThreadPool.create(processors);
        pool.shutdown();
        Assertions.assertEquals(0, pool.getNumberOfThreads());
    }


    @Test
    void testShutdownAfterGet() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(processors);
        LightFuture<Integer> task = pool.submit(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        });
        task.get();
        Assertions.assertTimeout(Duration.ofMillis(10), pool::shutdown);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testManyThreadsShutdown() throws NoSuchFieldException, InterruptedException, IllegalAccessException {
        ThreadPool pool = ThreadPool.create(10);
        for (int i = 0; i < 3; i++) {
            pool.submit(() -> 1);
        }
        Field threads = pool.getClass().getDeclaredField("threads");
        threads.setAccessible(true);
        pool.shutdown();
        Thread.sleep(1000);
        for (Thread thread : (List<Thread>) threads.get(pool)) {
            Assertions.assertFalse(thread.isAlive());
        }
    }

    @Test
    void testThenApply() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(2);
        LightFuture<Integer> task1 = pool.submit(() -> 2);
        LightFuture<Integer> task2 = task1.thenApply(a -> a * 2);
        Assertions.assertEquals(2, task1.get());
        Assertions.assertEquals(4, task2.get());
    }

    @Test
    void testManyThenApply() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(processors);
        List<LightFuture<Integer>> tasks = new ArrayList<>();
        tasks.add(pool.submit(() -> 1));
        for (int i = 1; i < processors * 2; i++) {
            tasks.add(tasks.get(i - 1).thenApply(x -> x + 1));
        }
        for (int i = processors * 2 - 1; i > -1; i--) {
            Assertions.assertEquals(i + 1, tasks.get(i).get());
        }
    }

    @Test
    void testThenApplyDifferentTypes() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(3);
        LightFuture<Integer> task1 = pool.submit(() -> 1);
        LightFuture<String> task2 = task1.thenApply(x -> Integer.valueOf(x + 1).toString());
        LightFuture<String> task3 = task1.thenApply(Object::toString);
        Assertions.assertEquals(Integer.valueOf(1), task1.get());
        Assertions.assertEquals("2", task2.get());
        Assertions.assertEquals("1", task3.get());
    }

    @Test
    void testThenApplyException() {
        ThreadPool pool = ThreadPool.create(3);
        LightFuture<String> task1 = pool.submit(() -> {
            throw new RuntimeException();
        });
        LightFuture<String> task2 = task1.thenApply(String::toUpperCase);
        Assertions.assertThrows(LightExecutionException.class, task1::get);
        Assertions.assertThrows(LightExecutionException.class, task2::get);
    }

    @Test
    void testTime() throws LightExecutionException {
        long m = System.currentTimeMillis();
        ThreadPool pool = ThreadPool.create(1);
        List<LightFuture<Integer>> tasks = new ArrayList<>();
        for (int i = 0; i < processors * 2; i++) {
            LightFuture<Integer> task = pool.submit(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 1;
            });
            tasks.add(task);
        }
        for (LightFuture<Integer> t : tasks) {
            t.get();
        }
        long k = System.currentTimeMillis() - m;
        long n = System.currentTimeMillis();
        ThreadPool pool2 = ThreadPool.create(processors);
        List<LightFuture<Integer>> tasks2 = new ArrayList<>();
        for (int i = 0; i < processors * 2; i++) {
            LightFuture<Integer> task = pool2.submit(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 1;
            });
            tasks2.add(task);
        }
        for (LightFuture<Integer> t : tasks2) {
            t.get();
        }
        long l = System.currentTimeMillis() - n;
        Assertions.assertTrue((double) l * processors / k < 1.1 && (double) l * processors / k > 0.9);
    }

    @Test
    void testReady() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(processors);
        long m = System.currentTimeMillis();
        LightFuture<Integer> task = pool.submit(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        });
        long k = System.currentTimeMillis();
        while (true) {
            boolean isReady = task.isReady();
            if (System.currentTimeMillis() - m < 100) {
                Assertions.assertFalse(isReady);
            } else if (System.currentTimeMillis() - k > 110) {
                Assertions.assertTrue(isReady);
                break;
            }
        }
        task.get();
        Assertions.assertTrue(task.isReady());
    }

    @Test
    void chained() {
        ThreadPool pool = ThreadPool.create(5);
        var task1 = pool.submit(() -> 2 / 0);
        var future = task1.thenApply(x -> x + 1).thenApply(y -> y + 1);
        try{
            future.get();
        }catch (LightExecutionException e){
            Assertions.assertEquals(ArithmeticException.class , e.getCause().getClass());
        }
    }


}
