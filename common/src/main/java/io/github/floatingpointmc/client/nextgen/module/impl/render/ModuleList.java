package io.github.floatingpointmc.client.nextgen.module.impl.render;

import io.github.floatingpointmc.client.nextgen.event.impl.entity.EntityTickedEvent;
import io.github.floatingpointmc.client.nextgen.event.impl.render.Render2DEvent;
import io.github.floatingpointmc.client.nextgen.language.LanguageManager;
import io.github.floatingpointmc.client.nextgen.module.Category;
import io.github.floatingpointmc.client.nextgen.module.Module;
import io.github.floatingpointmc.client.nextgen.module.ModuleRegistry;
import io.github.floatingpointmc.client.nextgen.module.argument.Argument;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.CheckBox;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.RangeSlider;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.SelectionList;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.Slider;
import io.github.floatingpointmc.client.nextgen.ui.clickui.Colors;
import io.github.floatingpointmc.client.nextgen.ui.font.FontRegistry;
import io.github.floatingpointmc.client.nextgen.util.math.FunctionUtil;
import io.github.floatingpointmc.client.nextgen.util.math.MathUtil;
import io.github.floatingpointmc.client.nextgen.util.render.BlurUtil;
import io.github.floatingpointmc.client.nextgen.util.render.RenderUtil;
import io.github.vlouboos.standaloneevent.api.EventHandler;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;

public class ModuleList extends Module {
    private static final DecimalFormat formatter = new DecimalFormat("#.####");
    private final CheckBox noRenderModule = new CheckBox("norendermodule", true);
    private ArrayList<RenderEntry> renderModules;

    public ModuleList() {
        super("modulelist", Category.RENDER);
        addArguments(noRenderModule);
        ModuleRegistry.enable(this);
    }

    @Override
    protected void onEnable() {
        if (renderModules == null) {
            renderModules = new ArrayList<>();
            for (Module m : ModuleRegistry.all()) {
                renderModules.add(new RenderEntry(m));
            }
        }
    }

    @EventHandler
    public void onUpdate(@NotNull EntityTickedEvent e) {
        for (RenderEntry renderEntry : renderModules) {
            renderEntry.update();
        }
        renderModules.sort(Comparator.comparing(module -> -module.width));
    }


    @EventHandler
    public void onRender2D(@NotNull Render2DEvent e) {
        if (renderModules == null || !this.isEnabled()) {
            return;
        }
        int currentY = 5;
        for (RenderEntry entry : renderModules) {
            float progress = entry.opacity.calculate() / 255.0f;
            if (progress <= 0) continue;
            int bgHeight = 18;
            int slideOffset = (int) ((1 - progress) * 15);

            int x2 = mc.getWindow().getGuiScaledWidth() - 4 + slideOffset;
            float x1 = x2 - entry.width;
            int y1 = currentY + 1;
            float y2 = y1 + bgHeight;
            int expectedAlpha = Math.max(10, entry.opacity.calculate());
            int bgAlpha = (int) (191 * progress);

            int bgColor = MathUtil.reAlpha(0x0F0F14, bgAlpha);
            int accentColor = MathUtil.reAlpha(Colors.MAIN_THEME, expectedAlpha);
            int nameColor = MathUtil.reAlpha(Colors.PINK, expectedAlpha);
            int modeColor = MathUtil.reAlpha(0x9696A0, expectedAlpha);
            BlurUtil.bloomKawase(e.graphics, 3, 1, RenderUtil.roundedRect(e.graphics, x1, y1, x2, y2, 2, 0xFF000000));
            RenderUtil.drawRoundedRect(e.graphics, x1, y1, x2, y2, 2, bgColor);
            if (x2 > 2) {
                e.graphics.enableScissor(x2 - 2, y1, Math.min(mc.getWindow().getGuiScaledWidth(), x2), y1 + bgHeight);
                RenderUtil.drawRoundedRect(e.graphics, x2 - 4, y1, x2, y2, 2, accentColor);
                e.graphics.disableScissor();
            }

            float textX = x1 + 8;
            float textY = y1 + 1;
            FontRegistry.GEIST_BOLD.drawString(e.graphics, entry.name, 6, textX, textY, nameColor);
            if (entry.suffix != null && !entry.suffix.isEmpty()) {
                FontRegistry.GEIST_BOLD.drawString(e.graphics, entry.suffix, 6, textX + entry.suffixWidth, textY, modeColor);
            }
            currentY += (int) ((bgHeight + 2) * progress);
        }
    }

    @RequiredArgsConstructor
    private class RenderEntry {
        private final Module module;
        private String name;
        private String suffix;
        private float width;
        private float suffixWidth;
        private boolean enabled = false;
        private FunctionUtil<Integer> opacity = FunctionUtil.done(0);

        private void update() {
            name = module.getName();
            if (enabled && (!module.isEnabled() || (module.getCategory().equals(Category.RENDER) && noRenderModule.getValue()))) {
                opacity = FunctionUtil.linear(255, 0, 200);
                enabled = false;
            } else if (!enabled && (module.isEnabled() && (!module.getCategory().equals(Category.RENDER) || !noRenderModule.getValue()))) {
                opacity = FunctionUtil.linear(0, 255, 200);
                enabled = true;
            }
            float moduleWidth = FontRegistry.GEIST_BOLD.width(name, 6) + 16;
            Argument<?> suffix = module.getSuffix();
            if (suffix != null) {
                switch (suffix) {
                    case SelectionList selectionList ->
                            this.suffix = LanguageManager.get("module.%s.%s.%s".formatted(module.getIdentity(), selectionList.identity, selectionList.getValue()));
                    case CheckBox checkBox -> this.suffix = checkBox.getValue() ? "Enabled" : "Disabled";
                    case Slider slider -> this.suffix = formatter.format(slider.getValue());
                    case RangeSlider rangeSlider -> this.suffix = formatter.format(rangeSlider.getMinValue()) + "-" + formatter.format(rangeSlider.getMaxValue());
                    default -> {
                    }
                }
                suffixWidth = moduleWidth - 11;
                moduleWidth += FontRegistry.GEIST_BOLD.width(this.suffix, 6) + 6;
            } else suffixWidth = 0;
            width = moduleWidth;
        }
    }
}