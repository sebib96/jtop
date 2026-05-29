package org.jtop.model;

public record SystemSnapshot(
        double[] cpuLoadPerCore,
        Double[] cpuTempPerCore,
        long timestamp
) {}
