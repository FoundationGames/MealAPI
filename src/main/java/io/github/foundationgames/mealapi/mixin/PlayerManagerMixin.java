package io.github.foundationgames.mealapi.mixin;

import io.github.foundationgames.mealapi.impl.PlayerFullnessUtilImpl;
import io.github.foundationgames.mealapi.util.MAUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At(value = "TAIL"))
    private void mealapi$sendFullness(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(PlayerFullnessUtilImpl.INSTANCE.getPlayerFullness(player));
        ServerPlayNetworking.send(player, MAUtil.id("update_fullness"), buf);
    }
}
