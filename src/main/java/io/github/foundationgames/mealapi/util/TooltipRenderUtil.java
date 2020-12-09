package io.github.foundationgames.mealapi.util;

import io.github.foundationgames.mealapi.api.MealItemRegistry;
import io.github.foundationgames.mealapi.api.PlayerFullnessManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

/*
 * This is so that there are no remapping issues with
 * the mixin class when used in production environment
 *
 * For this mod's internal use only
 */
public final class TooltipRenderUtil {
    public static int getModifiedBottomY(int bottomY, MatrixStack matrixStack, ItemStack hoveredStack, int toolTipX, int toolTipY, int toolTipW, int toolTipH) {
        return bottomY + (MealItemRegistry.getFullness(MinecraftClient.getInstance().player, hoveredStack) > 0 ? 8 : 0);
    }

    public static int getModifiedLeftX(int leftX, MatrixStack matrixStack, ItemStack hoveredStack, int toolTipX, int toolTipY, int toolTipW, int toolTipH) {
        int rightX = (toolTipX + toolTipW + 1 + 3) - 3;
        int fullnessWid = ((int)(Math.ceil((float)MealItemRegistry.getFullness(MinecraftClient.getInstance().player, hoveredStack) / ((float) PlayerFullnessManager.MAX_FULLNESS / 10)) * 6) + 3);
        return Math.min(leftX, rightX - fullnessWid);
    }

    public static int getModifiedY(int y, MatrixStack matrixStack, ItemStack hoveredStack, int toolTipX, int toolTipY, int toolTipW, int toolTipH) {
        return y - (MealItemRegistry.getFullness(MinecraftClient.getInstance().player, hoveredStack) > 0 ? 8 : 0);
    }
}
