package org.jtop.model;

public record DiskInfo(String name, long readBytesPerSec, long writeBytesPerSec,
                       long totalReadBytes, long totalWriteBytes) {}
