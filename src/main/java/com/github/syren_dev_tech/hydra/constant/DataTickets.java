package com.github.syren_dev_tech.hydra.constant;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import com.github.syren_dev_tech.hydra.HydraConstants;
import com.github.syren_dev_tech.hydra.constant.dataticket.DataTicket;
import com.github.syren_dev_tech.hydra.model.data.EntityModelData;
import com.github.syren_dev_tech.hydra.constant.dataticket.SerializableDataTicket;
import com.github.syren_dev_tech.hydra.util.HydraUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores the default (builtin) {@link DataTicket DataTickets} used in Hydra
 * <p>
 * Additionally handles registration of {@link SerializableDataTicket SerializableDataTickets}
 */
public final class DataTickets {
	private static final Map<String, SerializableDataTicket<?>> SERIALIZABLE_TICKETS = new ConcurrentHashMap<>();
	
	// Builtin tickets
	// These tickets are used by Hydra by default, usually added in by the HydraRenderer for use in animations
	public static final DataTicket<BlockEntity> BLOCK_ENTITY = new DataTicket<>("block_entity", BlockEntity.class);
	public static final DataTicket<ItemStack> ITEMSTACK = new DataTicket<>("itemstack", ItemStack.class);
	public static final DataTicket<Entity> ENTITY = new DataTicket<>("entity", Entity.class);
	public static final DataTicket<EquipmentSlot> EQUIPMENT_SLOT = new DataTicket<>("equipment_slot", EquipmentSlot.class);
	public static final DataTicket<EntityModelData> ENTITY_MODEL_DATA = new DataTicket<>("entity_model_data", EntityModelData.class);
	public static final DataTicket<Double> TICK = new DataTicket<>("tick", Double.class);
	public static final DataTicket<ItemDisplayContext> ITEM_RENDER_PERSPECTIVE = new DataTicket<>("item_render_perspective", ItemDisplayContext.class);
	
	// Builtin serializable tickets
	// These are not used anywhere by default, but are provided as examples and for ease of use
	public static final SerializableDataTicket<Integer> ANIM_STATE = HydraUtil.addDataTicket(SerializableDataTicket.ofInt(HydraConstants.id("anim_state")));
	public static final SerializableDataTicket<String> ANIM = HydraUtil.addDataTicket(SerializableDataTicket.ofString(HydraConstants.id("anim")));
	public static final SerializableDataTicket<Integer> USE_TICKS = HydraUtil.addDataTicket(SerializableDataTicket.ofInt(HydraConstants.id("use_ticks")));
	public static final SerializableDataTicket<Boolean> ACTIVE = HydraUtil.addDataTicket(SerializableDataTicket.ofBoolean(HydraConstants.id("active")));
	public static final SerializableDataTicket<Boolean> OPEN = HydraUtil.addDataTicket(SerializableDataTicket.ofBoolean(HydraConstants.id("open")));
	public static final SerializableDataTicket<Boolean> CLOSED = HydraUtil.addDataTicket(SerializableDataTicket.ofBoolean(HydraConstants.id("closed")));
	public static final SerializableDataTicket<Direction> DIRECTION = HydraUtil.addDataTicket(SerializableDataTicket.ofEnum(HydraConstants.id("direction"), Direction.class));

	@Nullable
	public static SerializableDataTicket<?> byName(String id) {
		return SERIALIZABLE_TICKETS.getOrDefault(id, null);
	}

	/**
	 * Register a {@link SerializableDataTicket} with Hydra for handling custom data transmission
	 * <p>
	 * It is recommended you don't call this directly, and instead call it via {@link HydraUtil#addDataTicket}
	 *
	 * @param ticket The SerializableDataTicket instance to register
	 * @return The registered instance
	 */
	public static <D> SerializableDataTicket<D> registerSerializable(SerializableDataTicket<D> ticket) {
		SerializableDataTicket<?> existingTicket = SERIALIZABLE_TICKETS.putIfAbsent(ticket.id(), ticket);

		if (existingTicket != null)
			HydraConstants.LOGGER.error("Duplicate SerializableDataTicket registered! This will cause issues. Existing: " + existingTicket.id() + ", New: " + ticket.id());

		return ticket;
	}
}
