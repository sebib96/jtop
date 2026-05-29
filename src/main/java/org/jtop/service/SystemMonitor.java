package org.jtop.service;

import org.jtop.model.SystemSnapshot;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SystemMonitor implements DataSource {
    private final CentralProcessor processor;
    private long[][] previousTicks;
    private final ScheduledExecutorService scheduler;
    private final AtomicReference<SystemSnapshot> latestSnapshot;

    public SystemMonitor() {
        this.processor = new SystemInfo().getHardware().getProcessor();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.previousTicks = this.processor.getProcessorCpuLoadTicks();
        this.latestSnapshot = new AtomicReference<>(null);
    }

    public void startPolling() {
        scheduler.scheduleAtFixedRate(this::poll, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void poll() {
        double[] updatedCpuLoadBetweenTicks = processor.getProcessorCpuLoadBetweenTicks(previousTicks);
        previousTicks = processor.getProcessorCpuLoadTicks();
        long updatedTimestamp = System.currentTimeMillis();

        SystemSnapshot updatedSystemSnapshot = new SystemSnapshot(
                updatedCpuLoadBetweenTicks,
                null,
                updatedTimestamp);

        latestSnapshot.set(updatedSystemSnapshot);
    }

    public SystemSnapshot getLatestSnapshot() {
        return latestSnapshot.get();
    }
}
