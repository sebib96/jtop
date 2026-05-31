package org.jtop.ui;

import static dev.tamboui.toolkit.Toolkit.column;
import static dev.tamboui.toolkit.Toolkit.formField;
import static dev.tamboui.toolkit.Toolkit.panel;
import static dev.tamboui.toolkit.Toolkit.row;

import dev.tamboui.toolkit.element.Element;
import dev.tamboui.widgets.form.BooleanFieldState;
import java.util.List;
import org.jtop.model.NetworkInfo;
import org.jtop.model.SystemSnapshot;

public class NetPanel {

	private final BooleanFieldState showPhysical = new BooleanFieldState(true);
	private final BooleanFieldState showLoopback = new BooleanFieldState(false);
	private final BooleanFieldState showVirtual = new BooleanFieldState(false);
	private final BooleanFieldState showVPN = new BooleanFieldState(true);

	public Element render(SystemSnapshot snapshot) {
		List<NetworkInfo> filtered =
				snapshot.networkInfos().stream().filter(n -> switch (n.type()) {
			case "physical" -> showPhysical.value();
			case "loopback" -> showLoopback.value();
			case "virtual" -> showVirtual.value();
			case "vpn" -> showVPN.value();
			default -> true;
		}).toList();

		return column(
				row(
						formField("PHYSICAL", showPhysical),
						formField("LOOPBACK", showLoopback),
						formField("VIRTUAL", showVirtual),
						formField("VPN", showVPN)
				), panel("NET", new NetworkTable(filtered)).rounded().fill()
		).fill();
	}
}
