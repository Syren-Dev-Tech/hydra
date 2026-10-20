package com.github.syren_dev_tech.hydra.loading.loader;

import com.github.syren_dev_tech.hydra.HydraConstants;
import com.github.syren_dev_tech.hydra.cache.animation.BakedAnimations;
import com.github.syren_dev_tech.hydra.cache.model.BakedGeoModel;
import com.github.syren_dev_tech.hydra.loading.definition.animation.ActorAnimations;
import com.github.syren_dev_tech.hydra.loading.definition.geometry.Geometry;
import com.github.syren_dev_tech.hydra.loading.definition.geometry.object.ModelFormatVersion;
import com.github.syren_dev_tech.hydra.loading.math.MathParser;
import com.github.syren_dev_tech.hydra.object.CompoundException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;

import java.io.IOException;
import java.io.Reader;

/// [Hydra resource loader][HydraLoader] using a [Gson][com.google.gson.Gson] implementation
public class HydraGsonLoader implements HydraLoader<JsonObject> {
    /// @return An array of file type extensions that this loader supports
    @Override
    public String[] supportedExtensions() {
        return new String[] {"json"};
    }

    /// Read a Hydra model resource from disk into memory, deserializing it into type `T`
    ///
    /// @param resourcePath The resource path of the resource to load
    /// @param resource The resource reference to load
    /// @return An instance of type `T` ready for baking
    @Override
    public JsonObject deserializeHydraModelFile(Identifier resourcePath, Resource resource) throws RuntimeException {
        return readResourceAsJson(resourcePath, resource);
    }

    /// Read a Hydra animations resource from disk into memory, deserializing it into type `T`
    ///
    /// @param resourcePath The resource path of the resource to load
    /// @param resource The resource reference to load
    /// @return An instance of type `T` ready for baking
    @Override
    public JsonObject deserializeHydraAnimationFile(Identifier resourcePath, Resource resource) throws RuntimeException {
        return readResourceAsJson(resourcePath, resource);
    }

    /// Bake a Hydra model file into its final [BakedGeoModel] form from the raw `T` type instance
    /// read into memory in [#deserializeHydraModelFile]
    ///
    /// @param resourcePath The resource path of the animations file that was loaded
    /// @param raw The raw `T` type object read in [#deserializeHydraModelFile]
    @Override
    public BakedGeoModel bakeHydraModelFile(Identifier resourcePath, JsonObject raw) throws RuntimeException {
        return deserializeJsonModel(resourcePath, raw).bake(resourcePath);
    }

    /// Bake a Hydra animations file into its final [BakedAnimations] form from the raw `T` type instance
    /// read into memory in [#deserializeHydraAnimationFile]
    ///
    /// @param resourcePath The resource path of the model file that was loaded
    /// @param raw The raw `T` type object read in [#deserializeHydraAnimationFile]
    /// @param mathParser The [MathParser] instance to use for this bake operation
    @Override
    public BakedAnimations bakeHydraAnimationsFile(Identifier resourcePath, JsonObject raw, MathParser mathParser) throws RuntimeException {
        return deserializeJsonAnimations(resourcePath, raw).bake(resourcePath, mathParser);
    }

    /// Read a single resource into its [JsonObject] form
    protected static JsonObject readResourceAsJson(Identifier resourcePath, Resource resource) throws RuntimeException {
        try (Reader reader = resource.openAsReader()) {
            return GsonHelper.parse(reader);
        }
        catch (IOException ex) {
            throw HydraConstants.exception(resourcePath, "Error reading JSON file", ex);
        }
    }

    /// Deserialize the Hydra model [JsonObject] into the intermediary [Geometry] instance
    protected static Geometry deserializeJsonModel(Identifier resourcePath, JsonObject json) throws RuntimeException {
        if (resourcePath.getPath().endsWith(".animation.json"))
            throw new IllegalStateException("Found animation file found in models folder! '" + resourcePath + "'");

        final Geometry geometry = Geometry.GSON.fromJson(json, Geometry.class);
        final ModelFormatVersion matchedVersion = ModelFormatVersion.match(geometry.formatVersion());

        if (matchedVersion == null) {
            HydraConstants.LOGGER.warn("{}: Unknown geo model format version: '{}'. This may not work correctly", resourcePath, geometry.formatVersion());
        }
        else if (!matchedVersion.isSupported()) {
            HydraConstants.LOGGER.error("{}: Unsupported geo model format version: '{}'. {}", resourcePath, geometry.formatVersion(), matchedVersion.getErrorMessage());
        }

        return geometry;
    }

    /// Deserialize the Hydra animations [JsonObject] into the intermediary [ActorAnimations] instance
    protected static ActorAnimations deserializeJsonAnimations(Identifier resourcePath, JsonObject json) throws RuntimeException {
        if (resourcePath.getPath().endsWith(".geo.json"))
            throw new IllegalStateException("Found model file in animations folder! '" + resourcePath + "'");

        try {
            return ActorAnimations.GSON.fromJson(json, ActorAnimations.class);
        }
        catch (CompoundException ex) {
            throw ex.withMessage(resourcePath + ": Error building animations from JSON");
        }
        catch (JsonParseException ex) {
            throw new CompoundException(resourcePath + ": Error building animations from JSON", ex);
        }
        catch (Exception ex) {
            throw HydraConstants.exception(resourcePath, "Error building animations from JSON", ex);
        }
    }
}
