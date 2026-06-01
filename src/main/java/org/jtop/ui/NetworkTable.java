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
import org.jtop.model.NetworkInfo;
import org.jtop.utils.FormatUtils;

public class NetworkTable
		extends StyledElement<NetworkTable> {

	private final List<NetworkInfo> interfaces;
	private final TableState tableState = new TableState();

	public NetworkTable(List<NetworkInfo> interfaces) {
		this.interfaces = interfaces;
	}

	@Override
	public Size preferredSize(int availableWidth, int availableHeight, RenderContext context) {
		return Size.of(availableWidth, availableHeight);
	}

	@Override
	public void renderContent(Frame frame, Rect area, RenderContext context) {
		List<Row> rows = new ArrayList<>();

		for (NetworkInfo net : interfaces) {
			rows.add(Row.from(
					net.name(),
					net.type(),
					FormatUtils.bytesToString(net.receivedBytesPerSec()) + "/s",
					FormatUtils.bytesToString(net.sentBytesPerSec()) + "/s",
					FormatUtils.bytesToString(net.totalReceivedBytes()),
					FormatUtils.bytesToString(net.totalSentBytes())
			));
		}

		int fillWidth = Math.max(7, area.width() - (10 + 10 + 10 + 10 + 10));
		Table table = Table.builder().columnSpacing(2).header(Row.from(
				headerCell("NAME", 10),
				headerCell("TYPE", 10),
				headerCell("RECV/s", 10),
				headerCell("SENT/s", 10),
				headerCell("TOTAL R", 10),
				headerCell("TOTAL S", fillWidth)
		).style(Style.EMPTY.bg(Color.DARK_GRAY))).rows(rows).widths(
				Constraint.length(10),
				Constraint.length(10),
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
}
