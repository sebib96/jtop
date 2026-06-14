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
import org.jtop.model.DiskInfo;
import org.jtop.utils.FormatUtils;

public class DiskTable
		extends StyledElement<DiskTable> {

	private static final int COL_SPACING = 2;
	private static final int NUM_COLS = 5;

	private List<DiskInfo> disks = List.of();
	private final TableState tableState = new TableState();
	private int sortColumn = -1;
	private boolean sortAscending = true;

	public DiskTable() {
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

	public void update(List<DiskInfo> disks) {
		if (tableState.selected() != null) return;
		this.disks = disks;
	}

	public void navigateUp() {
		if (tableState.selected() == null) return;
		tableState.selectPrevious();
	}

	public void navigateDown() {
		if (tableState.selected() == null) {
			tableState.select(0);
		} else {
			tableState.selectNext(disks.size());
		}
	}

	private void cycleSortLeft() {
		sortColumn = sortColumn < 0 ? NUM_COLS - 1 : sortColumn;
		sortColumn = sortColumn == 0 ? NUM_COLS - 1 : sortColumn - 1;
		sortAscending = true;
	}

	private void cycleSortRight() {
		sortColumn = sortColumn < 0 ? 0 : sortColumn;
		sortColumn = sortColumn == NUM_COLS - 1 ? 0 : sortColumn + 1;
		sortAscending = true;
	}

	private Comparator<DiskInfo> comparator() {
		Comparator<DiskInfo> c = switch (sortColumn) {
			case 0 -> Comparator.comparing(DiskInfo::name);
			case 1 -> Comparator.comparingLong(DiskInfo::readBytesPerSec);
			case 2 -> Comparator.comparingLong(DiskInfo::writeBytesPerSec);
			case 3 -> Comparator.comparingLong(DiskInfo::totalReadBytes);
			case 4 -> Comparator.comparingLong(DiskInfo::totalWriteBytes);
			default -> (a, b) -> 0;
		};
		return sortAscending ? c : c.reversed();
	}

	@Override
	public Size preferredSize(int availableWidth, int availableHeight, RenderContext context) {
		return Size.of(availableWidth, availableHeight);
	}

	@Override
	public void renderContent(Frame frame, Rect area, RenderContext context) {
		List<DiskInfo> sorted = sortColumn >= 0
				? disks.stream().sorted(comparator()).toList()
				: disks;

		List<Row> rows = new ArrayList<>();
		for (DiskInfo disk : sorted) {
			rows.add(Row.from(
					disk.name(),
					FormatUtils.bytesToString(disk.readBytesPerSec()) + "/s",
					FormatUtils.bytesToString(disk.writeBytesPerSec()) + "/s",
					FormatUtils.bytesToString(disk.totalReadBytes()),
					FormatUtils.bytesToString(disk.totalWriteBytes())
			));
		}

		int fillWidth = Math.max(7, area.width() - (12 + 10 + 10 + 10));

		Table table = Table.builder().columnSpacing(COL_SPACING)
				.highlightStyle(Style.EMPTY.bg(Color.CYAN).fg(Color.BLACK))
				.highlightSymbol("")
				.header(Row.from(
				headerCell("NAME", 12, 0),
				headerCell("READ/s", 10, 1),
				headerCell("WRITE/s", 10, 2),
				headerCell("TOTAL R", 10, 3),
				headerCell("TOTAL W", fillWidth, 4)
		).style(Style.EMPTY.bg(Color.DARK_GRAY))).rows(rows).widths(
				Constraint.length(12),
				Constraint.length(10),
				Constraint.length(10),
				Constraint.length(10),
				Constraint.fill()
		).build();

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
		return Cell.from(display).style(Style.EMPTY.bold().bg(Color.DARK_GRAY).fg(Color.WHITE));
	}

	public void deselect() {
		tableState.clearSelection();
	}
}