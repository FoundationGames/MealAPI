package io.github.foundationgames.mealapi.mixin;

import io.github.foundationgames.mealapi.util.ScreenAccess;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin implements ScreenAccess {
    @Inject(method = "renderTooltip", at = @At("HEAD"))
    private void mealapi$cacheCreativeItemForTooltip(MatrixStack matrices, ItemStack stack, int x, int y, CallbackInfo ci) {
        mealapi$cacheTooltipItem(stack);
    }
}
