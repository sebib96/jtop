package org.jtop;

import dev.tamboui.toolkit.app.ToolkitApp;
import dev.tamboui.toolkit.element.Element;
import static dev.tamboui.toolkit.Toolkit.*;

public class Main extends ToolkitApp {

    @Override
    protected Element render() {
        return panel("jtop",
            text("jtop is alive! Press 'q' to quit.").bold().green()
        ).rounded();
    }

    public static void main(String[] args) throws Exception {
        new Main().run();
    }
}
