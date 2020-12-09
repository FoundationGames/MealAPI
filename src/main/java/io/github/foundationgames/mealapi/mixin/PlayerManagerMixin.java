package io.github.foundationgames.mealapi.mixin;

import io.github.foundationgames.mealapi.api.PlayerFullnessManager;
import io.github.foundationgames.mealapi.util.MAUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Shadow private MinecraftServer server;

    @Inject(method = "onPlayerConnect", at = @At(value = "TAIL"))
    private void sendFullness(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(PlayerFullnessManager.getServerPlayerFullness(player));
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, MAUtil.id("update_fullness"), buf);
    }
}
