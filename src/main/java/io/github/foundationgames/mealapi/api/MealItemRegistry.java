package io.github.foundationgames.mealapi.api;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.function.BiFunction;

public final class MealItemRegistry {
    private static final Map<Item, BiFunction<PlayerEntity, ItemStack, Integer>> ENTRIES = Maps.newHashMap();

    public static void register(Item item, BiFunction<PlayerEntity, ItemStack, Integer> fullness) {
        ENTRIES.put(item, fullness);
    }

    public static int getFullness(PlayerEntity player, Item item, ItemStack stack) {
        if(ENTRIES.containsKey(item)) return ENTRIES.get(item).apply(player, stack);
        return 0;
    }
}
