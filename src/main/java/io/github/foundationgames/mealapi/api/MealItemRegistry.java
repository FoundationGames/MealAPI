package io.github.foundationgames.mealapi.api;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * A class used to register items as meal items,
 * and query the fullness value restored by those
 * registered items.
 *
 * @author FoundationGames
 */
public final class MealItemRegistry {
    private static final Map<Item, BiFunction<PlayerEntity, ItemStack, Integer>> ENTRIES = Maps.newHashMap();

    /**
     * Registers an {@code Item} as a meal item, allowing it
     * to restore a player's fullness when eaten in game.
     *
     * @param item The item being registered as a meal item.
     * @param fullness A function which is called before a player's
     *                 hunger and saturation have been restored upon
     *                 eating a food item, providing the fullness
     *                 value to restore for the player.
     */
    public static void register(Item item, BiFunction<PlayerEntity, ItemStack, Integer> fullness) {
        ENTRIES.put(item, fullness);
    }

    /**
     * Provides the fullness value that will be restored
     * by an {@code ItemStack}. Requires the {@code PlayerEntity}
     * for context.
     *
     * @param player The player whose fullness is to be restored.
     * @param stack The item that the player is eating.
     * @return The fullness value, determined by the function registered to the provided {@code ItemStack}'s {@code Item}.
     */
    public static int getFullness(PlayerEntity player, ItemStack stack) {
        if(ENTRIES.containsKey(stack.getItem())) return ENTRIES.get(stack.getItem()).apply(player, stack);
        return 0;
    }
}
