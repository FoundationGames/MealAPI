package io.github.foundationgames.mealapi.impl;

import com.google.common.util.concurrent.AtomicDouble;
import io.github.foundationgames.mealapi.api.v0.PlayerFullnessUtil;
import io.github.foundationgames.mealapi.util.HungerManagerAccess;
import io.github.foundationgames.mealapi.util.MAUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class PlayerFullnessUtilImpl implements PlayerFullnessUtil {
    /**
     * @deprecated INTERNAL, Use {@link PlayerFullnessUtil#instance()}
     */
    public static final PlayerFullnessUtil INSTANCE = new PlayerFullnessUtilImpl();

    private PlayerFullnessUtilImpl() {}

    private static final int MAX_FULLNESS = 120;

    @Environment(EnvType.CLIENT)
    private int clientSyncedFullness = 0;


    public void setPlayerFullness(ServerPlayerEntity player, int fullness) {
        int amount = MathHelper.clamp(fullness, 0, MAX_FULLNESS);
        ((HungerManagerAccess)player.getHungerManager()).mealapi$setFullness(amount);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(amount);
        if(player.networkHandler != null) ServerPlayNetworking.send(player, MAUtil.id("update_fullness"), buf);
    }

    @Override
    public int getPlayerFullness(ServerPlayerEntity player) {
        return ((HungerManagerAccess)player.getHungerManager()).mealapi$getFullness();
    }


    @Environment(EnvType.CLIENT)
    public int getClientFullness() {
        return clientSyncedFullness;
    }

    @Override
    public int getMaxFullness() {
        return MAX_FULLNESS;
    }

    @Override
    public void addFullness(ServerPlayerEntity player, int f, ItemStack food) {
        AtomicDouble atFullness = new AtomicDouble(f);
        int h = 0; if(food.isFood()) h = food.getItem().getFoodComponent().getHunger();
        float s = 0; if(food.isFood()) s = h * food.getItem().getFoodComponent().getSaturationModifier() * 2f;
        int hunger = (int) MAUtil.split(atFullness, Math.max(20 - Math.min(player.getHungerManager().getFoodLevel() + h, 20), 0));
        float saturation = (float) MAUtil.split(atFullness, Math.max(20.0f - Math.min(player.getHungerManager().getSaturationLevel() + s, player.getHungerManager().getFoodLevel()+h), 0));
        int fullness = (int)atFullness.get();
        player.getHungerManager().add(hunger, saturation);
        setPlayerFullness(player, Math.min(getPlayerFullness(player) + fullness, MAX_FULLNESS));
    }

    @Override
    public void addFullness(ServerPlayerEntity player, int f) {
        addFullness(player, f, ItemStack.EMPTY);
    }

    @Override
    public int getHealedFullness(PlayerEntity player, ItemStack stack) {
        if(MealItemRegistryImpl.INSTANCE.getFullness(player, stack) > 0) {
            AtomicDouble atFullness = new AtomicDouble(MealItemRegistryImpl.INSTANCE.getFullness(player, stack));
            int h = 0;
            float s = 0;
            if (stack.isFood()) {
                h = stack.getItem().getFoodComponent().getHunger();
                s = h * stack.getItem().getFoodComponent().getSaturationModifier() * 2f;
            }
            MAUtil.split(atFullness, Math.max(20 - Math.min(player.getHungerManager().getFoodLevel() + h, 20), 0));
            MAUtil.split(atFullness, Math.max(20.0f - Math.min(player.getHungerManager().getSaturationLevel() + s, player.getHungerManager().getFoodLevel() + h), 0));
            return (int) atFullness.get();
        }
        return 0;
    }

    public static void initCommon() {
        ServerPlayNetworking.registerGlobalReceiver(MAUtil.id("reset_fullness"), (server, player, handler, buf, responseSender) -> {
            server.execute(() -> PlayerFullnessUtilImpl.INSTANCE.setPlayerFullness(player, 0));
        });
    }

    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(MAUtil.id("update_fullness"), (client, handler, buf, sender) -> {
            ((PlayerFullnessUtilImpl)PlayerFullnessUtilImpl.INSTANCE).clientSyncedFullness = buf.readInt();
        });
    }
}
