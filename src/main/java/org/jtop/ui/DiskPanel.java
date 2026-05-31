package org.jtop.ui;

import static dev.tamboui.toolkit.Toolkit.panel;

import dev.tamboui.toolkit.element.Element;
import org.jtop.model.SystemSnapshot;

public class DiskPanel {

  public Element render(SystemSnapshot snapshot) {
    return panel("DISK", new DiskTable(snapshot.diskInfos())).rounded().fill();
  }
}
