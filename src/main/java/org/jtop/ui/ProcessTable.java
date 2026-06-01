package org.jtop.ui;

import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.toolkit.element.RenderContext;
import dev.tamboui.toolkit.element.Size;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.widgets.table.Cell;
import dev.tamboui.widgets.table.Row;
import dev.tamboui.widgets.table.Table;
import dev.tamboui.widgets.table.TableState;
import java.util.ArrayList;
import java.util.List;
import org.jtop.model.ProcessInfo;
import org.jtop.utils.FormatUtils;

public class ProcessTable
		extends StyledElement<ProcessTable> {

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

		List<ProcessInfo> sorted = ioView ? processes.stream().sorted((a, b) -> Long.compare(
				b.diskReadBytesPerSec() + b.diskWriteBytesPerSec(),
				a.diskReadBytesPerSec() + a.diskWriteBytesPerSec()
		)).toList() : processes;

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
			int fillWidth = Math.max(7, area.width() - (7 + 11 + 12 + 12 + 2 + 6 + 6));
			table = Table.builder().columnSpacing(2).header(Row.from(
					headerCell("PID", 7),
					headerCell("USER", 11),
					headerCell("DISK READ", 12),
					headerCell("DISK WRITE", 12),
					headerCell("S", 2),
					headerCell("CPU%", 6),
					headerCell("MEM%", 6),
					headerCell("COMMAND", fillWidth)
			).style(Style.EMPTY.bg(Color.DARK_GRAY))).rows(rows).widths(
					Constraint.length(7),
					Constraint.length(11),
					Constraint.length(12),
					Constraint.length(12),
					Constraint.length(2),
					Constraint.length(6),
					Constraint.length(6),
					Constraint.fill()
			).build();
		} else {
			int fillWidth = Math.max(7, area.width() - (7 + 11 + 5 + 5 + 8 + 8 + 8 + 3 + 7 + 7 + 10));
			table = Table.builder().columnSpacing(2).header(Row.from(
					headerCell("PID", 7),
					headerCell("USER", 11),
					headerCell("PRI", 5),
					headerCell("NI", 5),
					headerCell("VIRT", 8),
					headerCell("RES", 8),
					headerCell("SHR", 8),
					headerCell("S", 3),
					headerCell("CPU%", 7),
					headerCell("MEM%", 7),
					headerCell("TIME+", 10),
					headerCell("COMMAND", fillWidth)
			).style(Style.EMPTY.bg(Color.DARK_GRAY))).rows(rows).widths(
					Constraint.length(7),
					Constraint.length(11),
					Constraint.length(5),
					Constraint.length(5),
					Constraint.length(8),
					Constraint.length(8),
					Constraint.length(8),
					Constraint.length(3),
					Constraint.length(7),
					Constraint.length(7),
					Constraint.length(10),
					Constraint.fill()
			).build();
		}

		table.render(area, frame.buffer(), tableState);
	}

	private static Cell headerCell(String text, int width) {
		String padded = String.format("%-" + width + "s", text);
		return Cell.from(padded).style(Style.EMPTY.bold().bg(Color.DARK_GRAY).fg(Color.WHITE));
	}
}
