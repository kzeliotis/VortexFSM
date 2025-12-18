package dc.gtest.vortex.support;

import java.util.concurrent.atomic.AtomicInteger;

public final class StartupLoadTracker {

    private static AtomicInteger remaining;
    private static Runnable onComplete;

    private StartupLoadTracker() {}

    public static synchronized void start(int totalJobs, Runnable onAllDone) {
        remaining = new AtomicInteger(totalJobs);
        onComplete = onAllDone;
    }

    public static synchronized boolean isActive() {
        return remaining != null;
    }

    public static void jobDone() {
        AtomicInteger r = remaining;
        if (r == null) return; // 👈 SAFETY

        if (r.decrementAndGet() == 0) {
            Runnable done = onComplete;
            remaining = null;   // 👈 deactivate tracker
            onComplete = null;

            if (done != null) {
                done.run();
            }
        }
    }
}

