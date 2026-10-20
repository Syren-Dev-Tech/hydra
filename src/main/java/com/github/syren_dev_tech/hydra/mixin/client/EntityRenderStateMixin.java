package com.github.syren_dev_tech.hydra.mixin.client;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import com.github.syren_dev_tech.hydra.constant.DataTickets;
import com.github.syren_dev_tech.hydra.constant.dataticket.DataTicket;
import com.github.syren_dev_tech.hydra.renderer.base.GeoRenderState;

import java.util.Map;

/// Duck-typing mixin to apply the [GeoRenderState] duck interface to `EntityRenderStates`
@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements GeoRenderState {
    @Shadow
    public int lightCoords;
    @Shadow
    public float ageInTicks;
    @Unique
    private final Map<DataTicket<?>, Object> hydra$data = new Reference2ObjectOpenHashMap<>();

    @Unique
    @Override
    public <D> void addHydraData(DataTicket<D> dataTicket, D data) {
        this.hydra$data.put(dataTicket, data);
    }

    @Unique
    @Override
    public boolean hasHydraData(DataTicket<?> dataTicket) {
        return this.hydra$data.containsKey(dataTicket);
    }

    @Unique
    @Override
    public int getPackedLight() {
        return getOrDefaultHydraData(DataTickets.PACKED_LIGHT, this.lightCoords);
    }

    @Override
    public double getAnimatableAge() {
        return getOrDefaultHydraData(DataTickets.TICK, (double)this.ageInTicks);
    }

    @ApiStatus.Internal
    @Override
    public Map<DataTicket<?>, Object> getDataMap() {
        return this.hydra$data;
    }
}
