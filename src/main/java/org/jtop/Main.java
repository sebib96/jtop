package org.jtop;

import static dev.tamboui.toolkit.Toolkit.column;
import static dev.tamboui.toolkit.Toolkit.panel;
import static dev.tamboui.toolkit.Toolkit.row;
import static dev.tamboui.toolkit.Toolkit.spacer;
import static dev.tamboui.toolkit.Toolkit.text;

import dev.tamboui.layout.Padding;
import dev.tamboui.toolkit.app.ToolkitApp;
import dev.tamboui.toolkit.element.Element;
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

  @Override
  protected Element render() {
    SystemSnapshot snapshot = dataSource.getLatestSnapshot();

    if (snapshot == null) {
      return text("Loading...");
    }
    return column(cpuPanel.render(snapshot), row(memoryPanel.render(snapshot), spacer(),
        row(headerPanel.render(snapshot), systemPanel.render(snapshot))

    ), panel("PROC", new ProcessTable(snapshot.processes())).rounded().fill()
        .padding(new Padding(0, 1, 0, 1)))
        .fill();
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
