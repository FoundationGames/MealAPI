package io.github.foundationgames.mealapi.mixin;

import io.github.foundationgames.mealapi.api.MealItemRegistry;
import io.github.foundationgames.mealapi.api.PlayerFullnessManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "readCustomDataFromTag", at = @At(value = "TAIL"))
    private void readFullness(CompoundTag tag, CallbackInfo ci) {
        if((Object)this instanceof ServerPlayerEntity) PlayerFullnessManager.fromServerPlayerTag((ServerPlayerEntity)((Object)this), tag);
    }

    @Inject(method = "writeCustomDataToTag", at = @At(value = "TAIL"))
    private void writeFullness(CompoundTag tag, CallbackInfo ci) {
        if((Object)this instanceof ServerPlayerEntity) tag.putInt("fullnessLevel", PlayerFullnessManager.getServerPlayerFullness((ServerPlayerEntity)((Object)this)));
        else tag.putInt("fullnessLevel", PlayerFullnessManager.getClientFullness());
    }

    @Inject(method = "eatFood", at = @At(value = "HEAD"))
    private void applyFullness(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        PlayerFullnessManager.addFullness((PlayerEntity)(Object)this, MealItemRegistry.getFullness((PlayerEntity)(Object)this, stack), stack);
    }
}
