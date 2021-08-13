package io.github.foundationgames.mealapi.util;

import io.github.foundationgames.mealapi.MealAPI;
import io.github.foundationgames.mealapi.config.MealAPIConfig;
import io.github.foundationgames.mealapi.config.MealAPIConfig.DefaultedYesNo;
import io.github.foundationgames.mealapi.impl.MealItemRegistryImpl;
import io.github.foundationgames.mealapi.impl.PlayerFullnessUtilImpl;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public final class HudRenderUtil {
    private static float flashAlpha = 0f;
    private static boolean flashUp = false;

    public static final Identifier ICONS_TEX = MAUtil.id("textures/gui/mealapi_icons.png");

    public static void renderFullnessBar(MatrixStack matrices, int scaledWidth, int scaledHeight, DrawableHelper draw, PlayerEntity player, float delta) {
        int x = (scaledWidth / 2)+82;
        int y = scaledHeight - 39;

        MealAPIConfig cfg = MealAPI.getConfig();
        float opacity = ((float)cfg.getValues().fullnessBarOpacityPct / 100);
        int fullness = PlayerFullnessUtilImpl.INSTANCE.getClientFullness();

        MAUtil.alpha(opacity);
        drawFullnessBar(matrices, x, y, draw, fullness, player.hasStatusEffect(StatusEffects.HUNGER));

        if(cfg.getValues().showFlashingFullnessPreview == DefaultedYesNo.YES || (cfg.getValues().showFlashingFullnessPreview == DefaultedYesNo.DEFAULT && MAUtil.appleSkin())) {
            ItemStack previewStack = player.getStackInHand(Hand.MAIN_HAND);
            if (MealItemRegistryImpl.INSTANCE.getFullness(player, player.getStackInHand(Hand.MAIN_HAND)) <= 0) {
                previewStack = player.getStackInHand(Hand.OFF_HAND);
            }
            if (MealItemRegistryImpl.INSTANCE.getFullness(player, previewStack) > 0) {
                MAUtil.alpha(getFlashAlpha() * opacity);
                int potentialFull = Math.min(fullness + PlayerFullnessUtilImpl.INSTANCE.getHealedFullness(player, previewStack), PlayerFullnessUtilImpl.INSTANCE.getMaxFullness());
                drawFullnessBar(matrices, x, y, draw, potentialFull, MAUtil.isPoisonous(previewStack));
            } else {
                flashAlpha = 0.0F;
                flashUp = true;
            }
            MAUtil.alpha(1f);
        }
    }

    public static void drawFullnessBar(MatrixStack matrices, int x, int y, DrawableHelper draw, int fullness, boolean poisoned) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(ICONS_TEX);

        final int maxFullness = PlayerFullnessUtilImpl.INSTANCE.getMaxFullness();
        MealAPIConfig cfg = MealAPI.getConfig();
        boolean border = cfg.getValues().fullnessIconBorders == DefaultedYesNo.YES || (cfg.getValues().fullnessIconBorders == DefaultedYesNo.DEFAULT && !MAUtil.appleSkin());
        for (int i = 0; i < 10; i++) {
            int wid = (int) Math.max(Math.min((((float)fullness / maxFullness) * 60)-((i)*6), 6), 0);
            draw.drawTexture(matrices, x-i*8, y, 9*wid, (poisoned ? 9 : 0) + (border ? 0 : 18), 9, 9);
        }
    }

    public static void initClient() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> clientTick());
    }

    private static void clientTick() {
        flashAlpha += (flashUp ? 0.125f : -0.125f);
        if (flashAlpha >= 1.5f) {
            flashUp = false;
        } else if (flashAlpha <= -0.5f) {
            flashUp = true;
        }
    }

    private static float getFlashAlpha() {
        return MathHelper.clamp(flashAlpha, 0, 1);
    }
}
