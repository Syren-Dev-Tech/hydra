package com.github.syren_dev_tech.hydra.cache.object;

import com.github.syren_dev_tech.hydra.loading.json.raw.ModelProperties;

import java.util.List;
import java.util.Optional;

/**
 * Baked model object for Hydra models
 */
public record BakedHydraModel(List<HydraBone> topLevelBones, ModelProperties properties) {
	/**
	 * Gets a bone from this model by name
	 * <p>
	 * Generally not a very efficient method, should be avoided where possible
	 *
	 * @param name The name of the bone
	 * @return An {@link Optional} containing the {@link HydraBone} if one matches, otherwise an empty Optional
	 */
	public Optional<HydraBone> getBone(String name) {
		for (HydraBone bone : this.topLevelBones) {
			HydraBone childBone = searchForChildBone(bone, name);

			if (childBone != null)
				return Optional.of(childBone);
		}

		return Optional.empty();
	}

	/**
	 * Search a given {@link HydraBone}'s child bones and see if any of them match the given name, then return it
	 *
	 * @param parent The parent bone to search the children of
	 * @param name The name of the child bone to find
	 * @return The {@code HydraBone} found in the parent's children list, or null if not found
	 */
	public HydraBone searchForChildBone(HydraBone parent, String name) {
		if (parent.getName().equals(name))
			return parent;

		for (HydraBone bone : parent.getChildBones()) {
			if (bone.getName().equals(name))
				return bone;

			HydraBone subChildBone = searchForChildBone(bone, name);

			if (subChildBone != null)
				return subChildBone;
		}

		return null;
	}
}
