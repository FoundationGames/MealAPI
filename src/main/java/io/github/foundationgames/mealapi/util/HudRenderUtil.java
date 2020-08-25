package io.github.foundationgames.mealapi.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class HudRenderUtil {
    public static void renderFullnessBar(MatrixStack matrices, int scaledWidth, int scaledHeight, DrawableHelper draw, PlayerEntity player) {
        int x = (scaledWidth / 2)+82;
        int y = scaledHeight - 39;
        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("mealapi:textures/gui/mealapi_icons.png"));

        int fullness = PlayerFullnessManager.getClientFullness();
        final int maxFullness = PlayerFullnessManager.MAX_FULLNESS;

        for (int i = 0; i < 10; i++) {
            int wid = (int) MAUtil.minimum(MAUtil.maximum((((float)fullness / maxFullness) * 60)-((i)*6), 6), 0);
            draw.drawTexture(matrices, x-i*8, y, 9*wid, player.hasStatusEffect(StatusEffects.HUNGER) ? 9 : 0, 9, 9);
        }
    }
}
