package com.github.syren_dev_tech.hydra.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.github.syren_dev_tech.hydra.util.ItemStackAnimatableIdHelper;
import com.github.syren_dev_tech.hydra.util.InternalUtil;

/**
 * Injection into ItemStack functionality to handle duplication and splitting with Hydra stack identifiers
 */
@Mixin(ItemStack.class)
public class ItemStackMixin {
    /**
     * Remove the Hydra stack ID when splitting up a stack into two
     */
    @WrapOperation(method = "split", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copyWithCount(I)Lnet/minecraft/world/item/ItemStack;"))
    public ItemStack hydra$removeHydraIdOnCopy(ItemStack instance, int count, Operation<ItemStack> original) {
        ItemStack copy = original.call(instance, count);

        if (count < instance.getCount() && ItemStackAnimatableIdHelper.hasId(copy))
            ItemStackAnimatableIdHelper.removeId(copy);

        return copy;
    }

    /**
     * Consider ItemStacks equal if the only difference is their Hydra stack ID
     * <p>
     * We do this so that the game doesn't prevent combining stacks due solely to Hydra sync IDs.
     */
    @WrapOperation(method = "isSameItemSameComponents", at = @At(value = "INVOKE", target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z"))
    private static boolean hydra$skipHydraIdOnCompare(Object a, Object b, Operation<Boolean> original) {
        if (original.call(a, b))
            return true;

        return InternalUtil.areStacksEqualIgnoringHydraId(a, b);
    }
}
