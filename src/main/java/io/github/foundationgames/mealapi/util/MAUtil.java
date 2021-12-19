package io.github.foundationgames.mealapi.util;

import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public final class MAUtil {
    public static final String MOD_ID = "mealapi";

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static double split(AtomicDouble value, double amount, double minimum) {
        double val = value.get();
        double nv = (int)Math.max(val - amount, minimum);
        value.set(nv);
        return val - nv;
    }

    public static double split(AtomicDouble value, double amount) {
        return split(value, amount, 0);
    }

    public static void alpha(float alpha) {
        if (alpha < 1f) {
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1, 1, 1, alpha);
        } else {
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }

    public static boolean isPoisonous(ItemStack food) {
        if (food.isFood()) {
            for(var effectPair : food.getItem().getFoodComponent().getStatusEffects()) {
                if(effectPair.getFirst().getEffectType().getCategory() == StatusEffectCategory.HARMFUL) return true;
            }
        }
        return false;
    }

    public static boolean appleSkin() {
        return FabricLoader.getInstance().isModLoaded("appleskin");
    }
}
