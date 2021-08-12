package io.github.foundationgames.mealapi.util.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.foundationgames.mealapi.impl.PlayerFullnessUtilImpl;
import io.github.foundationgames.mealapi.util.HudRenderUtil;
import io.github.foundationgames.mealapi.util.MAUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class FullnessTooltipComponent implements TooltipComponent {
    private final ItemStack food;
    private final PlayerEntity player;

    public FullnessTooltipComponent(ItemStack food, PlayerEntity player) {
        this.food = food;
        this.player = player;
    }

    public int getFullness() {
        return PlayerFullnessUtilImpl.INSTANCE.getHealedFullness(player, food);
    }

    @Override
    public int getHeight() {
        return 9;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        final int maxFullness = PlayerFullnessUtilImpl.INSTANCE.getMaxFullness();
        return (int)(Math.ceil(((float)Math.min(getFullness(), maxFullness) / maxFullness) * 10)) * 7;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z, TextureManager textureManager) {
        var draw = MinecraftClient.getInstance().currentScreen;
        final int maxFullness = PlayerFullnessUtilImpl.INSTANCE.getMaxFullness();
        int fullness = Math.min(getFullness(), maxFullness);
        float fullnessF = (float)fullness / maxFullness;
        RenderSystem.setShaderTexture(0, HudRenderUtil.ICONS_TEX);
        int right = (((int)Math.ceil(Math.floor((fullnessF) * 60) / 6)) * 7) - 7;
        for (int i = 0; i < 10; i++) {
            int wid = (int) Math.max(Math.min(((fullnessF) * 60)-((i)*6), 6), 0);
            draw.drawTexture(matrices, right + (x-i*7), y - 1, 7*wid, MAUtil.isPoisonous(food) ? 43 : 36, 7, 7);
        }
    }
}
