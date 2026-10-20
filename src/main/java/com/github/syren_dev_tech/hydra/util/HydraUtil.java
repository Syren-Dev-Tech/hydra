package com.github.syren_dev_tech.hydra.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.HydraConstants;
import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.animatable.instance.AnimatableInstanceCache;
import com.github.syren_dev_tech.hydra.animatable.instance.InstancedAnimatableInstanceCache;
import com.github.syren_dev_tech.hydra.animatable.instance.SingletonAnimatableInstanceCache;
import com.github.syren_dev_tech.hydra.animation.Animation;
import com.github.syren_dev_tech.hydra.animation.EasingType;
import com.github.syren_dev_tech.hydra.constant.DataTickets;
import com.github.syren_dev_tech.hydra.constant.dataticket.DataTicket;
import com.github.syren_dev_tech.hydra.constant.dataticket.SerializableDataTicket;
import com.github.syren_dev_tech.hydra.loading.object.BakedModelFactory;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Helper class for various Hydra-specific functions.
 */
public final class HydraUtil {
	private static final IdentityHashMap<HydraAnimatable, String> ANIMATABLE_IDENTITIES = new IdentityHashMap<>();
	public static final Map<String, HydraAnimatable> SYNCED_ANIMATABLES = new Object2ObjectOpenHashMap<>();

	/**
	 * Creates a new AnimatableInstanceCache for the given animatable object
	 *
	 * @param animatable The animatable object
	 */
	public static AnimatableInstanceCache createInstanceCache(HydraAnimatable animatable) {
		AnimatableInstanceCache cache = animatable.animatableCacheOverride();

		return cache != null ? cache : createInstanceCache(animatable, !(animatable instanceof Entity) && !(animatable instanceof BlockEntity));
	}

	/**
	 * Creates a new AnimatableInstanceCache for the given animatable object
	 * <p>
	 * Recommended to use {@link HydraUtil#createInstanceCache(HydraAnimatable)} unless you know what you're doing
	 *
	 * @param animatable The animatable object
	 * @param singletonObject Whether the object is a singleton/flyweight object, and uses ints to differentiate animatable instances
	 */
	public static AnimatableInstanceCache createInstanceCache(HydraAnimatable animatable, boolean singletonObject) {
		AnimatableInstanceCache cache = animatable.animatableCacheOverride();

		if (cache != null)
			return cache;

		return singletonObject ? new SingletonAnimatableInstanceCache(animatable) : new InstancedAnimatableInstanceCache(animatable);
	}

	/**
	 * Register a custom {@link Animation.LoopType} with Hydra, allowing for dynamic handling of post-animation looping
	 * <p>
	 * <b><u>MUST be called during mod construct</u></b>
	 *
	 * @param name The name of the {@code LoopType} handler
	 * @param loopType The {@code LoopType} implementation to use for the given name
	 */
	synchronized public static Animation.LoopType addCustomLoopType(String name, Animation.LoopType loopType) {
		return Animation.LoopType.register(name, loopType);
	}

	/**
	 * Register a custom {@link EasingType} with Hydra allowing for dynamic handling of animation transitions and curves
	 * <p>
	 * <b><u>MUST be called during mod construct</u></b>
	 *
	 * @param name The name of the {@code EasingType} handler
	 * @param easingType The {@code EasingType} implementation to use for the given name
	 */
	synchronized public static EasingType addCustomEasingType(String name, EasingType easingType) {
		return EasingType.register(name, easingType);
	}

	/**
	 * Register a custom {@link BakedModelFactory} with Hydra, allowing for dynamic handling of hydra model loading
	 * <p>
	 * <b><u>MUST be called during mod construct</u></b>
	 *
	 * @param namespace The namespace (modid) to register the factory for
	 * @param factory The factory responsible for model loading under the given namespace
	 */
	synchronized public static void addCustomBakedModelFactory(String namespace, BakedModelFactory factory) {
		BakedModelFactory.register(namespace, factory);
	}

	/**
	 * Register a custom {@link SerializableDataTicket} with Hydra for handling custom data transmission
	 * <p>
	 * NOTE: You do not need to register non-serializable {@link DataTicket DataTickets}.
	 * <p>
	 * <b><u>MUST be called during mod construct</u></b>
	 *
	 * @param dataTicket The SerializableDataTicket to register
	 * @return The dataTicket you passed in
	 */
	synchronized public static <D> SerializableDataTicket<D> addDataTicket(SerializableDataTicket<D> dataTicket) {
		return DataTickets.registerSerializable(dataTicket);
	}

	/**
	 * Registers a synced {@link HydraAnimatable} object for networking support
	 * <p>
	 * It is recommended that you don't call this directly, instead implementing and calling {@link com.github.syren_dev_tech.hydra.animatable.SingletonHydraAnimatable#registerSyncedAnimatable}
	 */
	synchronized public static void registerSyncedAnimatable(HydraAnimatable animatable) {
		synchronized (SYNCED_ANIMATABLES) {
			HydraAnimatable existing = SYNCED_ANIMATABLES.put(getSyncedSingletonAnimatableId(animatable), animatable);

			if (existing == null)
				HydraConstants.LOGGER.debug("Registered SyncedAnimatable for {}", animatable.getClass());
		}
	}

	/**
	 * Gets a registered synced {@link HydraAnimatable} object by name
	 * <p>
	 * The input string <b>MUST</b> be the {@link Class#getName()} of the animatable, passed through
	 * {@link #getSyncedSingletonAnimatableId(HydraAnimatable)}
	 *
	 * @param syncedAnimatableId the animatable id
	 */
	@Nullable
	public static HydraAnimatable getSyncedAnimatable(String syncedAnimatableId) {
		synchronized (SYNCED_ANIMATABLES) {
			HydraAnimatable animatable = SYNCED_ANIMATABLES.get(syncedAnimatableId);

			if (animatable == null)
				HydraConstants.LOGGER.error("Attempting to retrieve unregistered synced animatable! ({})", syncedAnimatableId);

			return animatable;
		}
	}

	/**
	 * Get a synced singleton animatable's id for use with {@link #SYNCED_ANIMATABLES}
	 * <p>
	 * This <b><u>MUST</u></b> be used when retrieving from {@link #SYNCED_ANIMATABLES}
	 * as this method eliminates class duplication collisions
	 */
	public static String getSyncedSingletonAnimatableId(HydraAnimatable animatable) {
		synchronized (SYNCED_ANIMATABLES) {
			return ANIMATABLE_IDENTITIES.computeIfAbsent(animatable, key -> {
				String baseId = animatable.getClass().getName();
				int i = 0;

				while (SYNCED_ANIMATABLES.containsKey(baseId + i)) {
					i++;
				}

				return baseId + i;
			});
		}
	}
}
