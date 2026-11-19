package io.github.floatingpointmc.client.nextgen.module.impl.combat;

import io.github.floatingpointmc.client.nextgen.module.Category;
import io.github.floatingpointmc.client.nextgen.module.Module;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.CheckBox;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.RangeSlider;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.SelectionList;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.Slider;

public class KillAura extends Module {
    private final SelectionList mode = new SelectionList("mode", "single", "Switch", "single", "multi");
    private final Slider range = new Slider("range", 4.2, 3, 8, 0.01);
    private final RangeSlider aps = new RangeSlider("aps", 10, 10, 0.5, 20, 0.1);
    private final SelectionList autoBlocking = new SelectionList("autoblocking", "off", "off", "vanilla", "full");
    private final CheckBox rotation = new CheckBox("rotation", true);
    private final RangeSlider rotationSpeed = new RangeSlider("rotationspeed", 180, 180, rotation::getValue, 10, 180, 10);

    public KillAura() {
        super("killaura", Category.COMBAT);
        addArguments(mode, range, aps, autoBlocking, rotation, rotationSpeed);
    }
}
