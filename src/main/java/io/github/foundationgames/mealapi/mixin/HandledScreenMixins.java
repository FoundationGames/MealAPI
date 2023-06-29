package io.github.foundationgames.mealapi.mixin;

import io.github.foundationgames.mealapi.util.MAUtil;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = {HandledScreen.class, CreativeInventoryScreen.class})
public class HandledScreenMixins {
    @Inject(method = "getTooltipFromItem", at = @At("HEAD"))
    private void mealapi$cacheItemForTooltip(ItemStack stack, CallbackInfoReturnable<List<Text>> cir) {
        MAUtil.TOOLTIP_STACK = stack;
    }
}
