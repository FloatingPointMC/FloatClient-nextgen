package io.github.floatingpointmc.client.nextgen.module.impl.combat;

import io.github.floatingpointmc.client.nextgen.event.impl.client.TickEvent;
import io.github.floatingpointmc.client.nextgen.module.Category;
import io.github.floatingpointmc.client.nextgen.module.Module;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.CheckBox;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.RangeSlider;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.SelectionList;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.Slider;
import io.github.floatingpointmc.client.nextgen.util.container.Attackable;
import io.github.floatingpointmc.client.nextgen.util.container.Timer;
import io.github.floatingpointmc.client.nextgen.util.math.LocationUtil;
import io.github.floatingpointmc.client.nextgen.util.math.RotationUtil;
import io.github.vlouboos.standaloneevent.api.EventHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPunchPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


public class KillAura extends Module {
    private final SelectionList mode = new SelectionList("mode", "single", "switch", "single", "multi");
    private final SelectionList priority = new SelectionList("priority", "distance", () -> !mode.isCurrent("multi"), "distance", "fov", "health");
    private final Slider range = new Slider("range", 4.2, 3, 8, 0.01);
    private final CheckBox cooldown = new CheckBox("cooldown", true);
    private final RangeSlider aps = new RangeSlider("aps", 10, 10, () -> !cooldown.getValue(), 0.5, 20, 0.1);
    private final SelectionList autoBlocking = new SelectionList("autoblocking", "off", "off", "vanilla", "full");
    private final Slider blockingRange = new Slider("blockingrange", 0, () -> !mode.isCurrent("off"), 0, 3, 0.01);
    private final CheckBox rotation = new CheckBox("rotation", true, () -> !mode.isCurrent("multi"));
    private final RangeSlider rotationSpeed = new RangeSlider("rotationspeed", 180, 180, rotation::getValue, 10, 180, 10);
    private final Timer attackTimer = new Timer();
    private boolean suspendedAttack;
    private @NotNull LinkedList<@NotNull Attackable> targets = new LinkedList<>();

    public KillAura() {
        super("killaura", Category.COMBAT);
        addArguments(mode, range, aps, autoBlocking, rotation, rotationSpeed);
    }

    @EventHandler
    public void onTick(TickEvent e) {
        if (mc.level == null || mc.player == null || mc.player.isSpectator() || mc.player.isHandsBusy() || mc.gameMode == null) return;
        double expansion = range.getValue() + blockingRange.getValue();
        List<Entity> entities = mc.level.getEntities(mc.player, mc.player.getBoundingBox().expandTowards(expansion, expansion, expansion));
        entities.removeIf(entity -> !(entity instanceof LivingEntity));
        ArrayList<Attackable> targets = new ArrayList<>(entities.stream().map(entity -> new Attackable((LivingEntity) entity, LocationUtil.closestVecTo(entity, mc.player.getEyePosition()))).toList());
        if (targets.isEmpty()) {
            this.targets.clear();
            return;
        }
        switch (mode.getValue()) {
            case "switch" -> {
                if (!this.targets.isEmpty()) {
                    this.targets.removeIf(target -> !targets.contains(target));
                    targets.removeIf(this.targets::contains);
                    this.targets.addAll(targets);
                } else {
                    sort(mc.player, targets);
                    this.targets.addAll(targets);
                }
            }
            case "single" -> {
                sort(mc.player, targets);
                this.targets.clear();
                this.targets.add(targets.getFirst());
            }
            case "multi" -> {
                this.targets.clear();
                this.targets.addAll(targets);
            }
        }
        if (suspendedAttack || (cooldown.getValue() && mc.player.getAttackStrengthScale(0.5f) > 0.9f) || (!cooldown.getValue() && attackTimer.isComplete(aps.getValue().longValue()))) {
            while (!this.targets.isEmpty()) {
                Attackable target = this.targets.poll();
                LivingEntity entity = target.target();
                if (mc.player.isBlocking() && !suspendedAttack) {
                    mc.player.connection.send(new ServerboundPlayerActionPacket(
                            ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM,
                            BlockPos.ZERO,
                            Direction.DOWN
                    ));
                    suspendedAttack = true;
                    return;
                }
                suspendedAttack = false;
                mc.player.connection.send(new ServerboundAttackPacket(entity.getId()));
                mc.player.attack(entity);
                mc.player.connection.send(ServerboundPunchPacket.INSTANCE);
                if (mc.player.isUsingItem()) {
                    Vec3 location = target.hitVec().subtract(entity.getX(), entity.getY(), entity.getZ());
                    mc.player.connection.send(new ServerboundInteractPacket(entity.getId(), InteractionHand.MAIN_HAND, location, mc.player.isShiftKeyDown()));
                    mc.player.interactOn(entity, InteractionHand.MAIN_HAND, location);
                    mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
                }
                if (!mode.isCurrent("multi")) break;
            }
        }
    }

    private void sort(@NotNull LocalPlayer self, ArrayList<@NotNull Attackable> targets) {
        targets.sort(
                switch (priority.getValue()) {
                    case "distance" ->
                            Comparator.comparingDouble(target -> target.hitVec().distanceTo(self.getEyePosition()));
                    case "fov" -> Comparator.comparing(target -> RotationUtil.getRotationOffset(target.target()).yaw());
                    default -> Comparator.comparing(t -> t.target().getHealth());
                });
    }
}
