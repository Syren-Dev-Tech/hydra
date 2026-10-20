package com.github.syren_dev_tech.hydra.animatable.instance;

import com.github.syren_dev_tech.hydra.util.HydraUtil;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.github.syren_dev_tech.hydra.animatable.GeoAnimatable;
import com.github.syren_dev_tech.hydra.animatable.manager.AnimatableManager;

/// AnimatableInstanceCache implementation for instantiated objects such as Entities or BlockEntities. Returns a single [AnimatableManager] instance per cache
///
/// You should **<u>NOT</u>** be instantiating this directly unless you know what you are doing.
/// Use [HydraUtil.createInstanceCache][HydraUtil#createInstanceCache] instead
public class InstancedAnimatableInstanceCache extends AnimatableInstanceCache {
	protected final Supplier<AnimatableManager<?>> manager = Suppliers.memoize(() -> new AnimatableManager<>(this.animatable));

	public InstancedAnimatableInstanceCache(GeoAnimatable animatable) {
		super(animatable);
	}

	/// Gets the [AnimatableManager] instance from this cache
	///
	/// Because this cache subclass expects a 1:1 relationship of cache to animatable, only one `AnimatableManager` instance is used
	@SuppressWarnings("unchecked")
    @Override
	public AnimatableManager<?> getManagerForId(long uniqueId) {
		return this.manager.get();
	}
}