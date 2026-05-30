package org.jtop.ui;

import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.toolkit.element.Element;
import org.jtop.model.MemoryInfo;
import org.jtop.model.SystemSnapshot;
import org.jtop.ui.components.CpuGauge;

import static dev.tamboui.toolkit.Toolkit.*;
import static org.jtop.utils.FormatUtils.bytesToString;

public class MemoryPanel {

    private static final int MEM_PANEL_WIDTH_PERCENT = 25;

    public Element render(SystemSnapshot systemSnapshot) {
        MemoryInfo ram = systemSnapshot.ram();
        MemoryInfo swap = systemSnapshot.swap();
        Element[] rowElements = new Element[2];

        rowElements[0] = row(
                text("RAM["),
                new CpuGauge(
                        ((double) ram.used() / ram.total()),
                        bytesToString(ram.used()) + "/" + bytesToString(ram.total()))
                        .gaugeStyle(Style.EMPTY.fg(Color.GREEN))
                        .fill(),
                text("]"));

        rowElements[1] = row(
                text("SWP["),
                new CpuGauge(
                        ((double)swap.used() / swap.total()),
                        bytesToString(swap.used()) + "/" + bytesToString(swap.total()))
                        .gaugeStyle(Style.EMPTY.fg(Color.BLUE))
                        .fill(),
                text("]"));

        return panel("MEM", column(rowElements)).rounded().percent(MEM_PANEL_WIDTH_PERCENT);
    }
}
