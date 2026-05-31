package org.jtop;

import static dev.tamboui.toolkit.Toolkit.column;
import static dev.tamboui.toolkit.Toolkit.panel;
import static dev.tamboui.toolkit.Toolkit.row;
import static dev.tamboui.toolkit.Toolkit.spacer;
import static dev.tamboui.toolkit.Toolkit.text;

import dev.tamboui.toolkit.app.ToolkitApp;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.event.EventResult;
import java.util.Arrays;
import org.jtop.model.SystemSnapshot;
import org.jtop.service.DataSource;
import org.jtop.service.MockMonitor;
import org.jtop.service.SystemMonitor;
import org.jtop.ui.CpuPanel;
import org.jtop.ui.HeaderPanel;
import org.jtop.ui.MemoryPanel;
import org.jtop.ui.ProcessTable;
import org.jtop.ui.SystemPanel;

public class Main extends ToolkitApp {

    private static DataSource dataSource;
    private static CpuPanel cpuPanel;
    private static MemoryPanel memoryPanel;
    private static HeaderPanel headerPanel;
    private static SystemPanel systemPanel;
    private int activeTab = 0;

    @Override
    protected Element render() {
        SystemSnapshot snapshot = dataSource.getLatestSnapshot();

        if (snapshot == null) {
            return text("Loading...");
        }

        Element tabContent = activeTab == 0
            ? panel("PROC", new ProcessTable(snapshot.processes())).rounded().fill()
            : text("I/O — coming soon").fill();

        var mainContent = column(cpuPanel.render(snapshot), row(memoryPanel.render(snapshot), spacer(),
            row(headerPanel.render(snapshot), systemPanel.render(snapshot))), tabContent).fill();

        return mainContent.onKeyEvent(event -> {
            if (event.isChar('1')) {
                activeTab = 0;
                return EventResult.HANDLED;
            }
            if (event.isChar('2')) {
                activeTab = 1;
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

        new Main().run();
    }
}
