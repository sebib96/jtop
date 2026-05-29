package org.jtop;

import dev.tamboui.toolkit.app.ToolkitApp;
import dev.tamboui.toolkit.element.Element;
import org.jtop.model.SystemSnapshot;
import org.jtop.service.SystemMonitor;
import org.jtop.ui.CpuPanel;

import static dev.tamboui.toolkit.Toolkit.*;

public class Main extends ToolkitApp {
    private static SystemMonitor systemMonitor;
    private static CpuPanel cpuPanel;

    @Override
    protected Element render() {
        SystemSnapshot snapshot = systemMonitor.getLatestSnapshot();

        if (snapshot == null) {
            return text("Loading...");
        }
        return cpuPanel.render(snapshot);
    }

    public static void main(String[] args) throws Exception {
        systemMonitor = new SystemMonitor();
        cpuPanel = new CpuPanel();

        systemMonitor.startPolling();
        new Main().run();
    }
}
