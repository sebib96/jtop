package org.jtop.model;

import java.util.List;

public record SystemSnapshot(
    double[] cpuLoadPerCore,
    Double[] cpuTempPerCore,
    long timestamp,
    MemoryInfo ram,
    MemoryInfo swap,
    List<ProcessInfo> processes,
    long upTimeSeconds,
    double[] loadAverage,
    String hostname,
    String osInfo,
    int architecture,
    List<DiskInfo> diskInfos,
    List<NetworkInfo> networkInfos
) {}
