package org.jtop.ui;

import static dev.tamboui.toolkit.Toolkit.column;
import static dev.tamboui.toolkit.Toolkit.formField;
import static dev.tamboui.toolkit.Toolkit.panel;
import static dev.tamboui.toolkit.Toolkit.row;

import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.elements.Column;
import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.widgets.form.BooleanFieldState;
import java.util.List;
import org.jtop.model.NetworkInfo;
import org.jtop.model.SystemSnapshot;
import org.jtop.ui.components.NetworkTable;

public class NetPanel {

	private final BooleanFieldState showPhysical = new BooleanFieldState(true);
	private final BooleanFieldState showLoopback = new BooleanFieldState(false);
	private final BooleanFieldState showVirtual = new BooleanFieldState(false);
	private final BooleanFieldState showVPN = new BooleanFieldState(true);

	private final NetworkTable networkTable = new NetworkTable();


	public Element render(SystemSnapshot snapshot) {
		List<NetworkInfo> filtered =
				snapshot.networkInfos().stream().filter(n -> switch (n.type()) {
			case "physical" -> showPhysical.value();
			case "loopback" -> showLoopback.value();
			case "virtual" -> showVirtual.value();
			case "vpn" -> showVPN.value();
			default -> true;
		}).toList();

		networkTable.update(filtered);
		Column netColumn = column(
				row(
						formField("PHYSICAL", showPhysical),
						formField("LOOPBACK", showLoopback),
						formField("VIRTUAL", showVirtual),
						formField("VPN", showVPN)
				), panel("NET", networkTable).rounded().fill()
		).fill();

		return netColumn.onKeyEvent(event -> {
			if (event.isChar('p')) {
				showPhysical.toggle();
				return EventResult.HANDLED;
			}
			if (event.isChar('l')) {
				showLoopback.toggle();
				return EventResult.HANDLED;
			}
			if (event.isChar('v')) {
				showVirtual.toggle();
				return EventResult.HANDLED;
			}
			if (event.isChar('n')) {
				showVPN.toggle();
				return EventResult.HANDLED;
			}
			return EventResult.UNHANDLED;
		});
	}

	public void navigateUp()   { networkTable.navigateUp(); }
	public void navigateDown() { networkTable.navigateDown(); }
	public void deselect()     { networkTable.deselect(); }
	public void resetSort()    { networkTable.resetSort(); }
	public boolean isSortActive() { return networkTable.isSortActive(); }
	public void activateSort()   { networkTable.activateSort(); }
}
