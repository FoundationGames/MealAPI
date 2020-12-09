package io.github.foundationgames.mealapi.util;

import com.mojang.datafixers.util.Pair;
import io.github.foundationgames.mealapi.api.MealItemRegistry;
import io.github.foundationgames.mealapi.api.PlayerFullnessManager;
import io.github.foundationgames.mealapi.config.MealAPIConfig.DefaultedYesNo;
import io.github.foundationgames.mealapi.config.MealAPIConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public final class HudRenderUtil {
    private static float flashAlpha = 0f;
    private static boolean flashUp = false;
    public static void renderFullnessBar(MatrixStack matrices, int scaledWidth, int scaledHeight, DrawableHelper draw, PlayerEntity player, float delta) {
        int x = (scaledWidth / 2)+82;
        int y = scaledHeight - 39;
        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("mealapi:textures/gui/mealapi_icons.png"));

        int fullness = PlayerFullnessManager.getClientFullness();
        final int maxFullness = PlayerFullnessManager.MAX_FULLNESS;
        MealAPIConfig cfg = AutoConfig.getConfigHolder(MealAPIConfig.class).getConfig(); float opacity = ((float)cfg.fullnessBarOpacityPct / 100);
        boolean border = cfg.fullnessIconBorders == DefaultedYesNo.YES || (cfg.fullnessIconBorders == DefaultedYesNo.DEFAULT && !appleSkin());
        for (int i = 0; i < 10; i++) {
            MAUtil.alpha(opacity);
            int wid = (int) MAUtil.minimum(MAUtil.maximum((((float)fullness / maxFullness) * 60)-((i)*6), 6), 0);
            draw.drawTexture(matrices, x-i*8, y, 9*wid, (player.hasStatusEffect(StatusEffects.HUNGER) ? 9 : 0) + (border ? 0 : 18), 9, 9);
        }
        MAUtil.alpha(1f);
        ItemStack renderStack;
        if(cfg.showFlashingFullnessPreview == DefaultedYesNo.YES || (cfg.showFlashingFullnessPreview == DefaultedYesNo.DEFAULT && appleSkin())) {
            if (MealItemRegistry.getFullness(player, player.getStackInHand(Hand.MAIN_HAND)) <= 0) renderStack = player.getStackInHand(Hand.OFF_HAND);
            else renderStack = player.getStackInHand(Hand.MAIN_HAND);
            if (MealItemRegistry.getFullness(player, renderStack) > 0) {
                MAUtil.alpha(flashAlpha * opacity);
                for (int i = 0; i < 10; i++) {
                    int potentialFull = (int) MAUtil.maximum(fullness + PlayerFullnessManager.getHealedFullness(player, renderStack), maxFullness);
                    int wid = (int) MAUtil.minimum(MAUtil.maximum((((float) potentialFull / maxFullness) * 60) - ((i) * 6), 6), 0);
                    boolean full = (fullness - i * (maxFullness / 10)) > (maxFullness / 10);
                    if(!full) draw.drawTexture(matrices, x - i * 8, y, 9 * wid, (poisonous(renderStack) ? 9 : 0) + (border ? 0 : 18), 9, 9);
                }
            } else {
                flashAlpha = 0.0F;
                flashUp = true;
            }
            MAUtil.alpha(1f);
        }
    }

    public static void drawAppleSkinTooltip(MatrixStack matrices, ItemStack hoveredStack, int toolTipX, int toolTipY, int toolTipW, int toolTipH) {
        Screen draw = MinecraftClient.getInstance().currentScreen;
        PlayerEntity player = MinecraftClient.getInstance().player;
        int fullness = MealItemRegistry.getFullness(player, hoveredStack);
        final int maxFullness = PlayerFullnessManager.MAX_FULLNESS;
        MealAPIConfig cfg = AutoConfig.getConfigHolder(MealAPIConfig.class).getConfig();
        int x = toolTipX + toolTipW - 7;
        int y = toolTipY + toolTipH + 23;
        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("mealapi:textures/gui/mealapi_icons.png"));
        for (int i = 0; i < 10; i++) {
            int wid = (int) MAUtil.minimum(MAUtil.maximum((((float)fullness / maxFullness) * 60)-((i)*6), 6), 0);
            draw.drawTexture(matrices, x-i*6, y, 7*wid, poisonous(hoveredStack) ? 43 : 36, 7, 7);
        }
    }

    public static void initClient() {
        ClientTickCallback.EVENT.register(client -> {
            clientTick();
        });
    }

    private static boolean appleSkin() {
        return FabricLoader.INSTANCE.isModLoaded("appleskin");
    }

    private static boolean poisonous(ItemStack food) {
        boolean b = false;
        if(food.isFood()) {
            for(Pair<StatusEffectInstance, Float> f : food.getItem().getFoodComponent().getStatusEffects()) {
                if(f.getFirst().getEffectType().getType() == StatusEffectType.HARMFUL) b = true;
            }
        }
        return b;
    }

    private static void clientTick() {
        flashAlpha += (flashUp ? 1 : -1) * 0.125f;
        if (flashAlpha >= 1.5f) {
            flashAlpha = 1f;
            flashUp = false;
        } else if (flashAlpha <= -0.5f) {
            flashAlpha = 0f;
            flashUp = true;
        }
    }
}
