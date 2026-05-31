package org.jtop.ui;

import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Rect;
import dev.tamboui.terminal.Frame;
import dev.tamboui.toolkit.element.RenderContext;
import dev.tamboui.toolkit.element.Size;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.style.Style;
import dev.tamboui.widgets.table.Cell;
import dev.tamboui.widgets.table.Row;
import dev.tamboui.widgets.table.Table;
import dev.tamboui.widgets.table.TableState;
import java.util.ArrayList;
import java.util.List;
import org.jtop.model.ProcessInfo;
import org.jtop.utils.FormatUtils;

public class ProcessTable extends StyledElement<ProcessTable> {

    private final List<ProcessInfo> processes;
    private final boolean ioView;
    private final TableState tableState = new TableState();

    public ProcessTable(List<ProcessInfo> processes, boolean ioView) {
        this.processes = processes;
        this.ioView = ioView;
    }

    @Override
    public Size preferredSize(int availableWidth, int availableHeight, RenderContext context) {
        return Size.of(availableWidth, availableHeight);
    }

    @Override
    public void renderContent(Frame frame, Rect area, RenderContext context) {
        List<Row> rows = new ArrayList<>();

        List<ProcessInfo> sorted = ioView
            ? processes.stream()
                .sorted((a, b) -> Long.compare(
                    b.diskReadBytesPerSec() + b.diskWriteBytesPerSec(),
                    a.diskReadBytesPerSec() + a.diskWriteBytesPerSec()))
                .toList()
            : processes;

        for (ProcessInfo p : sorted) {
            if (ioView) {
                rows.add(Row.from(
                    String.valueOf(p.pid()),
                    p.user(),
                    FormatUtils.bytesToString(p.diskReadBytesPerSec()) + "/s",
                    FormatUtils.bytesToString(p.diskWriteBytesPerSec()) + "/s",
                    String.valueOf(p.state()),
                    String.format("%.1f", p.cpuUsage()),
                    String.format("%.1f", p.memoryUsage()),
                    p.command()
                ));
            } else {
                rows.add(Row.from(
                    String.valueOf(p.pid()),
                    p.user(),
                    String.valueOf(p.priority()),
                    String.valueOf(p.nice()),
                    FormatUtils.bytesToString(p.virtualSize()),
                    FormatUtils.bytesToString(p.residentSetSize()),
                    p.shr() != null ? FormatUtils.bytesToString(p.shr()) : "N/A",
                    String.valueOf(p.state()),
                    String.format("%.1f", p.cpuUsage()),
                    String.format("%.1f", p.memoryUsage()),
                    FormatUtils.cpuTimeToString(p.cpuTime()),
                    p.command()
                ));
            }
        }

        Table table;
        if (ioView) {
            table = Table.builder()
                .header(Row.from(h("PID"), h("USER"), h("DISK READ"), h("DISK WRITE"), h("S"), h("CPU%"), h("MEM%"), h("COMMAND")))
                .rows(rows)
                .widths(
                    Constraint.length(6),
                    Constraint.length(10),
                    Constraint.length(12),
                    Constraint.length(12),
                    Constraint.length(2),
                    Constraint.length(6),
                    Constraint.length(6),
                    Constraint.fill())
                .build();
        } else {
            table = Table.builder()
                .header(Row.from(h("PID"), h("USER"), h("PRI"), h("NI"), h("VIRT"), h("RES"), h("SHR"), h("S"), h("CPU%"), h("MEM%"), h("TIME+"), h("COMMAND")))
                .rows(rows)
                .widths(
                    Constraint.length(6),
                    Constraint.length(10),
                    Constraint.length(4),
                    Constraint.length(4),
                    Constraint.length(7),
                    Constraint.length(7),
                    Constraint.length(7),
                    Constraint.length(2),
                    Constraint.length(6),
                    Constraint.length(6),
                    Constraint.length(9),
                    Constraint.fill())
                .build();
        }

        table.render(area, frame.buffer(), tableState);
    }

    private static Cell h(String text) {
        return Cell.from(text).style(Style.EMPTY.bold());
    }
}
