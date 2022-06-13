package io.github.foundationgames.mealapi.mixin;

import io.github.foundationgames.mealapi.api.v0.PlayerFullnessUtil;
import io.github.foundationgames.mealapi.util.HungerManagerAccess;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class HungerManagerMixin implements HungerManagerAccess {
    @Shadow private float exhaustion;
    @Shadow private int foodLevel;
    @Shadow private float saturationLevel;

    private int mealapi$fullness;

    @Inject(method = "update", at = @At(value = "HEAD"))
    private void mealapi$updateFullness(PlayerEntity player, CallbackInfo ci) {
        if (this.exhaustion > 4.0F) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                if (PlayerFullnessUtil.instance().getPlayerFullness(serverPlayer) > 0) {
                    PlayerFullnessUtil.instance().setPlayerFullness(serverPlayer, PlayerFullnessUtil.instance().getPlayerFullness(serverPlayer) - 1);
                }
            }
        }
    }

    @Inject(method = "update", at = @At(value = "TAIL"))
    private void mealapi$applyEffectsFromFullness(PlayerEntity player, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (PlayerFullnessUtil.instance().getPlayerFullness(serverPlayer) > 0) {
                foodLevel = 20;
                saturationLevel = 20.0f;
            }
        } else if (PlayerFullnessUtil.instance().getClientFullness() > 0) {
            foodLevel = 20;
            saturationLevel = 20.0f;
        }
    }

    @Override
    public int mealapi$getFullness() {
        return mealapi$fullness;
    }

    @Override
    public void mealapi$setFullness(int amount) {
        this.mealapi$fullness = Math.min(amount, PlayerFullnessUtil.instance().getMaxFullness());
    }
}
