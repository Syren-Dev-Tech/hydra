package com.github.syren_dev_tech.hydra.animatable;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.client.HydraRenderProvider;
import com.github.syren_dev_tech.hydra.animatable.instance.AnimatableInstanceCache;
import com.github.syren_dev_tech.hydra.animatable.instance.SingletonAnimatableInstanceCache;
import com.github.syren_dev_tech.hydra.animation.AnimatableManager;
import com.github.syren_dev_tech.hydra.animation.AnimationController;
import com.github.syren_dev_tech.hydra.constant.dataticket.SerializableDataTicket;
import com.github.syren_dev_tech.hydra.util.HydraUtil;

import java.util.function.Consumer;

/**
 * The {@link HydraAnimatable} interface specific to singleton objects
 * <p>
 * This primarily applies to armor and items
 *
 * @see <a href="https://github.com/bernie-g/hydra/wiki/Item-Animations">Hydra Wiki - Item Animations</a>
 */
public interface SingletonHydraAnimatable extends HydraAnimatable {
    /**
     * Register this as a synched {@code HydraAnimatable} instance with Hydra's networking functions
     * <p>
     * This should be called inside the constructor of your object.
     */
    static void registerSyncedAnimatable(HydraAnimatable animatable) {
        HydraUtil.registerSyncedAnimatable(animatable);
    }

    /**
     * Get server-synced animation data via its relevant {@link SerializableDataTicket}
     * <p>
     * Should only be used on the <u>client-side</u>
     * <p>
     * <b><u>DO NOT OVERRIDE</u></b>
     *
     * @param instanceId The animatable's instance id
     * @param dataTicket The data ticket for the data to retrieve
     * @return The synced data, or null if no data of that type has been synced
     */
    @ApiStatus.NonExtendable
    @Nullable
    default <D> D getAnimData(long instanceId, SerializableDataTicket<D> dataTicket) {
        return getAnimatableInstanceCache().getManagerForId(instanceId).getData(dataTicket);
    }

    /**
     * Saves an arbitrary piece of syncable data to this animatable's {@link AnimatableManager}
     * <p>
     * <b><u>DO NOT OVERRIDE</u></b>
     *
     * @param relatedEntity An entity related to the state of the data for syncing (E.G. The player holding the item)
     * @param instanceId    The unique id that identifies the specific animatable instance
     * @param dataTicket    The DataTicket to sync the data for
     * @param data          The data to sync
     */
    @ApiStatus.NonExtendable
    default <D> void setAnimData(Entity relatedEntity, long instanceId, SerializableDataTicket<D> dataTicket, D data) {
        // Client-side only
        getAnimatableInstanceCache().getManagerForId(instanceId).setData(dataTicket, data);
    }

    /**
     * Syncs an arbitrary piece of data to all players targeted by the packetTarget
     * <p>
     * This method should only be called on the <u>server side</u>
     * <p>
     * <b><u>DO NOT OVERRIDE</u></b>
     *
     * @param instanceId The unique id that identifies the specific animatable instance
     * @param dataTicket The DataTicket to sync the data for
     * @param data       The data to sync
     */
    @ApiStatus.NonExtendable
    default <D> void syncAnimData(long instanceId, SerializableDataTicket<D> dataTicket, D data, Entity entityToTrack) {
        // Client-side only (no networking)
    }

