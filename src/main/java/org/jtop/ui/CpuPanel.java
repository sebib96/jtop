package org.jtop.ui;

import static dev.tamboui.toolkit.Toolkit.column;
import static dev.tamboui.toolkit.Toolkit.panel;
import static dev.tamboui.toolkit.Toolkit.row;
import static dev.tamboui.toolkit.Toolkit.text;

import dev.tamboui.layout.Padding;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.toolkit.element.Element;
import org.jtop.model.SystemSnapshot;
import org.jtop.ui.components.CpuGauge;

public class CpuPanel {

	private static final int MAX_CORES_PER_ROW = 8;
	private static final Color YELLOW = Color.rgb(255, 215, 0);
	private static final Color ORANGE = Color.rgb(255, 140, 0);

	public Element render(SystemSnapshot systemSnapshot) {
		int cores = systemSnapshot.cpuLoadPerCore().length;
		int cols = Math.min(cores, MAX_CORES_PER_ROW);
		int rows = (int) Math.ceil((double) cores / cols);
		Element[] rowElements = new Element[rows];

		for (int r = 0; r < rows; r++) {
			Element[] slots = new Element[cols];
			for (int c = 0; c < cols; c++) {
				int coreIndex = r * cols + c;
				if (coreIndex < cores) {
					double load = systemSnapshot.cpuLoadPerCore()[coreIndex];
					Color fill = loadColor(load);
					slots[c] = row(
							text(String.format("CPU%02d|", coreIndex + 1)),
							new CpuGauge(load, String.format("%5.1f%%", load * 100)).gaugeStyle(Style.EMPTY.fg(
									fill)).labelFgColor(labelColor(fill)).fill(),
							text("| ")
					);
				} else {
					slots[c] = text("");
				}
			}
			rowElements[r] = row(slots);
		}

		return panel("CPU", column(rowElements)).rounded().bold().padding(new Padding(0, 1, 0, 1));
	}

	private Color loadColor(double load) {
		if (load < 0.25) {
			return Color.GREEN;
		}
		if (load < 0.50) {
			return YELLOW;
		}
		if (load < 0.75) {
			return ORANGE;
		}
		return Color.RED;
	}

	private Color labelColor(Color fillColor) {
		if (fillColor == Color.RED) {
			return Color.WHITE;
		}
		return Color.BLACK;
	}

}
