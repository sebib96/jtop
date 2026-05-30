package org.jtop.service;

import org.jtop.model.MemoryInfo;
import org.jtop.model.ProcessInfo;
import org.jtop.model.SystemSnapshot;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SystemMonitor implements DataSource {
    private final CentralProcessor processor;
    private final GlobalMemory memory;
    private final Map<Integer, OSProcess> processMap;
    private final OperatingSystem os;
    private long[][] previousTicks;
    private final ScheduledExecutorService scheduler;
    private final AtomicReference<SystemSnapshot> latestSnapshot;

    private static final long POLLING_INITIAL_DELAY = 0;
    private static final long POLLING_INTERVAL = 500;

    public SystemMonitor() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer has =  systemInfo.getHardware();

        this.processor = has.getProcessor();
        this.memory = has.getMemory();
        this.processMap = new HashMap<Integer, OSProcess>();
        this.os = systemInfo.getOperatingSystem();
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

        List<OSProcess> currentProcesses = os.getProcesses();
        long totalRam = memory.getTotal();
        List<ProcessInfo> processList = new ArrayList<>();

        for (OSProcess process : currentProcesses) {
            OSProcess prev = processMap.get(process.getProcessID());

            double cpu = prev != null ? process.getProcessCpuLoadBetweenTicks(prev) * 100 : 0.0;
            double mem = (double) process.getResidentMemory() / totalRam * 100;
            Long shr = process.getResidentMemory() - process.getPrivateResidentMemory();

            processList.add(new ProcessInfo(
                    process.getProcessID(),
                    process.getUser(),
                    process.getPriority(),
                    process.getPriority(),
                    process.getVirtualSize(),
                    process.getResidentMemory(),
                    shr,
                    stateChar(process.getState()),
                    cpu,
                    mem,
                    process.getUserTime() + process.getKernelTime(),
                    process.getName()
            ));

            processMap.put(process.getProcessID(), process);
        }

        processList.sort((a, b) -> Double.compare(b.cpuUsage(), a.cpuUsage()));

        SystemSnapshot updatedSystemSnapshot = new SystemSnapshot(
                updatedCpuLoadBetweenTicks,
                null,
                updatedTimestamp,
                ram,
                swap,
                processList);

        latestSnapshot.set(updatedSystemSnapshot);
    }

    private char stateChar(OSProcess.State state) {
        return switch (state) {
            case RUNNING -> 'R';
            case SLEEPING -> 'S';
            case WAITING -> 'W';
            case ZOMBIE -> 'Z';
            case STOPPED -> 'T';
            default -> '?';
        };
    }

    public SystemSnapshot getLatestSnapshot() {
        return latestSnapshot.get();
    }
}
