package io.github.foundationgames.mealapi.mixin;

import io.github.foundationgames.mealapi.util.HudRenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;
    
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "renderStatusBars", at = @At(value = "TAIL"))
    private void mealapi$renderFullnessBar(DrawContext context, CallbackInfo ci) {
        if(!(MinecraftClient.getInstance().player.getVehicle() instanceof LivingEntity)) {
            HudRenderUtil.renderFullnessBar(context, scaledWidth, scaledHeight, client.player, MinecraftClient.getInstance().getTickDelta());
        }
    }
}
