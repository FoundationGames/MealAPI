package io.github.foundationgames.mealapi.mixin;

import io.github.foundationgames.mealapi.util.HudRenderUtil;
import io.github.foundationgames.mealapi.util.TooltipRenderUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.appleskin.client.TooltipOverlayHandler;

@Mixin(value = TooltipOverlayHandler.class)
public class ASTooltipOverlayHandlerMixin {
    @ModifyVariable(method = "onRenderTooltip", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableLighting()V", ordinal = 0), index = 25)
    private static int modifyBottomY(int bottomY, MatrixStack matrixStack, ItemStack hoveredStack, int toolTipX, int toolTipY, int toolTipW, int toolTipH) {
        return TooltipRenderUtil.getModifiedBottomY(bottomY, matrixStack, hoveredStack, toolTipX, toolTipY, toolTipW, toolTipH);
    }

    @ModifyVariable(method = "onRenderTooltip", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableLighting()V", ordinal = 0), index = 23)
    private static int modifyLeftX(int leftX, MatrixStack matrixStack, ItemStack hoveredStack, int toolTipX, int toolTipY, int toolTipW, int toolTipH) {
        return TooltipRenderUtil.getModifiedLeftX(leftX, matrixStack, hoveredStack, toolTipX, toolTipY, toolTipW, toolTipH);
    }

    @ModifyVariable(method = "onRenderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureManager;bindTexture(Lnet/minecraft/util/Identifier;)V", ordinal = 0), index = 28)
    private static int modifyY(int y, MatrixStack matrixStack, ItemStack hoveredStack, int toolTipX, int toolTipY, int toolTipW, int toolTipH) {
        return TooltipRenderUtil.getModifiedY(y, matrixStack, hoveredStack, toolTipX, toolTipY, toolTipW, toolTipH);
    }

    @Inject(remap = false, method = "onRenderTooltip", at = @At("TAIL"))
    private static void drawFullness(MatrixStack matrixStack, ItemStack hoveredStack, int toolTipX, int toolTipY, int toolTipW, int toolTipH, CallbackInfo ci) {
        HudRenderUtil.drawAppleSkinTooltip(matrixStack, hoveredStack, toolTipX, toolTipY, toolTipW, toolTipH);
    }
}
