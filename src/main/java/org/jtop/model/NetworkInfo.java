package org.jtop.model;

public record NetworkInfo(
    String name,
    String type,
    long receivedBytesPerSec,
    long sentBytesPerSec,
    long totalReceivedBytes,
    long totalSentBytes
) {}
