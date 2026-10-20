package com.github.syren_dev_tech.hydra.loading.loader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import com.github.syren_dev_tech.hydra.cache.model.BakedGeoModel;
import com.github.syren_dev_tech.hydra.loading.math.MathParser;
import com.github.syren_dev_tech.hydra.cache.animation.BakedAnimations;

import java.util.function.BiPredicate;

/// Hydra interface for alternate deserialization schemes, replacing Hydra's default [Gson] handling
///
/// @param <T> The raw object type this loader uses in memory. E.G. [GSON][JsonObject]
public interface HydraLoader<T> {
    /// @return An array of file type extensions that this loader supports
    String[] supportedExtensions();

    /// Read a Hydra model resource from disk into memory, deserializing it into type `T`
    ///
    /// @param id The resource path of the resource to load
    /// @param resource The resource reference to load
    /// @return An instance of type `T` ready for baking
    T deserializeHydraModelFile(Identifier id, Resource resource) throws RuntimeException;

    /// Read a Hydra animations resource from disk into memory, deserializing it into type `T`
    ///
    /// @param id The resource path of the resource to load
    /// @param resource The resource reference to load
    /// @return An instance of type `T` ready for baking
    T deserializeHydraAnimationFile(Identifier id, Resource resource) throws RuntimeException;

    /// Bake a Hydra model file into its final [BakedGeoModel] form from the raw `T` type instance
    /// read into memory in [#deserializeHydraModelFile]
    ///
    /// @param id The resource path of the animations file that was loaded
    /// @param raw The raw `T` type object read in [#deserializeHydraModelFile]
    BakedGeoModel bakeHydraModelFile(Identifier id, T raw) throws RuntimeException;

    /// Bake a Hydra animations file into its final [BakedAnimations] form from the raw `T` type instance
    /// read into memory in [#deserializeHydraAnimationFile]
    ///
    /// @param id The resource path of the model file that was loaded
    /// @param raw The raw `T` type object read in [#deserializeHydraAnimationFile]
    /// @param mathParser The [MathParser] instance to use for this bake operation
    BakedAnimations bakeHydraAnimationsFile(Identifier id, T raw, MathParser mathParser) throws RuntimeException;

    /// Predicate interface for determining whether a given [HydraLoader] should handle a specific resource
    @FunctionalInterface
    interface Predicate extends BiPredicate<Identifier, Resource> {
        /// Return whether a given `HydraLoader` should be used to handle the provided resource
        ///
        /// @return `true` if the resource should be handled by the accompanying loader, or false to fall back to default
        boolean shouldHandle(Identifier id, Resource resource);

        @Override
        @Deprecated
        default boolean test(Identifier identifier, Resource resource) {
            return shouldHandle(identifier, resource);
        }
    }
}
