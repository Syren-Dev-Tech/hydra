package com.github.syren_dev_tech.hydra.animatable;

import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.instance.AnimatableInstanceCache;
import com.github.syren_dev_tech.hydra.animatable.instance.SingletonAnimatableInstanceCache;
import com.github.syren_dev_tech.hydra.animation.AnimatableManager;
import com.github.syren_dev_tech.hydra.animation.ContextAwareAnimatableManager;
import com.github.syren_dev_tech.hydra.cache.AnimatableIdCache;
import com.github.syren_dev_tech.hydra.constant.DataTickets;
import com.github.syren_dev_tech.hydra.util.ItemStackAnimatableIdHelper;
import com.github.syren_dev_tech.hydra.util.RenderUtil;

import java.util.EnumMap;
import java.util.Map;

/**
 * The {@link HydraAnimatable HydraAnimatable} interface specific to {@link net.minecraft.world.item.Item Items}
 * <p>
 * This also applies to armor, as they are just items too.
 *
 * @see <a href="https://github.com/bernie-g/hydra/wiki/Item-Animations">Hydra Wiki - Item Animations</a>
 * @see <a href="https://github.com/bernie-g/hydra/wiki/Armor-Animations">Hydra Wiki - Armor Animations</a>
 */
public interface HydraItem extends SingletonHydraAnimatable {
    /**
     * Register this as a synched {@code HydraAnimatable} instance with Hydra's networking functions
     * <p>
     * This should be called inside the constructor of your object.
     */
    static void registerSyncedAnimatable(HydraAnimatable animatable) {
        SingletonHydraAnimatable.registerSyncedAnimatable(animatable);
    }

    /**
     * Gets the unique identifying number from this ItemStack's NBT, or {@link Long#MAX_VALUE} if one hasn't been assigned
     */
    static long getId(ItemStack stack) {
        return ItemStackAnimatableIdHelper.getId(stack);
    }

    /**
     * Gets the unique identifying number from this ItemStack's NBT
     * <p>
     * If no ID has been reserved for this stack yet, it will reserve a new id and assign it
     */
    static long getOrAssignId(ItemStack stack, ServerLevel level) {
        long id = ItemStackAnimatableIdHelper.getId(stack);
        
        if (id == Long.MAX_VALUE) {
            id = AnimatableIdCache.getFreeId(level);
            ItemStackAnimatableIdHelper.setId(stack, id);
        }
        
        return id;
    }

    /**
     * Returns the current age/tick of the animatable instance
     * <p>
     * By default this is just the animatable's age in ticks, but this method allows for non-ticking custom animatables to provide their own values
     *
     * @param itemStack The ItemStack representing this animatable
     * @return The current tick/age of the animatable, for animation purposes
     */
    @Override
    default double getTick(Object itemStack) {
        return RenderUtil.getCurrentTick();
    }

    /**
     * Whether this item animatable is perspective-aware, handling animations differently depending on the {@link ItemDisplayContext render perspective}
     */
    default boolean isPerspectiveAware() {
        return false;
    }

    /**
     * Replaces the default AnimatableInstanceCache for HydraItems if {@link HydraItem#isPerspectiveAware()} is true, for perspective-dependent handling
     */
    @Nullable
    @Override
    default AnimatableInstanceCache animatableCacheOverride() {
        if (isPerspectiveAware())
            return new ContextBasedAnimatableInstanceCache(this);

        return SingletonHydraAnimatable.super.animatableCacheOverride();
    }

    /**
     * AnimatableInstanceCache specific to HydraItems, for doing render perspective based animations
     * <p>
     * You should <b><u>NOT</u></b> be instantiating this directly unless you know what you are doing. Use {@link com.github.syren_dev_tech.hydra.util.HydraUtil#createInstanceCache HydraUtil.createInstanceCache} instead
     */
    class ContextBasedAnimatableInstanceCache extends SingletonAnimatableInstanceCache {
        public ContextBasedAnimatableInstanceCache(HydraAnimatable animatable) {
            super(animatable);
        }

        /**
         * Gets an {@link AnimatableManager} instance from this cache, cached under the id provided, or a new one if one doesn't already exist
         * <p>
         * This subclass assumes that all animatable instances will be sharing this cache instance, and so differentiates data by ids
         */
        @Override
        public AnimatableManager<?> getManagerForId(long uniqueId) {
            if (!this.managers.containsKey(uniqueId))
                this.managers.put(uniqueId, new ContextAwareAnimatableManager<HydraItem, ItemDisplayContext>(this.animatable) {
                    @Override
                    protected Map<ItemDisplayContext, AnimatableManager<HydraItem>> buildContextOptions(HydraAnimatable animatable) {
                        Map<ItemDisplayContext, AnimatableManager<HydraItem>> map = new EnumMap<>(ItemDisplayContext.class);

                        for (ItemDisplayContext context : ItemDisplayContext.values()) {
                            map.put(context, new AnimatableManager<>(animatable));
                        }

                        return map;
                    }

                    @Override
                    public ItemDisplayContext getCurrentContext() {
                        ItemDisplayContext context = getData(DataTickets.ITEM_RENDER_PERSPECTIVE);

                        return context == null ? ItemDisplayContext.NONE : context;
                    }
                });

            return this.managers.get(uniqueId);
        }
    }
}
