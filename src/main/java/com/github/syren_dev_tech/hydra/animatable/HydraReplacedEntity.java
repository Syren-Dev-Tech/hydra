package com.github.syren_dev_tech.hydra.animatable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.client.HydraRenderProvider;
import com.github.syren_dev_tech.hydra.animation.AnimatableManager;
import com.github.syren_dev_tech.hydra.animation.AnimationController;
import com.github.syren_dev_tech.hydra.constant.dataticket.SerializableDataTicket;

import java.util.function.Consumer;

/**
 * The {@link HydraAnimatable} interface specific to {@link Entity Entities}
 * <p>
 * This interface is <u>specifically</u> for entities replacing the rendering of other, existing entities
 *
 * @see <a href="https://github.com/bernie-g/hydra/wiki/Entity-Animations">Hydra Wiki - Entity Animations</a>
 */
public interface HydraReplacedEntity extends SingletonHydraAnimatable {
	/**
	 * Returns the {@link EntityType} this entity is intending to replace
	 */
	EntityType<?> getReplacingEntityType();

	/**
	 * Get server-synced animation data via its relevant {@link SerializableDataTicket}
	 * <p>
	 * Should only be used on the <u>client-side</u>
	 * <p>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 *
	 * @param entity The entity instance relevant to the data being set
	 * @param dataTicket The data ticket for the data to retrieve
	 * @return The synced data, or null if no data of that type has been synced
	 */
	@ApiStatus.NonExtendable
	@Nullable
	default <D> D getAnimData(Entity entity, SerializableDataTicket<D> dataTicket) {
		return getAnimatableInstanceCache().getManagerForId(entity.getId()).getData(dataTicket);
	}

	/**
	 * Saves an arbitrary syncable piece of data to this animatable's {@link AnimatableManager}
	 * <p>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 *
	 * @param relatedEntity An entity related to the state of the data for syncing
	 * @param dataTicket The DataTicket to sync the data for
	 * @param data The data to sync
	 */
	@ApiStatus.NonExtendable
	default <D> void setAnimData(Entity relatedEntity, SerializableDataTicket<D> dataTicket, D data) {
		// Client-side only
		getAnimatableInstanceCache().getManagerForId(relatedEntity.getId()).setData(dataTicket, data);
	}

	/**
	 * Trigger an animation for this Entity, based on the controller name and animation name
	 * <p>
	 * This can be fired from either the client or the server, but optimally you would call it from the server
	 * <p>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 *
	 * @param relatedEntity An entity related to the state of the data for syncing
	 * @param controllerName The name of the controller name the animation belongs to, or null to do an inefficient lazy search
	 * @param animName The name of animation to trigger. This needs to have been registered with the controller via {@link AnimationController#triggerableAnim AnimationController.triggerableAnim}
	 */
	@ApiStatus.NonExtendable
	default void triggerAnim(Entity relatedEntity, @Nullable String controllerName, String animName) {
		// Client-side only
		if (controllerName != null) {
			getAnimatableInstanceCache().getManagerForId(relatedEntity.getId()).tryTriggerAnimation(controllerName, animName);
		}
		else {
			getAnimatableInstanceCache().getManagerForId(relatedEntity.getId()).tryTriggerAnimation(animName);
		}
	}

	/**
	 * Stop a previously triggered animation for this Entity for the given controller name and animation name
	 * <p>
	 * This can be fired from either the client or the server, but optimally you would call it from the server
	 * <p>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 *
	 * @param relatedEntity An entity related to the state of the data for syncing
	 * @param controllerName The name of the controller the animation belongs to, or null to do an inefficient lazy search
	 * @param animName The name of the triggered animation to stop, or null to stop any currently playing triggered animation
	 */
	@ApiStatus.NonExtendable
	default void stopTriggeredAnim(Entity relatedEntity, @Nullable String controllerName, @Nullable String animName) {
		// Client-side only
		AnimatableManager<HydraAnimatable> animatableManager = getAnimatableInstanceCache().getManagerForId(relatedEntity.getId());

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
	 * Returns the current age/tick of the animatable instance
	 * <p>
	 * By default this is just the animatable's age in ticks, but this method allows for non-ticking custom animatables to provide their own values
	 *
	 * @param entity The Entity representing this animatable
	 * @return The current tick/age of the animatable, for animation purposes
	 */
	@Override
	default double getTick(Object entity) {
		return ((Entity)entity).tickCount;
	}

	// These methods aren't used for HydraReplacedEntity
	@ApiStatus.NonExtendable
	@Override
	default void createHydraRenderer(Consumer<HydraRenderProvider> consumer) {}

	// These methods aren't used for HydraReplacedEntity
	@ApiStatus.NonExtendable
	@Override
	default Object getRenderProvider() {
		return null;
	}
}
