package io.github.foundationgames.mealapi.mixin;

import io.github.foundationgames.mealapi.util.HudRenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin extends DrawableHelper {
    @Shadow public int scaledWidth;
    @Shadow public int scaledHeight;

    @Shadow private PlayerEntity getCameraPlayer() { return null; }

    @Inject(method = "renderStatusBars", at = @At(value = "TAIL"))
    private void renderFullnessBar(MatrixStack matrices, CallbackInfo ci) {
        if(MinecraftClient.getInstance().player.getVehicle() == null)
            HudRenderUtil.renderFullnessBar(matrices, scaledWidth, scaledHeight, this, getCameraPlayer(), MinecraftClient.getInstance().getTickDelta());
    }
}
