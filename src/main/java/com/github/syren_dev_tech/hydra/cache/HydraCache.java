package com.github.syren_dev_tech.hydra.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import com.github.syren_dev_tech.hydra.HydraConstants;
import com.github.syren_dev_tech.hydra.animation.Animation;
import com.github.syren_dev_tech.hydra.cache.object.BakedHydraModel;
import com.github.syren_dev_tech.hydra.loading.FileLoader;
import com.github.syren_dev_tech.hydra.loading.json.raw.Model;
import com.github.syren_dev_tech.hydra.loading.json.typeadapter.BakedAnimationsAdapter;
import com.github.syren_dev_tech.hydra.loading.object.BakedAnimations;
import com.github.syren_dev_tech.hydra.loading.object.BakedModelFactory;
import com.github.syren_dev_tech.hydra.loading.object.GeometryTree;
import com.github.syren_dev_tech.hydra.model.HydraModel;
import com.github.syren_dev_tech.hydra.util.CompoundException;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Cache class for holding loaded {@link Animation Animations} and {@link HydraModel Models}
 */
public final class HydraCache {
    private HydraCache() {
        /* This utility class should not be instantiated */
    }

    private static final Set<String> EXCLUDED_NAMESPACES = ObjectOpenHashSet.of();
    private static Map<ResourceLocation, BakedAnimations> ANIMATIONS = Collections.emptyMap();
    private static Map<ResourceLocation, BakedHydraModel> MODELS = Collections.emptyMap();

    public static Map<ResourceLocation, BakedAnimations> getBakedAnimations() {
        return ANIMATIONS;
    }

    public static Map<ResourceLocation, BakedHydraModel> getBakedModels() {
        return MODELS;
    }

    /**
     * Register a new namespace to be excluded from Hydra's resource loader. Use this if your mod uses "hydra" or "animations" folders but doesn't use Hydra models.
     *
     * @param namespace The namespace (modid) to exclude
     */
    public static synchronized void registerNamespaceExclusion(String namespace) {
        EXCLUDED_NAMESPACES.add(namespace.toLowerCase(Locale.ROOT));
    }

    public static void registerReloadListener() {
        Minecraft mc = Minecraft.getInstance();

        if (mc != null && mc.getResourceManager() instanceof ReloadableResourceManager resourceManager)
            resourceManager.registerReloadListener(HydraCache::reload);
    }

    public static CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        HydraConstants.LOGGER.info("Starting Hydra cache reload...");
        long reloadStartTime = System.currentTimeMillis();
        Map<ResourceLocation, BakedAnimations> animations = new Object2ObjectOpenHashMap<>();
        Map<ResourceLocation, BakedHydraModel> models = new Object2ObjectOpenHashMap<>();

