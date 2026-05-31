package org.jtop.ui;

import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Rect;
import dev.tamboui.terminal.Frame;
import dev.tamboui.toolkit.element.RenderContext;
import dev.tamboui.toolkit.element.Size;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.style.Style;
import dev.tamboui.widgets.table.Cell;
import dev.tamboui.widgets.table.Row;
import dev.tamboui.widgets.table.Table;
import dev.tamboui.widgets.table.TableState;
import java.util.ArrayList;
import java.util.List;
import org.jtop.model.NetworkInfo;
import org.jtop.utils.FormatUtils;

public class NetworkTable extends StyledElement<NetworkTable> {

  private final List<NetworkInfo> interfaces;
  private final TableState tableState = new TableState();

  public NetworkTable(List<NetworkInfo> interfaces) {
    this.interfaces = interfaces;
  }

  @Override
  public Size preferredSize(int availableWidth, int availableHeight, RenderContext context) {
    return Size.of(availableWidth, availableHeight);
  }

  @Override
  public void renderContent(Frame frame, Rect area, RenderContext context) {
    List<Row> rows = new ArrayList<>();

    for (NetworkInfo net : interfaces) {
      rows.add(Row.from(
          net.name(),
          net.type(),
          FormatUtils.bytesToString(net.receivedBytesPerSec()) + "/s",
          FormatUtils.bytesToString(net.sentBytesPerSec()) + "/s",
          FormatUtils.bytesToString(net.totalReceivedBytes()),
          FormatUtils.bytesToString(net.totalSentBytes())
      ));
    }

    Table table = Table.builder()
        .header(Row.from(h("NAME"), h("TYPE"), h("RECV/s"), h("SENT/s"), h("TOTAL R"), h("TOTAL S")))
        .rows(rows)
        .widths(
            Constraint.length(10),
            Constraint.length(10),
            Constraint.length(10),
            Constraint.length(10),
            Constraint.length(10),
            Constraint.fill()
        )
        .build();

    table.render(area, frame.buffer(), tableState);
  }

  private static Cell h(String text) {
    return Cell.from(text).style(Style.EMPTY.bold());
  }
}
