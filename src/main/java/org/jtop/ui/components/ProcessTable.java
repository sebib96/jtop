package org.jtop.ui.components;

import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.toolkit.element.RenderContext;
import dev.tamboui.toolkit.element.Size;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.widgets.table.Cell;
import dev.tamboui.widgets.table.Row;
import dev.tamboui.widgets.table.Table;
import dev.tamboui.widgets.table.TableState;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.jtop.model.ProcessInfo;
import org.jtop.utils.FormatUtils;

public class ProcessTable
		extends StyledElement<ProcessTable> {

	private static final Style HEADER_STYLE = Style.EMPTY.bold().bg(Color.DARK_GRAY).fg(Color.WHITE);
	private static final Style HIGHLIGHT_STYLE = Style.EMPTY.bg(Color.CYAN).fg(Color.BLACK);
	private static final int COL_SPACING = 2;

	private List<ProcessInfo> processes = List.of();
	private boolean ioView;
	private final TableState tableState = new TableState();
	private int sortColumn = -1;
	private boolean sortAscending = true;

	public ProcessTable() {
		onKeyEvent(event -> {
			if (event.isLeft() && sortColumn >= 0) {
				cycleSortLeft();
				return EventResult.HANDLED;
			}
			if (event.isRight() && sortColumn >= 0) {
				cycleSortRight();
				return EventResult.HANDLED;
			}
			if (event.isChar('s')) {
				if (sortColumn >= 0) sortAscending = !sortAscending;
				return EventResult.HANDLED;
			}
			return EventResult.UNHANDLED;
		});
	}

	public void resetSort() {
		sortColumn = -1;
		sortAscending = true;
	}

	public boolean isSortActive() {
		return sortColumn >= 0;
	}

	public void activateSort() {
		sortColumn = 0;
		sortAscending = false;
	}

	public void update(List<ProcessInfo> processes, boolean ioView) {
		if (tableState.selected() != null) return;
		boolean viewChanged = this.ioView != ioView;
		this.processes = processes;
		this.ioView = ioView;
		if (viewChanged) {
			int maxCol = ioView ? 7 : 11;
			if (sortColumn > maxCol) sortColumn = maxCol;
		}
	}

	private int numCols() {
		return ioView ? 8 : 12;
	}

	private void cycleSortLeft() {
		int n = numCols();
		sortColumn = sortColumn < 0 ? n - 1 : sortColumn;
		sortColumn = sortColumn == 0 ? n - 1 : sortColumn - 1;
		sortAscending = true;
	}

	private void cycleSortRight() {
		int n = numCols();
		sortColumn = sortColumn < 0 ? 0 : sortColumn;
		sortColumn = sortColumn == n - 1 ? 0 : sortColumn + 1;
		sortAscending = true;
	}

	private Comparator<ProcessInfo> comparator() {
		if (sortColumn < 0) {
			if (ioView) {
				return (a, b) -> Long.compare(
						b.diskReadBytesPerSec() + b.diskWriteBytesPerSec(),
						a.diskReadBytesPerSec() + a.diskWriteBytesPerSec()
				);
			}
			return (a, b) -> Double.compare(b.cpuUsage(), a.cpuUsage());
		}

		Comparator<ProcessInfo> c;
		if (ioView) {
			c = switch (sortColumn) {
				case 0 -> Comparator.comparingInt(ProcessInfo::pid);
				case 1 -> Comparator.comparing(ProcessInfo::user);
				case 2 -> Comparator.comparingLong(ProcessInfo::diskReadBytesPerSec);
				case 3 -> Comparator.comparingLong(ProcessInfo::diskWriteBytesPerSec);
				case 4 -> Comparator.comparing(ProcessInfo::state);
				case 5 -> Comparator.comparingDouble(ProcessInfo::cpuUsage);
				case 6 -> Comparator.comparingDouble(ProcessInfo::memoryUsage);
				case 7 -> Comparator.comparing(ProcessInfo::command);
				default -> (a, b) -> 0;
			};
		} else {
			c = switch (sortColumn) {
				case 0 -> Comparator.comparingInt(ProcessInfo::pid);
				case 1 -> Comparator.comparing(ProcessInfo::user);
				case 2 -> Comparator.comparingInt(ProcessInfo::priority);
				case 3 -> Comparator.comparingInt(ProcessInfo::nice);
				case 4 -> Comparator.comparingLong(ProcessInfo::virtualSize);
				case 5 -> Comparator.comparingLong(ProcessInfo::residentSetSize);
				case 6 -> Comparator.comparing(
						ProcessInfo::shr, Comparator.nullsLast(Comparator.naturalOrder()));
				case 7 -> Comparator.comparing(ProcessInfo::state);
				case 8 -> Comparator.comparingDouble(ProcessInfo::cpuUsage);
				case 9 -> Comparator.comparingDouble(ProcessInfo::memoryUsage);
				case 10 -> Comparator.comparingLong(ProcessInfo::cpuTime);
				case 11 -> Comparator.comparing(ProcessInfo::command);
				default -> (a, b) -> 0;
			};
		}
		return sortAscending ? c : c.reversed();
	}

	public void navigateUp() {
		if (tableState.selected() == null) return;
		tableState.selectPrevious();
	}

	public void navigateDown() {
		if (tableState.selected() == null) {
			tableState.select(0);
		} else {
			tableState.selectNext(processes.size());
		}
	}

	@Override
	public Size preferredSize(int availableWidth, int availableHeight, RenderContext context) {
		return Size.of(availableWidth, availableHeight);
	}

	@Override
	public void renderContent(Frame frame, Rect area, RenderContext context) {
		if (sortColumn >= numCols()) sortColumn = numCols() - 1;

		List<ProcessInfo> sorted = processes.stream().sorted(comparator()).toList();

		List<Row> rows = new ArrayList<>();
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

		Row headerRow;
		if (ioView) {
			int fillWidth = Math.max(7, area.width() - (7 + 11 + 12 + 12 + 2 + 6 + 6));
			headerRow = Row.from(
					headerCell("PID", 7, 0),
					headerCell("USER", 11, 1),
					headerCell("DISK READ", 12, 2),
					headerCell("DISK WRITE", 12, 3),
					headerCell("S", 2, 4),
					headerCell("CPU%", 6, 5),
					headerCell("MEM%", 6, 6),
					headerCell("COMMAND", fillWidth, 7)
			).style(Style.EMPTY.bg(Color.DARK_GRAY));
		} else {
			int fillWidth = Math.max(7, area.width() - (7 + 11 + 5 + 5 + 8 + 8 + 8 + 3 + 7 + 7 + 10));
			headerRow = Row.from(
					headerCell("PID", 7, 0),
					headerCell("USER", 11, 1),
					headerCell("PRI", 5, 2),
					headerCell("NI", 5, 3),
					headerCell("VIRT", 8, 4),
					headerCell("RES", 8, 5),
					headerCell("SHR", 8, 6),
					headerCell("S", 3, 7),
					headerCell("CPU%", 7, 8),
					headerCell("MEM%", 7, 9),
					headerCell("TIME+", 10, 10),
					headerCell("COMMAND", fillWidth, 11)
			).style(Style.EMPTY.bg(Color.DARK_GRAY));
		}

		Constraint[] constraints;
		if (ioView) {
			constraints = new Constraint[]{
					Constraint.length(7), Constraint.length(11), Constraint.length(12),
					Constraint.length(12), Constraint.length(2), Constraint.length(6),
					Constraint.length(6), Constraint.fill()
			};
		} else {
			constraints = new Constraint[]{
					Constraint.length(7), Constraint.length(11), Constraint.length(5),
					Constraint.length(5), Constraint.length(8), Constraint.length(8),
					Constraint.length(8), Constraint.length(3), Constraint.length(7),
					Constraint.length(7), Constraint.length(10), Constraint.fill()
			};
		}

		Table table = Table.builder().columnSpacing(COL_SPACING)
				.highlightStyle(HIGHLIGHT_STYLE)
				.highlightSymbol("")
				.header(headerRow).rows(rows).widths(constraints).build();

		table.render(area, frame.buffer(), tableState);
	}

	private Cell headerCell(String text, int width, int colIndex) {
		String display;
		if (colIndex == sortColumn) {
			String indicator = sortAscending ? "\u25B2" : "\u25BC";
			if (text.length() + 2 <= width) {
				display = String.format("%-" + width + "s", text + " " + indicator);
			} else {
				display = String.format("%-" + width + "s", indicator);
			}
		} else {
			display = String.format("%-" + width + "s", text);
		}
		return Cell.from(display).style(HEADER_STYLE);
	}

	public void deselect() {
		tableState.clearSelection();
	}
}