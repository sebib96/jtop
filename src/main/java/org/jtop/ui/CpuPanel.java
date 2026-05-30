package org.jtop.ui;

import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.toolkit.element.Element;
import org.jtop.model.SystemSnapshot;

import static dev.tamboui.toolkit.Toolkit.row;
import static dev.tamboui.toolkit.Toolkit.text;
import static dev.tamboui.toolkit.Toolkit.panel;
import static dev.tamboui.toolkit.Toolkit.column;

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
                            new CpuGauge(load, String.format("%5.1f%%", load * 100))
                                    .gaugeStyle(Style.EMPTY.fg(Color.GREEN))
                                    .fill(),
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
