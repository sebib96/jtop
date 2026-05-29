package org.jtop.service;

import org.jtop.model.SystemSnapshot;

public class MockMonitor implements DataSource {
    private final int cores;

    public MockMonitor(int cores) {
        this.cores = cores;
    }

    @Override
    public SystemSnapshot getLatestSnapshot() {
        double[] testCpuLoadPerCore = new double[cores];
        long timestamp = System.currentTimeMillis();

        for ( int i = 0; i < cores; i++ ) {
            testCpuLoadPerCore[i] = (double) i / (cores - 1);
        }

        return new SystemSnapshot(testCpuLoadPerCore, null, timestamp);

    }

    @Override
    public void startPolling() {

    }
}
