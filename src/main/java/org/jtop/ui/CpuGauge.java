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
import dev.tamboui.widgets.gauge.Gauge;

public class CpuGauge extends StyledElement<CpuGauge> {
    private final double ratio;
    private final String labelText;
    private Style gaugeStyle = Style.EMPTY;

    public CpuGauge(double ratio, String labelText) {
        this.ratio = ratio;
        this.labelText = labelText;
    }

    public CpuGauge gaugeStyle(Style gaugeStyle) {
        this.gaugeStyle = gaugeStyle;
        return this;
    }

    @Override
    public Size preferredSize(int availableWidth, int availableHeight, RenderContext context) {
        return Size.of(labelText.length() + 10, 1);
    }

    @Override
    protected void renderContent(Frame frame, Rect area, RenderContext context) {
        int gaugeWidth = area.width();
        int fillWidth = (int)(gaugeWidth * ratio);
        int labelStart = (gaugeWidth - labelText.length()) / 2;

        Span[] spans = new Span[labelText.length()];

        for (int i = 0; i < labelText.length(); i++) {
            String ch = String.valueOf(labelText.charAt(i));
            Style charStyle = (labelStart + i) < fillWidth
                    ? Style.EMPTY.fg(Color.BLACK).bg(Color.GREEN)
                    : Style.EMPTY.fg(Color.WHITE);
            spans[i] = Span.styled(ch, charStyle);
        }

        frame.renderWidget(
                Gauge.builder()
                        .ratio(ratio)
                        .gaugeStyle(gaugeStyle)
                        .style(context.currentStyle())
                        .label(Line.from(spans))
                        .build(),
                area
        );
    }
}
