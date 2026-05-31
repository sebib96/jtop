package org.jtop.ui;

import static dev.tamboui.toolkit.Toolkit.column;
import static dev.tamboui.toolkit.Toolkit.panel;
import static dev.tamboui.toolkit.Toolkit.row;
import static dev.tamboui.toolkit.Toolkit.text;
import static org.jtop.utils.FormatUtils.upTimeToString;

import dev.tamboui.layout.Padding;
import dev.tamboui.style.Color;
import dev.tamboui.toolkit.element.Element;
import org.jtop.model.ProcessInfo;
import org.jtop.model.SystemSnapshot;

public class HeaderPanel {

	public Element render(SystemSnapshot systemSnapshot) {
		String uptime = upTimeToString(systemSnapshot.upTimeSeconds());
		double[] loadAverage = systemSnapshot.loadAverage();
		int running = 0, sleeping = 0, zombie = 0;
		String loadStr = loadAverage[0] < 0 ? "Load: N/A" : String.format(
				"Load: %.2f %.2f %.2f",
				loadAverage[0],
				loadAverage[1],
				loadAverage[2]
		);

		for (ProcessInfo p : systemSnapshot.processes()) {
			if (p.state() == 'R') {
				running++;
			}
			if (p.state() == 'S') {
				sleeping++;
			}
			if (p.state() == 'Z') {
				zombie++;
			}

		}
		return panel(
				"INFO", column(
						text("Uptime: " + uptime + "     " + loadStr), row(
								text("Tasks: " + systemSnapshot.processes().size() + "   ").fg(Color.WHITE),
								text("Running: " + running + "   ").fg(Color.GREEN),
								text("Sleeping: " + sleeping + "   ").fg(Color.BLUE),
								text("Zombie: " + zombie).fg(Color.RED)
						)
				)
		).rounded().padding(new Padding(0, 1, 0, 1));
	}
}