        return CompletableFuture.allOf(loadAnimations(backgroundExecutor, resourceManager, animations::put), loadModels(backgroundExecutor, resourceManager, models::put)).thenCompose(stage::wait).thenAcceptAsync(empty -> {
            HydraCache.ANIMATIONS = animations;
            HydraCache.MODELS = models;
            long reloadTime = System.currentTimeMillis() - reloadStartTime;
            HydraConstants.LOGGER.info("Hydra cache reload complete in {}ms. Loaded {} animations and {} models", reloadTime, animations.size(), models.size());
        }, gameExecutor);
    }

    private static CompletableFuture<Void> loadAnimations(Executor backgroundExecutor, ResourceManager resourceManager, BiConsumer<ResourceLocation, BakedAnimations> elementConsumer) {
        HydraConstants.LOGGER.debug("Starting animation file loading...");
        long animStartTime = System.currentTimeMillis();
        return CompletableFuture.runAsync(() -> {
            HydraConstants.LOGGER.debug("Initializing compression cache for animations...");
            BakedAnimationsAdapter.COMPRESSION_CACHE = new ConcurrentHashMap<>();
        }, backgroundExecutor).thenRunAsync(() -> loadResources(backgroundExecutor, resourceManager, "animations", resource -> {
            HydraConstants.LOGGER.debug("Loading animation file: {}", resource);
            try {
                BakedAnimations animations = FileLoader.loadAnimationsFile(resource, resourceManager);
                HydraConstants.LOGGER.debug("Successfully loaded animation file: {} with {} animations", resource, animations.toString());
                return animations;
            } catch (CompoundException ex) {
                ex.withMessage(resource.toString() + ": Error loading animation file").printStackTrace();
                HydraConstants.LOGGER.warn("Loaded empty animation set for: {}", resource);
                return new BakedAnimations(new Object2ObjectOpenHashMap<>());
            } catch (Exception ex) {
                throw HydraConstants.exception(resource, "Error loading animation file", ex);
            }
        }, elementConsumer)).thenRunAsync(() -> {
            HydraConstants.LOGGER.debug("Clearing compression cache after animation loading...");
            long animLoadTime = System.currentTimeMillis() - animStartTime;
            HydraConstants.LOGGER.debug("Animation loading completed in {}ms", animLoadTime);
            BakedAnimationsAdapter.COMPRESSION_CACHE = null;
        });
    }

    private static CompletableFuture<Void> loadModels(Executor backgroundExecutor, ResourceManager resourceManager, BiConsumer<ResourceLocation, BakedHydraModel> elementConsumer) {
        HydraConstants.LOGGER.debug("Starting Hydra model file loading...");
        long modelStartTime = System.currentTimeMillis();
        return loadResources(backgroundExecutor, resourceManager, "hydra", resource -> {
            HydraConstants.LOGGER.debug("Loading Hydra model file: {}", resource);
            long modelFileStartTime = System.currentTimeMillis();
            try {
                Model model = FileLoader.loadModelFile(resource, resourceManager);
                HydraConstants.LOGGER.debug("Deserialized model file {}, format version: {}", resource, model.formatVersion());

                switch (model.formatVersion()) {
                    case V_1_12_0 -> HydraConstants.LOGGER.debug("Model {} uses supported format version 1.12.0", resource);
                    case V_1_14_0 -> HydraConstants.LOGGER.warn("Unsupported geometry json version: 1.14.0 for model {}. This model may not appear as expected", resource);
                    case V_1_21_0 -> HydraConstants.LOGGER.warn("Unsupported geometry json version: 1.21.0 for model {}. Supported versions: 1.12.0. Remove any rotated face UVs and re-export the model to fix", resource);
                    case null, default -> HydraConstants.LOGGER.warn("Unsupported geometry json version for model {}. Supported versions: 1.12.0", resource);
                }

                HydraConstants.LOGGER.debug("Building geometry tree for model: {}", resource);
                GeometryTree tree = GeometryTree.fromModel(model);
                HydraConstants.LOGGER.debug("Geometry tree built with {} top-level bones", tree.topLevelBones().size());

                HydraConstants.LOGGER.debug("Constructing Hydra model for namespace: {}", resource.getNamespace());
                BakedHydraModel bakedModel = BakedModelFactory.getForNamespace(resource.getNamespace()).constructHydraModel(tree);
                long modelFileLoadTime = System.currentTimeMillis() - modelFileStartTime;
                HydraConstants.LOGGER.debug("Successfully loaded and baked model file: {} in {}ms", resource, modelFileLoadTime);
                return bakedModel;
            } catch (Exception ex) {
                throw HydraConstants.exception(resource, "Error loading model file", ex);
            }
        }, elementConsumer).thenRunAsync(() -> {
            long modelLoadTime = System.currentTimeMillis() - modelStartTime;
            HydraConstants.LOGGER.debug("Model loading completed in {}ms", modelLoadTime);
        });
    }

    private static <T> CompletableFuture<Void> loadResources(Executor executor, ResourceManager resourceManager, String path, Function<ResourceLocation, T> loader, BiConsumer<ResourceLocation, T> map) {
        HydraConstants.LOGGER.debug("Starting resource scan for path: {}", path);
        long resourceScanStartTime = System.currentTimeMillis();
        return CompletableFuture.supplyAsync(() -> {
            HydraConstants.LOGGER.debug("Listing all JSON files in path: {}", path);
            return resourceManager.listResources(path, fileName -> fileName.toString().endsWith(".json"));
        }, executor).thenApplyAsync(resources -> {
            long resourceScanTime = System.currentTimeMillis() - resourceScanStartTime;
            HydraConstants.LOGGER.debug("Found {} JSON files in path {} (scan took {}ms)", resources.size(), path, resourceScanTime);

            Map<ResourceLocation, CompletableFuture<T>> tasks = new Object2ObjectOpenHashMap<>();
            for (ResourceLocation resource : resources.keySet()) {
                HydraConstants.LOGGER.debug("Queuing resource for loading: {} [namespace: {}]", resource, resource.getNamespace());
                tasks.put(resource, CompletableFuture.supplyAsync(() -> loader.apply(resource), executor));
            }

            HydraConstants.LOGGER.debug("Queued {} resources for parallel loading", tasks.size());
            return tasks;
        }, executor).thenAcceptAsync(tasks -> {
            int successCount = 0;
            int skipCount = 0;
            int errorCount = 0;

            HydraConstants.LOGGER.debug("Processing {} tasks...", tasks.size());
            for (Entry<ResourceLocation, CompletableFuture<T>> entry : tasks.entrySet()) {
                // Skip excluded namespaces that may use hydra/animations folders for other purposes
                if (EXCLUDED_NAMESPACES.contains(entry.getKey().getNamespace().toLowerCase(Locale.ROOT))) {
                    HydraConstants.LOGGER.debug("Skipping resource from excluded namespace: {}", entry.getKey());
                    skipCount++;
                    continue;
                }

                try {
                    long taskStartTime = System.currentTimeMillis();
                    map.accept(entry.getKey(), entry.getValue().join());
                    long taskTime = System.currentTimeMillis() - taskStartTime;
                    HydraConstants.LOGGER.debug("Successfully processed resource: {} (took {}ms)", entry.getKey(), taskTime);
                    successCount++;
                } catch (Exception ex) {
                    HydraConstants.LOGGER.error("Failed to load resource: " + entry.getKey(), ex);
                    errorCount++;
                }
            }

            HydraConstants.LOGGER.debug("Resource processing complete. Success: {}, Skipped: {}, Errors: {}", successCount, skipCount, errorCount);
        }, executor);
    }
}
