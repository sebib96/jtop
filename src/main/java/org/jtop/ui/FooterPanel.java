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

	public Element render(int activeTab, boolean ioView) {
		if (activeTab == PROC_TAB_INDEX) {
			if (!ioView) {
				return panel(
						"CONTROLS",
						column(
								row(
										text("[i] Toggle I/O view")
												.style(Style.EMPTY.fg(Color.WHITE).bg(Color.DARK_GRAY)
												),
										spacer(),
										row(
												text("[q] Quit")
														.style(Style.EMPTY.fg(Color.WHITE).bg(Color.DARK_GRAY))
										)
								).bg(Color.DARK_GRAY)
						)
				);
			} else  {
				return panel(
						"CONTROLS",
						column(
								row(
								text("[i] Toggle CPU view")
										.style(Style.EMPTY.fg(Color.WHITE).bg(Color.DARK_GRAY)
										),
								spacer(),
								row(
										text("[q] Quit")
												.style(Style.EMPTY.fg(Color.WHITE).bg(Color.DARK_GRAY))
								)
								).bg(Color.DARK_GRAY)
						)
				);
			}
		} else if (activeTab == NET_TAB_INDEX) {
			return panel(
					"CONTROLS",
					column(
							row(
							text("[p]Physical  [l]Loopback  [v]Virtual  [n]VPN")
									.style(Style.EMPTY.fg(Color.WHITE).bg(Color.DARK_GRAY)
							),
							spacer(),
							row(
									text("[q] Quit")
											.style(Style.EMPTY.fg(Color.WHITE).bg(Color.DARK_GRAY))
							)
							)
					)
			).bg(Color.DARK_GRAY);
		}
		return text("");
	}

}
