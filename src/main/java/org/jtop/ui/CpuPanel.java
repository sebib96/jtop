package org.jtop.ui;

import dev.tamboui.toolkit.element.Element;
import org.jtop.model.SystemSnapshot;

import static dev.tamboui.toolkit.Toolkit.gauge;
import static dev.tamboui.toolkit.Toolkit.panel;

public class CpuPanel {

    public Element render(SystemSnapshot systemSnapshot) {
        int cores = systemSnapshot.cpuLoadPerCore().length;
        Element[] gauges = new Element[cores];

        for( int i = 0; i < cores; i++ ) {
            gauges[i] = gauge(systemSnapshot.cpuLoadPerCore()[i]).label("CPU" + (i + 1));
        }

        return panel("CPU", gauges).rounded();
    }

}
