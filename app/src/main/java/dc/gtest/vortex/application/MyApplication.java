package dc.gtest.vortex.application;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import lombok.Getter;

public class MyApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    @Getter
    private static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }

}
