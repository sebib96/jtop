package org.jtop.ui;

import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.text.Line;
import dev.tamboui.text.Span;
import dev.tamboui.toolkit.element.RenderContext;
import dev.tamboui.toolkit.element.Size;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.widgets.block.Block;
import dev.tamboui.widgets.block.BorderType;
import dev.tamboui.widgets.block.Borders;
import dev.tamboui.widgets.tabs.Tabs;
import dev.tamboui.widgets.tabs.TabsState;

public class TabBar extends StyledElement<TabBar> {

  private static final String[] TAB_NAMES = {"PROC", "I/O", "NET"};
  private final TabsState tabsState;

  public TabBar(TabsState tabsState) {
    this.tabsState = tabsState;
  }

  @Override
  public Size preferredSize(int availableWidth, int availableHeight, RenderContext context) {
    return Size.of(availableWidth, 3);
  }

  @Override
  public void renderContent(Frame frame, Rect area, RenderContext context) {
    Line[] tabLines = new Line[TAB_NAMES.length];
    for (int i = 0; i < TAB_NAMES.length; i++) {
      tabLines[i] = Line.from(
          Span.raw(String.valueOf(i + 1)).dim(),
          Span.raw(":"),
          Span.raw(TAB_NAMES[i])
      );
    }

    Tabs tabs = Tabs.builder()
        .titles(tabLines)
        .highlightStyle(Style.EMPTY.fg(Color.GREEN).bold())
        .style(Style.EMPTY.fg(Color.WHITE))
        .divider(Span.raw("  │  ").fg(Color.DARK_GRAY))
        .padding(" ", " ")
        .block(Block.builder()
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .build())
        .build();

    frame.renderStatefulWidget(tabs, area, tabsState);
  }
}
