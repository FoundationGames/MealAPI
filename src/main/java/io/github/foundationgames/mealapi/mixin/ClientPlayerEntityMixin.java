package io.github.foundationgames.mealapi.mixin;

import com.mojang.authlib.GameProfile;
import io.github.foundationgames.mealapi.util.MAUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends PlayerEntity {
    public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }
    @Override
    public boolean isSpectator() {
        return false;
    }
    @Override
    public boolean isCreative() {
        return false;
    }

    @Inject(method = "requestRespawn", at = @At(value = "HEAD"))
    private void sendFullnessResetPacket(CallbackInfo ci) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeUuid(((ClientPlayerEntity)(Object)this).getUuid());
        ClientSidePacketRegistry.INSTANCE.sendToServer(MAUtil.id("reset_fullness"), buf);
    }
}
