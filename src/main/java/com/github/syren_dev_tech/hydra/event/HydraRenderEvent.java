package com.github.syren_dev_tech.hydra.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animatable.HydraAnimatable;
import com.github.syren_dev_tech.hydra.cache.object.BakedHydraModel;
import com.github.syren_dev_tech.hydra.renderer.*;
import com.github.syren_dev_tech.hydra.renderer.layer.HydraRenderLayer;

/**
 * Hydra events base-class for the various event stages of rendering
 * <p>
 * These are fired on the {@link net.neoforged.fml.common.EventBusSubscriber.Bus FORGE} mod bus
 */
public interface HydraRenderEvent {
	/**
	 * Returns the renderer for this event
	 *
	 * @see com.github.syren_dev_tech.hydra.renderer.specialty.DynamicHydraEntityRenderer DynamicHydraEntityRenderer
	 * @see com.github.syren_dev_tech.hydra.renderer.HydraArmorRenderer HydraArmorRenderer
	 * @see com.github.syren_dev_tech.hydra.renderer.HydraBlockRenderer HydraBlockRenderer
	 * @see com.github.syren_dev_tech.hydra.renderer.HydraEntityRenderer HydraEntityRenderer
	 * @see com.github.syren_dev_tech.hydra.animatable.HydraItem HydraItem
	 * @see com.github.syren_dev_tech.hydra.renderer.HydraObjectRenderer HydraObjectRenderer
	 * @see com.github.syren_dev_tech.hydra.renderer.HydraReplacedEntityRenderer HydraReplacedEntityRenderer
	 */
	HydraRenderer<?> getRenderer();

	/**
	 * Renderer events for armor pieces being rendered by {@link HydraArmorRenderer}
	 */
	abstract class Armor extends Event implements HydraRenderEvent {
		private final HydraArmorRenderer<?> renderer;

