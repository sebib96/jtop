package org.jtop.ui;

import dev.tamboui.toolkit.element.Element;
import org.jtop.model.SystemSnapshot;

import static dev.tamboui.toolkit.Toolkit.*;

public class CpuPanel {

    public Element render(SystemSnapshot systemSnapshot) {
        int cores = systemSnapshot.cpuLoadPerCore().length;
        int half = cores / 2;
        Element[] left = new Element[half];
        Element[] right = new Element[cores - half];

        for( int i = 0; i < half; i++ ) {
            left[i] = row(
                    text(String.format("CPU%02d[", (i + 1))),
                    gauge(systemSnapshot.cpuLoadPerCore()[i])
                            .label(String.format("%5.1f%%", systemSnapshot.cpuLoadPerCore()[i] * 100)+ "]")
            );
        }

        for ( int i = half; i < cores; i++ ) {
            right[i - half] = row(
                    text(String.format("CPU%02d[", (i + 1))),
                    gauge(systemSnapshot.cpuLoadPerCore()[i])
                            .label(String.format("%5.1f%%", systemSnapshot.cpuLoadPerCore()[i] * 100) + "]")
            );
        }

        return panel("CPU", row(column(left), spacer(), column(right))).rounded();
    }

}
