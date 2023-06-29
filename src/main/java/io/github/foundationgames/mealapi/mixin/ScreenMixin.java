package io.github.foundationgames.mealapi.mixin;

import io.github.foundationgames.mealapi.MealAPI;
import io.github.foundationgames.mealapi.config.MealAPIConfig;
import io.github.foundationgames.mealapi.impl.MealItemRegistryImpl;
import io.github.foundationgames.mealapi.util.MAUtil;
import io.github.foundationgames.mealapi.util.ScreenAccess;
import io.github.foundationgames.mealapi.util.tooltip.FullnessTooltipComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public class ScreenMixin implements ScreenAccess {
    @Shadow @Nullable protected MinecraftClient client;
    @Unique private static ItemStack mealapi$tooltipStackCache;

    @Inject(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V", at = @At("HEAD"))
    private void mealapi$cacheItemStack(MatrixStack matrices, ItemStack stack, int x, int y, CallbackInfo ci) {
        this.mealapi$cacheTooltipItem(stack);
    }

    @Inject(method = "renderTooltipFromComponents", at = @At("HEAD"))
    private void mealapi$addTooltipComponent(MatrixStack matrices, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, CallbackInfo ci) {
        if (mealapi$tooltipStackCache != null) {
            var cfg = MealAPI.getConfig();
            if (MealItemRegistryImpl.INSTANCE.getFullness(client.player, mealapi$tooltipStackCache) > 0 &&
                    ((cfg.getValues().showFullnessTooltip == MealAPIConfig.DefaultedYesNo.DEFAULT && MAUtil.appleSkin()) ||
                    cfg.getValues().showFullnessTooltip == MealAPIConfig.DefaultedYesNo.YES)
            ) {
                components.add(new FullnessTooltipComponent(mealapi$tooltipStackCache, MinecraftClient.getInstance().player));
            }
            mealapi$tooltipStackCache = null;
        }
    }

    @Override
    public void mealapi$cacheTooltipItem(ItemStack stack) {
        mealapi$tooltipStackCache = stack;
    }
}
