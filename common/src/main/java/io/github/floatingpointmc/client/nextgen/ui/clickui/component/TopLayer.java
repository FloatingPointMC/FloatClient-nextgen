package io.github.floatingpointmc.client.nextgen.ui.clickui.component;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;

public abstract class TopLayer {
    protected int x, y;

    public void init(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void draw(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY);

    public abstract boolean onMouseClicked(double mouseX, double mouseY, int mouseButton);

    public abstract void finish();

    public abstract boolean finished();
}
