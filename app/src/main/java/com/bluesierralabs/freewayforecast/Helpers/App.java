package com.bluesierralabs.freewayforecast.helpers;

import android.app.Application;
import android.content.Context;

/**
 * Created by timothy on 12/29/14.
 * Used to give context to classes that reside outside of activity
 */
public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
