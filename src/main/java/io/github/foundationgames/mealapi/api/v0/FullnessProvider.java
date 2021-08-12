package io.github.foundationgames.mealapi.api.v0;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * An interface used when registering
 * meal items.
 *
 * @author FoundationGames
 */
@FunctionalInterface
public interface FullnessProvider {
    /**
     * The function that is run to determine
     * the restored fullness of an item. Called
     * before the restoration of the player's
     * hunger and saturation, and before the item
     * is consumed.
     *
     * @param player The player whose fullness is being restored
     * @param stack  The food item stack, pre-consumption
     *
     * @return The amount of fullness to try to restore to the player.
     */
    int getFullness(PlayerEntity player, ItemStack stack);
}
