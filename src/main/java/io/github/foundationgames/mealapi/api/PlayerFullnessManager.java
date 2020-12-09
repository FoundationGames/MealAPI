package io.github.foundationgames.mealapi.api;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import io.github.foundationgames.mealapi.api.MealItemRegistry;
import io.github.foundationgames.mealapi.util.MAUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.UUID;

/**
 * @author FoundationGames
 * A class used to manage and manipulate
 * players' fullness values on both the client
 * and server side.
 */
public final class PlayerFullnessManager {
    /**
     * The maximum fullness value a player can have.
     * Use for reference.
     */
    public static final int MAX_FULLNESS = 120;

    private static final Map<ServerPlayerEntity, Integer> SERVER_PLAYER_FULLNESS_STORAGE = Maps.newHashMap();

    @Environment(EnvType.CLIENT)
    private static int CLIENT_SYNCED_FULLNESS = 0;

    /**
     * Sets a {@code ServerPlayerEntity}'s fullness, based on the data
     * in the provided NBT tag.
     *
     * This method is to only be used server-side, make sure to
     * check {@code !world.isClient} before calling.
     *
     * @param player The player whose fullness is to be set.
     * @param tag The NBT tag based on which the player's fullness
     *            is to be set.
     */
    public static void fromServerPlayerTag(ServerPlayerEntity player, CompoundTag tag) {
        setServerPlayerFullness(player, tag.getInt("fullnessLevel"));
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
    public static void setServerPlayerFullness(ServerPlayerEntity player, int fullness) {
        int f = (int) MAUtil.minimum(MAUtil.maximum(fullness, MAX_FULLNESS), 0);
        SERVER_PLAYER_FULLNESS_STORAGE.put(player, f);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(f);
        if(player.networkHandler != null) ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, MAUtil.id("update_fullness"), buf);
    }

    /**
     * Gets a {@code ServerPlayerEntity}'s fullness.
     *
     * This method is to only be used server-side, make sure to
     * check {@code !world.isClient} before calling.
     *
     * @param player The player whose fullness value is being retrieved.
     * @return The player's fullness value.
     */
    public static int getServerPlayerFullness(ServerPlayerEntity player) {
        return SERVER_PLAYER_FULLNESS_STORAGE.getOrDefault(player, 0);
    }

    /**
     * Gets the last synced client player fullness.
     *
     * This method is to only be used client-side, make sure that
     * it is only called in {@code EnvType.CLIENT}
     *
     * @return The client's last synced player fullness value.
     */
    @Environment(EnvType.CLIENT)
    public static int getClientFullness() {
        return CLIENT_SYNCED_FULLNESS;
    }

    /**
     * Adds fullness to a player, if called on the
     * server side.
     *
     * This method can be used without
     * a {@code !world.isClient}, check, but it will
     * do nothing if called on the client.
     *
     * @param player The player whose fullness value is being increased.
     * @param f The fullness value to be added to the player.
     * @param stack The item being eaten. Can be {@code ItemStack.EMPTY}
     *              if no food is being eaten.
     */
    public static void addFullness(PlayerEntity player, int f, ItemStack stack) {
        if(player instanceof ServerPlayerEntity) {
            AtomicDouble atFullness = new AtomicDouble(f);
            int h = 0; if(stack.isFood()) h = stack.getItem().getFoodComponent().getHunger();
            float s = 0; if(stack.isFood()) s = h * stack.getItem().getFoodComponent().getSaturationModifier() * 2f;
            int hunger = (int) MAUtil.split(atFullness, (int) MAUtil.minimum(20 - MAUtil.maximum(player.getHungerManager().getFoodLevel() + h, 20), 0));
            float saturation = (float) MAUtil.split(atFullness, MAUtil.minimum(20.0f - MAUtil.maximum(player.getHungerManager().getSaturationLevel() + s, player.getHungerManager().getFoodLevel()+h), 0));
            int fullness = (int)atFullness.get();
            player.getHungerManager().add(hunger, saturation);
            setServerPlayerFullness((ServerPlayerEntity)player, (int) MAUtil.maximum(getServerPlayerFullness((ServerPlayerEntity)player) + fullness, MAX_FULLNESS));
        }
    }

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
    public static int getHealedFullness(PlayerEntity player, ItemStack stack) {
        if(MealItemRegistry.getFullness(player, stack) > 0) {
            AtomicDouble atFullness = new AtomicDouble(MealItemRegistry.getFullness(player, stack));
            int h = 0;if (stack.isFood()) h = stack.getItem().getFoodComponent().getHunger();
            float s = 0;if (stack.isFood()) s = h * stack.getItem().getFoodComponent().getSaturationModifier() * 2f;
            MAUtil.split(atFullness, (int) MAUtil.minimum(20 - MAUtil.maximum(player.getHungerManager().getFoodLevel() + h, 20), 0));
            MAUtil.split(atFullness, MAUtil.minimum(20.0f - MAUtil.maximum(player.getHungerManager().getSaturationLevel() + s, player.getHungerManager().getFoodLevel() + h), 0));
            return (int) atFullness.get();
        } return 0;
    }

    /**
     * An initialization method.
     * Do not call.
     */
    public static void initCommon() {
        ServerSidePacketRegistry.INSTANCE.register(MAUtil.id("reset_fullness"), (ctx, buf) -> {
            UUID playerId = buf.readUuid();
            ctx.getTaskQueue().execute(() -> {
                for(ServerPlayerEntity player : SERVER_PLAYER_FULLNESS_STORAGE.keySet()) if(player.getUuid().equals(playerId)) {
                    System.out.println(player.getDisplayName());
                    setServerPlayerFullness(player, 0);
                }
            });
        });
    }

    /**
     * An initialization method.
     * Do not call.
     */
    public static void initClient() {
        ClientSidePacketRegistry.INSTANCE.register(MAUtil.id("update_fullness"), (ctx, buf) -> {
            int fullness = buf.readInt();
            ctx.getTaskQueue().execute(() -> CLIENT_SYNCED_FULLNESS = fullness);
        });
    }
}
