package com.github.syren_dev_tech.hydra.cache;

import com.github.syren_dev_tech.hydra.HydraConstants;
import com.github.syren_dev_tech.hydra.cache.animation.Animation;
import com.github.syren_dev_tech.hydra.cache.animation.BakedAnimations;
import com.github.syren_dev_tech.hydra.cache.model.BakedGeoModel;
import com.github.syren_dev_tech.hydra.loading.loader.HydraGsonLoader;
import com.github.syren_dev_tech.hydra.loading.loader.HydraLoader;
import com.github.syren_dev_tech.hydra.loading.math.MathParser;
import com.github.syren_dev_tech.hydra.model.GeoModel;
import com.github.syren_dev_tech.hydra.util.HydraUtil;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/// Cache class for holding loaded [Animations][Animation] and [Models][GeoModel]
public final class HydraResources implements PreparableReloadListener {
	public static final Identifier RELOAD_LISTENER_ID = HydraConstants.id("hydra_resources");
	public static final Identifier ANIMATIONS_PATH = HydraConstants.id("hydra/animations");
	public static final Identifier MODELS_PATH = HydraConstants.id("hydra/models");
	public static final Pattern SUFFIX_STRIPPER = Pattern.compile("((\\.geo)|((\\.animation)s?))?(\\.json)$");
	public static final Pattern PREFIX_STRIPPER = Pattern.compile("^(hydra/)((animations/)|(models/))?");
	public static final PreparableReloadListener.StateKey<PendingResources> STATE_KEY = new PreparableReloadListener.StateKey<>();
	@SuppressWarnings("unchecked")
    private static Pair<HydraLoader.Predicate, HydraLoader<?>>[] LOADERS = new Pair[] {Pair.<HydraLoader.Predicate, HydraLoader<?>>of((_, _) -> true, new HydraGsonLoader())};
	private static final Set<String> SUPPORTED_FILE_TYPES = ObjectArraySet.of("json");

	private static BakedAnimationCache ANIMATIONS = new BakedAnimationCache(Collections.emptyMap());
	private static BakedModelCache MODELS = new BakedModelCache(Collections.emptyMap());

	/// Get Hydra's cache of all the loaded animations from the [#ANIMATIONS_PATH]
	public static BakedAnimationCache getBakedAnimations() {
		return ANIMATIONS;
	}

	/// Get Hydra's cache of all the loaded geo models from the [#MODELS_PATH]
	public static BakedModelCache getBakedModels() {
		return MODELS;
	}

	/// Add a `HydraLoader` to the loaders array, for handling custom resource loading
	///
	/// This is an internal method, you should **<u>NOT</u>** be using this<br/>
	/// Use [HydraUtil#addResourceLoader] instead.
	@SuppressWarnings("unchecked")
	@ApiStatus.Internal
	public static void addLoader(HydraLoader.Predicate predicate, HydraLoader<?> loader) {
		final Pair<HydraLoader.Predicate, HydraLoader<?>>[] copy = new Pair[LOADERS.length + 1];

		copy[0] = Pair.of(predicate, loader);
		System.arraycopy(LOADERS, 0, copy, 1, LOADERS.length);

		LOADERS = copy;

        Collections.addAll(SUPPORTED_FILE_TYPES, loader.supportedExtensions());

		HydraConstants.LOGGER.info("Added custom resource loader {}", loader.getClass().getSimpleName());
	}

	@Override
	public void prepareSharedState(SharedState sharedState) {
		sharedState.set(STATE_KEY, new PendingResources());
	}

	@ApiStatus.Internal
	@Override
	public CompletableFuture<Void> reload(PreparableReloadListener.SharedState sharedState, Executor prepExecutor, PreparationBarrier preparationBarrier, Executor applicationExecutor) {
		final ResourceManager resourceManager = sharedState.resourceManager();
		final PendingResources pending = sharedState.get(STATE_KEY);
		final MathParser mathParser = MathParser.createWithDeduplication();

		return CompletableFuture.allOf(loadModels(prepExecutor, resourceManager, pending),
									   loadAnimations(prepExecutor, resourceManager, pending, mathParser))
				.thenCompose(preparationBarrier::wait)
				.thenRunAsync(() -> applyResources(pending), applicationExecutor);
	}

