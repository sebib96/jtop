package org.jtop.model;

public record ProcessInfo(
        int pid,
        String user,
        int priority,
        int nice,
        long virtualSize,
        long residentSetSize,
        Long shr,
        char state,
        double cpuUsage,
        double memoryUsage,
        long cpuTime,
        String command
) {}
