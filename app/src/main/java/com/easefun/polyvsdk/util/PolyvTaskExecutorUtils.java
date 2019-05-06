package com.easefun.polyvsdk.util;

import android.support.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author df
 * @create 2019/4/4
 * @Describe
 */
public class PolyvTaskExecutorUtils implements Executor {
    private static PolyvTaskExecutorUtils instance;
    public static PolyvTaskExecutorUtils getInstance() {
        if(instance == null){
            synchronized (PolyvTaskExecutorUtils.class){
                if(instance == null){
                    instance = new PolyvTaskExecutorUtils();
                }
            }
        }
        return instance;
    }
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "PolyvTask #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);

    /**
     * An {@link Executor} that can be used to execute tasks in parallel.
     */
    public static final Executor THREAD_POOL_EXECUTOR
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

    public static final Executor SINGLE_POOL_EXECUTOR = Executors.newSingleThreadExecutor();

    @Override
    public void execute(@NonNull Runnable command) {
        THREAD_POOL_EXECUTOR.execute(command);
    }

    public void executeInSinglePool(@NonNull Runnable command){
        SINGLE_POOL_EXECUTOR.execute(command);
    }
}
