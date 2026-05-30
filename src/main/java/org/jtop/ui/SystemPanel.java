package org.jtop.ui;

import static dev.tamboui.toolkit.Toolkit.column;
import static dev.tamboui.toolkit.Toolkit.panel;
import static dev.tamboui.toolkit.Toolkit.row;
import static dev.tamboui.toolkit.Toolkit.text;

import dev.tamboui.layout.Padding;
import dev.tamboui.toolkit.element.Element;
import java.time.LocalDateTime;
import org.jtop.model.SystemSnapshot;

public class SystemPanel {

  public Element render(SystemSnapshot systemSnapshot) {
    String hostname = systemSnapshot.hostname();
    String osInfo = systemSnapshot.osInfo();
    int architecture = systemSnapshot.architecture();

    return panel("SYS",
        row(column(text("OS: " + osInfo), text("ARCH: " + architecture + "-bit")),
            column(text("  HOST: " + hostname), text("  DATE:" + LocalDateTime.now()))))
        .rounded().padding(new Padding(0, 1, 0, 1));
  }
}
