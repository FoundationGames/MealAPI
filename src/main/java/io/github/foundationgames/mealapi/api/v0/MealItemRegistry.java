package io.github.foundationgames.mealapi.api.v0;

import io.github.foundationgames.mealapi.impl.MealItemRegistryImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Used to register items as meal items,
 * and query the fullness value restored by those
 * registered items.
 *
 * @author FoundationGames
 */
public interface MealItemRegistry {
    /**
     * Get the instance of this interface's implementation.
     *
     * @return The impl of v0.MealItemRegistry
     */
    static MealItemRegistry instance() {
        return MealItemRegistryImpl.INSTANCE;
    }

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
    void register(Item item, FullnessProvider fullness);

    /**
     * Provides the fullness value that will be restored
     * by an {@code ItemStack}. Requires the {@code PlayerEntity}
     * for context.
     *
     * @param player The player whose fullness is to be restored.
     * @param stack The item that the player is eating.
     * @return The fullness value, determined by the function registered to the provided {@code ItemStack}'s {@code Item}.
     */
    int getFullness(PlayerEntity player, ItemStack stack);
}
