package com.github.syren_dev_tech.hydra.mixin.common;

import com.github.syren_dev_tech.hydra.HydraConstants;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/// Injection into container slots to handle ItemStack cloning in relation to Hydra ItemStack IDs
@Mixin(Slot.class)
public class SlotMixin {
	/// Remove the Hydra stack ID from a stack when copying it with middle-click<br/>
	/// This prevents split-stacks from having duplicate IDs, and allows for stacks to have their own IDs when created anew from an existing one
	@WrapOperation(method = "safeClone", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copyWithCount(I)Lnet/minecraft/world/item/ItemStack;"))
	public ItemStack hydra$removeHydraIdOnCopy(ItemStack instance, int count, Operation<ItemStack> original) {
		final ItemStack copy = original.call(instance, count);
		
		if (copy.has(HydraConstants.STACK_ANIMATABLE_ID_COMPONENT.get()))
			copy.remove(HydraConstants.STACK_ANIMATABLE_ID_COMPONENT.get());
		
		return copy;
	}
}
