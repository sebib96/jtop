package org.jtop.service;

import org.jtop.model.MemoryInfo;
import org.jtop.model.SystemSnapshot;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SystemMonitor implements DataSource {
    private final CentralProcessor processor;
    private final GlobalMemory memory;
    private long[][] previousTicks;
    private final ScheduledExecutorService scheduler;
    private final AtomicReference<SystemSnapshot> latestSnapshot;

    private static final long POLLING_INITIAL_DELAY = 0;
    private static final long POLLING_INTERVAL = 500;

    public SystemMonitor() {
        HardwareAbstractionLayer hal = new SystemInfo().getHardware();

        this.processor = hal.getProcessor();
        this.memory = hal.getMemory();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.previousTicks = this.processor.getProcessorCpuLoadTicks();
        this.latestSnapshot = new AtomicReference<>(null);
    }

    public void startPolling() {
        scheduler.scheduleAtFixedRate(this::poll, POLLING_INITIAL_DELAY, POLLING_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private void poll() {
        double[] updatedCpuLoadBetweenTicks = processor.getProcessorCpuLoadBetweenTicks(previousTicks);
        previousTicks = processor.getProcessorCpuLoadTicks();
        long updatedTimestamp = System.currentTimeMillis();

        MemoryInfo ram = new MemoryInfo(
                memory.getTotal() - memory.getAvailable(),
                memory.getTotal());

        MemoryInfo swap = new MemoryInfo(
                memory.getVirtualMemory().getSwapUsed(),
                memory.getVirtualMemory().getSwapTotal());

        SystemSnapshot updatedSystemSnapshot = new SystemSnapshot(
                updatedCpuLoadBetweenTicks,
                null,
                updatedTimestamp,
                ram,
                swap);

        latestSnapshot.set(updatedSystemSnapshot);
    }

    public SystemSnapshot getLatestSnapshot() {
        return latestSnapshot.get();
    }
}