	/// Provide a [Future] for retrieving and baking all animation JSONs from the [#ANIMATIONS_PATH]
	private static CompletableFuture<Map<Identifier, BakedAnimations>> loadAnimations(Executor executor, ResourceManager resourceManager, PendingResources pendingResources, MathParser mathParser) {
		return bakeAllResources(executor, resourceManager, ANIMATIONS_PATH.getPath(), HydraLoader::deserializeHydraAnimationFile,
								(loader, resourcePath, raw) -> loader.bakeHydraAnimationsFile(resourcePath, raw, mathParser), () -> null)
				.whenComplete((map, err) -> pendingResources.complete(map, err, PendingResources::animations));
	}

	/// Provide a [Future] for retrieving and baking all geo model JSONs from the [#MODELS_PATH]
	private static CompletableFuture<Map<Identifier, BakedGeoModel>> loadModels(Executor executor, ResourceManager resourceManager, PendingResources pendingResources) {
		return bakeAllResources(executor, resourceManager, MODELS_PATH.getPath(), HydraLoader::deserializeHydraModelFile, HydraLoader::bakeHydraModelFile, () -> null)
				.whenComplete((map, err) -> pendingResources.complete(map, err, PendingResources::models));
	}

	/// Provide a [Future] for retrieving and baking all asset JSON files for a given type into a compiled map
	@SuppressWarnings("SameParameterValue")
    private static <UNBAKED, BAKED> CompletableFuture<Map<Identifier, BAKED>> bakeAllResources(Executor executor, ResourceManager resourceManager, String rootPath,
                                                                                               TriFunction<HydraLoader<UNBAKED>, Identifier, Resource, UNBAKED> deserializer,
                                                                                               TriFunction<HydraLoader<UNBAKED>, Identifier, UNBAKED, BAKED> bakery,
                                                                                               Supplier<@Nullable BAKED> onException) {
		return CompletableFuture.supplyAsync(() -> resourceManager.listResources(rootPath, HydraResources::isFileTypeSupported), executor)
				.thenCompose(resources -> {
					final List<CompletableFuture<@Nullable Pair<Identifier, BAKED>>> tasks = new ObjectArrayList<>(resources.size());

					resources.forEach((path, resource) -> tasks.add(bakeResource(executor, path, resource, deserializer, bakery, onException)));

					return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
							.thenApply(_ -> {
								final Set<Identifier> uniqueResources = new ObjectOpenHashSet<>(tasks.size());

								return tasks.stream().map(CompletableFuture::join).filter(Objects::nonNull).collect(Collectors.filtering(pair -> {
									if (uniqueResources.add(pair.left()))
										return true;

									HydraConstants.LOGGER.error("Found duplicate Hydra resource in {}: {}", rootPath, pair.left());

									return false;
								}, Collectors.toUnmodifiableMap(Pair::left, Pair::right)));
							});
				});
	}

	/// Provide a [Future] to retrieve a single resource and fully bake it into a finalized object
	@SuppressWarnings({"unchecked"})
    private static <UNBAKED, BAKED> CompletableFuture<@Nullable Pair<Identifier, BAKED>> bakeResource(Executor executor, Identifier path, Resource resource,
																							TriFunction<HydraLoader<UNBAKED>, Identifier, Resource, UNBAKED> deserializer,
																							TriFunction<HydraLoader<UNBAKED>, Identifier, UNBAKED, BAKED> bakery,
																							Supplier<@Nullable BAKED> onException) {
		return CompletableFuture.supplyAsync(() -> (HydraLoader<UNBAKED>)findLoaderForFile(path, resource), executor)
				.thenApply(loader -> Pair.of(stripPrefixAndSuffix(path), bakery.apply(loader, path, deserializer.apply(loader, path, resource))))
				.exceptionally(ex -> {
					final BAKED replacement = onException.get();
					ex = ex instanceof CompletionException completionException && completionException.getCause() != null ? completionException.getCause() : ex;

                    HydraConstants.LOGGER.error("Error loading resource: {}", path, ex);

					return replacement == null ? null : Pair.of(stripPrefixAndSuffix(path), replacement);
				});
	}