    /**
     * Trigger a client-side animation for this HydraAnimatable for the given controller name and animation name
     * <p>
     * This can be fired from either the client or the server, but optimally you would call it from the server
     * <p>
     * <b><u>DO NOT OVERRIDE</u></b>
     *
     * @param relatedEntity  An entity related to the animatable to trigger the animation for (E.G. The player holding the item)
     * @param instanceId     The unique id that identifies the specific animatable instance
     * @param controllerName The name of the controller the animation belongs to, or null to do an inefficient lazy search
     * @param animName       The name of animation to trigger. This needs to have been registered with the controller via {@link AnimationController#triggerableAnim AnimationController.triggerableAnim}
     */
    @ApiStatus.NonExtendable
    default <D> void triggerAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, String animName) {
        // Client-side only
        if (controllerName != null) {
            getAnimatableInstanceCache().getManagerForId(instanceId).tryTriggerAnimation(controllerName, animName);
        }
        else {
            getAnimatableInstanceCache().getManagerForId(instanceId).tryTriggerAnimation(animName);
        }
    }

    /**
     * Stop a previously triggered animation for this HydraAnimatable for the given controller name and animation name
     * <p>
     * This can be fired from either the client or the server, but optimally you would call it from the server
     * <p>
     * <b><u>DO NOT OVERRIDE</u></b>
     *
     * @param relatedEntity An entity related to the animatable to trigger the animation for (E.G. The player holding the item)
     * @param instanceId The unique id that identifies the specific animatable instance
     * @param controllerName The name of the controller the animation belongs to, or null to do an inefficient lazy search
     * @param animName The name of the triggered animation to stop, or null to stop any currently playing triggered animation
     */
    @ApiStatus.NonExtendable
    default void stopTriggeredAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, @Nullable String animName) {
        // Client-side only
        AnimatableManager<HydraAnimatable> animatableManager = getAnimatableInstanceCache().getManagerForId(instanceId);

        if (animatableManager == null)
            return;

        if (controllerName != null) {
            animatableManager.stopTriggeredAnimation(controllerName, animName);
        }
        else {
            animatableManager.stopTriggeredAnimation(animName);
        }
    }

    /**
     * Trigger a client-side animation for this HydraAnimatable's armor rendering for the given controller name and animation name
     * <p>
     * This can be fired from either the client or the server, but optimally you would call it from the server
     * <p>
     * <b><u>DO NOT OVERRIDE</u></b>
     *
     * @param relatedEntity  An entity related to the animatable to trigger the animation for (E.G. The player holding the item)
     * @param instanceId     The unique id that identifies the specific animatable instance
     * @param controllerName The name of the controller the animation belongs to, or null to do an inefficient lazy search
     * @param animName       The name of animation to trigger. This needs to have been registered with the controller via {@link AnimationController#triggerableAnim AnimationController.triggerableAnim}
     */
    @ApiStatus.NonExtendable
    default void triggerArmorAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, String animName) {
        triggerAnim(relatedEntity, -instanceId, controllerName, animName);
    }

    /**
     * Stop a previously triggered animation for this HydraAnimatable's armor rendering for the given controller name and animation name
     * <p>
     * This can be fired from either the client or the server, but optimally you would call it from the server
     * <p>
     * <b><u>DO NOT OVERRIDE</u></b>
     *
     * @param relatedEntity An entity related to the animatable to trigger the animation for (E.G. The player holding the item)
     * @param instanceId The unique id that identifies the specific animatable instance
     * @param controllerName The name of the controller the animation belongs to, or null to do an inefficient lazy search
     * @param animName The name of the triggered animation to stop, or null to stop any currently playing triggered animation
     */
    @ApiStatus.NonExtendable
    default void stopTriggeredArmorAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, @Nullable String animName) {
        stopTriggeredAnim(relatedEntity, -instanceId, controllerName, animName);
    }

    /**
     * Override the default handling for instantiating an AnimatableInstanceCache for this animatable
     * <p>
     * Don't override this unless you know what you're doing
     */
    @Override
    default @Nullable AnimatableInstanceCache animatableCacheOverride() {
        return new SingletonAnimatableInstanceCache(this);
    }

    /**
     * Create your HydraRenderProvider reference here
     * <p>
     * <b><u>MUST provide an anonymous class</u></b>
     * <p>
     * Example Code:
     * <pre>{@code
     * @Override
     * public void createRenderer(Consumer<HydraRenderProvider> consumer) {
     * 	consumer.accept(new HydraRenderProvider() {
     * 		private final BlockEntityWithoutLevelRenderer itemRenderer;
     *
     *        @Override
     *        @Nullable
     *        BlockEntityWithoutLevelRenderer getItemRenderer(HydraArmor armor) {
     *          if (this.itemRenderer == null)
     *              this.itemRenderer = new MyItemRenderer();
     *
     * 			return this.itemRenderer;
     *        }
     *    }
     * }
     * }</pre>
     *
     * @param consumer Consumer of your new HydraRenderProvider instance
     */
    default void createHydraRenderer(Consumer<HydraRenderProvider> consumer) {}

    /**
     * Get the cached {@link HydraRenderProvider} from your {@link AnimatableInstanceCache}
     */
    default Object getRenderProvider() {
        return getAnimatableInstanceCache().getRenderProvider();
    }
}
