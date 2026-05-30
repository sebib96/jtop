package org.jtop.ui.components;

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
import dev.tamboui.widgets.block.Borders;
import dev.tamboui.widgets.gauge.Gauge;

public class CpuGauge extends StyledElement<CpuGauge> {

  private final double ratio;
  private final String labelText;
  private Style gaugeStyle = Style.EMPTY;
  private Color labelFgColor = Color.BLACK;
  private String title = "";

  public CpuGauge(double ratio, String labelText) {
    this.ratio = ratio;
    this.labelText = labelText;
  }

  public CpuGauge gaugeStyle(Style gaugeStyle) {
    this.gaugeStyle = gaugeStyle;
    return this;
  }

  public CpuGauge labelFgColor(Color color) {
    this.labelFgColor = color;
    return this;
  }

  public CpuGauge title(String title) {
    this.title = title;
    return this;
  }

  @Override
  public Size preferredSize(int availableWidth, int availableHeight, RenderContext context) {
    int height = title.isEmpty() ? 1 : 3;
    return Size.of(labelText.length() + 10, height);
  }

  @Override
  protected void renderContent(Frame frame, Rect area, RenderContext context) {
    int gaugeWidth = area.width();
    int fillWidth = (int) (gaugeWidth * ratio);
    int labelStart = (gaugeWidth - labelText.length()) / 2;

    Span[] spans = new Span[labelText.length()];

    for (int i = 0; i < labelText.length(); i++) {
      String ch = String.valueOf(labelText.charAt(i));
      Color fillColor = gaugeStyle.fg().orElse(Color.GREEN);
      Style charStyle = (labelStart + i) < fillWidth
          ? Style.EMPTY.fg(labelFgColor).bg(fillColor)
          : Style.EMPTY.fg(Color.WHITE);
      spans[i] = Span.styled(ch, charStyle);
    }

    Gauge.Builder builder = Gauge.builder()
        .ratio(ratio)
        .gaugeStyle(gaugeStyle)
        .style(context.currentStyle())
        .label(Line.from(spans));

    if (!title.isEmpty()) {
      builder = builder.block(Block.builder().borders(Borders.ALL).title(title).build());
    }

    frame.renderWidget(builder.build(), area);
  }
}