	/// Find the first applicable [HydraLoader] for a given resource, based on its [Identifier] and [Resource]
	/// Allows for injected loaders to handle resource loading
	///
	/// @see HydraUtil#addResourceLoader(HydraLoader.Predicate, HydraLoader)
	private static HydraLoader<?> findLoaderForFile(Identifier id, Resource resource) {
		if (LOADERS.length > 1) {
			for (Pair<HydraLoader.Predicate, HydraLoader<?>> entry : LOADERS) {
				final HydraLoader<?> loader = entry.second();

				if (anyExtensionMatches(id.getPath(), Arrays.asList(loader.supportedExtensions())) && entry.first().shouldHandle(id, resource))
					return entry.second();
			}
		}

		return LOADERS[LOADERS.length - 1].second();
	}

	/// Strip the asset prefix and suffix from the given filepath, returning the stripped location
	///
	/// @return The stripped location, or the original path if no match is found
	public static Identifier stripPrefixAndSuffix(Identifier path) {
		String newPath = path.getPath();
		Matcher prefixMatcher = PREFIX_STRIPPER.matcher(newPath);
		newPath = prefixMatcher.find() ? newPath.substring(prefixMatcher.end()) : newPath;
		Matcher suffixMatcher = SUFFIX_STRIPPER.matcher(newPath);
		newPath = suffixMatcher.find() ? newPath.substring(0, suffixMatcher.start()) : newPath;

		return newPath.length() == path.getPath().length() ? path : path.withPath(newPath);
	}

	/// Apply the fully loaded resources to the publicly accessible maps for use
	private static void applyResources(PendingResources resources) {
		MODELS = new BakedModelCache(resources.models.join());
		ANIMATIONS = new BakedAnimationCache(resources.animations.join());

		HydraConstants.LOGGER.info("Loaded {} models and {} animations from resources", MODELS.size(), ANIMATIONS.size());
	}

	/// @return Whether the given resource file is supported by the currently registered loaders
	private static boolean isFileTypeSupported(Identifier resourcePath) {
		final String path = resourcePath.getPath();

		if (SUPPORTED_FILE_TYPES.size() <= 3)
			return anyExtensionMatches(path, SUPPORTED_FILE_TYPES);

		return SUPPORTED_FILE_TYPES.contains(path.substring(path.lastIndexOf('.') + 1));
	}

	/// @return Whether the provided resource path matches any of the file type extensions in the provided [Iterable]
	private static boolean anyExtensionMatches(String path, Iterable<String> extensions) {
		for (String extension : extensions) {
			if (path.endsWith("." + extension))
				return true;
		}

		return false;
	}

	/// Resource-loading state holder object to pair with the associated [PreparableReloadListener.StateKey] for resource loading
	public record PendingResources(CompletableFuture<Map<Identifier, BakedGeoModel>> models, CompletableFuture<Map<Identifier, BakedAnimations>> animations) {
		public PendingResources() {
			this(new CompletableFuture<>(), new CompletableFuture<>());
		}

		/// Mark one of this instance's futures as complete, either with a result, or exceptionally
        public <T extends Map<Identifier, ? extends Object>> void complete(@Nullable T map, @Nullable Throwable error, Function<PendingResources, CompletableFuture<T>> target) {
			final CompletableFuture<T> future = target.apply(this);

			if (map != null) {
				future.complete(map);
			}
			else {
				future.completeExceptionally(error);
			}
		}
	}
}
