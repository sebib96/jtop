package org.jtop;

import dev.tamboui.toolkit.app.ToolkitApp;
import dev.tamboui.toolkit.element.Element;
import org.jtop.model.SystemSnapshot;
import org.jtop.service.DataSource;
import org.jtop.service.MockMonitor;
import org.jtop.service.SystemMonitor;
import org.jtop.ui.CpuPanel;

import java.util.Arrays;

import static dev.tamboui.toolkit.Toolkit.*;

public class Main extends ToolkitApp {
    private static DataSource dataSource;
    private static CpuPanel cpuPanel;

    @Override
    protected Element render() {
        SystemSnapshot snapshot = dataSource.getLatestSnapshot();

        if (snapshot == null) {
            return text("Loading...");
        }
        return cpuPanel.render(snapshot);
    }

    public static void main(String[] args) throws Exception {
        dataSource = Arrays.asList(args).contains("--test")
                ? new MockMonitor(Runtime.getRuntime().availableProcessors())
                : new SystemMonitor();

        dataSource.startPolling();
        cpuPanel = new CpuPanel();

        new Main().run();
    }
}
