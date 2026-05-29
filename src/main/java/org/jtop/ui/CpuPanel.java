package org.jtop.ui;

import dev.tamboui.toolkit.element.Element;
import org.jtop.model.SystemSnapshot;

import static dev.tamboui.toolkit.Toolkit.*;

public class CpuPanel {

    public Element render(SystemSnapshot systemSnapshot) {
        int cores = systemSnapshot.cpuLoadPerCore().length;
        int cols = Math.min(cores, 8);
        int rows = (int) Math.ceil((double) cores / cols);
        Element[] rowElements = new Element[rows];

        for( int r = 0; r < rows; r++ ) {
            Element[] slots = new Element[cols];
            for( int c = 0; c < cols; c++ ) {
                int coreIndex = r * cols + c;
                if (coreIndex < cores) {
                    double load = systemSnapshot.cpuLoadPerCore()[coreIndex];
                    slots[c] = row(
                            text(String.format("CPU%02d[", coreIndex + 1)),
                            gauge(load).label(String.format("%5.1f%%", load * 100)),
                            text("]")
                    );
                } else {
                    slots[c] = text("");
                }
            }
            rowElements[r] = row(slots);
        }

        return panel("CPU", column(rowElements)).rounded();
    }

}
