package io.github.floatingpointmc.client.nextgen.event.impl.player;

import io.github.vlouboos.standaloneevent.api.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PreMotionEvent extends Event {
    public double x, y, z;
    public float yaw, pitch;
    public boolean onGround;
}
