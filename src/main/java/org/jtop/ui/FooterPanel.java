package org.jtop.ui;

import static dev.tamboui.toolkit.Toolkit.column;
import static dev.tamboui.toolkit.Toolkit.panel;
import static dev.tamboui.toolkit.Toolkit.row;
import static dev.tamboui.toolkit.Toolkit.spacer;
import static dev.tamboui.toolkit.Toolkit.text;

import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.toolkit.element.Element;

public class FooterPanel {

	private static final int PROC_TAB_INDEX = 0;
	private static final int NET_TAB_INDEX = 2;

	public Element render(int activeTab, boolean ioView, boolean sortActive) {
		String left;
		if (sortActive) {
			left = "[\u2190] Prev  [\u2192] Next  [s] Toggle  [ESC] Cancel";
		} else {
			left = "[t] Sort";
		}

		if (activeTab == PROC_TAB_INDEX) {
			String toggleLabel = ioView ? "[i] Toggle CPU view" : "[i] Toggle I/O view";
			return panel(
					"CONTROLS",
					column(
							row(
									text(left)
											.style(Style.EMPTY.fg(Color.WHITE).bg(Color.DARK_GRAY)),
									spacer(),
									text(toggleLabel)
											.style(Style.EMPTY.fg(Color.WHITE).bg(Color.DARK_GRAY)),
									spacer(),
									text("[q] Quit")
											.style(Style.EMPTY.fg(Color.WHITE).bg(Color.DARK_GRAY))
							).bg(Color.DARK_GRAY)
					)
			);
		}
		if (activeTab == NET_TAB_INDEX) {
			return panel(
					"CONTROLS",
					column(
							row(
									text(left)
											.style(Style.EMPTY.fg(Color.WHITE).bg(Color.DARK_GRAY)),
									spacer(),
									text("[p]Physical  [l]Loopback  [v]Virtual  [n]VPN")
											.style(Style.EMPTY.fg(Color.WHITE).bg(Color.DARK_GRAY)),
									spacer(),
									text("[q] Quit")
											.style(Style.EMPTY.fg(Color.WHITE).bg(Color.DARK_GRAY))
							).bg(Color.DARK_GRAY)
					)
			);
		}
		return panel(
				"CONTROLS",
				column(
						row(
								text(left)
										.style(Style.EMPTY.fg(Color.WHITE).bg(Color.DARK_GRAY)),
								spacer(),
								text("[q] Quit")
										.style(Style.EMPTY.fg(Color.WHITE).bg(Color.DARK_GRAY))
						).bg(Color.DARK_GRAY)
				)
		);
	}

}
