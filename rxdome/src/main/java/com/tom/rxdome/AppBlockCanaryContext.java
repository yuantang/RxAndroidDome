package com.tom.rxdome;

import com.github.moduth.blockcanary.BlockCanaryContext;

/**
 * Created by tom on 2016/1/25.
 */
public class AppBlockCanaryContext extends BlockCanaryContext {
    @Override
    public int getConfigBlockThreshold() {
        return 500;
    }

    // if set true, notification will be shown, else only write log file
    @Override
    public boolean isNeedDisplay() {
        return BuildConfig.DEBUG;
    }

    // path to save log file
    @Override
    public String getLogPath() {
        return "/blockcanary/performance";
    }
}
