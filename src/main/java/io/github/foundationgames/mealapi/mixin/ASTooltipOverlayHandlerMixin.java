package io.github.foundationgames.mealapi.mixin;

import io.github.foundationgames.mealapi.api.MealItemRegistry;
import io.github.foundationgames.mealapi.util.HudRenderUtil;
import io.github.foundationgames.mealapi.api.PlayerFullnessManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.appleskin.client.TooltipOverlayHandler;

@Mixin(value = TooltipOverlayHandler.class, remap = false)
public class ASTooltipOverlayHandlerMixin {
    @ModifyVariable(method = "onRenderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;IIII)V",at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableLighting()V", ordinal = 0), index = 25)
    private static int modifyBottomY(int bottomY, MatrixStack matrixStack, ItemStack hoveredStack, int toolTipX, int toolTipY, int toolTipW, int toolTipH) {
        return bottomY + (MealItemRegistry.getFullness(MinecraftClient.getInstance().player, hoveredStack) > 0 ? 8 : 0);
    }

    @ModifyVariable(method = "onRenderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;IIII)V",at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableLighting()V", ordinal = 0), index = 23)
    private static int modifyLeftX(int leftX, MatrixStack matrixStack, ItemStack hoveredStack, int toolTipX, int toolTipY, int toolTipW, int toolTipH) {
        int rightX = (toolTipX + toolTipW + 1 + 3) - 3;
        int fullnessWid = ((int)(Math.ceil((float)MealItemRegistry.getFullness(MinecraftClient.getInstance().player, hoveredStack) / ((float)PlayerFullnessManager.MAX_FULLNESS / 10)) * 6) + 3);
        return Math.min(leftX, rightX - fullnessWid);
    }

    @ModifyVariable(method = "onRenderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;IIII)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureManager;bindTexture(Lnet/minecraft/util/Identifier;)V", ordinal = 0), index = 28)
    private static int modifyY(int y, MatrixStack matrixStack, ItemStack hoveredStack, int toolTipX, int toolTipY, int toolTipW, int toolTipH) {
        return y - (MealItemRegistry.getFullness(MinecraftClient.getInstance().player, hoveredStack) > 0 ? 8 : 0);
    }

    @Inject(method = "onRenderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;IIII)V", at = @At("TAIL"))
    private static void drawFullness(MatrixStack matrixStack, ItemStack hoveredStack, int toolTipX, int toolTipY, int toolTipW, int toolTipH, CallbackInfo ci) {
        HudRenderUtil.drawAppleSkinTooltip(matrixStack, hoveredStack, toolTipX, toolTipY, toolTipW, toolTipH);
    }
}
