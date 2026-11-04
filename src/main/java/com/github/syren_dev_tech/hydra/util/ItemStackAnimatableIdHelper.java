package com.github.syren_dev_tech.hydra.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for managing animatable IDs stored on ItemStacks using NBT
 * <p>
 * This replaces the platform-dependent DataComponentType approach with direct NBT storage
 */
public final class ItemStackAnimatableIdHelper {
    private static final String NBT_KEY = "HydraAnimatableId";

    /**
     * Gets the unique identifying number from this ItemStack's NBT, or Long.MAX_VALUE if one hasn't been assigned
     *
     * @param stack The ItemStack to get the ID from
     * @return The stored animatable ID, or Long.MAX_VALUE if not set
     */
    public static long getId(ItemStack stack) {
        // Fetch the custom data component from the item stack
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);

        if (customData != null) {
            // Access the underlying tag directly without copying it (highly efficient for read-only checks)
            CompoundTag tag = customData.copyTag();
            if (tag.contains(NBT_KEY, Tag.TAG_LONG)) {
                return tag.getLong(NBT_KEY);
            }
        }
        return Long.MAX_VALUE;
    }

    /**
     * Gets the unique identifying number from this ItemStack's NBT
     * <p>
     * If no ID has been reserved for this stack yet, it will reserve a new id and assign it
     *
     * @param stack The ItemStack to get or assign an ID to
     * @param newId The new ID to assign if one doesn't exist
     * @return The stored or newly assigned animatable ID
     */
    public static long getOrAssignId(ItemStack stack, long newId) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag;

        if (customData != null) {
            tag = customData.copyTag();
            if (tag.contains(NBT_KEY, Tag.TAG_LONG)) {
                return tag.getLong(NBT_KEY);
            }
        } else {
            tag = new CompoundTag();
        }

        tag.putLong(NBT_KEY, newId);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return newId;
    }

    /**
     * Sets the animatable ID on this ItemStack's NBT
     *
     * @param stack The ItemStack to set the ID on
     * @param id The ID to set
     */
    public static void setId(ItemStack stack, long id) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = customData != null ? customData.copyTag() : new CompoundTag();
        tag.putLong(NBT_KEY, id);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /**
     * Checks if this ItemStack has an animatable ID stored
     *
     * @param stack The ItemStack to check
     * @return true if an ID is stored, false otherwise
     */
    public static boolean hasId(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            return tag.contains(NBT_KEY, Tag.TAG_LONG);
        }
        return false;
    }

    /**
     * Removes the animatable ID from this ItemStack's NBT
     *
     * @param stack The ItemStack to remove the ID from
     */
    public static void removeId(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains(NBT_KEY)) {
                tag.remove(NBT_KEY);
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
        }
    }

    /**
     * Gets the stored ID with a default value if not present
     *
     * @param stack The ItemStack to get the ID from
     * @param defaultValue The value to return if no ID is stored
     * @return The stored ID or the default value
     */
    public static long getOrDefault(ItemStack stack, long defaultValue) {
        long id = getId(stack);
        return id == Long.MAX_VALUE ? defaultValue : id;
    }

    private ItemStackAnimatableIdHelper() {}
}
