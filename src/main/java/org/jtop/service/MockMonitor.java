package org.jtop.service;

import org.jtop.model.MemoryInfo;
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

        MemoryInfo ram = new MemoryInfo(8L * 1024 * 1024 * 1024, 16L * 1024 * 1024 * 1024);
        MemoryInfo swap =  new MemoryInfo(512L * 1024 * 1024, 2L * 1024 * 1024 * 1024);

        return new SystemSnapshot(
                testCpuLoadPerCore,
                null,
                timestamp,
                ram,
                swap);
    }

    @Override
    public void startPolling() {

    }
}
