package org.jtop.ui;

import static dev.tamboui.toolkit.Toolkit.column;
import static dev.tamboui.toolkit.Toolkit.panel;
import static dev.tamboui.toolkit.Toolkit.row;
import static dev.tamboui.toolkit.Toolkit.text;

import dev.tamboui.layout.Padding;
import dev.tamboui.toolkit.element.Element;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.jtop.model.SystemSnapshot;

public class SystemPanel {

    public Element render(SystemSnapshot systemSnapshot) {
        String hostname = systemSnapshot.hostname();
        String osInfo = systemSnapshot.osInfo();
        int architecture = systemSnapshot.architecture();

        ZonedDateTime now = ZonedDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));

        return panel("SYS",
            row(
                column(
                    text("OS: " + osInfo),
                    text("ARCH: " + architecture + "-bit")
                ),
                column(
                    text("  HOST: " + hostname),
                    text("  DATE: " + date)
                )
            )
        ).rounded().padding(new Padding(0, 1, 0, 1));
    }
}
