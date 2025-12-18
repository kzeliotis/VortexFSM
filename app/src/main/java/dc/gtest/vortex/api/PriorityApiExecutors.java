package dc.gtest.vortex.api;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class PriorityApiExecutors {

    private static final int THREAD_COUNT = 6;

    public static final ThreadPoolExecutor IO =
            new ThreadPoolExecutor(
                    THREAD_COUNT,
                    THREAD_COUNT,
                    60,
                    TimeUnit.SECONDS,
                    new PriorityBlockingQueue<>(),
                    runnable -> {
                        Thread t = new Thread(runnable);
                        t.setName("Vortex-API");
                        t.setPriority(Thread.NORM_PRIORITY);
                        return t;
                    }
            );

    private PriorityApiExecutors() { }
}
