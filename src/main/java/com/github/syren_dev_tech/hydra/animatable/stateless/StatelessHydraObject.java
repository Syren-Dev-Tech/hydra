package com.github.syren_dev_tech.hydra.animatable.stateless;

import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.animation.RawAnimation;

/**
 * Extension of {@link StatelessAnimatable} for custom {@link HydraAnimatable}s
 * <p>
 * Implementation of {@link StatelessAnimatable#playAnimation(RawAnimation)} and {@link StatelessAnimatable#stopAnimation(String)} is left up
 * to the implementers of this interface, as well as any additional handling required
 */
public non-sealed interface StatelessHydraObject extends StatelessAnimatable, HydraAnimatable {}