		public Armor(HydraArmorRenderer<?> renderer) {
			this.renderer = renderer;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public HydraArmorRenderer<?> getRenderer() {
			return this.renderer;
		}

		/**
		 * Shortcut method for retrieving the entity being rendered
		 */
		@Nullable
		public net.minecraft.world.entity.Entity getEntity() {
			return getRenderer().getCurrentEntity();
		}

		/**
		 * Shortcut method for retrieving the ItemStack relevant to the armor piece being rendered
		 */
		@Nullable
		public ItemStack getItemStack() {
			return getRenderer().getCurrentStack();
		}

		/**
		 * Shortcut method for retrieving the equipped slot of the armor piece being rendered
		 */
		@Nullable
		public EquipmentSlot getEquipmentSlot() {
			return getRenderer().getCurrentSlot();
		}

		/**
		 * Pre-render event for armor pieces being rendered by {@link HydraArmorRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link HydraRenderer#preRender}
		 * <p>
		 * This event is {@link ICancellableEvent Cancelable}<br>
		 * If the event is cancelled, the armor piece will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre extends Armor implements ICancellableEvent {
			private final PoseStack poseStack;
			private final BakedHydraModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Pre(HydraArmorRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedHydraModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}
		}

		/**
		 * Post-render event for armor pieces being rendered by {@link HydraEntityRenderer}
		 * <p>
		 * This event is called after {@link HydraRenderer#postRender}
		 */
		public static class Post extends Armor {
			private final PoseStack poseStack;
			private final BakedHydraModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Post(HydraArmorRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedHydraModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}
		}

		/**
		 * One-time event for a {@link HydraArmorRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers extends Armor {
			public CompileRenderLayers(HydraArmorRenderer<?> renderer) {
				super(renderer);
			}

			/**
			 * Adds a {@link HydraRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(HydraRenderLayer renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}
		}
	}

	/**
	 * Renderer events for {@link BlockEntity BlockEntities} being rendered by {@link HydraBlockRenderer}
	 */
	abstract class Block extends Event implements HydraRenderEvent {
		private final HydraBlockRenderer<?> renderer;

		public Block(HydraBlockRenderer<?> renderer) {
			this.renderer = renderer;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public HydraBlockRenderer<?> getRenderer() {
			return this.renderer;
		}

		/**
		 * Shortcut method for retrieving the block entity being rendered
		 */
		public BlockEntity getBlockEntity() {
			return getRenderer().getAnimatable();
		}

		/**
		 * Pre-render event for block entities being rendered by {@link HydraBlockRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link HydraRenderer#preRender}
		 * <p>
		 * This event is {@link ICancellableEvent Cancelable}<br>
		 * If the event is cancelled, the block entity will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre extends Block implements ICancellableEvent {
			private final PoseStack poseStack;
			private final BakedHydraModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Pre(HydraBlockRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedHydraModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}
		}

		/**
		 * Post-render event for block entities being rendered by {@link HydraBlockRenderer}
		 * <p>
		 * This event is called after {@link HydraRenderer#postRender}
		 */
		public static class Post extends Block {
			private final PoseStack poseStack;
			private final BakedHydraModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Post(HydraBlockRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedHydraModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}
		}

		/**
		 * One-time event for a {@link HydraBlockRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers extends Block {
			public CompileRenderLayers(HydraBlockRenderer<?> renderer) {
				super(renderer);
			}

			/**
			 * Adds a {@link HydraRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(HydraRenderLayer renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}
		}
	}

	/**
	 * Renderer events for {@link net.minecraft.world.entity.Entity Entities} being rendered by {@link HydraEntityRenderer}, as well as
	 * {@link com.github.syren_dev_tech.hydra.renderer.specialty.DynamicHydraEntityRenderer DynamicHydraEntityRenderer}
	 */
	abstract class Entity extends Event implements HydraRenderEvent {
		private final HydraEntityRenderer<?> renderer;

		public Entity(HydraEntityRenderer<?> renderer) {
			this.renderer = renderer;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public HydraEntityRenderer<?> getRenderer() {
			return this.renderer;
		}

		/**
		 * Shortcut method for retrieving the entity being rendered
		 */
		public net.minecraft.world.entity.Entity getEntity() {
			return this.renderer.getAnimatable();
		}

		/**
		 * Pre-render event for entities being rendered by {@link HydraEntityRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link HydraRenderer#preRender}
		 * <p>
		 * This event is {@link ICancellableEvent Cancelable}<br>
		 * If the event is cancelled, the entity will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre extends Entity implements ICancellableEvent {
			private final PoseStack poseStack;
			private final BakedHydraModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Pre(HydraEntityRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedHydraModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}
		}

		/**
		 * Post-render event for entities being rendered by {@link HydraEntityRenderer}
		 * <p>
		 * This event is called after {@link HydraRenderer#postRender}
		 */
		public static class Post extends Entity {
			private final PoseStack poseStack;
			private final BakedHydraModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Post(HydraEntityRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedHydraModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}
		}

		/**
		 * One-time event for a {@link HydraEntityRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers extends Entity {
			public CompileRenderLayers(HydraEntityRenderer<?> renderer) {
				super(renderer);
			}

			/**
			 * Adds a {@link HydraRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(HydraRenderLayer renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}
		}
	}

	/**
	 * Renderer events for {@link ItemStack Items} being rendered by {@link HydraItemRenderer}
	 */
	abstract class Item extends Event implements HydraRenderEvent {
		private final HydraItemRenderer<?> renderer;

		public Item(HydraItemRenderer<?> renderer) {
			this.renderer = renderer;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public HydraItemRenderer<?> getRenderer() {
			return this.renderer;
		}

		/**
		 * Shortcut method for retrieving the ItemStack being rendered
		 */
		public ItemStack getItemStack() {
			return getRenderer().getCurrentItemStack();
		}

		/**
		 * Pre-render event for armor being rendered by {@link HydraItemRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link HydraRenderer#preRender}
		 * <p>
		 * This event is {@link ICancellableEvent Cancelable}<br>
		 * If the event is cancelled, the ItemStack will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre extends Item implements ICancellableEvent {
			private final PoseStack poseStack;
			private final BakedHydraModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Pre(HydraItemRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedHydraModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}
		}

		/**
		 * Post-render event for ItemStacks being rendered by {@link HydraItemRenderer}
		 * <p>
		 * This event is called after {@link HydraRenderer#postRender}
		 */
		public static class Post extends Item {
			private final PoseStack poseStack;
			private final BakedHydraModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Post(HydraItemRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedHydraModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}
		}

		/**
		 * One-time event for a {@link HydraItemRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers extends Item {
			public CompileRenderLayers(HydraItemRenderer<?> renderer) {
				super(renderer);
			}

			/**
			 * Adds a {@link HydraRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(HydraRenderLayer renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}
		}
	}

	/**
	 * Renderer events for miscellaneous {@link HydraAnimatable animatables} being rendered by {@link HydraObjectRenderer}
	 */
	abstract class Object extends Event implements HydraRenderEvent {
		private final HydraObjectRenderer<?> renderer;

		public Object(HydraObjectRenderer<?> renderer) {
			this.renderer = renderer;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public HydraObjectRenderer<?> getRenderer() {
			return this.renderer;
		}

		/**
		 * Pre-render event for miscellaneous animatables being rendered by {@link HydraObjectRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link HydraRenderer#preRender}
		 * <p>
		 * This event is {@link ICancellableEvent Cancelable}<br>
		 * If the event is cancelled, the object will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre extends Object implements ICancellableEvent {
			private final PoseStack poseStack;
			private final BakedHydraModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Pre(HydraObjectRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedHydraModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}
		}

		/**
		 * Post-render event for miscellaneous animatables being rendered by {@link HydraObjectRenderer}
		 <p>
		 * This event is called after {@link HydraRenderer#postRender}
		 */
		public static class Post extends Object {
			private final PoseStack poseStack;
			private final BakedHydraModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Post(HydraObjectRenderer<?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedHydraModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}
		}

		/**
		 * One-time event for a {@link HydraObjectRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers extends Object {
			public CompileRenderLayers(HydraObjectRenderer<?> renderer) {
				super(renderer);
			}

			/**
			 * Adds a {@link HydraRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(HydraRenderLayer renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}
		}
	}

	/**
	 * Renderer events for miscellaneous {@link com.github.syren_dev_tech.hydra.animatable.HydraReplacedEntity replaced entities} being rendered by {@link HydraReplacedEntityRenderer}
	 */
	abstract class ReplacedEntity extends Event implements HydraRenderEvent {
		private final HydraReplacedEntityRenderer<?, ?> renderer;

		public ReplacedEntity(HydraReplacedEntityRenderer<?, ?> renderer) {
			this.renderer = renderer;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public HydraReplacedEntityRenderer<?, ?> getRenderer() {
			return this.renderer;
		}

		/**
		 * Shortcut method to get the Entity currently being rendered
		 */
		public net.minecraft.world.entity.Entity getReplacedEntity() {
			return getRenderer().getCurrentEntity();
		}

		/**
		 * Pre-render event for replaced entities being rendered by {@link HydraReplacedEntityRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link HydraRenderer#preRender}
		 * <p>
		 * This event is {@link ICancellableEvent Cancelable}<br>
		 * If the event is cancelled, the entity will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre extends ReplacedEntity implements ICancellableEvent {
			private final PoseStack poseStack;
			private final BakedHydraModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Pre(HydraReplacedEntityRenderer<?, ?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedHydraModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}
		}

		/**
		 * Post-render event for replaced entities being rendered by {@link HydraReplacedEntityRenderer}
		 * <p>
		 * This event is called after {@link HydraRenderer#postRender}
		 */
		public static class Post extends ReplacedEntity {
			private final PoseStack poseStack;
			private final BakedHydraModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Post(HydraReplacedEntityRenderer<?, ?> renderer, PoseStack poseStack, BakedHydraModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}

			public BakedHydraModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}
		}

		/**
		 * One-time event for a {@link HydraReplacedEntityRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers extends ReplacedEntity {
			public CompileRenderLayers(HydraReplacedEntityRenderer<?, ?> renderer) {
				super(renderer);
			}

			/**
			 * Adds a {@link HydraRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(HydraRenderLayer renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}
		}
	}
}
