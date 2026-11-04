package com.github.syren_dev_tech.hydra.loading.object;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import com.github.syren_dev_tech.hydra.HydraConstants;
import com.github.syren_dev_tech.hydra.loading.json.raw.Bone;
import com.github.syren_dev_tech.hydra.loading.json.raw.MinecraftGeometry;
import com.github.syren_dev_tech.hydra.loading.json.raw.Model;
import com.github.syren_dev_tech.hydra.loading.json.raw.ModelProperties;

import java.util.Map;

/**
 * Container class for a {@link Bone} structure, used at startup during deserialization
 */
public record GeometryTree(Map<String, BoneStructure> topLevelBones, ModelProperties properties) {
	public static GeometryTree fromModel(Model model) {
		long startTime = System.currentTimeMillis();
		HydraConstants.LOGGER.debug("GeometryTree: Starting to build geometry tree from model...");
		
		final Map<String, BoneStructure> topLevelBones = new Object2ObjectOpenHashMap<>();
		final MinecraftGeometry geometry = model.minecraftGeometry()[0];
		final Bone[] bones = geometry.bones();
		final Map<String, BoneStructure> lookup = new Object2ObjectOpenHashMap<>(bones.length);

		HydraConstants.LOGGER.debug("GeometryTree: Processing {} bones from model", bones.length);
		int topLevelCount = 0;
		for (Bone bone : bones) {
			final BoneStructure boneStructure = new BoneStructure(bone);

			lookup.put(bone.name(), boneStructure);

			if (bone.parent() == null) {
				topLevelBones.put(bone.name(), boneStructure);
				topLevelCount++;
				HydraConstants.LOGGER.debug("GeometryTree: Found top-level bone: {}", bone.name());
			}
		}
		
		HydraConstants.LOGGER.debug("GeometryTree: Identified {} top-level bones out of {}", topLevelCount, bones.length);

		int childCount = 0;
		for (Bone bone : bones) {
			final String parentName = bone.parent();

			if (parentName != null) {
				final String boneName = bone.name();

				if (parentName.equals(boneName))
					throw new IllegalArgumentException("Invalid model definition. Bone has defined itself as its own parent: " + boneName);

				final BoneStructure parentStructure = lookup.get(parentName);

				if (parentStructure == null)
					throw new IllegalArgumentException("Invalid model definition. Found bone with undefined parent (child -> parent): " + boneName + " -> " + parentName);

				parentStructure.children().put(boneName, lookup.get(boneName));
				childCount++;
				HydraConstants.LOGGER.debug("GeometryTree: Linked child bone '{}' to parent '{}'", boneName, parentName);
			}
		}
		
		long buildTime = System.currentTimeMillis() - startTime;
		HydraConstants.LOGGER.debug("GeometryTree: Geometry tree built successfully. {} parent-child links in {}ms", childCount, buildTime);
		return new GeometryTree(topLevelBones, geometry.modelProperties());
	}
}
