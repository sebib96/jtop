package org.jtop;

import static dev.tamboui.toolkit.Toolkit.column;
import static dev.tamboui.toolkit.Toolkit.panel;
import static dev.tamboui.toolkit.Toolkit.row;
import static dev.tamboui.toolkit.Toolkit.spacer;
import static dev.tamboui.toolkit.Toolkit.text;

import dev.tamboui.toolkit.app.ToolkitApp;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.elements.Column;
import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.widgets.tabs.TabsState;
import java.util.Arrays;
import org.jtop.model.SystemSnapshot;
import org.jtop.service.DataSource;
import org.jtop.service.MockMonitor;
import org.jtop.service.SystemMonitor;
import org.jtop.ui.CpuPanel;
import org.jtop.ui.DiskPanel;
import org.jtop.ui.FooterPanel;
import org.jtop.ui.HeaderPanel;
import org.jtop.ui.MemoryPanel;
import org.jtop.ui.NetPanel;
import org.jtop.ui.components.ProcessTable;
import org.jtop.ui.SystemPanel;
import org.jtop.ui.components.TabBar;

public class Main
		extends ToolkitApp {

	private static DataSource dataSource;
	private static CpuPanel cpuPanel;
	private static MemoryPanel memoryPanel;
	private static HeaderPanel headerPanel;
	private static SystemPanel systemPanel;
	private static DiskPanel diskPanel;
	private static NetPanel netPanel;
	private static FooterPanel footerPanel;
	private final TabsState tabsState = new TabsState(0);
	private boolean ioProcessView = false;

	@Override
	protected Element render() {
		SystemSnapshot snapshot = dataSource.getLatestSnapshot();

		if (snapshot == null) {
			return text("Loading...");
		}

		int selected = tabsState.selected() != null ? tabsState.selected() : 0;
		Element tabContent = switch (selected) {
			case 1 -> diskPanel.render(snapshot);
			case 2 -> netPanel.render(snapshot);
			default ->
					panel("PROC", new ProcessTable(snapshot.processes(), ioProcessView)).rounded().fill();
		};

		Column mainContent = column(
				cpuPanel.render(snapshot),
				row(
						memoryPanel.render(snapshot),
						spacer(),
						row(
								headerPanel.render(snapshot),
								systemPanel.render(snapshot)
						)
				), new TabBar(tabsState), tabContent,
				footerPanel.render(selected, ioProcessView)
		).fill();

		return mainContent.onKeyEvent(event -> {
			if (event.isChar('1')) {
				tabsState.select(0);
				return EventResult.HANDLED;
			}
			if (event.isChar('2')) {
				tabsState.select(1);
				return EventResult.HANDLED;
			}
			if (event.isChar('3')) {
				tabsState.select(2);
				return EventResult.HANDLED;
			}
			if (event.isChar('i')) {
				ioProcessView = !ioProcessView;
				return EventResult.HANDLED;
			}
			return EventResult.UNHANDLED;
		});
	}

	public static void main(String[] args) throws Exception {
		dataSource = Arrays.asList(args).contains("--test")
								 ? new MockMonitor(Runtime.getRuntime().availableProcessors())
								 : new SystemMonitor();

		dataSource.startPolling();
		cpuPanel = new CpuPanel();
		memoryPanel = new MemoryPanel();
		headerPanel = new HeaderPanel();
		systemPanel = new SystemPanel();
		diskPanel = new DiskPanel();
		netPanel = new NetPanel();
		footerPanel = new FooterPanel();

		new Main().run();
	}
}
