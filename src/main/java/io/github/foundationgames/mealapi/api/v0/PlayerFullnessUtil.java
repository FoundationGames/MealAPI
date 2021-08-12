package io.github.foundationgames.mealapi.api.v0;

import io.github.foundationgames.mealapi.impl.PlayerFullnessUtilImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Used to manage and manipulate
 * players' fullness values on both the client
 * and server side.
 *
 * @author FoundationGames
 */
public interface PlayerFullnessUtil {
    /**
     * Get the instance of this interface's implementation.
     *
     * @return The impl of v0.PlayerFullnessUtil
     */
    static PlayerFullnessUtil instance() {
        return PlayerFullnessUtilImpl.INSTANCE;
    }

    /**
     * Sets a {@code ServerPlayerEntity}'s fullness to a certain
     * integer value.
     *
     * This method is to only be used server-side, make sure to
     * check {@code !world.isClient} before calling.
     *
     * @param player The player whose fullness is to be set.
     * @param fullness The value to set the player's fullness to.
     */
    void setPlayerFullness(ServerPlayerEntity player, int fullness);

    /**
     * Gets a {@code ServerPlayerEntity}'s fullness.
     *
     * This method is to only be used server-side, make sure to
     * check {@code !world.isClient} before calling.
     *
     * @param player The player whose fullness value is being retrieved.
     * @return The player's fullness value.
     */
    int getPlayerFullness(ServerPlayerEntity player);

    /**
     * Gets the last synced client player fullness.
     *
     * This method is to only be used client-side, make sure that
     * it is only called in {@code EnvType.CLIENT}
     *
     * @return The client's last synced player fullness value.
     */
    @Environment(EnvType.CLIENT)
    int getClientFullness();

    /**
     * Gets the maximum fullness value a player can
     * have. To be used for reference.
     *
     * @return The maximum fullness value a player can have
     */
    int getMaxFullness();

    /**
     * Adds fullness to a player, taking into account
     * the hunger restoration of the item they are
     * eating.
     *
     * This method is to only be used server-side, make sure to
     * check {@code !world.isClient} before calling.
     *
     * @param player The player whose fullness value is being increased.
     * @param f The fullness value to be added to the player.
     * @param food The item being eaten, for context.
     */
    void addFullness(ServerPlayerEntity player, int f, ItemStack food);

    /**
     * Adds fullness to a player without the context of
     * a specific item being eaten.
     *
     * This method is to only be used server-side, make sure to
     * check {@code !world.isClient} before calling.
     *
     * @param player The player whose fullness value is being increased.
     * @param f The fullness value to be added to the player.
     */
    void addFullness(ServerPlayerEntity player, int f);

    /**
     * Gets the amount of fullness restored by an
     * item after filling the player's hunger and saturation.
     * Does not actually restore the player's hunger or saturation.
     *
     * This method can be used on both the client
     * and server sides.
     *
     * @param player The player whose fullness is to be restored.
     * @param stack The item being eaten.
     *
     * @return The fullness value that will be applied to a player after it is split off to restore their hunger and saturation.
     */
    int getHealedFullness(PlayerEntity player, ItemStack stack);
}
