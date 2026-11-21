package io.github.floatingpointmc.client.nextgen;

import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackMetadataResources;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public final class FloatMetadataResources implements PackMetadataResources {
    @Override
    public @NotNull PackLocationInfo location() {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("all")
    public @Nullable IoSupplier<InputStream> getRootResource(String @NotNull ... path) {
        Path p = Path.of("/assets/floatclient_nextgen", path);
        InputStream in = FloatMetadataResources.class.getResourceAsStream(p.toString().replace('\\', '/'));
        if (in == null) return null;
        return () -> in;
    }

    @Override
    public @Nullable <T> T getMetadataSection(@NotNull MetadataSectionType<T> metadataSerializer) throws IOException {
        return null;
    }

    @Override
    public void close() {

    }
}