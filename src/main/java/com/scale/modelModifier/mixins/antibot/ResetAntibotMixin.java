package com.scale.modelModifier.mixins.antibot;

import com.scale.modelModifier.utils.antibot.TargetUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListWidget;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ResetAntibotMixin {
    // my other mod that has antibot does this, idk why tbh
    @Inject(method = "onGameJoin", at = @At("TAIL"))
    public void onJoinGame(GameJoinS2CPacket packet, CallbackInfo ci) {
        TargetUtil.PLAYER_LIST_WIDGET = new SocialInteractionsPlayerListWidget(new SocialInteractionsScreen(), MinecraftClient.getInstance(), Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);
    }
}
