package io.github.foundationgames.mealapi.util;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
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

public class PlayerFullnessManager {
    public static final int MAX_FULLNESS = 180;

    private static final Map<ServerPlayerEntity, Integer> SERVER_PLAYER_FULLNESS_STORAGE = Maps.newHashMap();

    @Environment(EnvType.CLIENT)
    private static int CLIENT_SYNCED_FULLNESS = 0;

    public static void fromServerPlayerTag(ServerPlayerEntity player, CompoundTag tag) {
        setServerPlayerFullness(player, tag.getInt("fullnessLevel"));
    }
    public static void setServerPlayerFullness(ServerPlayerEntity player, int fullness) {
        int f = (int) MAUtil.minimum(MAUtil.maximum(fullness, MAX_FULLNESS), 0);
        SERVER_PLAYER_FULLNESS_STORAGE.put(player, f);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(f);
        if(player.networkHandler != null) ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, MAUtil.id("update_fullness"), buf);
    }
    public static int getServerPlayerFullness(ServerPlayerEntity player) {
        return SERVER_PLAYER_FULLNESS_STORAGE.getOrDefault(player, 0);
    }
    @Environment(EnvType.CLIENT)
    public static int getClientFullness() {
        return CLIENT_SYNCED_FULLNESS;
    }

    public static void addFullness(PlayerEntity player, int f, ItemStack stack) {
        AtomicDouble atFullness = new AtomicDouble(f);
        int h = 0; if(stack.isFood()) h = stack.getItem().getFoodComponent().getHunger();
        float s = 0; if(stack.isFood()) s = h * stack.getItem().getFoodComponent().getSaturationModifier() * 2f;
        int hunger = (int) MAUtil.split(atFullness, (int) MAUtil.minimum(20 - MAUtil.maximum(player.getHungerManager().getFoodLevel() + h, 20), 0));
        float saturation = (float) MAUtil.split(atFullness, MAUtil.minimum(20.0f - MAUtil.maximum(player.getHungerManager().getSaturationLevel() + s, player.getHungerManager().getFoodLevel()+h), 0));
        int fullness = (int)atFullness.get();
        player.getHungerManager().add(hunger, saturation);
        if(player instanceof ServerPlayerEntity) {
            setServerPlayerFullness((ServerPlayerEntity)player, (int) MAUtil.maximum(getServerPlayerFullness((ServerPlayerEntity)player) + fullness, MAX_FULLNESS));
        }
    }

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

    public static void initClient() {
        ClientSidePacketRegistry.INSTANCE.register(MAUtil.id("update_fullness"), (ctx, buf) -> {
            int fullness = buf.readInt();
            ctx.getTaskQueue().execute(() -> CLIENT_SYNCED_FULLNESS = fullness);
        });
    }
}
