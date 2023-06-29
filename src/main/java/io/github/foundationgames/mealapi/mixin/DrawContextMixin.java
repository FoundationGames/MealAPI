package io.github.foundationgames.mealapi.mixin;

import io.github.foundationgames.mealapi.MealAPI;
import io.github.foundationgames.mealapi.config.MealAPIConfig;
import io.github.foundationgames.mealapi.impl.MealItemRegistryImpl;
import io.github.foundationgames.mealapi.util.MAUtil;
import io.github.foundationgames.mealapi.util.tooltip.FullnessTooltipComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DrawContext.class)
public class DrawContextMixin {
    @Final @Shadow @Nullable private MinecraftClient client;

    @Inject(method = "drawItemTooltip", at = @At("HEAD"))
    private void mealapi$cacheItemStack(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        MAUtil.TOOLTIP_STACK = stack;
    }

    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At("HEAD"))
    private void mealapi$addTooltipComponent(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, CallbackInfo ci) {
        if (MAUtil.TOOLTIP_STACK != null) {
            var cfg = MealAPI.getConfig();
            if (MealItemRegistryImpl.INSTANCE.getFullness(client.player, MAUtil.TOOLTIP_STACK) > 0 &&
                    ((cfg.getValues().showFullnessTooltip == MealAPIConfig.DefaultedYesNo.DEFAULT && MAUtil.appleSkin()) ||
                    cfg.getValues().showFullnessTooltip == MealAPIConfig.DefaultedYesNo.YES)
            ) {
                components.add(new FullnessTooltipComponent(MAUtil.TOOLTIP_STACK, MinecraftClient.getInstance().player));
            }
            MAUtil.TOOLTIP_STACK = null;
        }
    }
}
