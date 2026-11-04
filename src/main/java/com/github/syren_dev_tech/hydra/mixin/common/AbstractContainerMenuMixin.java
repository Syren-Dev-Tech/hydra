package com.github.syren_dev_tech.hydra.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.github.syren_dev_tech.hydra.util.ItemStackAnimatableIdHelper;

/**
 * Injection into the base container functionality to handle ItemStack duplication and splitting with Hydra stack identifiers
 */
@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    /**
     * Remove the Hydra stack ID from a stack when copying it with middle-click
     */
    @WrapOperation(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copyWithCount(I)Lnet/minecraft/world/item/ItemStack;", ordinal = 1))
    public ItemStack hydra$removeHydraIdOnCopy(ItemStack instance, int count, Operation<ItemStack> original) {
        ItemStack copy = original.call(instance, count);

        if (ItemStackAnimatableIdHelper.hasId(copy))
            ItemStackAnimatableIdHelper.removeId(copy);

        return copy;
    }

    /**
     * Force ItemStacks that don't match their Hydra stack ID to sync, even though Hydra tells the game they're equivalent
     */
    @WrapOperation(method = "synchronizeSlotToRemote", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    public boolean hydra$forceHydraIdSync(ItemStack stack, ItemStack other, Operation<Boolean> original) {
        return original.call(stack, other) && ItemStackAnimatableIdHelper.getOrDefault(stack, -1L) == ItemStackAnimatableIdHelper.getOrDefault(other, -1L);
    }

    /**
     * Force ItemStacks that don't match their Hydra stack ID to trigger slot listeners, even though Hydra tells the game they're equivalent
     */
    @WrapOperation(method = "triggerSlotListeners", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    public boolean hydra$forceHydraSlotChange(ItemStack stack, ItemStack other, Operation<Boolean> original) {
        return original.call(stack, other) && ItemStackAnimatableIdHelper.getOrDefault(stack, -1L) == ItemStackAnimatableIdHelper.getOrDefault(other, -1L);
    }
}
