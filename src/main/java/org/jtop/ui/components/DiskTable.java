package org.jtop.ui.components;

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
import org.jtop.model.DiskInfo;
import org.jtop.utils.FormatUtils;

public class DiskTable
		extends StyledElement<DiskTable> {

	private List<DiskInfo> disks = List.of();
	private final TableState tableState = new TableState();

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

	@Override
	public Size preferredSize(int availableWidth, int availableHeight, RenderContext context) {
		return Size.of(availableWidth, availableHeight);
	}

	@Override
	public void renderContent(Frame frame, Rect area, RenderContext context) {
		List<Row> rows = new ArrayList<>();

		for (DiskInfo disk : disks) {
			rows.add(Row.from(
					disk.name(),
					FormatUtils.bytesToString(disk.readBytesPerSec()) + "/s",
					FormatUtils.bytesToString(disk.writeBytesPerSec()) + "/s",
					FormatUtils.bytesToString(disk.totalReadBytes()),
					FormatUtils.bytesToString(disk.totalWriteBytes())
			));
		}

		int fillWidth = Math.max(7, area.width() - (12 + 10 + 10 + 10));
		Table table = Table.builder().columnSpacing(2)
				.highlightStyle(Style.EMPTY.bg(Color.CYAN).fg(Color.BLACK))
				.highlightSymbol("")
				.header(Row.from(
				headerCell("NAME", 12),
				headerCell("READ/s", 10),
				headerCell("WRITE/s", 10),
				headerCell("TOTAL R", 10),
				headerCell("TOTAL W", fillWidth)
		).style(Style.EMPTY.bg(Color.DARK_GRAY))).rows(rows).widths(
				Constraint.length(12),
				Constraint.length(10),
				Constraint.length(10),
				Constraint.length(10),
				Constraint.fill()
		).build();

		table.render(area, frame.buffer(), tableState);
	}

	private static Cell headerCell(String text, int width) {
		String padded = String.format("%-" + width + "s", text);
		return Cell.from(padded).style(Style.EMPTY.bold().bg(Color.DARK_GRAY).fg(Color.WHITE));
	}

	public void deselect() {
		tableState.clearSelection();
	}
}
