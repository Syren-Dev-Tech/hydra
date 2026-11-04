package com.github.syren_dev_tech.hydra.loading;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import com.github.syren_dev_tech.hydra.HydraConstants;
import com.github.syren_dev_tech.hydra.animation.Animation;
import com.github.syren_dev_tech.hydra.cache.object.BakedHydraModel;
import com.github.syren_dev_tech.hydra.loading.json.raw.Model;
import com.github.syren_dev_tech.hydra.loading.json.typeadapter.KeyFramesAdapter;
import com.github.syren_dev_tech.hydra.loading.object.BakedAnimations;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Extracts raw information from given files, and other similar functions
 */
public final class FileLoader {
	/**
	 * Load up and deserialize an animation json file to its respective {@link Animation} components
	 *
	 * @param location The resource path of the animations file
	 * @param manager The Minecraft {@code ResourceManager} responsible for maintaining in-memory resource access
	 */
	public static BakedAnimations loadAnimationsFile(ResourceLocation location, ResourceManager manager) {
		HydraConstants.LOGGER.debug("FileLoader: Starting to load animation file from: {}", location);
		if (location.getPath().endsWith(".hydra.json"))
			throw new IllegalArgumentException("Hydra model file found in animations folder!");

		if (!location.getPath().endsWith(".animation.json"))
			HydraConstants.LOGGER.warn("Found animation file with improper file name format; animation files should end in .animation.json: '" + location + "'");

		long startTime = System.currentTimeMillis();
		BakedAnimations result = KeyFramesAdapter.HYDRA_GSON.fromJson(GsonHelper.getAsJsonObject(loadFile(location, manager), "animations"), BakedAnimations.class);
		long loadTime = System.currentTimeMillis() - startTime;
		HydraConstants.LOGGER.debug("FileLoader: Animation file loaded successfully from {} in {}ms", location, loadTime);
		return result;
	}

	/**
	 * Load up and deserialize a hydra model json file to its respective {@link BakedHydraModel} format
	 *
	 * @param location The resource path of the model file
	 * @param manager The Minecraft {@code ResourceManager} responsible for maintaining in-memory resource access
	 */
	public static Model loadModelFile(ResourceLocation location, ResourceManager manager) {
		HydraConstants.LOGGER.debug("FileLoader: Starting to load model file from: {}", location);
		if (location.getPath().endsWith(".animation.json"))
			throw new IllegalArgumentException("Animation file found in hydra models folder!");

		if (!location.getPath().endsWith(".hydra.json"))
			HydraConstants.LOGGER.warn("Found hydra model file with improper file name format; hydra model files should end in .hydra.json: '" + location + "'");

		long startTime = System.currentTimeMillis();
		Model result = KeyFramesAdapter.HYDRA_GSON.fromJson(loadFile(location, manager), Model.class);
		long loadTime = System.currentTimeMillis() - startTime;
		HydraConstants.LOGGER.debug("FileLoader: Model file loaded successfully from {} in {}ms", location, loadTime);
		return result;
	}

	/**
	 * Load a given json file into memory
	 *
	 * @param location The resource path of the json file
	 * @param manager The Minecraft {@code ResourceManager} responsible for maintaining in-memory resource access
	 */
	public static JsonObject loadFile(ResourceLocation location, ResourceManager manager) {
		HydraConstants.LOGGER.debug("FileLoader: Deserializing JSON file: {}", location);
		long startTime = System.currentTimeMillis();
		JsonObject result = GsonHelper.fromJson(KeyFramesAdapter.HYDRA_GSON, getFileContents(location, manager), JsonObject.class);
		long parseTime = System.currentTimeMillis() - startTime;
		HydraConstants.LOGGER.debug("FileLoader: JSON deserialization complete for {} in {}ms", location, parseTime);
		return result;
	}

	/**
	 * Read a text-based file into memory in the form of a single string
	 *
	 * @param location The resource path of the file
	 * @param manager The Minecraft {@code ResourceManager} responsible for maintaining in-memory resource access
	 */
	public static String getFileContents(ResourceLocation location, ResourceManager manager) {
		HydraConstants.LOGGER.debug("FileLoader: Attempting to read file contents from: {}", location);
		long startTime = System.currentTimeMillis();
		try (InputStream inputStream = manager.getResourceOrThrow(location).open()) {
			String contents = IOUtils.toString(inputStream, Charset.defaultCharset());
			long readTime = System.currentTimeMillis() - startTime;
			HydraConstants.LOGGER.debug("FileLoader: File read successfully from {} ({} bytes in {}ms)", location, contents.length(), readTime);
			return contents;
		}
		catch (Exception e) {
			HydraConstants.LOGGER.error("Couldn't load model file from resource location: " + location + ". Ensure the file exists and is in the correct location (assets/<modid>/hydra/ or assets/<modid>/animations/)", e);

			throw new RuntimeException(new FileNotFoundException("Failed to load model file: " + location.toString()));
		}
	}
}
