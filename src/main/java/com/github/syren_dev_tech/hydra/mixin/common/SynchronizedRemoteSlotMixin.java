package com.github.syren_dev_tech.hydra.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.inventory.RemoteSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.github.syren_dev_tech.hydra.HydraConstants;

/// Injection into the RemoteSlot handling to allow for bypassing Hydra ItemStack ID parity
@Mixin(RemoteSlot.Synchronized.class)
public class SynchronizedRemoteSlotMixin {
    /// In `ItemStackMixin#hydra$skipHydraIdOnCompare`, we tell Minecraft to ignore the contents of Hydra
    /// stack ids for the purposes of ItemStack parity.
    ///
    /// We temporarily reinstate it here so that the game syncs changes to this specific component
    @WrapOperation(method = "matches", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    public boolean hydra$allowLazyStackIdParity(ItemStack remoteStack, ItemStack localStack, Operation<Boolean> original) {
        return original.call(remoteStack, localStack) && remoteStack.getOrDefault(HydraConstants.STACK_ANIMATABLE_ID_COMPONENT.get(), Integer.MIN_VALUE).equals(localStack.getOrDefault(HydraConstants.STACK_ANIMATABLE_ID_COMPONENT.get(), Integer.MIN_VALUE));
    }
}
