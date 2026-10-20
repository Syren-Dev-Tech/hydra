package com.github.syren_dev_tech.hydra.cache.object;

import net.minecraft.world.phys.Vec3;

/**
 * Baked cuboid for a {@link HydraBone}
 */
public record HydraCube(HydraQuad[] quads, Vec3 pivot, Vec3 rotation, Vec3 size, double inflate, boolean mirror) {}
