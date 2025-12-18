package dc.gtest.vortex.support;

import android.os.Handler;
import android.os.Looper;

public final class UiThread {

    private static final Handler MAIN =
            new Handler(Looper.getMainLooper());

    private UiThread() { }

    public static void run(Runnable r) {
        MAIN.post(r);
    }
}
