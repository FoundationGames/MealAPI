package io.github.foundationgames.mealapi.mixin;

import io.github.foundationgames.mealapi.api.PlayerFullnessManager;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
    @Shadow private float exhaustion;
    @Shadow private int foodLevel;
    @Shadow private float foodSaturationLevel;

    @Inject(method = "update", at = @At(value = "HEAD"))
    private void updateFullness(PlayerEntity player, CallbackInfo ci) {
        if (this.exhaustion > 4.0F) {
            if(player instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                if (PlayerFullnessManager.getServerPlayerFullness(serverPlayer) > 0) {
                    PlayerFullnessManager.setServerPlayerFullness(serverPlayer, PlayerFullnessManager.getServerPlayerFullness(serverPlayer) - 1);
                }
            }
        }
    }

    @Inject(method = "update", at = @At(value = "TAIL"))
    private void applyEffectsFromFullness(PlayerEntity player, CallbackInfo ci) {
        if(player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
            if(PlayerFullnessManager.getServerPlayerFullness(serverPlayer) > 0) {
                foodLevel = 20;
                foodSaturationLevel = 20.0f;
            }
        } else if(PlayerFullnessManager.getClientFullness() > 0) {
            foodLevel = 20;
            foodSaturationLevel = 20.0f;
        }
    }
}
