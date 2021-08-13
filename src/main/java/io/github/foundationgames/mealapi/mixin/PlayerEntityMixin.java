package io.github.foundationgames.mealapi.mixin;

import io.github.foundationgames.mealapi.impl.PlayerFullnessUtilImpl;
import io.github.foundationgames.mealapi.impl.MealItemRegistryImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    private void mealapi$readFullness(NbtCompound tag, CallbackInfo ci) {
        if((Object)this instanceof ServerPlayerEntity) PlayerFullnessUtilImpl.INSTANCE.setPlayerFullness((ServerPlayerEntity)(Object)this, tag.getInt("fullnessLevel"));;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    private void mealapi$writeFullness(NbtCompound tag, CallbackInfo ci) {
        if((Object)this instanceof ServerPlayerEntity) tag.putInt("fullnessLevel", PlayerFullnessUtilImpl.INSTANCE.getPlayerFullness((ServerPlayerEntity)(Object)this));
        else tag.putInt("fullnessLevel", PlayerFullnessUtilImpl.INSTANCE.getClientFullness());
    }

    @Inject(method = "eatFood", at = @At(value = "HEAD"))
    private void mealapi$applyFullness(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if((Object)this instanceof ServerPlayerEntity) PlayerFullnessUtilImpl.INSTANCE.addFullness((ServerPlayerEntity)(Object)this, MealItemRegistryImpl.INSTANCE.getFullness((PlayerEntity)(Object)this, stack), stack);
    }
}
